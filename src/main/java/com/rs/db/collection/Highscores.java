package com.rs.db.collection;

import static com.mongodb.client.model.Filters.eq;

import java.util.ArrayList;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.simple.JSONObject;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.FindOneAndReplaceOptions;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.Sorts;
import com.rs.db.WorldDB;
import com.rs.db.model.Highscore;
import com.rs.game.player.Player;
import com.rs.lib.file.JsonFileManager;
import com.rs.lib.game.Rights;
import com.rs.lib.util.MongoUtil;

public class Highscores {
	
	private static MongoCollection<Document> HIGHSCORES;
	
	public static void init() {
		if (!MongoUtil.collectionExists(WorldDB.getDatabase(), "highscores")) {
			WorldDB.getDatabase().createCollection("highscores");
			HIGHSCORES = WorldDB.getDatabase().getCollection("highscores");
			HIGHSCORES.createIndex(Indexes.text("username"));
			HIGHSCORES.createIndex(Indexes.descending("totalLevel", "totalXp"));
		} else
			HIGHSCORES = WorldDB.getDatabase().getCollection("highscores");
	}

	public static void savePlayer(Player player) {
		if (!WorldDB.ACTIVE)
			return;
		if (player.hasRights(Rights.OWNER)|| player.hasRights(Rights.DEVELOPER) || player.hasRights(Rights.ADMIN) || player.getSkills().getTotalXp() < 50000)
			return;
		try {
			HIGHSCORES.findOneAndReplace(eq("username", player.getUsername()), Document.parse(JsonFileManager.toJson(new Highscore(player))), new FindOneAndReplaceOptions().upsert(true));
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static ArrayList<Document> getTotal(int page, int ironman) {
		ArrayList<Document> docs = new ArrayList<Document>();
		
		Bson filters = null, iron = null;
		
		if (ironman != -1) {
			iron = eq("ironman", ironman == 1);
			if (filters == null)
				filters = iron;
		}
						
		FindIterable<Document> res = filters == null ? HIGHSCORES.find() : HIGHSCORES.find(filters);
		MongoCursor<Document> cursor = res.sort(Sorts.descending("totalLevel", "totalXp")).skip(20 * page).limit(20).iterator();
		
        while (cursor.hasNext()) {
            docs.add(cursor.next());
        }
        cursor.close();
		
		return docs;
	}
	
	public static ArrayList<Document> getLevel(int skill, int page, int ironman) {
		ArrayList<Document> docs = new ArrayList<Document>();
		
		try {
		Bson filters = null, iron = null;
		
		if (ironman != -1) {
			iron = eq("ironman", ironman == 1);
			if (filters == null)
				filters = iron;
		}
		
		FindIterable<Document> res = filters == null ? HIGHSCORES.find() : HIGHSCORES.find(filters);
		
		MongoCursor<Document> cursor = res.sort(new BasicDBObject("xp."+skill+"", -1)).skip(20 * page).limit(20).iterator();
        while (cursor.hasNext()) {
            docs.add(cursor.next());
        }
        cursor.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
        
		return docs;
	}
	
	public JSONObject toJSONObject() {
		return new JSONObject();
	}

	public static void clearAllHighscores() {
		HIGHSCORES.drop();
	}
}
