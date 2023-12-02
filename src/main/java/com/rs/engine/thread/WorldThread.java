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

import com.rs.Settings;
import com.rs.game.World;
import com.rs.game.map.ChunkManager;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.object.OwnedObject;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.util.Logger;
import com.rs.lib.util.Utils;
import com.rs.lib.web.APIUtil;
import com.rs.utils.Timer;
import com.rs.utils.WorldUtil;
import com.rs.web.Telemetry;
import jdk.jfr.Configuration;
import jdk.jfr.Recording;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class WorldThread extends Thread {

	public static long START_CYCLE;
	public static volatile long WORLD_CYCLE;
	public static long LOWEST_TICK = 50000;
	public static long HIGHEST_TICK = 0;

	protected WorldThread() {
		setPriority(Thread.MAX_PRIORITY);
		setName("World Thread");
		setUncaughtExceptionHandler((th, ex) -> Logger.handle(WorldThread.class, "uncaughtExceptionHandler", ex));
	}

	public static void init() {
		WORLD_CYCLE = START_CYCLE = System.currentTimeMillis() / 600L;
		LowPriorityTaskExecutor.getWorldExecutor().execute(new WorldThread());
	}

	public static Set<String> NAMES = new HashSet<>();

	@Override
	public void run() {
		Configuration config = null;
		if (Settings.getConfig().isEnableJFR()) {
			try {
				config = Configuration.create(Paths.get("./darkan.jfc"));
			} catch (IOException | ParseException e) {
				Logger.handle(WorldThread.class, "run", e);
				throw new RuntimeException(e);
			}
			if (config == null) {
				RuntimeException e = new RuntimeException("Unable to load darkan flight recorder template.");
				Logger.handle(WorldThread.class, "run", e);
				throw e;
			}
		}
		Logger.debug(WorldThread.class, "run", "WorldThread initialized...");
		while(true) {
			long startTime = System.currentTimeMillis();
			Recording tickRecording = Settings.getConfig().isEnableJFR() ? new Recording(config) : null;
			try {
				if (Settings.getConfig().isEnableJFR()) {
					Timer timerJFR = new Timer().start();
					tickRecording.start();
					Logger.trace(WorldThread.class, "tick", "JFR recording() - " + timerJFR.stop());
				}
				Timer timerChunk = new Timer().start();
				ChunkManager.processChunks();
				Logger.trace(WorldThread.class, "tick", "processChunks() - " + timerChunk.stop());
				Timer timerTask = new Timer().start();
				WorldTasks.processTasks();
				Logger.trace(WorldThread.class, "tick", "processTasks() - " + timerTask.stop());
				OwnedObject.processAll();
				Timer timerNpcProcTasks = new Timer().start();
				for (NPC npc : World.getNPCs()) {
					try {
						if (npc == null || npc.hasFinished())
							continue;
						npc.processTasks();
					} catch (Throwable e) {
						Logger.handle(WorldThread.class, "run:npcProcessEntityTasks", "Error processing NPC: " + (npc == null ? "NULL NPC" : npc.getId()), e);
					}
				}
				Logger.trace(WorldThread.class, "tick", "npcProcessEntityTasks() - " + timerNpcProcTasks.stop());

				Timer timerPlayerProcTasks = new Timer().start();
				for (Player player : World.getPlayers()) {
					try {
						player.processTasks();
					} catch (Throwable e) {
						Logger.handle(WorldThread.class, "run:playerProcessEntityTasks", "Error processing player: " + (player == null ? "NULL PLAYER" : player.getUsername()), e);
					}
				}
				Logger.trace(WorldThread.class, "tick", "playerProcessEntityTasks() - " + timerPlayerProcTasks.stop());
				NAMES.clear();
				Timer timerNpcProc = new Timer().start();
				for (NPC npc : World.getNPCs()) {
					try {
						if (npc == null || npc.hasFinished())
							continue;
						npc.processEntity();
					} catch (Throwable e) {
						Logger.handle(WorldThread.class, "run:npcProcessEntity", "Error processing NPC: " + (npc == null ? "NULL NPC" : npc.getId()), e);
					}
				}
				Logger.trace(WorldThread.class, "tick", "npcProcessEntity() - " + timerNpcProc.stop());

				Timer timerPlayerProc = new Timer().start();
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
					} catch (Throwable e) {
						Logger.handle(WorldThread.class, "run:playerProcessEntity", "Error processing player: " + (player == null ? "NULL PLAYER" : player.getUsername()), e);
					}
				}
				Logger.trace(WorldThread.class, "tick", "playerProcessEntity() - " + timerPlayerProc.stop());

				Timer timerNpcMove = new Timer().start();
				for (NPC npc : World.getNPCs()) {
					if (npc == null || npc.hasFinished())
						continue;
					try {
						npc.processMovement();
					} catch (Throwable e) {
						Logger.handle(WorldThread.class, "processNPCMovement", e);
					}
				}
				Logger.trace(WorldThread.class, "tick", "processNPCMovement() - " + timerNpcMove.stop());

				Timer timerPlayerMove = new Timer().start();
				for (Player player : World.getPlayers()) {
					if (player == null || !player.hasStarted() || player.hasFinished())
						continue;
					try {
						player.processMovement();
					} catch (Throwable e) {
						Logger.handle(WorldThread.class, "processPlayerMovement", e);
					}
				}
				Logger.trace(WorldThread.class, "tick", "processPlayerMovement() - " + timerPlayerMove.stop());

				Timer timerEntityUpdate = new Timer().start();
				for (Player player : World.getPlayers()) {
					if (player == null || !player.hasStarted() || player.hasFinished())
						continue;
					try {
						player.getPackets().sendLocalPlayersUpdate();
						player.getPackets().sendLocalNPCsUpdate();
						player.postSync();
					} catch (Throwable e) {
						Logger.handle(WorldThread.class, "processPlayersPostSync", e);
					}
				}
				Logger.trace(WorldThread.class, "tick", "processPlayersPostSync() - " + timerEntityUpdate.stop());

				Timer timerUpdateZones = new Timer().start();
				ChunkManager.processUpdateZones();
				Logger.trace(WorldThread.class, "tick", "processUpdateZones() - " + timerUpdateZones.stop());

				Timer timerFlushPackets = new Timer().start();
				for (Player player : World.getPlayers()) {
					if (player == null || !player.hasStarted() || player.hasFinished())
						continue;
					player.getSession().flush();
				}
				Logger.trace(WorldThread.class, "tick", "flushPlayerPackets() - " + timerFlushPackets.stop());

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
				Logger.trace(WorldThread.class, "tick", "Tick finished - Mem: " + (Utils.formatDouble(WorldUtil.getMemUsedPerc())) + "% - " + time + "ms - Players online: " + World.getPlayers().size());

				Telemetry.queueTelemetryTick(time);
				if (time > HIGHEST_TICK)
					HIGHEST_TICK = time;
				if (time < LOWEST_TICK)
					LOWEST_TICK = time;
				if (time > 300l && Settings.getConfig().getStaffWebhookUrl() != null) {
					if (Settings.getConfig().isEnableJFR())
						tickRecording.stop();
					StringBuilder content = new StringBuilder();
					content.append("Tick concern - " + time + "ms - " + Settings.getConfig().getServerName() + " - Players online: " + World.getPlayers().size() + " - Uptime: " + Utils.ticksToTime(WORLD_CYCLE - START_CYCLE));
					content.append("```\n");
					content.append("Chunk: " + timerChunk.getFormattedTime() + "\n");
					content.append("Task: " + timerTask.getFormattedTime() + "\n");
					content.append("Update zone: " + timerUpdateZones.getFormattedTime() + "\n");
					content.append("Player proc: " + timerPlayerProc.getFormattedTime() + "\n");
					content.append("NPC proc: " + timerNpcProc.getFormattedTime() + "\n");
					content.append("Player move: " + timerPlayerMove.getFormattedTime() + "\n");
					content.append("NPC move: " + timerNpcMove.getFormattedTime() + "\n");
					content.append("Entity update: " + timerEntityUpdate.getFormattedTime() + "\n");
					content.append("Flush: " + timerFlushPackets.getFormattedTime() + "\n");
					content.append("Lowest/Highest tick time: " + LOWEST_TICK + "/"+HIGHEST_TICK);
					content.append("```\n");
					content.append("JVM Stats:\n");
					content.append("```\n");
					/**
					 * Memory and CPU usage stats
					 */
					MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
					MemoryUsage heapMemoryUsage = memoryMXBean.getHeapMemoryUsage();
					MemoryUsage nonHeapMemoryUsage = memoryMXBean.getNonHeapMemoryUsage();

					long jvmHeapUsed = heapMemoryUsage.getUsed() / 1048576L; // in MB
					long jvmNonHeapUsed = nonHeapMemoryUsage.getUsed() / 1048576L; // in MB
					long jvmTotalUsed = jvmHeapUsed + jvmNonHeapUsed;

					long jvmMaxMemory = (heapMemoryUsage.getMax() + nonHeapMemoryUsage.getMax()) / 1048576L; // in MB
					double jvmMemUsedPerc = ((double) jvmTotalUsed / jvmMaxMemory) * 100.0;
					content.append("Total JVM memory usage: " + Utils.formatLong(jvmTotalUsed) + "mb/" + Utils.formatLong(jvmMaxMemory) + "mb (" + Utils.formatDouble(jvmMemUsedPerc) + "%)\n");
					content.append("```\n");
					MultipartBody.Builder builder = new MultipartBody.Builder()
							.setType(MultipartBody.FORM)
							.addFormDataPart("content", content.toString());

					if (Settings.getConfig().isEnableJFR()) {
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						try (InputStream is = tickRecording.getStream(null, null)) {
							byte[] buffer = new byte[1024];
							int len;
							while ((len = is.read(buffer)) > -1) {
								baos.write(buffer, 0, len);
							}
							baos.flush();
						}
						builder.addFormDataPart("file", "tickRecord.jfr", RequestBody.create(baos.toByteArray(), MediaType.parse("application/octet-stream")));
					}
					APIUtil.request(Boolean.class, new Request.Builder()
							.url(Settings.getConfig().getStaffWebhookUrl())
							.post(builder.build())
							.build(), null);
					//APIUtil.post(Boolean.class, JsonFileManager.getGson().fromJson(jsonPayload, Object.class), Settings.getConfig().getStaffWebhookUrl(), "", null);
				}
			} catch (Throwable e) {
				Logger.handle(WorldThread.class, "tick", e);
			}
			long endTime = System.currentTimeMillis();
			long timeToSleep = Math.max(600 - (endTime - startTime), 0);
			try {
				if (timeToSleep > 0)
					Thread.sleep(timeToSleep);
			} catch (InterruptedException e) { }
			WORLD_CYCLE++;
		}
	}
}
