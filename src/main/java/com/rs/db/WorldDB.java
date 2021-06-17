package com.rs.db;

import com.rs.Settings;
import com.rs.db.collection.HighscoresManager;
import com.rs.db.collection.PlayerManager;
import com.rs.lib.db.DBConnection;

public class WorldDB extends DBConnection {
	
	private static PlayerManager PLAYERS = new PlayerManager();
	private static HighscoresManager HIGHSCORES = new HighscoresManager();
	//private static GEManager GE = new GEManager(); //TODO
	
	public WorldDB() {
		super(Settings.getConfig().getMongoDb());
		addItemManager(PLAYERS);
		addItemManager(HIGHSCORES);
		//addItemManager(GE);
	}

	public static PlayerManager getPlayers() {
		return PLAYERS;
	}

	public static HighscoresManager getHighscores() {
		return HIGHSCORES;
	}
}
