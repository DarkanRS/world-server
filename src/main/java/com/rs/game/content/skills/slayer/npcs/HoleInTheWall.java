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
package com.rs.game.content.skills.slayer.npcs;

import com.rs.game.content.skills.slayer.Slayer;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.Hit;
import com.rs.game.model.entity.Hit.HitLook;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.npc.combat.NPCCombatDefinitions;
import com.rs.game.model.entity.player.Player;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCInstanceHandler;

@PluginEventHandler
public class HoleInTheWall extends NPC {

	private transient boolean hasGrabbed;

	public HoleInTheWall(int id, Tile tile) {
		super(id, tile);
		setCantFollowUnderCombat(true);
		setCantInteract(true);
		setForceAgressive(true);
	}

	@Override
	public void processNPC() {
		super.processNPC();
		if (getId() == 2058) {
			if (!hasGrabbed)
				for (Entity entity : getPossibleTargets()) {
					if (entity == null || entity.isDead() || !withinDistance(entity.getTile(), 1))
						continue;
					if (entity instanceof Player player) {
						player.resetWalkSteps();
						hasGrabbed = true;
						if (Slayer.hasSpinyHelmet(player)) {
							setNextNPCTransformation(7823);
							setNextAnimation(new Animation(1805));
							setCantInteract(false);
							player.sendMessage("The spines on your helmet repel the beast's hand.");
							return;
						}
						setNextAnimation(new Animation(1802));
						player.lock(4);
						player.setNextAnimation(new Animation(425));
						player.sendMessage("A giant hand appears and grabs your head.");
						WorldTasks.schedule(new WorldTask() {

							@Override
							public void run() {
								player.applyHit(new Hit(player, Utils.getRandomInclusive(44), HitLook.TRUE_DAMAGE));
								setNextAnimation(new Animation(-1));
								WorldTasks.schedule(new WorldTask() {

									@Override
									public void run() {
										hasGrabbed = false;
									}
								}, 20);
							}
						}, 5);
					}
				}
		} else if (!getCombat().process()) {
			setCantInteract(true);
			setNextNPCTransformation(2058);
		}
	}

	@Override
	public void sendDeath(Entity source) {
		final NPCCombatDefinitions defs = getCombatDefinitions();
		resetWalkSteps();
		getCombat().removeTarget();
		setNextAnimation(null);
		WorldTasks.scheduleTimer(loop -> {
			if (loop == 0)
				setNextAnimation(new Animation(defs.getDeathEmote()));
			else if (loop >= defs.getDeathDelay()) {
				setNPC(2058);
				drop();
				reset();
				setLocation(getRespawnTile());
				finish();
				WorldTasks.schedule(8, () -> hasGrabbed = false);
				spawn();
				return false;
			}
			return true;
		});
	}

	public static NPCInstanceHandler toFunc = new NPCInstanceHandler(2058, (npcId, tile) -> new HoleInTheWall(npcId, tile));
}
