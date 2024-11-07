package com.rs.db.collection;

import com.google.gson.JsonParseException;
import com.mongodb.MongoException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.model.FindOneAndReplaceOptions;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.rs.db.model.Penguin;
import com.rs.db.model.PolarBear;
import com.rs.game.content.dnds.penguins.PolarBearLocation;
import com.rs.game.content.dnds.penguins.PolarBearManager;
import com.rs.lib.db.DBItemManager;
import com.rs.lib.file.JsonFileManager;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Logger;
import com.rs.lib.util.Utils;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

public class PenguinHASManager extends DBItemManager {

	public PenguinHASManager() {
		super("penguinHAS");
	}

	@Override
	public void initCollection() {
		getDocs().createIndex(Indexes.text("name"));
		getDocs().createIndex(Indexes.ascending("date"), new IndexOptions().expireAfter(7L, TimeUnit.DAYS));
	}

	public void savePolarBearSync(PolarBear polarBear) {
		try {
			getDocs().findOneAndReplace(eq("name", polarBear.getName()), Document.parse(JsonFileManager.toJson(polarBear)), new FindOneAndReplaceOptions().upsert(true));
		} catch (Throwable e) {
			Logger.handle(PenguinHASManager.class, "savePolarBearSync", e);
		}
	}

	public void savePolarBearSpotters(PolarBear polarBear) {
		Document currentDoc = getDocs().find(eq("name", polarBear.getName())).first();
		if (currentDoc != null) {
			Document updateDoc = new Document();

			if (!polarBear.getSpotters().equals(currentDoc.get("spotters"))) {
				updateDoc.append("spotters", polarBear.getSpotters());
			}

			if (!updateDoc.isEmpty()) {
				getDocs().updateOne(eq("name", polarBear.getName()), new Document("$set", updateDoc));
			}
		} else {
			getDocs().insertOne(Document.parse(JsonFileManager.toJson(polarBear)));
		}
	}

	public PolarBear createPolarBear(PolarBearLocation location, List<String> previousLocations, List<String> spotters, int weekNumber, int points) {
		List<String> filteredSpotters = (spotters != null) ? new ArrayList<>(spotters) : new ArrayList<>();
		PolarBear newPolarBear = new PolarBear("PolarBear", location, previousLocations, filteredSpotters, weekNumber, points);
		savePolarBearSync(newPolarBear);
		return newPolarBear;
	}

	public PolarBear getPolarBear() {
		try {
			Bson filter = and(eq("name", "PolarBear"));
			Document doc = getDocs().find(filter).first();
			if (doc != null) {
				return JsonFileManager.fromJSONString(doc.toJson(), PolarBear.class);
			}
		} catch (MongoException | JsonParseException | IOException e) {
			Logger.handle(PolarBearManager.class, "getPolarBear", e);
		}
		return null;
	}

	public void savePenguinSpotters(Penguin entry) {
		Document currentDoc = getDocs().find(eq("name", entry.getName())).first();
		if (currentDoc != null) {
			Document updateDoc = new Document();

			if (!entry.getSpotters().equals(currentDoc.get("spotters"))) {
				updateDoc.append("spotters", entry.getSpotters());
			}

			if (!updateDoc.isEmpty()) {
				getDocs().updateOne(eq("name", entry.getName()), new Document("$set", updateDoc));
			}
		} else {
			getDocs().insertOne(Document.parse(JsonFileManager.toJson(entry)));
		}
	}

	public void save(Penguin entry, Runnable done) {
		execute(() -> {
			saveSync(entry);
			if (done != null)
				done.run();
		});
	}

	public void saveSync(Penguin entry) {
		try {
			getDocs().findOneAndReplace(eq("name", entry.getName()), Document.parse(JsonFileManager.toJson(entry)), new FindOneAndReplaceOptions().upsert(true));
		} catch(Throwable e) {
			Logger.handle(PenguinHASManager.class, "saveSync", e);
		}
	}

	public Penguin createPenguin(int npcId, String name, List<String> spotters, int weekNumber, int points, Tile location, String wikiLocation, String locationHint) {
		List<String> filteredSpotters = (spotters != null) ? new ArrayList<>(spotters) : new ArrayList<>();
		Penguin newPenguin = new Penguin(npcId, name, filteredSpotters, location, wikiLocation, weekNumber, points, locationHint);
		saveSync(newPenguin);
		return newPenguin;
	}

	public void clearAllPenguins() {
		try {
			Document query = new Document("name", new Document("$ne", "PolarBear"));
			getDocs().deleteMany(query);
		} catch (Throwable e) {
			Logger.handle(PenguinHASManager.class, "clearAll", e);
		}
	}

	public Penguin getPenguinByIdAndLocation(int npcId, Tile location) {
		try {
			Bson filter = eq("npcId", npcId);
			filter = and(filter, eq("location", location));
			Document doc = getDocs().find(filter).first();
			if (doc != null) {
				return JsonFileManager.fromJSONString(doc.toJson(), Penguin.class);
			}
		} catch (Throwable e) {
			Logger.handle(PenguinHASManager.class, "getPenguinByNpcIdAndLocation", e);
		}
		return null;
	}

	public List<Penguin> getAllPenguins() throws IOException {
		List<Penguin> penguins = new ArrayList<>();
		FindIterable<Document> docs = getDocs().find();
		for (Document doc : docs) {
			String name = doc.getString("name");
			if (!"PolarBear".equals(name)) {
				Penguin penguin = JsonFileManager.fromJSONString(doc.toJson(), Penguin.class);
				penguins.add(penguin);
			}
		}
		return penguins;
	}

	public int getPenguinsSpottedByPlayer(String playerUsername) throws IOException {
		List<Penguin> allPenguins = getAllPenguins();
		int count = 0;

		for (Penguin penguin : allPenguins) {
			if (penguin.getSpotters().contains(playerUsername)) {
				count++;
			}
		}

		return count;
	}

	public String getHintForPenguin(String playerUsername) throws IOException {
		List<Penguin> allPenguins = getAllPenguins();
		List<String> unspottedHints = new ArrayList<>();
		for (Penguin penguin : allPenguins) {
			if (!penguin.getSpotters().contains(playerUsername)) {
				unspottedHints.add(penguin.getLocationHint());
			}
		}
		if (unspottedHints.isEmpty()) {
			return null;
		}
		return unspottedHints.get(Utils.random((unspottedHints.size())));
	}

	public List<String> getAllParticipants() throws IOException {
		List<String> spotters = new ArrayList<>();

		for (Penguin penguin : getAllPenguins()) {
			spotters.addAll(penguin.getSpotters());
		}

		PolarBear polarBear = getPolarBear();
		if (polarBear != null) {
			spotters.addAll(polarBear.getSpotters());
		}

		return spotters.stream().distinct().toList();
	}

	public List<String> getFoundEntitiesByUsername(String username) {
		List<String> foundEntities = new ArrayList<>();

		try {
			for (Penguin penguin : getAllPenguins()) {
				if (penguin.getSpotters().contains(username)) {
					foundEntities.add(penguin.getName());
				}
			}

			PolarBear polarBear = getPolarBear();
			if (polarBear != null && polarBear.getSpotters().contains(username)) {
				foundEntities.add(polarBear.getName());
			}
		} catch (IOException e) {
			Logger.handle(PenguinHASManager.class, "getFoundEntitiesByUsername", e);
		}

		return foundEntities;
	}

}
