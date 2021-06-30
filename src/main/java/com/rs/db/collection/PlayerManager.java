package com.rs.db.collection;

import static com.mongodb.client.model.Filters.eq;

import java.io.IOException;
import java.util.function.Consumer;

import org.bson.Document;
import com.google.gson.JsonIOException;
import com.mongodb.client.model.FindOneAndReplaceOptions;
import com.mongodb.client.model.Indexes;
import com.rs.game.World;
import com.rs.game.player.Player;
import com.rs.lib.db.DBItemManager;
import com.rs.lib.file.JsonFileManager;
import com.rs.lib.util.Logger;

public class PlayerManager extends DBItemManager {
	
	public PlayerManager() {
		super("players");
	}
	
	@Override
	public void initCollection() {
		getDocs().createIndex(Indexes.text("username"));
	}

	public void get(String username, Consumer<Player> func) {
		execute(() -> {
			func.accept(getSync(username));
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
		getDocs().findOneAndReplace(eq("username", account.getUsername()), Document.parse(JsonFileManager.toJson(account)), new FindOneAndReplaceOptions().upsert(true));
	}

	public Player getSync(String username) {
		Player loggedIn = World.getPlayer(username);
		if (loggedIn != null)
			return loggedIn;
		Document accDoc = getDocs().find(eq("username", username)).first();
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

	public boolean exists(String username) {
		return getSync(username) == null;
	}

}
