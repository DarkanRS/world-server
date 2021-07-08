package com.rs.db.collection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.bson.Document;
import com.google.gson.JsonIOException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.FindOneAndReplaceOptions;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.Sorts;
import com.rs.game.grandexchange.Offer;
import com.rs.game.grandexchange.GrandExchange.GrandExchangeType;
import com.rs.lib.db.DBItemManager;
import com.rs.lib.file.JsonFileManager;
import com.rs.lib.util.Logger;

public class GEManager extends DBItemManager {
	
	public GEManager() {
		super("grandexchange");
	}
	
	@Override
	public void initCollection() {
		getDocs().createIndex(Indexes.text("owner"));
		getDocs().createIndex(Indexes.compoundIndex(Indexes.ascending("itemId"), Indexes.ascending("amountLeft")));
		getDocs().createIndex(Indexes.compoundIndex(Indexes.ascending("itemId"), Indexes.ascending("pricePerItem")));
		getDocs().createIndex(Indexes.compoundIndex(Indexes.ascending("itemId"), Indexes.ascending("currentType")));
	}

	public void get(String username, Consumer<Offer[]> func) {
		execute(() -> {
			func.accept(getSync(username));
		});
	}
	
	public void save(Offer offer) {
		save(offer, null);
	}
	
	public void save(Offer offer, Runnable done) {
		execute(() -> {
			saveSync(offer);
			if (done != null)
				done.run();
		});
	}
	
	public void saveSync(Offer offers) {
		getDocs().findOneAndReplace(Filters.and(Filters.eq("owner", offers.getOwner()), Filters.eq("box", offers.getBox())), Document.parse(JsonFileManager.toJson(offers)), new FindOneAndReplaceOptions().upsert(true));
	}

	public Offer[] getSync(String username) {
		FindIterable<Document> offerDocs = getDocs().find(Filters.eq("owner", username));
		if (offerDocs == null)
			return null;
		else
			try {
				Offer[] offers = new Offer[6];
				for (Document d : offerDocs) {
					Offer offer = JsonFileManager.fromJSONString(JsonFileManager.toJson(d), Offer.class);
					offers[offer.getBox()] = offer;
				}
				return offers;
			} catch (JsonIOException | IOException e) {
				Logger.handle(e);
				return null;
			}
	}
	
	public void remove(String username, int box, Runnable done) {
		execute(() -> {
			removeSync(username, box);
			if (done != null)
				done.run();
		});
	}

	public void removeSync(String username, int box) {
		getDocs().findOneAndDelete(Filters.and(Filters.eq("owner", username), Filters.eq("box", box)));
	}
	
	public List<Offer> getBestOffersSync(Offer other) {
		switch(other.getCurrentType()) {
		case ABORTED:
			return null;
		case BUYING:
			FindIterable<Document> docs = getDocs()
				.find(Filters.and(Filters.eq("itemId", other.getItemId()), Filters.eq("currentType", GrandExchangeType.SELLING.name()), Filters.lt("pricePerItem", other.getPricePerItem())))
				.sort(Sorts.ascending("pricePerItem"));
			try {
				return JsonFileManager.fromJSONString(JsonFileManager.toJson(docs.first()), Offer[].class);
			} catch (JsonIOException | IOException e) {
				return null;
			}
		case SELLING:
			docs = getDocs()
				.find(Filters.and(Filters.eq("itemId", other.getItemId()), Filters.eq("currentType", GrandExchangeType.BUYING.name()), Filters.gt("pricePerItem", other.getPricePerItem())))
				.sort(Sorts.descending("pricePerItem"));
			try {
				return JsonFileManager.fromJSONString(JsonFileManager.toJson(docs.first()), Offer[].class);
			} catch (JsonIOException | IOException e) {
				return null;
			}
		default:
			return null;
		}
	}

	public void getBestOffer(int itemId, boolean sell, Consumer<Offer> func) {
		execute(() -> {
			if (sell) {
				FindIterable<Document> docs = getDocs()
						.find(Filters.and(Filters.eq("itemId", itemId), Filters.eq("currentType", GrandExchangeType.BUYING.name())))
						.sort(Sorts.descending("pricePerItem"));
				try {
					func.accept(JsonFileManager.fromJSONString(JsonFileManager.toJson(docs.first()), Offer.class));
				} catch (JsonIOException | IOException e) {
					func.accept(null);
				}
			} else {
				FindIterable<Document> docs = getDocs()
						.find(Filters.and(Filters.eq("itemId", itemId), Filters.eq("currentType", GrandExchangeType.SELLING.name())))
						.sort(Sorts.ascending("pricePerItem"));
				try {
					func.accept(JsonFileManager.fromJSONString(JsonFileManager.toJson(docs.first()), Offer.class));
				} catch (JsonIOException | IOException e) {
					func.accept(null);
				}
			}
		});
	}
	
	public void getAllOffersOfType(GrandExchangeType type, Consumer<List<Offer>> func) {
		execute(() -> {
			List<Offer> result = new ArrayList<Offer>();
			FindIterable<Document> docs = getDocs().find(Filters.eq("currentType", type.name()));
			for (Document doc : docs) {
				try {
					Offer offer = JsonFileManager.fromJSONString(JsonFileManager.toJson(doc), Offer.class);
					result.add(offer);
				} catch (JsonIOException | IOException e) {
					System.out.println("Error converting document: " + result);
				}
			}
			func.accept(result);
		});
	}

}
