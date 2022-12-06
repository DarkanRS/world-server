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
package com.rs.game.content.bosses;

import com.rs.game.World;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.Hit;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.npc.combat.CombatScript;
import com.rs.game.model.entity.npc.combat.NPCCombatDefinitions.AttackStyle;
import com.rs.game.model.entity.pathing.Direction;
import com.rs.game.model.entity.player.Equipment;
import com.rs.game.model.entity.player.Player;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;

public class ChaosElementalCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { "Chaos Elemental" };
	}

	@Override
	public int attack(NPC npc, final Entity target) {
		int atk = Utils.random(100);
		npc.setNextAnimation(new Animation(npc.getCombatDefinitions().getAttackEmote()));
		if (atk <= 10) {
			World.sendProjectile(npc, target, 2966, 30, 30, 45, 30, 15, 0);
			WorldTasks.schedule(new WorldTask() {
				@Override
				public void run() {
					for (int i = 6;i > 0;i++) {
						Direction dir = Direction.random();
						if (World.floorFree(target.getPlane(), target.getX() + dir.getDx()*i, target.getY() + dir.getDy()*i, target.getSize())) {
							target.setNextWorldTile(WorldTile.of(target.getX() + dir.getDx()*i, target.getY() + dir.getDy()*i, target.getPlane()));
							break;
						}
					}
				}
			}, Utils.getDistanceI(npc.getTile(), target.getTile())/3);
		} else if (atk <= 18) {
			World.sendProjectile(npc, target, 310, 30, 30, 45, 30, 15, 0);
			if (target instanceof Player player)
				WorldTasks.schedule(new WorldTask() {
					@Override
					public void run() {
						int num = player.getInventory().getFreeSlots();
						if (num > 0) {
							if (player.getEquipment().getWeaponId() != -1)
								Equipment.remove(player, Equipment.WEAPON, false);
							if (num <= 1) {
								player.getAppearance().generateAppearanceData();
								return;
							}
							int i = -1;
							while (i < Equipment.SIZE && player.getInventory().hasFreeSlots()) {
								i++;
								if (i == 3)
									continue;
								if (player.getInventory().getFreeSlots() <= 0)
									break;
								Equipment.remove(player, i, false);
							}
							player.getAppearance().generateAppearanceData();
						}
					}
				}, Utils.getDistanceI(npc.getTile(), target.getTile())/3);
		} else {
			int damage = getMaxHit(npc, 300, AttackStyle.MAGE, target);
			Hit hit = getMagicHit(npc, damage);
			int rand = Utils.random(3);
			if (rand == 0) {
				damage = getMaxHit(npc, 300, AttackStyle.RANGE, target);
				hit = getRangeHit(npc, damage);
			} else if (rand == 1) {
				damage = getMaxHit(npc, 300, AttackStyle.MELEE, target);
				hit = getMeleeHit(npc, damage);
			}
			World.sendProjectile(npc, target, 1279, 30, 30, 45, 30, 15, 0);
			delayHit(npc, Utils.getDistanceI(npc.getTile(), target.getTile())/3, target, hit);
		}
		return 4;
	}

}
