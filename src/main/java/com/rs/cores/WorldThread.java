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
package com.rs.cores;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.rs.Settings;
import com.rs.game.World;
import com.rs.game.npc.NPC;
import com.rs.game.object.OwnedObject;
import com.rs.game.player.Player;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.util.Logger;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.annotations.ServerStartupEvent;
import com.rs.web.Telemetry;

@PluginEventHandler
public final class WorldThread extends Thread {

	public static volatile long WORLD_CYCLE;

	protected WorldThread() {
		setPriority(Thread.MAX_PRIORITY);
		setName("World Thread");
		setUncaughtExceptionHandler((th, ex) -> Logger.handle(ex));
	}

	@ServerStartupEvent
	public static void init() {
		WORLD_CYCLE = System.currentTimeMillis() / 600L;
		CoresManager.getWorldExecutor().scheduleAtFixedRate(new WorldThread(), 0, Settings.WORLD_CYCLE_MS, TimeUnit.MILLISECONDS);
	}

	public static Set<String> NAMES = new HashSet<>();

	@Override
	public final void run() {
		WORLD_CYCLE++;
		try {
			long startTime = System.currentTimeMillis();
			WorldTasks.processTasks();
			OwnedObject.process();
			World.processRegions();
			NAMES.clear();
			for (NPC npc : World.getNPCs()) {
				if (npc == null || npc.hasFinished())
					continue;
				npc.processEntity();
			}
			for (Player player : World.getPlayers()) {
				if (player != null && player.getTempAttribs().getB("realFinished"))
					player.realFinish();
				if (player == null || !player.hasStarted() || player.hasFinished())
					continue;
				if (NAMES.contains(player.getUsername()))
					player.logout(false);
				else
					NAMES.add(player.getUsername());
				player.processEntity();
			}
			for (Player player : World.getPlayers()) {
				if (player == null)
					continue;
				player.getPackets().sendLocalPlayersUpdate();
				player.getPackets().sendLocalNPCsUpdate(player);
				player.postSync();
				player.processProjectiles();
				player.getSession().flush();
			}
			World.removeProjectiles();
			for (Player player : World.getPlayers()) {
				if (player == null || !player.hasStarted() || player.hasFinished())
					continue;
				player.resetMasks();
			}
			for (NPC npc : World.getNPCs()) {
				if (npc == null || npc.hasFinished())
					continue;
				npc.resetMasks();
			}
			Telemetry.queueTelemetryTick((System.currentTimeMillis() - startTime));
		} catch (Throwable e) {
			Logger.handle(e);
		}
	}
}
