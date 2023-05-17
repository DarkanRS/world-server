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
package com.rs;

import com.google.gson.GsonBuilder;
import com.rs.cache.Cache;
import com.rs.cache.Index;
import com.rs.cache.loaders.ItemDefinitions;
import com.rs.cache.loaders.NPCDefinitions;
import com.rs.cache.loaders.ObjectDefinitions;
import com.rs.db.WorldDB;
import com.rs.engine.thread.LowPriorityTaskExecutor;
import com.rs.engine.thread.WorldThread;
import com.rs.game.World;
import com.rs.game.map.ChunkManager;
import com.rs.game.model.entity.player.Controller;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.file.JsonFileManager;
import com.rs.lib.json.DateAdapter;
import com.rs.lib.net.ServerChannelHandler;
import com.rs.lib.net.decoders.GameDecoder;
import com.rs.lib.net.packets.Packet;
import com.rs.lib.net.packets.PacketEncoder;
import com.rs.lib.util.*;
import com.rs.net.LobbyCommunicator;
import com.rs.net.decoders.BaseWorldDecoder;
import com.rs.plugin.PluginManager;
import com.rs.utils.Ticks;
import com.rs.utils.WorldPersistentData;
import com.rs.utils.json.ControllerAdapter;
import com.rs.web.WorldAPI;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.Date;
import java.util.logging.Level;

public final class Launcher {

	private static WorldDB DB;

	public static void main(String[] args) throws Exception {
		Logger.setupFormat();
		Logger.setLevel(Level.FINE); //FINER for traces
		JsonFileManager.setGSON(new GsonBuilder()
				.registerTypeAdapter(Controller.class, new ControllerAdapter())
				.registerTypeAdapter(Date.class, new DateAdapter())
				.registerTypeAdapter(PacketEncoder.class, new PacketEncoderAdapter())
				.registerTypeAdapter(Packet.class, new PacketAdapter())
				.registerTypeAdapterFactory(new RecordTypeAdapterFactory())
				.disableHtmlEscaping()
				.setPrettyPrinting()
				.create());

		Settings.loadConfig();
		if (!Settings.getConfig().isDebug())
			Logger.setLevel(Level.WARNING);

		long currentTime = System.currentTimeMillis();

		Cache.init(Settings.getConfig().getCachePath());

		MapXTEAs.loadKeys();

		DB = new WorldDB();
		DB.init();

		GameDecoder.loadPacketDecoders();

		LowPriorityTaskExecutor.initExecutors();
		PluginManager.loadPlugins();
		PluginManager.executeStartupHooks();

		try {
			ServerChannelHandler.init(Settings.getConfig().getWorldInfo().port(), BaseWorldDecoder.class);
		} catch (Throwable e) {
			Logger.handle(Launcher.class, "main", e);
			Logger.error(Launcher.class, "main", "Failed to initialize server channel handler. Shutting down...");
			System.exit(1);
			return;
		}
		WorldThread.init();
		Logger.info(Launcher.class, "main", "Server launched in " + (System.currentTimeMillis() - currentTime) + " ms...");
		Logger.info(Launcher.class, "main", "Server is listening at " + InetAddress.getLocalHost().getHostAddress() + ":" + Settings.getConfig().getWorldInfo().port() + "...");
		Logger.info(Launcher.class, "main", "Player will be directed to "+Settings.getConfig().getWorldInfo()+"...");
		Logger.info(Launcher.class, "main", "Registering world with lobby server...");
		Logger.info(Launcher.class, "main", Settings.getConfig().getWorldInfo());
		new WorldAPI().start();
		LobbyCommunicator.post(Boolean.class, Settings.getConfig().getWorldInfo(), "addworld", success -> {
			if (success)
				Logger.info(Launcher.class, "main", "Registered world with lobby server...");
			else
				Logger.warn(Launcher.class, "main", "Failed to register world with lobby server... You can still login locally but social features will not work properly.");
		});
		addCleanMemoryTask();
//		Runtime.getRuntime().addShutdownHook(new Thread() {
//			@Override
//			public void run() {
//				try {
//					for (Player player : World.getPlayers()) {
//						if (player == null || !player.hasStarted())
//							continue;
//						try {
//							player.getPackets().sendLogout(player, true);
//							player.realFinish();
//						} catch(Throwable e) {
//							
//						}
//					}
//					PartyRoom.save();
//					Launcher.shutdown();
//				} catch (Throwable e) {
//					Logger.handle(this, e);
//				}
//			}
//		});
	}

	private static void addCleanMemoryTask() {
		LowPriorityTaskExecutor.schedule(() -> {
			try {
				cleanMemory(getMemUsedPerc() > Settings.HIGH_MEM_USE_THRESHOLD);
			} catch (Throwable e) {
				Logger.handle(Launcher.class, "addCleanMemoryTask", e);
			}
		}, 0, Ticks.fromMinutes(10));
		//}, 0, Ticks.fromSeconds(10));
	}

	public static void saveFilesSync() {
		for (Player player : World.getPlayers()) {
			if (player == null || !player.hasStarted() || player.hasFinished())
				continue;
			WorldDB.getPlayers().saveSync(player);
		}
		WorldPersistentData.save();
	}

	public static void saveFilesAsync() {
		for (Player player : World.getPlayers()) {
			if (player == null || !player.hasStarted() || player.hasFinished())
				continue;
			WorldDB.getPlayers().save(player);
		}
		WorldPersistentData.save();
	}

	public static void cleanMemory(boolean force) {
		if (force) {
			ItemDefinitions.clearItemsDefinitions();
			NPCDefinitions.clearNPCDefinitions();
			ObjectDefinitions.clearObjectDefinitions();
			ChunkManager.clearUnusedMemory();
			Logger.debug(Launcher.class, "cleanMemory", "Force cleaning cached data.");
		}
		for (Index index : Cache.STORE.getIndices())
			index.resetCachedFiles();
		System.gc();
	}

	public static void shutdown() {
		try {
			closeServices();
		} finally {
			System.exit(0);
		}
	}

	public static void closeServices() {
		ServerChannelHandler.shutdown();
		LowPriorityTaskExecutor.shutdown();
	}

	private Launcher() {

	}

	public static void executeCommand(String cmd) {
		executeCommand(null, cmd);
	}

	public static void executeCommand(Player player, String cmd) {
		LowPriorityTaskExecutor.execute(() -> {
			try {
				String line;
				ProcessBuilder builder = new ProcessBuilder(cmd.split(" "));
				Process proc = builder.start();
				BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
				while ((line = in.readLine()) != null) {
					if (player != null)
						player.getPackets().sendDevConsoleMessage(line);
					Logger.debug(Launcher.class, "executeCommand", line);
				}
				proc.waitFor();
				in.close();
			} catch (IOException | InterruptedException e) {
				if (player != null)
					player.getPackets().sendDevConsoleMessage("Error: " + e.getMessage());
				Logger.handle(Launcher.class, "executeCommand", e);
			}
		});
	}

	public static double getMemUsedPerc() {
		return 100.0 - (((double) Runtime.getRuntime().freeMemory() / Runtime.getRuntime().maxMemory()) * 100.0);
	}
}
