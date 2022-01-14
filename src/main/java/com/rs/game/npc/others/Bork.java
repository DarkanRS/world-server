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
import com.rs.game.player.Player;
import com.rs.game.player.dialogues.DagonHai;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Logger;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCInstanceHandler;
import com.rs.utils.Ticks;

@PluginEventHandler
public class Bork extends NPC {

	public static long deadTime;

	public Bork(int id, WorldTile tile, boolean spawned) {
		super(id, tile, spawned);
		setLureDelay(0);
		setForceAgressive(true);
	}

	@Override
	public void sendDeath(Entity source) {
		deadTime = System.currentTimeMillis() + (1000 * 60 * 60);
		resetWalkSteps();
		for (Entity e : getPossibleTargets())
			if (e instanceof Player player) {
				player.getInterfaceManager().sendInterface(693);
				player.getDialogueManager().execute(new DagonHai(), 7137, player, 1);
				WorldTasks.schedule(new WorldTask() {
					@Override
					public void run() {
						player.stopAll();
					}
				}, 8);
			}
		getCombat().removeTarget();
		setNextAnimation(new Animation(getCombatDefinitions().getDeathEmote()));
		WorldTasks.schedule(new WorldTask() {

			@Override
			public void run() {
				drop();
				reset();
				setLocation(getRespawnTile());
				finish();
				if (!isSpawned())
					setRespawnTask();
				stop();
			}

		}, 4);
	}

	@Override
	public void setRespawnTask() {
		if (!hasFinished()) {
			reset();
			setLocation(getRespawnTile());
			finish();
		}
		CoresManager.schedule(() -> {
			try {
				spawn();
			} catch (Throwable e) {
				Logger.handle(e);
			}
		}, Ticks.fromHours(1));
	}

	public static String convertToTime() {
		String time = "You have to wait " + (getTime() == 0 ? "few more seconds" : getTime() + " mins") + " to kill bork again.";
		return time;
	}

	public static int getTime() {
		return (int) (deadTime - System.currentTimeMillis() / 60000);
	}

	public static boolean atBork(WorldTile tile) {
		if ((tile.getX() >= 3083 && tile.getX() <= 3120) && (tile.getY() >= 5522 && tile.getY() <= 5550))
			return true;
		return false;
	}

	public static NPCInstanceHandler toFunc = new NPCInstanceHandler(7134) {
		@Override
		public NPC getNPC(int npcId, WorldTile tile) {
			return new Bork(npcId, tile, false);
		}
	};
}
