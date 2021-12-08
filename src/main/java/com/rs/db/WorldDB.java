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
package com.rs.db;

import com.rs.Settings;
import com.rs.db.collection.GEManager;
import com.rs.db.collection.HighscoresManager;
import com.rs.db.collection.PlayerManager;
import com.rs.lib.db.DBConnection;

public class WorldDB extends DBConnection {

	private static PlayerManager PLAYERS = new PlayerManager();
	private static HighscoresManager HIGHSCORES = new HighscoresManager();
	private static GEManager GE = new GEManager();

	public WorldDB() {
		super(Settings.getConfig().getMongoDb());
		addItemManager(PLAYERS);
		addItemManager(HIGHSCORES);
		addItemManager(GE);
	}

	public static PlayerManager getPlayers() {
		return PLAYERS;
	}

	public static HighscoresManager getHighscores() {
		return HIGHSCORES;
	}

	public static GEManager getGE() {
		return GE;
	}
}
