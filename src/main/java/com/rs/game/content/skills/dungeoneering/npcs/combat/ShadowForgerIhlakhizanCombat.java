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
import com.rs.game.content.skills.dungeoneering.npcs.ShadowForgerIhlakhizan;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.ForceMovement;
import com.rs.game.model.entity.Hit;
import com.rs.game.model.entity.Hit.HitLook;
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
import com.rs.utils.WorldUtil;

public class ShadowForgerIhlakhizanCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { "Shadow-Forger Ihlakhizan" };
	}

	@Override
	public int attack(final NPC npc, final Entity target) {

		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		final ShadowForgerIhlakhizan forger = (ShadowForgerIhlakhizan) npc;
		final DungeonManager manager = forger.getManager();

		for (Entity t : npc.getPossibleTargets())
			t.getTempAttribs().removeB("SHADOW_FORGER_SHADOW");

		if (Utils.random(4) == 0) {
			if (Utils.random(3) == 0) {
				npc.setNextAnimation(new Animation(13019));
				npc.setNextSpotAnim(new SpotAnim(2370));
				for (int i = 0; i < 10; i++) {
					final Tile tile = ((ShadowForgerIhlakhizan) npc).getManager().getTile(forger.getReference(), 2 + Utils.random(12), 2 + Utils.random(12));
					if (WorldUtil.collides(npc.getX(), npc.getY(), npc.getSize(), tile.getX(), tile.getY(), 1))
						continue;
					World.sendProjectile(npc, tile, 2371, 120, 30, 41, 30, 16, 0);
					WorldTasks.schedule(new WorldTask() {

						@Override
						public void run() {
							World.sendSpotAnim(tile, new SpotAnim(2374));
							for (Player player : forger.getManager().getParty().getTeam()) {
								if (player.isDead() || player.getX() != tile.getX() || player.getY() != tile.getY())
									continue;
								player.applyHit(new Hit(npc, Utils.random(npc.getLevelForStyle(AttackStyle.RANGE)) + 1, HitLook.RANGE_DAMAGE));
							}
						}

					}, 2);
				}
				return npc.getAttackSpeed();
			}
			npc.setNextSpotAnim(new SpotAnim(2600));
			for (Entity t : npc.getPossibleTargets())
				if (t instanceof Player player)
					player.sendMessage("The shadow-forger starts to glow.");
			forger.setUsedShadow();
			WorldTasks.schedule(new WorldTask() {

				@Override
				public void run() {
					npc.setNextAnimation(new Animation(13016));
					for (Entity t : npc.getPossibleTargets()) {
						t.applyHit(new Hit(npc, Utils.random((int) (t.getMaxHitpoints() * 0.74)) + 1, HitLook.TRUE_DAMAGE));
						if (t instanceof Player player) {
							WorldTasks.schedule(new WorldTask() {
								private int ticks;
								private Tile tile;

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
											tile = Tile.of(Tile.of(target.getX() + (dirs[0] * distance), target.getY() + (dirs[1] * distance), target.getPlane()));
											if (World.floorFree(tile.getPlane(), tile.getX(), tile.getY()) && manager.isAtBossRoom(tile))
												break;
											if (distance == 0)
												tile = Tile.of(target.getTile());
										}
										target.faceEntity(forger);
										target.setNextAnimation(new Animation(10070));
										target.setNextForceMovement(new ForceMovement(target.getTile(), 0, tile, 2, target.getFaceAngle()));
									} else if (ticks == 2) {
										target.setNextTile(tile);
										stop();
										return;
									}
								}
							}, 0, 0);
							for (int stat = 0; stat < 7; stat++) {
								if (stat == Constants.HITPOINTS)
									continue;
								int drain = player.getSkills().getLevel(stat) / 2;
								if (stat == Constants.PRAYER)
									player.getPrayer().drainPrayer(drain * 10);
								player.getSkills().drainLevel(stat, drain);
							}
						}
					}
				}

			}, 3);
			return npc.getAttackSpeed() + 3;
		}

		int attackStyle = Utils.random(WorldUtil.isInRange(npc.getX(), npc.getY(), npc.getSize(), target.getX(), target.getY(), target.getSize(), 0) ? 2 : 1);

		switch (attackStyle) {
		case 0:
			npc.setNextAnimation(new Animation(13025));
			npc.setNextSpotAnim(new SpotAnim(2375));
			World.sendProjectile(npc, target, 2376, 120, 30, 60, 70, 16, 0);
			target.setNextSpotAnim(new SpotAnim(2377, 120, 0));
			delayHit(npc, 3, target, getRegularHit(npc, getMaxHitFromAttackStyleLevel(npc, AttackStyle.MAGE, target)));
			break;
		case 1:
			npc.setNextAnimation(new Animation(defs.getAttackEmote()));
			for (Entity t : npc.getPossibleTargets()) {
				if (!WorldUtil.isInRange(npc.getX(), npc.getY(), npc.getSize(), t.getX(), t.getY(), t.getSize(), 0))
					continue;
				delayHit(npc, 0, t, getMeleeHit(npc, getMaxHitFromAttackStyleLevel(npc, AttackStyle.MELEE, t)));
			}
			break;
		}

		return npc.getAttackSpeed();
	}
}
