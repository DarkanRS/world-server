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
package com.rs.game.content.bosses.corp;

import java.util.List;

import com.rs.game.World;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.npc.combat.CombatScript;
import com.rs.game.model.entity.npc.combat.NPCCombatDefinitions;
import com.rs.game.model.entity.npc.combat.NPCCombatDefinitions.AttackStyle;
import com.rs.game.model.entity.player.Player;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Utils;

public class CorporealBeastCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 8133 };
	}

	@Override
	public int attack(final NPC npc, final Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		if (Utils.getRandomInclusive(3) == 0 && npc.getHitpoints() < (npc.getMaxHitpoints()/2)) {
			CorporealBeast beast = (CorporealBeast) npc;
			beast.spawnDarkEnergyCore();
		}
		int size = npc.getSize();
		final List<Entity> possibleTargets = npc.getPossibleTargets();
		int attackStyle = Utils.getRandomInclusive(4);
		if (attackStyle == 0 || attackStyle == 1) { // melee
			int distanceX = target.getX() - npc.getX();
			int distanceY = target.getY() - npc.getY();
			if ((distanceX <= size) && (distanceX >= -1) && (distanceY <= size) && (distanceY >= -1)) {
				npc.setNextAnimation(new Animation(attackStyle == 0 ? defs.getAttackEmote() : 10058));
				delayHit(npc, 0, target, getMeleeHit(npc, getMaxHit(npc, defs.getMaxHit(), AttackStyle.MELEE, target)));
				return npc.getAttackSpeed();
			}
			attackStyle = 2 + Utils.getRandomInclusive(2);
		}
		if (attackStyle == 2) { // powerfull mage spiky ball
			npc.setNextAnimation(new Animation(10410));
			delayHit(npc, World.sendProjectile(npc, target, 1825, 41, 16, 10, 1, 16, 0).getTaskDelay(), target, getMagicHit(npc, getMaxHit(npc, 650, AttackStyle.MAGE, target)));
		} else if (attackStyle == 3) { // translucent ball of energy
			npc.setNextAnimation(new Animation(10410));
			int delay = World.sendProjectile(npc, target, 1823, 41, 16, 10, 1, 16, 0).getTaskDelay();
			delayHit(npc, delay, target, getMagicHit(npc, getMaxHit(npc, 550, AttackStyle.MAGE, target)));
			if (target instanceof Player player)
				WorldTasks.schedule(new WorldTask() {
					@Override
					public void run() {
						int skill = Utils.getRandomInclusive(2);
						skill = skill == 0 ? Constants.MAGIC : (skill == 1 ? Constants.SUMMONING : Constants.PRAYER);
						if (skill == Constants.PRAYER)
							player.getPrayer().drainPrayer(10 + Utils.getRandomInclusive(40));
						else {
							int lvl = player.getSkills().getLevel(skill);
							lvl -= 1 + Utils.getRandomInclusive(4);
							player.getSkills().set(skill, lvl < 0 ? 0 : lvl);
						}
						player.sendMessage("Your " + Constants.SKILL_NAME[skill] + " has been slighly drained!");
					}

				}, delay);
		} else if (attackStyle == 4) {
			npc.setNextAnimation(new Animation(10410));
			final Tile tile = Tile.of(target.getTile());
			WorldTasks.schedule(new WorldTask() {
				@Override
				public void run() {
					for (int i = 0; i < 6; i++) {
						final Tile newTile = Tile.of(tile, 3);
						if (!World.floorAndWallsFree(newTile, 1))
							continue;
						for (Entity t : possibleTargets) {
							if (Utils.getDistance(newTile.getX(), newTile.getY(), t.getX(), t.getY()) > 1 || !t.lineOfSightTo(newTile, false))
								continue;
							delayHit(npc, 0, t, getMagicHit(npc, getMaxHit(npc, 350, AttackStyle.MAGE, t)));
						}
						WorldTasks.schedule(new WorldTask() {
							@Override
							public void run() {
								World.sendSpotAnim(newTile, new SpotAnim(1806));
							}
						}, World.sendProjectile(tile, newTile, 1824, 0, 0, 0, 1, 30, 0).getTaskDelay());
					}
				}
			}, World.sendProjectile(npc, tile, 1824, 41, 16, 0, 1, 16, 0).getTaskDelay());
		}
		return npc.getAttackSpeed();
	}
}
