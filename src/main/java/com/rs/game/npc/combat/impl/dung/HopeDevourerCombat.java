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
//  Copyright © 2021 Trenton Kress
//  This file is part of project: Darkan
//
package com.rs.game.npc.combat.impl.dung;

import com.rs.game.Entity;
import com.rs.game.ForceMovement;
import com.rs.game.ForceTalk;
import com.rs.game.World;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.CombatScript;
import com.rs.game.npc.combat.NPCCombatDefinitions.AttackStyle;
import com.rs.game.npc.dungeoneering.HopeDevourer;
import com.rs.game.player.Player;
import com.rs.game.player.content.skills.dungeoneering.DungeonManager;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;
import com.rs.utils.WorldUtil;

public class HopeDevourerCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { "Hope devourer" };
	}

	@Override
	public int attack(final NPC npc, final Entity target) {
		final HopeDevourer boss = (HopeDevourer) npc;
		final DungeonManager manager = boss.getManager();

		boolean stomp = false;
		for (Player player : manager.getParty().getTeam())
			if (WorldUtil.collides(player.getX(), player.getY(), player.getSize(), npc.getX(), npc.getY(), npc.getSize())) {
				stomp = true;
				delayHit(npc, 0, player, getRegularHit(npc, getMaxHit(npc, AttackStyle.MELEE, player)));
			}
		if (stomp) {
			npc.setNextAnimation(new Animation(14459));
			return 6;
		}

		if (Utils.random(10) == 0) {
			npc.setNextForceTalk(new ForceTalk("Grrrrrrrrrroooooooooaaaarrrrr"));
			WorldTasks.schedule(new WorldTask() {

				@Override
				public void run() {
					npc.setNextAnimation(new Animation(14460));
					npc.setNextSpotAnim(new SpotAnim(2844, 30, 0));
					int healedDamage = 0;
					for (Entity t : npc.getPossibleTargets()) {
						Player player = (Player) t;
						int damage = (int) Utils.random(npc.getMaxHit(AttackStyle.MAGE) * .85, npc.getMaxHit(AttackStyle.MAGE));
						if (damage > 0 && player.getPrayer().isUsingProtectionPrayer()) {
							healedDamage += damage;
							player.setProtectionPrayBlock(2);
							t.setNextSpotAnim(new SpotAnim(2845, 75, 0));
							delayHit(npc, 0, t, getMagicHit(npc, damage));
						}
					}
					npc.heal(healedDamage);
				}
			}, 2);
			return 8;
		}

		if (!WorldUtil.isInRange(npc.getX(), npc.getY(), npc.getSize(), target.getX(), target.getY(), target.getSize(), 0))
			return 0;

		if (Utils.random(5) == 0) {
			npc.setNextAnimation(new Animation(14458));
			final int damage = (int) Utils.random(npc.getMaxHit(AttackStyle.MELEE) * .85, npc.getMaxHit(AttackStyle.MELEE));
			if (target instanceof Player player)
				player.getSkills().set(Constants.DEFENSE, (int) (player.getSkills().getLevel(Constants.DEFENSE) - (damage * .05)));
			delayHit(npc, 0, target, getMeleeHit(npc, damage));
			WorldTasks.schedule(new WorldTask() {
				private int ticks;
				private WorldTile tile;

				@Override
				public void run() {
					ticks++;
					if (ticks == 1) {
						if (target instanceof Player player) {
							player.lock(2);
							player.stopAll();
						}
						byte[] dirs = Utils.getDirection(npc.getFaceAngle());
						for (int distance = 2; distance >= 0; distance--) {
							tile = new WorldTile(new WorldTile(target.getX() + (dirs[0] * distance), target.getY() + (dirs[1] * distance), target.getPlane()));
							if (World.floorFree(tile.getPlane(), tile.getX(), tile.getY()) && manager.isAtBossRoom(tile))
								break;
							if (distance == 0)
								tile = new WorldTile(target);
						}
						target.faceEntity(boss);
						target.setNextAnimation(new Animation(10070));
						target.setNextForceMovement(new ForceMovement(target, 0, tile, 2, target.getFaceAngle()));
					} else if (ticks == 2) {
						target.setNextWorldTile(tile);
						stop();
						return;
					}
				}
			}, 0, 0);
		} else {
			npc.setNextAnimation(new Animation(14457));
			int damage = (int) Utils.random(npc.getMaxHit(AttackStyle.MELEE) * .75, npc.getMaxHit(AttackStyle.MELEE));
			if (target instanceof Player player)
				if (player.getPrayer().isProtectingMelee()) {
					player.sendMessage("Your prayer completely negates the attack.", true);
					damage = 0;
				}
			delayHit(npc, 0, target, getMeleeHit(npc, damage));
		}
		return 6;
	}
}
