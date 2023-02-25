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

import java.util.LinkedList;
import java.util.List;

import com.rs.game.World;
import com.rs.game.content.skills.dungeoneering.DungeonManager;
import com.rs.game.content.skills.dungeoneering.npcs.LakkTheRiftSplitter;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.ForceTalk;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.npc.combat.CombatScript;
import com.rs.game.model.entity.npc.combat.NPCCombatDefinitions.AttackStyle;
import com.rs.game.model.entity.player.Player;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Utils;
import com.rs.utils.WorldUtil;

public class LakkTheRiftSplitterCombat extends CombatScript {

	//	private static final int[] VOICES =
	//	{ 3034, 2993, 3007 };
	private static final String[] MESSAGES = { "A flame portal will flush you out!", "Taste miasma!", "This will cut you down to size!" };

	@Override
	public Object[] getKeys() {
		return new Object[] { "Har'Lakk the Riftsplitter" };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		final LakkTheRiftSplitter boss = (LakkTheRiftSplitter) npc;

		DungeonManager manager = boss.getManager();

		boolean smash = false;
		for (Player player : manager.getParty().getTeam())
			if (WorldUtil.collides(player.getX(), player.getY(), player.getSize(), npc.getX(), npc.getY(), npc.getSize())) {
				smash = true;
				player.setProtectionPrayBlock(2);
				delayHit(npc, 0, player, getRegularHit(npc, getMaxHit(npc, (int) (npc.getLevelForStyle(AttackStyle.MELEE) * .85), AttackStyle.MELEE, player)));
				delayHit(npc, 0, player, getRegularHit(npc, getMaxHit(npc, (int) (npc.getLevelForStyle(AttackStyle.MELEE) * .60), AttackStyle.MELEE, player)));
			}
		if (smash) {
			npc.setNextAnimation(new Animation(14383));
			return 5;
		}

		if (Utils.random(4) == 0) {
			final int type = Utils.random(3);
			switch (type) {
			case 0:
			case 1:
			case 2:
				final List<Tile> boundary = new LinkedList<>();
				for (int x = -1; x < 2; x++)
					for (int y = -1; y < 2; y++)
						boundary.add(target.transform(x, y, 0));
				if (boss.doesBoundaryOverlap(boundary)) {
					regularMagicAttack(target, npc);
					return 5;
				}
				// npc.playSoundEffect(VOICES[type]);
				WorldTasks.schedule(new WorldTask() {

					@Override
					public void run() {
						boss.setNextForceTalk(new ForceTalk(MESSAGES[type]));
						boss.setNextAnimation(new Animation(14398));
						boss.addPortalCluster(type, boundary.toArray(new Tile[1]));
					}
				}, 1);
				return 5;
			}
		}

		// melee or magic
		boolean onRange = WorldUtil.isInRange(npc.getX(), npc.getY(), npc.getSize(), target.getX(), target.getY(), target.getSize(), 0);
		boolean melee = onRange && Utils.random(2) == 0;
		if (melee) {
			npc.setNextAnimation(new Animation(14375));
			delayHit(npc, 0, target, getMeleeHit(npc, getMaxHitFromAttackStyleLevel(npc, AttackStyle.MELEE, target)));
		} else
			regularMagicAttack(target, npc);
		return 5;
	}

	private void regularMagicAttack(Entity target, NPC npc) {
		npc.setNextAnimation(new Animation(14398));
		World.sendProjectile(npc, target, 2579, 50, 30, 41, 40, 0, 0);
		if (target instanceof Player player) {
			int damage = getMaxHitFromAttackStyleLevel(npc, AttackStyle.MAGE, player);
			if (player.getPrayer().getPoints() > 0 && player.getPrayer().isProtectingMage()) {
				player.getPrayer().drainPrayer((int) (damage * .5));
				player.sendMessage("Your prayer points feel drained.");
			} else
				delayHit(npc, 1, player, getMagicHit(npc, damage));
		}
		target.setNextSpotAnim(new SpotAnim(2580, 75, 0));
	}
}
