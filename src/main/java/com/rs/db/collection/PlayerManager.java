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
package com.rs.db.collection;

import com.google.gson.JsonIOException;
import com.mongodb.client.model.FindOneAndReplaceOptions;
import com.mongodb.client.model.Indexes;
import com.rs.game.World;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.db.DBItemManager;
import com.rs.lib.file.JsonFileManager;
import com.rs.lib.util.Logger;
import org.bson.Document;

import java.io.IOException;
import java.util.function.Consumer;

import static com.mongodb.client.model.Filters.eq;

public class PlayerManager extends DBItemManager {

	public PlayerManager() {
		super("players");
	}

	@Override
	public void initCollection() {
		getDocs().createIndex(Indexes.text("username"));
	}

	public void getByUsername(String username, Consumer<Player> func) {
		execute(() -> {
			func.accept(getSyncUsername(username));
		});
	}

	public void save(Player player) {
		save(player, null);
	}

	public void save(Player account, Runnable done) {
		execute(() -> {
			saveSync(account);
			if (done != null)
				done.run();
		});
	}

	public void saveSync(Player account) {
		try {
			getDocs().findOneAndReplace(eq("username", account.getUsername()), Document.parse(JsonFileManager.toJson(account)), new FindOneAndReplaceOptions().upsert(true));
		} catch(Throwable e) {
			Logger.handle(PlayerManager.class, "saveSync", "Error saving player: " + account.getUsername(), e);
		}
	}

	public Player getSyncUsername(String username) {
		Player loggedIn = World.getPlayerByUsername(username);
		if (loggedIn != null)
			return loggedIn;
		Document accDoc = getDocs().find(eq("username", username)).first();
		if (accDoc == null)
			return null;
		try {
			return JsonFileManager.fromJSONString(JsonFileManager.toJson(accDoc), Player.class);
		} catch (JsonIOException | IOException e) {
			Logger.handle(PlayerManager.class, "getSyncUsername", e);
			return null;
		}
	}

	public boolean usernameExists(String username) {
		return getSyncUsername(username) == null;
	}

}
