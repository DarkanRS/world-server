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
package com.rs.engine.thread;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.rs.Launcher;
import com.rs.Settings;
import com.rs.game.World;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.object.OwnedObject;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.util.Logger;
import com.rs.lib.util.Utils;
import com.rs.web.Telemetry;

public final class WorldThread extends Thread {

	public static volatile long WORLD_CYCLE;

	protected WorldThread() {
		setPriority(Thread.MAX_PRIORITY);
		setName("World Thread");
		setUncaughtExceptionHandler((th, ex) -> Logger.handle(WorldThread.class, "uncaughtExceptionHandler", ex));
	}

	public static void init() {
		WORLD_CYCLE = System.currentTimeMillis() / 600L;
		LowPriorityTaskExecutor.getWorldExecutor().scheduleAtFixedRate(new WorldThread(), 0, Settings.WORLD_CYCLE_MS, TimeUnit.MILLISECONDS);
	}

	public static Set<String> NAMES = new HashSet<>();

	@Override
	public final void run() {
		WORLD_CYCLE++;
		try {
			long startTime = System.currentTimeMillis();
			long nsS = System.nanoTime();
			World.processChunks();
			Logger.trace(WorldThread.class, "tick", "processChunks() - " + TimeUnit.NANOSECONDS.toMillis((System.nanoTime() - nsS)) + "ms");
			nsS = System.nanoTime();
			WorldTasks.processTasks();
			Logger.trace(WorldThread.class, "tick", "processTasks() - " + TimeUnit.NANOSECONDS.toMillis((System.nanoTime() - nsS)) + "ms");
			OwnedObject.process();
			NAMES.clear();
			nsS = System.nanoTime();
			for (Player player : World.getPlayers()) {
				try {
					if (player != null && player.getTempAttribs().getB("realFinished"))
						player.realFinish();
					if (player == null || !player.hasStarted() || player.hasFinished())
						continue;
					if (NAMES.contains(player.getUsername()))
						player.logout(false);
					else
						NAMES.add(player.getUsername());
					player.processEntity();
				} catch(Throwable e) {
					Logger.handle(WorldThread.class, "run:playerProcessEntity", "Error processing player: " + (player == null ? "NULL PLAYER" : player.getUsername()), e);
				}
			}
			Logger.trace(WorldThread.class, "tick", "playerProcessEntity() - " + TimeUnit.NANOSECONDS.toMillis((System.nanoTime() - nsS)) + "ms");
			nsS = System.nanoTime();
			for (NPC npc : World.getNPCs()) {
				try {
					if (npc == null || npc.hasFinished())
						continue;
					npc.processEntity();
				} catch(Throwable e) {
					Logger.handle(WorldThread.class, "run:npcProcessEntity", "Error processing NPC: " + (npc == null ? "NULL NPC" : npc.getId()), e);
				}
			}
			Logger.trace(WorldThread.class, "tick", "npcProcessEntity() - " + TimeUnit.NANOSECONDS.toMillis((System.nanoTime() - nsS)) + "ms");
			nsS = System.nanoTime();
			for (Player player : World.getPlayers()) {
				if (player == null || !player.hasStarted() || player.hasFinished())
					continue;
				try {
					player.processMovement();
				} catch(Throwable e) {
					Logger.handle(WorldThread.class, "processPlayerMovement", e);
				}
			}
			Logger.trace(WorldThread.class, "tick", "processPlayerMovement() - " + TimeUnit.NANOSECONDS.toMillis((System.nanoTime() - nsS)) + "ms");
			nsS = System.nanoTime();
			for (NPC npc : World.getNPCs()) {
				if (npc == null || npc.hasFinished())
					continue;
				try {
					npc.processMovement();
				} catch(Throwable e) {
					Logger.handle(WorldThread.class, "processNPCMovement", e);
				}
			}
			Logger.trace(WorldThread.class, "tick", "processNPCMovement() - " + TimeUnit.NANOSECONDS.toMillis((System.nanoTime() - nsS)) + "ms");
			nsS = System.nanoTime();
			for (Player player : World.getPlayers()) {
				if (player == null || !player.hasStarted() || player.hasFinished())
					continue;
				try {
					player.getPackets().sendLocalPlayersUpdate();
					player.getPackets().sendLocalNPCsUpdate();
					player.postSync();
				} catch(Throwable e) {
					Logger.handle(WorldThread.class, "processPlayersPostSync", e);
				}
			}
			Logger.trace(WorldThread.class, "tick", "processPlayersPostSync() - " + TimeUnit.NANOSECONDS.toMillis((System.nanoTime() - nsS)) + "ms");
			nsS = System.nanoTime();
			World.processUpdateZones();
			Logger.trace(WorldThread.class, "tick", "processUpdateZones() - " + TimeUnit.NANOSECONDS.toMillis((System.nanoTime() - nsS)) + "ms");
			nsS = System.nanoTime();
			for (Player player : World.getPlayers()) {
				if (player == null || !player.hasStarted() || player.hasFinished())
					continue;
				player.getSession().flush();
			}
			Logger.trace(WorldThread.class, "tick", "flushPlayerPackets() - " + TimeUnit.NANOSECONDS.toMillis((System.nanoTime() - nsS)) + "ms");
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
			World.processEntityLists();
			long time = (System.currentTimeMillis() - startTime);
			Logger.info(WorldThread.class, "tick", "Tick finished - Mem: " + (Utils.formatDouble(Launcher.getMemUsedPerc())) + "% - " + time + "ms - Players online: " + World.getPlayers().size());
			Telemetry.queueTelemetryTick(time);
		} catch (Throwable e) {
			Logger.handle(WorldThread.class, "tick", e);
		}
	}
}
