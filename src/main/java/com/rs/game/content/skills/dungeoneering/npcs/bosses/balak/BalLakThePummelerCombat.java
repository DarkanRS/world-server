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
package com.rs.game.content.skills.dungeoneering.npcs.bosses.balak;

import com.rs.game.World;
import com.rs.game.content.skills.dungeoneering.DungeonManager;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.ForceTalk;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.npc.combat.CombatScript;
import com.rs.game.model.entity.npc.combat.NPCCombatDefinitions;
import com.rs.game.model.entity.npc.combat.NPCCombatDefinitions.AttackStyle;
import com.rs.game.model.entity.player.Player;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Utils;
import com.rs.utils.WorldUtil;

public class BalLakThePummelerCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[]
				{ "Bal'lak the Pummeller" };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		final BalLakThePummeler boss = (BalLakThePummeler) npc;
		final DungeonManager manager = boss.getManager();

		final NPCCombatDefinitions defs = npc.getCombatDefinitions();

		boolean smash = Utils.random(5) == 0 && boss.getPoisionPuddles().size() == 0;
		for (Player player : manager.getParty().getTeam())
			if (WorldUtil.collides(player.getX(), player.getY(), player.getSize(), npc.getX(), npc.getY(), npc.getSize())) {
				smash = true;
				delayHit(npc, 0, player, getRegularHit(npc, getMaxHitFromAttackStyleLevel(npc, AttackStyle.MELEE, player)));
			}
		if (smash) {
			npc.setNextAnimation(new Animation(14384));
			npc.setNextForceTalk(new ForceTalk("Rrrraargh!"));
			//npc.playSoundEffect(3038);
			final Tile center = manager.getRoomCenterTile(boss.getReference());
			WorldTasks.schedule(new WorldTask() {

				@Override
				public void run() {
					for (int i = 0; i < 3; i++)
						boss.addPoisionBubble(World.getFreeTile(center, 6));
				}
			}, 1);
			return npc.getAttackSpeed();
		}

		if (Utils.random(5) == 0) {
			boss.setNextAnimation(new Animation(14383));
			for (Entity t : boss.getPossibleTargets()) {
				if (!WorldUtil.isInRange(npc.getX(), npc.getY(), npc.getSize(), t.getX(), t.getY(), t.getSize(), 0))
					continue;
				int damage = getMaxHitFromAttackStyleLevel(npc, AttackStyle.MELEE, t);
				int damage2 = getMaxHitFromAttackStyleLevel(npc, AttackStyle.MELEE, t);
				if (t instanceof Player player)
					if ((damage > 0 || damage2 > 0)) {
						player.setProtectionPrayBlock(2);
						player.sendMessage("You are injured and currently cannot use protection prayers.");
					}
				delayHit(npc, 0, t, getRegularHit(npc, damage));
				delayHit(npc, 0, t, getRegularHit(npc, damage2));
			}
			return npc.getAttackSpeed();
		}

		switch (Utils.random(2)) {
		case 0://reg melee left

			final boolean firstHand = Utils.random(2) == 0;

			boss.setNextAnimation(new Animation(firstHand ? defs.getAttackEmote() : defs.getAttackEmote() + 1));
			delayHit(npc, 0, target, getMeleeHit(npc, getMaxHit(npc, (int) (npc.getLevelForStyle(AttackStyle.MELEE) * 0.8), AttackStyle.MELEE, target)));
			delayHit(npc, 2, target, getMeleeHit(npc, getMaxHit(npc, (int) (npc.getLevelForStyle(AttackStyle.MELEE) * 0.8), AttackStyle.MELEE, target)));
			WorldTasks.schedule(new WorldTask() {

				@Override
				public void run() {
					boss.setNextAnimation(new Animation(firstHand ? defs.getAttackEmote() + 1 : defs.getAttackEmote()));
				}

			}, 1);
			break;
		case 1://magic attack multi
			boss.setNextAnimation(new Animation(14380));
			boss.setNextSpotAnim(new SpotAnim(2441));
			for (Entity t : npc.getPossibleTargets()) {
				World.sendProjectile(npc, t, 2872, 50, 30, 41, 40, 0, 0);
				delayHit(npc, 1, t, getMagicHit(npc, getMaxHit(npc, (int) (boss.getMaxHit() * 0.6), AttackStyle.MAGE, t)));
			}
			return npc.getAttackSpeed() - 2;
		}

		return npc.getAttackSpeed();
	}
}
