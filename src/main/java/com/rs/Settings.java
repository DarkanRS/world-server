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
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;

import com.google.common.net.PercentEscaper;
import com.google.gson.JsonIOException;
import com.rs.lib.Globals;
import com.rs.lib.file.JsonFileManager;
import com.rs.lib.game.Item;
import com.rs.lib.game.WorldInfo;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Logger;

public final class Settings {

	private static Settings SETTINGS;
	private static Settings DEFAULTS = new Settings();

	public static Settings getConfig() {
		if (SETTINGS == null)
			loadConfig();
		return SETTINGS;
	}

	private String serverName;
	private String ownerName;
	private String cachePath;
	private boolean debug = false;
	private String mongoUrl;
	private int mongoPort;
	private String mongoUser;
	private String mongoPass;
	private String lobbyIp;
	private String lobbyApiKey;
	private WorldInfo worldInfo;
	private String loginMessage;
	private WorldTile playerStartTile;
	private WorldTile playerRespawnTile;
	private int xpRate;
	private double dropModifier;
	private Item[] startItems;

	public Settings() {
		serverName = "Darkan";
		ownerName = "trent";
		cachePath = "../darkan-cache/";
		debug = false;
		lobbyIp = "testlobby.darkan.org";
		mongoUrl = "testlobby.darkan.org";
		mongoPort = 27017;
		mongoUser = "darkan";
		mongoPass = "test";
		lobbyApiKey = "TEST_API_KEY";
		worldInfo = new WorldInfo(3, "127.0.0.1", 43595, "Admin Server", 1, true, true);
		loginMessage = "";
		playerStartTile = new WorldTile(3226, 3219, 0);
		playerRespawnTile = new WorldTile(3221, 3218, 0);
		xpRate = 1;
		dropModifier = 1.0;
		startItems = new Item[] {
				new Item(1351, 1),
				new Item(590, 1),
				new Item(303, 1),
				new Item(315, 1),
				new Item(1925, 1),
				new Item(1931, 1),
				new Item(2309, 1),
				new Item(1265, 1),
				new Item(1205, 1),
				new Item(1277, 1),
				new Item(1171, 1),
				new Item(841, 1),
				new Item(882, 25),
				new Item(556, 25),
				new Item(558, 15),
				new Item(555, 6),
				new Item(557, 4),
				new Item(559, 2),
		};
	}

	public static final long WORLD_CYCLE_NS = 600000000L;
	public static final long WORLD_CYCLE_MS = WORLD_CYCLE_NS / 1000000L;

	public static final int AIR_GUITAR_MUSICS_COUNT = 150;

	public static final int PLAYERS_LIMIT = 2000;
	public static final int LOCAL_PLAYERS_LIMIT = 250;
	public static final int NPCS_LIMIT = 64000;
	public static final int LOCAL_NPCS_LIMIT = 250;
	public static final int MIN_FREE_MEM_ALLOWED = 300000000; // 30mb

	public static ArrayList<String> COMMIT_HISTORY = new ArrayList<>();

	public static void loadConfig() {
		try {
			File configFile = new File("./worldConfig.json");
			if (configFile.exists())
				SETTINGS = JsonFileManager.loadJsonFile(new File("./worldConfig.json"), Settings.class);
			else
				SETTINGS = new Settings();
			for (Field f : SETTINGS.getClass().getDeclaredFields())
				if (f.get(SETTINGS) == null)
					f.set(SETTINGS, f.get(DEFAULTS));
			JsonFileManager.saveJsonFile(SETTINGS, configFile);
		} catch (JsonIOException | IOException | IllegalArgumentException | IllegalAccessException e1) {
			e1.printStackTrace();
			System.exit(5);
		}
		try {
			String line;
			Process proc = Runtime.getRuntime().exec("git log -n 1");
			BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
			boolean markMsg = false;
			while ((line = in.readLine()) != null) {
				if (line.isEmpty())
					continue;
				line = line.replace("commit ", "Hash: ");
				if (markMsg) {
					line = "Message: " + line.trim();
					markMsg = false;
				}
				if (line.contains("Date: "))
					markMsg = true;
				COMMIT_HISTORY.add(line);
			}
			proc.waitFor();
			in.close();
		} catch (JsonIOException | IOException | InterruptedException e) {
			Logger.handle(e);
		}
		Globals.DEBUG = getConfig().debug;
	}

	public String getServerName() {
		return serverName;
	}

	public String getOwnerName() {
		return ownerName;
	}

	public boolean isDebug() {
		return debug;
	}

	public String getLoginMessage() {
		return loginMessage;
	}

	public void setLoginMessage(String loginMessage) {
		this.loginMessage = loginMessage;
	}

	public WorldTile getPlayerStartTile() {
		return playerStartTile != null ? playerStartTile : DEFAULTS.playerStartTile;
	}

	public WorldTile getPlayerRespawnTile() {
		return playerRespawnTile != null ? playerRespawnTile : DEFAULTS.playerRespawnTile;
	}

	public int getXpRate() {
		return xpRate;
	}

	public double getDropModifier() {
		return dropModifier;
	}

	public String getCachePath() {
		return cachePath != null ? cachePath : DEFAULTS.cachePath;
	}

	public static boolean isOwner(String string) {
		return string.equalsIgnoreCase(SETTINGS.getOwnerName());
	}

	public Item[] getStartItems() {
		return startItems != null ? startItems : DEFAULTS.startItems;
	}

	public WorldInfo getWorldInfo() {
		return worldInfo != null ? worldInfo : DEFAULTS.worldInfo;
	}

	public String getLobbyIp() {
		return lobbyIp;
	}

	public String getLobbyApiKey() {
		return lobbyApiKey;
	}

	public String getMongoDb() {
		String db = "mongodb://";
		if (mongoUser != null && !mongoUser.isEmpty())
			db += mongoUser + ":" + new PercentEscaper("", false).escape(mongoPass) + "@";
		db += mongoUrl;
		if (mongoPort > 0)
			db += ":" + mongoPort;
		db += "/darkan-server?retryWrites=true&w=majority";
		return db;
	}
}
