package com.rs.game.npc.combat.impl;

import com.rs.game.Entity;
import com.rs.game.Hit;
import com.rs.game.World;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.CombatScript;
import com.rs.game.npc.combat.NPCCombatDefinitions.AttackStyle;
import com.rs.game.pathing.Direction;
import com.rs.game.player.Equipment;
import com.rs.game.player.Player;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
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
			WorldTasksManager.schedule(new WorldTask() {
				@Override
				public void run() {
					for (int i = 6;i > 0;i++) {
						Direction dir = Direction.random();
						if (World.floorFree(target.getPlane(), target.getX() + dir.getDx()*i, target.getY() + dir.getDy()*i, target.getSize())) {
							target.setNextWorldTile(new WorldTile(target.getX() + dir.getDx()*i, target.getY() + dir.getDy()*i, target.getPlane()));
							break;
						}
					}
				}
			}, Utils.getDistanceI(npc, target)/3);
		} else if (atk <= 18) {
			World.sendProjectile(npc, target, 310, 30, 30, 45, 30, 15, 0);
			if (target instanceof Player) {
				WorldTasksManager.schedule(new WorldTask() {
					@Override
					public void run() {
						Player player = (Player) target;
						int num = player.getInventory().getFreeSlots();
						if (num > 0) {
							if (player.getEquipment().getWeaponId() != -1) {
								player.getInventory().addItem(player.getEquipment().getItem(Equipment.WEAPON).getId(), player.getEquipment().getItem(Equipment.WEAPON).getAmount(), true);
								player.getEquipment().set(Equipment.WEAPON, null);
								player.getEquipment().refresh(Equipment.WEAPON);
							}
							if (num > 1) {
								int i = -1;
								while(i < Equipment.SIZE && player.getInventory().hasFreeSlots()) {
									i++;
									if (i == 3)
										continue;
									if (player.getInventory().getFreeSlots() <= 0)
										break;
									if (player.getEquipment().getItem(i) != null) {
										player.getInventory().addItem(player.getEquipment().getItem(i).getId(), player.getEquipment().getItem(i).getAmount(), true);
										player.getEquipment().set(i, null);
										player.getEquipment().refresh(i);
									}
								}
							}
							player.getAppearance().generateAppearanceData();
						}
					}
				}, Utils.getDistanceI(npc, target)/3);
			}
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
			delayHit(npc, Utils.getDistanceI(npc, target)/3, target, hit);
		}
		return 4;
	}

}
