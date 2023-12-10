// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.
//
//  Copyright (C) 2021 Trenton Kress
//  This file is part of project: Darkan
//
package com.rs.game.content.skills.dungeoneering.npcs.combat;

import com.rs.cache.loaders.ObjectType;
import com.rs.game.World;
import com.rs.game.content.skills.dungeoneering.npcs.DungeonNPC;
import com.rs.game.content.skills.dungeoneering.npcs.LexicusRunewright;
import com.rs.game.content.skills.summoning.Familiar;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.ForceTalk;
import com.rs.game.model.entity.Hit;
import com.rs.game.model.entity.Hit.HitLook;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.npc.combat.CombatScript;
import com.rs.game.model.entity.npc.combat.NPCCombatDefinitions.AttackStyle;
import com.rs.game.model.object.GameObject;
import com.rs.game.tasks.Task;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Utils;
import com.rs.utils.WorldUtil;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class LexicusRunewrightCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { "Lexicus Runewright" };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		LexicusRunewright boss = (LexicusRunewright) npc;
		if (boss.getAttackStage() == 0) {
			boss.sendAlmanacArmyAttack(target);
			boss.incrementAttackStage();
			return 3;
		}

		if (boss.getAttackStage() == 1 && Utils.random(4) == 0) {
			sendBookBarrage(boss);
			boss.incrementAttackStage();
			return 5;
		}
		if (boss.getAttackStage() == 2 && Utils.random(4) == 0) {
			boss.sendTeleport();
			boss.resetAttackStage();
			return 9;
		}

		int attack = Utils.random(WorldUtil.isInRange(target.getX(), target.getY(), target.getSize(), npc.getX(), npc.getY(), npc.getSize(), 0) ? 5 : 4);
		switch (attack) {
		case 0:// Range
		case 1:
		case 2:
		case 3:// Magic
			boolean range_style = attack == 0 || attack == 1;
			boss.setNextAnimation(new Animation(13470));
			boss.setNextSpotAnim(new SpotAnim(range_style ? 2408 : 2424));
			World.sendProjectile(npc, target, range_style ? 2409 : 2425, 40, 40, 54, 35, 5, 0);
			if (range_style)
				delayHit(npc, 1, target, getRangeHit(npc, getMaxHitFromAttackStyleLevel(npc, AttackStyle.RANGE, target)));
			else
				delayHit(npc, 1, target, getMagicHit(npc, getMaxHitFromAttackStyleLevel(npc, AttackStyle.MAGE, target)));
			target.setNextSpotAnim(new SpotAnim(range_style ? 2410 : 2426, 75, 0));
			break;
		case 4:// MELEE
			boss.setNextAnimation(new Animation(13469));
			delayHit(npc, 0, target, getMeleeHit(npc, getMaxHitFromAttackStyleLevel(npc, AttackStyle.MELEE, target)));
			break;
		}
		return 5;
	}

	public static void sendBookBarrage(final LexicusRunewright npc) {

		final List<GameObject> cases = new ArrayList<>();
		for (int x = 0; x < 16; x++)
			for (int y = 0; y < 16; y++) {
				GameObject o = npc.getManager().getObjectWithType(npc.getReference(), ObjectType.SCENERY_INTERACT, x, y);
				if (o != null && o.getId() >= 49280 && o.getId() <= 49282)
					cases.add(o);
			}

		npc.setNextForceTalk(new ForceTalk("Book barrage!"));
		WorldTasks.schedule(new Task() {

			private int cycle = 0;
			private final LinkedList<Tile> targets = new LinkedList<>();

			@Override
			public void run() {
				cycle++;

				if (npc.isDead()) {
					stop();
					return;
				}

				if (cycle == 1)
					for (Entity entity : npc.getPossibleTargets(true)) {
						if (entity instanceof DungeonNPC)
							continue;
						Tile tile = Tile.of(entity.getTile());
						targets.add(tile);

						for (int i = 0; i < 3; i++) {
							if (cases.isEmpty())
								break;
							GameObject c = cases.get(Utils.random(cases.size()));
							cases.remove(c);
							World.sendProjectile(c, tile, 2422, 60, 75, 30, 0, 0, 0);
						}
					}
				else if (cycle == 4) {

					for (Tile tile : targets)
						World.sendSpotAnim(tile, new SpotAnim(2423));

					for (Entity entity : npc.getPossibleTargets(true)) {
						if (entity instanceof DungeonNPC)
							continue;
						for (Tile tile : targets) {
							if (entity.getX() != tile.getX() || entity.getY() != tile.getY())
								continue;
							entity.applyHit(new Hit(npc, (int) (entity instanceof Familiar ? 1000 : Utils.random(entity.getMaxHitpoints() * .6, entity.getMaxHitpoints() * .9)), HitLook.TRUE_DAMAGE));
						}
					}
					targets.clear();
					stop();
				}

			}
		}, 0, 0);
		// GFX 2421+
	}
}
