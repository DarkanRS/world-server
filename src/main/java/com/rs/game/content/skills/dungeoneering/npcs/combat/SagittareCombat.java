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

import com.rs.game.World;
import com.rs.game.content.skills.dungeoneering.DungeonManager;
import com.rs.game.content.skills.dungeoneering.RoomReference;
import com.rs.game.content.skills.dungeoneering.npcs.Sagittare;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.ForceTalk;
import com.rs.game.model.entity.Hit;
import com.rs.game.model.entity.Hit.HitLook;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.npc.combat.CombatScript;
import com.rs.game.model.entity.npc.combat.NPCCombatDefinitions.AttackStyle;
import com.rs.game.model.entity.player.Player;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;

public class SagittareCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { "Sagittare" };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		final Sagittare boss = (Sagittare) npc;
		if (boss.isUsingSpecial() && !boss.isCantInteract()) {
			sendRainDropAttack(boss);
			return 10;
		}
		int attack = Utils.random(5);
		switch (attack) {
		case 0:// Normal range
		case 1:// Normal magic
		case 2:// Multi magic
			npc.setNextAnimation(new Animation(13271));
			npc.setNextSpotAnim(new SpotAnim(attack == 0 ? 2532 : 2534, 0, 96));
			for (Entity t : npc.getPossibleTargets()) {
				if ((attack == 0 || attack == 1) && t != target)
					continue;
				World.sendProjectile(npc, t, attack == 0 ? 2533 : 2535, 65, 50, 54, 35, 5, 0);
				if (attack == 0)
					delayHit(npc, 1, t, getRangeHit(npc, getMaxHitFromAttackStyleLevel(npc, AttackStyle.RANGE, t)));
				else
					delayHit(npc, 1, t, getMagicHit(npc, getMaxHitFromAttackStyleLevel(npc, AttackStyle.MAGE, t)));
			}
			break;
		case 3:// Bind attacks
		case 4:
			boolean isMagicAttack = attack == 3;
			npc.setNextAnimation(new Animation(13271));
			npc.setNextSpotAnim(new SpotAnim(isMagicAttack ? 2536 : 2539, 0, 96));

			for (Entity t : npc.getPossibleTargets()) {
				if (!(t instanceof Player player))
					continue;
				boolean bindTarget = false;

				World.sendProjectile(npc, t, isMagicAttack ? 2537 : 2540, 65, 50, 54, 35, 5, 0);
				if (isMagicAttack) {
					if (!player.getPrayer().isProtectingMage())
						bindTarget = true;
					delayHit(npc, 1, t, getMagicHit(npc, getMaxHitFromAttackStyleLevel(npc, AttackStyle.MAGE, t)));
				} else {
					bindTarget = Utils.random(2) == 0;// 50/50
					delayHit(npc, 1, t, getMagicHit(npc, getMaxHitFromAttackStyleLevel(npc, AttackStyle.MAGE, t)));
				}
				if (bindTarget) {
					player.freeze(8);
					player.setRun(false);
				}
				player.setNextSpotAnim(new SpotAnim(2541, 75, 65));
			}
			break;

		}
		return 5;
	}

	private void sendRainDropAttack(final Sagittare boss) {
		boss.setCantInteract(true);
		boss.setNextForceTalk(new ForceTalk("Back off!"));
		final WorldTile center = WorldTile.of(boss.getTile());
		final DungeonManager manager = boss.getManager();
		final RoomReference rRef = manager.getCurrentRoomReference(center);
		if (rRef == null)
			return;
		WorldTasks.schedule(new WorldTask() {

			int cycles;

			@Override
			public void run() {
				cycles++;

				if (boss == null || boss.isDead()) {
					stop();
					return;
				}

				if (cycles == 2) {
					boss.setNextForceTalk(new ForceTalk("Arrow-rain!"));
					boss.setNextAnimation(new Animation(13270));
					boss.setNextSpotAnim(new SpotAnim(2542, 0, 93));
				} else if (cycles == 5) {
					boss.setCantInteract(false);
					boss.setNextAnimation(new Animation(8939));
					boss.setNextSpotAnim(new SpotAnim(1576));
				} else if (cycles == 7) {
					int stage = boss.getStage();
					WorldTile teleport = World.getFreeTile(manager.getTile(rRef, 6, 6), 1);

					if (stage != 1 && stage != -1) {
						int corner = Utils.random(4);
						if (corner == 0)// this is good
							teleport = manager.getTile(rRef, 1, Utils.random(14) + 1);// 1,1 14, 1, 14, 14, 2, 14
						else if (corner == 1)// this is good
							teleport = manager.getTile(rRef, 14 - Utils.random(14), 1);
						else if (corner == 2)// this is good
							teleport = manager.getTile(rRef, 14, 14 - Utils.random(14));
						else
							// this is good
							teleport = manager.getTile(rRef, Utils.random(14) + 1, 14);
					}

					for (int x = -1; x < 2; x++)
						for (int y = -1; y < 2; y++)
							World.sendProjectile(boss, center.transform(x, y, 0), 2533, 250, 0, 40, 0, 0, 0);
					boss.setNextWorldTile(teleport);
					boss.setNextAnimation(new Animation(8941));
					boss.setNextSpotAnim(new SpotAnim(1577));
				} else if (cycles == 8) {
					targetL: for (Entity target : boss.getPossibleTargets()) {
						if (!(target instanceof Player player))
							continue;
						for (int x = -1; x < 2; x++)
							for (int y = -1; y < 2; y++) {
								WorldTile projectileTile = center.transform(x, y, 0);
								if (player.getX() != projectileTile.getX() || player.getY() != projectileTile.getY())
									continue targetL;
							}
						player.setRun(false);
						player.freeze(8);
						player.sendMessage("You have been injured and can't move.");
						int hit = (int) (boss.getMaxHit() * .1 + getMaxHit(boss, (int) (boss.getMaxHit() * .90), AttackStyle.RANGE, player));
						player.applyHit(new Hit(boss, hit, HitLook.TRUE_DAMAGE));
					}
					boss.setUsingSpecial(false);
					stop();
					return;
				}
			}
		}, 0, 0);
	}
}
