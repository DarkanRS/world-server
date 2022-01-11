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
package com.rs.game.npc.kalph;

import com.rs.game.Entity;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.NPCCombatDefinitions;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.WorldTile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCInstanceHandler;

@PluginEventHandler
public class KalphiteQueen extends NPC {

	public KalphiteQueen(int id, WorldTile tile, boolean spawned) {
		super(id, tile, spawned);
		setLureDelay(0);
		setForceAgressive(true);
		setIgnoreDocile(true);
	}

	@Override
	public void sendDeath(Entity source) {
		final NPCCombatDefinitions defs = getCombatDefinitions();
		resetWalkSteps();
		getCombat().removeTarget();
		setNextAnimation(null);
		WorldTasks.schedule(new WorldTask() {
			int loop;

			@Override
			public void run() {
				if (loop == 0)
					setNextAnimation(new Animation(defs.getDeathEmote()));
				else if (loop >= defs.getDeathDelay()) {
					if (getId() == 1158) {
						setCantInteract(true);
						transformIntoNPC(1160);
						setNextSpotAnim(new SpotAnim(1055));
						setNextAnimation(new Animation(6270));
						WorldTasks.schedule(new WorldTask() {

							@Override
							public void run() {
								reset();
								setCantInteract(false);
							}

						}, 5);
					} else {
						drop();
						reset();
						setLocation(getRespawnTile());
						finish();
						if (!isSpawned())
							setRespawnTask();
						transformIntoNPC(1158);
					}
					stop();
				}
				loop++;
			}
		}, 0, 1);
	}

	public static NPCInstanceHandler toFunc = new NPCInstanceHandler(1158, 1160) {
		@Override
		public NPC getNPC(int npcId, WorldTile tile) {
			return new KalphiteQueen(npcId, tile, false);
		}
	};
}
