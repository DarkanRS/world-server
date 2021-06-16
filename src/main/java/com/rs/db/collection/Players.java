package com.rs.db.collection;

import static com.mongodb.client.model.Filters.eq;

import java.io.IOException;
import java.util.function.Consumer;

import org.bson.Document;
import com.google.gson.JsonIOException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.FindOneAndReplaceOptions;
import com.mongodb.client.model.Indexes;
import com.rs.db.WorldDB;
import com.rs.game.World;
import com.rs.game.player.Player;
import com.rs.lib.file.JsonFileManager;
import com.rs.lib.util.Logger;
import com.rs.lib.util.MongoUtil;

public class Players {
	
	private static MongoCollection<Document> PLAYERS;
	
	public static void init() {
		if (!MongoUtil.collectionExists(WorldDB.getDatabase(), "players")) {
			WorldDB.getDatabase().createCollection("players");
			PLAYERS = WorldDB.getDatabase().getCollection("players");
			PLAYERS.createIndex(Indexes.text("username"));
		} else
			PLAYERS = WorldDB.getDatabase().getCollection("players");
	}

	public static void get(String username, Consumer<Player> func) {
		WorldDB.execute(() -> {
			func.accept(getSync(username));
		});
	}
	
	public static void save(Player account, Runnable done) {
		WorldDB.execute(() -> {
			saveSync(account);
			done.run();
		});
	}
	
	public static void saveSync(Player account) {
		PLAYERS.findOneAndReplace(eq("username", account.getUsername()), Document.parse(JsonFileManager.toJson(account)), new FindOneAndReplaceOptions().upsert(true));
	}

	public static Player getSync(String username) {
		Player loggedIn = World.getPlayer(username);
		if (loggedIn != null)
			return loggedIn;
		Document accDoc = PLAYERS.find(eq("username", username)).first();
		if (accDoc == null)
			return null;
		else
			try {
				return JsonFileManager.fromJSONString(JsonFileManager.toJson(accDoc), Player.class);
			} catch (JsonIOException | IOException e) {
				Logger.handle(e);
				return null;
			}
	}

	public static boolean exists(String username) {
		return getSync(username) == null;
	}

}
