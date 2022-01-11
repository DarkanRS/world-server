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
package com.rs.game.npc.combat.impl;

import com.rs.game.Entity;
import com.rs.game.World;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.CombatScript;
import com.rs.game.npc.combat.NPCCombatDefinitions.AttackStyle;
import com.rs.game.npc.nomad.FlameVortex;
import com.rs.game.npc.nomad.Nomad;
import com.rs.game.player.Player;
import com.rs.game.player.content.skills.magic.Magic;
import com.rs.game.player.dialogues.Dialogue;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.WorldTile;
import com.rs.utils.Ticks;

public class NomadCombat extends CombatScript {

	@Override
	public Object[] getKeys() {

		return new Object[] { 8528 };
	}

	private void spawnFlameVortex(WorldTile tile) {
		if (!World.floorAndWallsFree(tile, 1))
			return;
		new FlameVortex(tile);
	}

	@Override
	public int attack(final NPC npc, final Entity target) {
		final Nomad nomad = (Nomad) npc;
		if (target instanceof Player player)
			if (!nomad.isMeleeMode() && nomad.getHitpoints() < nomad.getMaxHitpoints() * 0.25) {
				if (!nomad.isHealed()) {
					nomad.setNextAnimation(new Animation(12700));
					nomad.heal(2500);
					nomad.setHealed(true);
					Dialogue.sendNPCDialogueNoContinue(player, nomad.getId(), 9790, "You're thougher than I thought, time to even things up!");
					player.getPackets().sendVoice(8019);
					WorldTasks.schedule(new WorldTask() {
						@Override
						public void run() {
							Dialogue.closeNoContinueDialogue(player);
						}
					}, 9);
					return npc.getAttackSpeed();
				}
				nomad.setMeleeMode();
				Dialogue.sendNPCDialogueNoContinue(player, nomad.getId(), 9790, "Enough! THIS..ENDS..NOW!");
				player.getPackets().sendVoice(7964);
				WorldTasks.schedule(new WorldTask() {
					@Override
					public void run() {
						Dialogue.closeNoContinueDialogue(player);
					}
				}, 9);
			}
		if (nomad.isMeleeMode()) {
			int distanceX = target.getX() - npc.getX();
			int distanceY = target.getY() - npc.getY();
			int size = npc.getSize();
			if (distanceX > size || distanceX < -1 || distanceY > size || distanceY < -1)
				return 0;
			npc.setNextAnimation(new Animation(12696));
			delayHit(npc, 0, target, getRegularHit(npc, getMaxHit(npc, 322, AttackStyle.MELEE, target)));
			return 2;
		}
		if (target instanceof Player player && nomad.useSpecialSpecialMove())
			switch (nomad.getNextMove()) {
			case 0:
				nomad.setNextMovePerform();
				npc.setNextAnimation(new Animation(12701));
				Dialogue.sendNPCDialogueNoContinue(player, nomad.getId(), 9842, "Let's make things interesting!");
				player.getPackets().sendVoice(8039);
				final WorldTile middle = new WorldTile(player);
				WorldTasks.schedule(new WorldTask() {
					int count;

					@Override
					public void run() {
						switch (count) {
						case 0:
							spawnFlameVortex(middle.transform(2, 2, 0));
							break;
						case 1:
							spawnFlameVortex(middle.transform(2, 0, 0));
							break;
						case 2:
							spawnFlameVortex(middle.transform(2, -2, 0));
							break;
						case 3:
							spawnFlameVortex(middle.transform(-2, -2, 0));
							break;
						case 4:
							spawnFlameVortex(middle.transform(-2, 0, 0));
							break;
						case 5:
							spawnFlameVortex(middle.transform(-2, 2, 0));
							break;
						case 6:
							spawnFlameVortex(middle.transform(3, 1, 0));
							break;
						case 7:
							spawnFlameVortex(middle.transform(3, -1, 0));
							break;
						case 8:
							spawnFlameVortex(middle.transform(1, -3, 0));
							break;
						case 9:
							spawnFlameVortex(middle.transform(-1, -3, 0));
							break;
						case 10:
							spawnFlameVortex(middle.transform(-3, -1, 0));
							break;
						case 11:
							spawnFlameVortex(middle.transform(-3, 1, 0));
							break;
						case 12:
							Dialogue.closeNoContinueDialogue(player);
							stop();
							break;
						}
						count++;
					}

				}, 0, 0);
				break;
			case 1:
				nomad.setCantFollowUnderCombat(true);
				WorldTile throne = nomad.getThroneTile();
				if (nomad.getX() != throne.getX() || nomad.getY() != throne.getY())
					nomad.sendTeleport(nomad.getThroneTile());
				WorldTasks.schedule(new WorldTask() {

					private boolean secondLoop;

					@Override
					public void run() {
						if (!secondLoop) {
							npc.setNextAnimation(new Animation(12698));
							npc.setNextSpotAnim(new SpotAnim(2281));
							Dialogue.sendNPCDialogueNoContinue(player, nomad.getId(), 9790, "You cannot hide from my wrath!");
							player.getPackets().sendVoice(7960);
							secondLoop = true;
						} else {
							if (npc.lineOfSightTo(target, false)) {
								delayHit(npc, 2, target, getRegularHit(npc, 750));
								World.sendProjectile(npc, target, 1658, 30, 30, 75, 25, 0, 0);
							}
							nomad.setCantFollowUnderCombat(false);
							Dialogue.closeNoContinueDialogue(player);
							nomad.setNextMovePerform();
							stop();
						}

					}

				}, 7, 10);
				return 25;
			case 2:
				Dialogue.sendNPCDialogueNoContinue(player, nomad.getId(), 9842, "Let's see how well you senses serve you!");
				player.getActionManager().forceStop();
				nomad.createCopies(player);
				WorldTasks.schedule(new WorldTask() {
					@Override
					public void run() {
						Dialogue.closeNoContinueDialogue(player);
					}
				}, 9);
				return 7;
			case 3:
				nomad.setCantFollowUnderCombat(true);
				throne = nomad.getThroneTile();
				nomad.sendTeleport(nomad.getThroneTile());
				Magic.sendObjectTeleportSpell(player, false, throne.transform(1, -3, 0));
				player.lock();
				WorldTasks.schedule(new WorldTask() {
					private boolean secondLoop;

					@Override
					public void run() {
						if (!secondLoop) {
							npc.setNextAnimation(new Animation(12699));
							npc.setNextSpotAnim(new SpotAnim(2280));
							player.freeze(Ticks.fromSeconds(17));
							Dialogue.sendNPCDialogueNoContinue(player, nomad.getId(), 9790, "Let's see how much punishment you can take!");
							player.getPackets().sendVoice(8001);
							player.setNextFaceWorldTile(new WorldTile(player.getX(), player.getY() + 1, 0));
							player.setNextSpotAnim(new SpotAnim(369));
							player.unlock();
							secondLoop = true;
							WorldTasks.schedule(new WorldTask() {
								@Override
								public void run() {
									Dialogue.closeNoContinueDialogue(player);
								}
							}, 9);
						} else {
							delayHit(npc, 2, target, getRegularHit(npc, player.getMaxHitpoints() - 1));
							World.sendProjectile(npc, target, 2280, 30, 30, 5, 25, 0, 0);
							nomad.setCantFollowUnderCombat(false);
							nomad.setNextMovePerform();
							stop();
						}

					}

				}, 7, 23);

				return 40;
			}
		else {
			npc.setNextAnimation(new Animation(12697));
			int damage = getMaxHit(npc, 322, AttackStyle.MAGE, target);
			delayHit(npc, 2, target, getRegularHit(npc, damage));
			if (damage == 0)
				WorldTasks.schedule(new WorldTask() {
					@Override
					public void run() {
						target.setNextSpotAnim(new SpotAnim(85, 0, 100));
					}
				}, 1);
			World.sendProjectile(npc, target, 1657, 30, 30, 75, 25, 0, 0);
		}

		return npc.getAttackSpeed();
	}

}
