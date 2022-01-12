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
package com.rs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;

import com.google.gson.GsonBuilder;
import com.rs.cache.Cache;
import com.rs.cache.Index;
import com.rs.cache.loaders.ItemDefinitions;
import com.rs.cache.loaders.NPCDefinitions;
import com.rs.cache.loaders.ObjectDefinitions;
import com.rs.cores.CoresManager;
import com.rs.db.WorldDB;
import com.rs.game.World;
import com.rs.game.npc.familiar.Familiar;
import com.rs.game.player.Player;
import com.rs.game.player.content.minigames.partyroom.PartyRoom;
import com.rs.game.player.controllers.Controller;
import com.rs.lib.file.JsonFileManager;
import com.rs.lib.json.DateAdapter;
import com.rs.lib.net.ServerChannelHandler;
import com.rs.lib.net.decoders.GameDecoder;
import com.rs.lib.net.packets.Packet;
import com.rs.lib.net.packets.PacketEncoder;
import com.rs.lib.util.Logger;
import com.rs.lib.util.MapXTEAs;
import com.rs.lib.util.PacketAdapter;
import com.rs.lib.util.PacketEncoderAdapter;
import com.rs.lib.util.RecordTypeAdapterFactory;
import com.rs.net.LobbyCommunicator;
import com.rs.net.decoders.BaseWorldDecoder;
import com.rs.plugin.PluginManager;
import com.rs.utils.Ticks;
import com.rs.utils.json.ControllerAdapter;
import com.rs.utils.json.FamiliarAdapter;
import com.rs.web.WorldAPI;

public final class Launcher {

	private static WorldDB DB;

	public static void main(String[] args) throws Exception {
		System.setProperty("java.util.logging.SimpleFormatter.format", "%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS %4$s %2$s %5$s%6$s%n");
		Logger.log("Settings", "Loading config...");
		JsonFileManager.setGSON(new GsonBuilder()
				.registerTypeAdapter(Familiar.class, new FamiliarAdapter())
				.registerTypeAdapter(Controller.class, new ControllerAdapter())
				.registerTypeAdapter(Date.class, new DateAdapter())
				.registerTypeAdapter(PacketEncoder.class, new PacketEncoderAdapter())
				.registerTypeAdapter(Packet.class, new PacketAdapter())
				.registerTypeAdapterFactory(new RecordTypeAdapterFactory())
				.disableHtmlEscaping()
				.setPrettyPrinting()
				.create());

		Settings.loadConfig();

		long currentTime = System.currentTimeMillis();

		Logger.log("Cache", "Loading cache...");
		Cache.init(Settings.getConfig().getCachePath());

		Logger.log("XTEAs", "Loading map XTEAs...");
		MapXTEAs.loadKeys();

		Logger.log("CoresManager", "Initializing world threads...");
		CoresManager.startThreads();

		Logger.log("GameDecoder", "Initializing packet decoders...");
		GameDecoder.loadPacketDecoders();

		Logger.log("PluginManager", "Initializing plugins...");
		PluginManager.loadPlugins();
		PluginManager.executeStartupHooks();

		Logger.log("MongoDB", "Connecting to MongoDB and initializing databases...");
		DB = new WorldDB();
		DB.init();

		Logger.log("ServerChannelHandler", "Putting server online...");
		try {
			ServerChannelHandler.init(Settings.getConfig().getWorldInfo().getPort(), BaseWorldDecoder.class);
		} catch (Throwable e) {
			Logger.handle(e);
			Logger.log("Launcher", "Failed to initialize server channel handler. Shutting down...");
			System.exit(1);
			return;
		}
		Logger.log("Launcher", "Server launched in " + (System.currentTimeMillis() - currentTime) + " ms...");
		Logger.log("Launcher", "Registering world with lobby server...");
		Logger.log("Launcher", Settings.getConfig().getWorldInfo());
		new WorldAPI().start();
		LobbyCommunicator.post(Settings.getConfig().getWorldInfo(), "addworld");
		addAccountsSavingTask();
		addCleanMemoryTask();
	}

	private static void addCleanMemoryTask() {
		CoresManager.schedule(() -> {
			try {
				cleanMemory(Runtime.getRuntime().freeMemory() < Settings.MIN_FREE_MEM_ALLOWED);
			} catch (Throwable e) {
				Logger.handle(e);
			}
		}, 0, Ticks.fromMinutes(10));
	}

	private static void addAccountsSavingTask() {
		CoresManager.schedule(() -> {
			try {
				saveFiles();
			} catch (Throwable e) {
				Logger.handle(e);
			}

		}, Ticks.fromMinutes(15));
	}

	public static void saveFiles() {
		for (Player player : World.getPlayers()) {
			if (player == null || !player.hasStarted() || player.hasFinished())
				continue;
			WorldDB.getPlayers().saveSync(player);
		}
		PartyRoom.save();
	}

	public static void cleanMemory(boolean force) {
		if (force) {
			ItemDefinitions.clearItemsDefinitions();
			NPCDefinitions.clearNPCDefinitions();
			ObjectDefinitions.clearObjectDefinitions();
			World.cleanRegions();
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
		CoresManager.shutdown();
	}

	private Launcher() {

	}

	public static void executeCommand(String cmd) {
		executeCommand(null, cmd);
	}

	public static void executeCommand(Player player, String cmd) {
		CoresManager.execute(() -> {
			try {
				String line;
				Process proc = Runtime.getRuntime().exec(cmd);
				BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
				while ((line = in.readLine()) != null) {
					if (player != null)
						player.getPackets().sendDevConsoleMessage(line);
					System.out.println(line);
				}
				proc.waitFor();
				in.close();
			} catch (IOException | InterruptedException e) {
				if (player != null)
					player.getPackets().sendDevConsoleMessage("Error: " + e.getMessage());
				Logger.handle(e);
			}
		});
	}

	public static void pullAndCompile() {
		executeCommand("git pull origin master");
		executeCommand("mvn clean install");
	}

}
