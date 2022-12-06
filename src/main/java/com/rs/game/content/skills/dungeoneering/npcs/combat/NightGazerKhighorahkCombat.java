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
import com.rs.game.content.skills.dungeoneering.npcs.NightGazerKhighorahk;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.ForceMovement;
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
import com.rs.utils.WorldUtil;

public class NightGazerKhighorahkCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { "Night-gazer Khighorahk" };
	}

	public void sendRangeAoe(final NightGazerKhighorahk gazer) {
		if (gazer.isDead())
			return;
		gazer.setNextAnimation(new Animation(13425));
		for (Entity target : gazer.getPossibleTargets()) {
			World.sendProjectile(gazer, target, 2385, 60, 16, 41, 30, 0, 0);
			delayHit(gazer, 1, target, getRangeHit(gazer, getMaxHit(gazer, (int) (gazer.getMaxHit() * 0.6), AttackStyle.RANGE, target)));
		}

		if (!gazer.isSecondStage())
			WorldTasks.schedule(new WorldTask() {

				@Override
				public void run() {
					if (gazer.isDead())
						return;
					gazer.setNextAnimation(new Animation(13422));
				}

			}, 5);
	}

	@Override
	public int attack(final NPC npc, final Entity target) {
		final NightGazerKhighorahk gazer = (NightGazerKhighorahk) npc;
		final DungeonManager manager = gazer.getManager();

		/*
		 * without this check its possible to lure him so that he always nukes
		 */
		if (!gazer.isUsedSpecial()) {
			final List<Entity> targets = gazer.getPossibleTargets();
			boolean success = false;
			for (Entity t : targets)
				if (WorldUtil.isInRange(npc.getX(), npc.getY(), npc.getSize(), t.getX(), t.getY(), t.getSize(), 1)) {
					if (!success)
						success = true;
					npc.setNextAnimation(new Animation(gazer.isSecondStage() ? 13427 : 13429));
					npc.setNextSpotAnim(new SpotAnim(/* gazer.isSecondStage() ? 2391 : */2390));
					gazer.setUsedSpecial(true);
				}
			if (success) {
				WorldTasks.schedule(new WorldTask() {

					private int ticks;
					private List<WorldTile> tiles = new LinkedList<>();

					@Override
					public void run() {
						ticks++;
						if (ticks == 1)
							npc.setNextAnimation(new Animation(gazer.isSecondStage() ? 13426 : 13428));
						else if (ticks == 3) {
							for (Entity t : targets)
								if (WorldUtil.isInRange(npc.getX(), npc.getY(), npc.getSize(), t.getX(), t.getY(), t.getSize(), 1)) {
									t.applyHit(new Hit(npc, Utils.random((int) (t.getMaxHitpoints() * 0.74)) + 1, HitLook.TRUE_DAMAGE));
									if (t instanceof Player player) {
										player.lock(2);
										player.stopAll();
									}
									byte[] dirs = Utils.getDirection(npc.getFaceAngle());
									WorldTile tile = null;
									distanceLoop: for (int distance = 2; distance >= 0; distance--) {
										tile = WorldTile.of(WorldTile.of(t.getX() + (dirs[0] * distance), t.getY() + (dirs[1] * distance), t.getPlane()));
										if (World.floorFree(tile.getPlane(), tile.getX(), tile.getY()) && manager.isAtBossRoom(tile))
											break distanceLoop;
										if (distance == 0)
											tile = WorldTile.of(t.getTile());
									}
									tiles.add(tile);
									t.faceEntity(gazer);
									t.setNextAnimation(new Animation(10070));
									t.setNextForceMovement(new ForceMovement(t.getTile(), 0, tile, 2, t.getFaceAngle()));
								}
						} else if (ticks == 4) {
							for (int index = 0; index < tiles.size(); index++) {
								Entity t = targets.get(index);
								if (WorldUtil.isInRange(npc.getX(), npc.getY(), npc.getSize(), t.getX(), t.getY(), t.getSize(), 1))
									t.setNextWorldTile(tiles.get(index));
							}
							stop();
							return;
						}
					}
				}, 0, 0);
				return 10;
			}
		} else
			gazer.setUsedSpecial(false);

		if (Utils.random(10) == 0) {
			if (gazer.isSecondStage()) {
				sendRangeAoe(gazer);
				return npc.getAttackSpeed() + 1;
			}
			npc.setNextAnimation(new Animation(13423));
			WorldTasks.schedule(new WorldTask() {

				@Override
				public void run() {
					sendRangeAoe(gazer);
				}

			}, 1);
			return npc.getAttackSpeed() + 6;
		}
		if (Utils.random(3) == 0) { // range single target
			npc.setNextAnimation(new Animation(gazer.isSecondStage() ? 13433 : 13434));
			World.sendProjectile(npc, target, 2385, gazer.isSecondStage() ? 60 : 40, 16, 41, 90, 0, 0);
			delayHit(npc, 3, target, getRangeHit(npc, getMaxHitFromAttackStyleLevel(npc, AttackStyle.RANGE, target)));
			return npc.getAttackSpeed() + 1;
		}
		// magic
		npc.setNextAnimation(new Animation(gazer.isSecondStage() ? 13430 : 13431));
		World.sendProjectile(npc, target, 2385, gazer.isSecondStage() ? 60 : 40, 16, 41, 30, 0, 0);
		target.setNextSpotAnim(new SpotAnim(2386, 70, 100));
		delayHit(npc, 1, target, getMagicHit(npc, getMaxHitFromAttackStyleLevel(npc, AttackStyle.MAGE, target)));
		return npc.getAttackSpeed();
	}
}
