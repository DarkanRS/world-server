package com.rs.db.collection;

import static com.mongodb.client.model.Filters.eq;

import java.util.ArrayList;

import org.bson.Document;
import org.bson.conversions.Bson;
import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.FindOneAndReplaceOptions;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.Sorts;
import com.rs.db.model.Highscore;
import com.rs.game.player.Player;
import com.rs.lib.db.DBItemManager;
import com.rs.lib.file.JsonFileManager;
import com.rs.lib.game.Rights;

public class HighscoresManager extends DBItemManager {
	
	public HighscoresManager() {
		super("highscores");
	}
	
	@Override
	public void initCollection() {
		getDocs().createIndex(Indexes.text("username"));
		getDocs().createIndex(Indexes.descending("totalLevel", "totalXp"));
	}
	
	public void save(Player player) {
		save(player, null);
	}
	
	public void save(Player player, Runnable done) {
		getConn().execute(() -> {
			saveSync(player);
			if (done != null)
				done.run();
		});
	}

	public void saveSync(Player player) {
		if (player.hasRights(Rights.OWNER)|| player.hasRights(Rights.DEVELOPER) || player.hasRights(Rights.ADMIN) || player.getSkills().getTotalXp() < 50000)
			return;
		try {
			getDocs().findOneAndReplace(eq("username", player.getUsername()), Document.parse(JsonFileManager.toJson(new Highscore(player))), new FindOneAndReplaceOptions().upsert(true));
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public ArrayList<Document> getTotalSync(int page, int ironman) {
		ArrayList<Document> docs = new ArrayList<Document>();
		
		Bson filters = null, iron = null;
		
		if (ironman != -1) {
			iron = eq("ironman", ironman == 1);
			if (filters == null)
				filters = iron;
		}
						
		FindIterable<Document> res = filters == null ? getDocs().find() : getDocs().find(filters);
		MongoCursor<Document> cursor = res.sort(Sorts.descending("totalLevel", "totalXp")).skip(20 * page).limit(20).iterator();
		
        while (cursor.hasNext()) {
            docs.add(cursor.next());
        }
        cursor.close();
		
		return docs;
	}
	
	public ArrayList<Document> getLevelSync(int skill, int page, int ironman) {
		ArrayList<Document> docs = new ArrayList<Document>();
		
		try {
		Bson filters = null, iron = null;
		
		if (ironman != -1) {
			iron = eq("ironman", ironman == 1);
			if (filters == null)
				filters = iron;
		}
		
		FindIterable<Document> res = filters == null ? getDocs().find() : getDocs().find(filters);
		
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

	public void clearAllHighscores() {
		getConn().execute(() -> {
			getDocs().drop();
		});
	}
}
