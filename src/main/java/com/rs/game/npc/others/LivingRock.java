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
package com.rs.game.npc.others;

import com.rs.cores.CoresManager;
import com.rs.game.Entity;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.NPCCombatDefinitions;
import com.rs.game.player.Player;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Logger;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCInstanceHandler;
import com.rs.utils.Ticks;

@PluginEventHandler
public class LivingRock extends NPC {

	private Entity source;
	private long deathTime;

	public LivingRock(int id, WorldTile tile, boolean spawned) {
		super(id, tile, spawned);
		setForceAggroDistance(4);
		setIgnoreDocile(true);
	}

	@Override
	public void sendDeath(final Entity source) {
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
					drop();
					reset();
					transformIntoRemains(source);
					stop();
				}
				loop++;
			}
		}, 0, 1);
	}

	public void transformIntoRemains(Entity source) {
		this.source = source;
		deathTime = System.currentTimeMillis();
		final int remainsId = getId() + 5;
		transformIntoNPC(remainsId);
		setRandomWalk(false);
		CoresManager.schedule(() -> {
			try {
				if (remainsId == getId())
					takeRemains();
			} catch (Throwable e) {
				Logger.handle(e);
			}
		}, Ticks.fromMinutes(3));

	}

	public boolean canMine(Player player) {
		return System.currentTimeMillis() - deathTime > 60000 || player == source;
	}

	public void takeRemains() {
		setNPC(getId() - 5);
		setLocation(getRespawnTile());
		setRandomWalk(true);
		finish();
		if (!isSpawned())
			setRespawnTask();
	}

	public static NPCInstanceHandler toFunc = new NPCInstanceHandler(8832, 8833, 8834) {
		@Override
		public NPC getNPC(int npcId, WorldTile tile) {
			return new LivingRock(npcId, tile, false);
		}
	};
}
