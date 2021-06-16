package com.rs.game.grandexchange;

import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.World;
import com.rs.game.grandexchange.GrandExchange.GrandExchangeType;
import com.rs.game.player.Player;
import com.rs.lib.file.FileManager;
import com.rs.lib.file.JsonFileManager;
import com.rs.lib.util.Logger;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.annotations.ServerStartupEvent;

@PluginEventHandler
public class GrandExchangeDatabase {
	public static final String PATH = "data/grandexchange/";

	public static HashMap<String, OfferSet> offers; // make a sign that displays
													// these
	@ServerStartupEvent
	public static void init() {
		offers = new HashMap<String, OfferSet>();

		File file = new File(PATH);
		if (!file.exists()) {
			file.mkdirs();
		}
		int amount = 0;
		for (File offer : file.listFiles()) {
			try {
				OfferSet offerSet = (OfferSet) JsonFileManager.loadJsonFile(offer, OfferSet.class);
				if (offerSet != null) {
					offers.put(offerSet.owner, offerSet);
					amount++;
				} else {
					FileManager.logError("Error loading GE offer: " + offer.getName());
				}
			} catch (Throwable e) {
				Logger.handle(e);
			}
		}
		Logger.log("GrandExchangeDatabase", "Loaded " + amount + " grand exchange offer sets..");
	}

	public static final void save(Player player) {
		if (player == null)
			return;
		try {
			JsonFileManager.saveJsonFile(player.getOfferSet(), new File(PATH + player.getUsername() + ".json"));
		} catch (Throwable e) {
			Logger.handle(e);
		}
	}

	public static final void save(String username, OfferSet offerSet) {
		try {
			JsonFileManager.saveJsonFile(offerSet, new File(PATH + username + ".json"));
		} catch (Throwable e) {
			Logger.handle(e);
		}
	}

	public static OfferSet getOfferSet(Player player) {
		File file = new File(PATH + player.getUsername() + ".json");
		if (!file.exists()) {
			return new OfferSet(player.getUsername());
		} else {
			try {
				return (OfferSet) JsonFileManager.loadJsonFile(file, OfferSet.class);
			} catch (IOException e) {
				FileManager.logError("Error loading GE file for " + player.getUsername());
			}
		}
		return null;
	}

	public static void processOffers() {
		ArrayList<Offer> buyOffers = new ArrayList<Offer>();
		ArrayList<Offer> sellOffers = new ArrayList<Offer>();

		for (OfferSet offerSet : offers.values()) {
			if (offerSet == null)
				continue;
			for (Offer offer : offerSet.offers) {
				if (offer == null)
					continue;
				if (offer.getAmountLeft() > 0) {
					if (offer.getCurrentType() == GrandExchangeType.BUYING) {
						buyOffers.add(offer);
					} else if (offer.getCurrentType() == GrandExchangeType.SELLING) {
						sellOffers.add(offer);
					}
				}
			}
		}

		for (Offer buyOffer : buyOffers) {
			Offer sellOffer = getBestSellOffer(sellOffers, buyOffer);
			if (sellOffer == null)
				continue;
			int numberToTransact;
			if (buyOffer.getAmountLeft() >= sellOffer.getAmountLeft()) {
				numberToTransact = sellOffer.getAmountLeft();
			} else {
				numberToTransact = buyOffer.getAmountLeft();
			}

			int finalPrice = sellOffer.getPricePerItem();
			int change = Math.abs(buyOffer.getPricePerItem() - sellOffer.getPricePerItem());

			buyOffer.transactBuy(numberToTransact, finalPrice, change);
			sellOffer.transactSell(numberToTransact, finalPrice);

			updateOffer(buyOffer);
			updateOffer(sellOffer);
			FileManager.writeToFile("GELog.txt", "" + buyOffer.getOwner() + ": Bought " + numberToTransact + " " + ItemDefinitions.getDefs(buyOffer.getItemId()).getName() + " from " + sellOffer.getOwner());
		}
	}
	
	public static Offer getBestSellOffer(ArrayList<Offer> sellOffers, Offer buyOffer) {
		for (Offer sellOffer : sellOffers) {
			if (buyOffer.getOwner().equals(sellOffer.getOwner()))
				continue;
			if (buyOffer.getItemId() != sellOffer.getItemId())
				continue;
			if (buyOffer.getPricePerItem() < sellOffer.getPricePerItem())
				continue;
			return sellOffer;
		}
		return null;
	}

	public static void updateOffer(Offer offer) {
		Player player = World.getPlayer(offer.getOwner());
		OfferSet offerSet = offers.get(offer.getOwner());
		offerSet.offers[offer.getBox()] = offer;
		updateOfferSet(offer.getOwner(), offerSet, player != null);
	}

	public static void updateOfferSet(String username, OfferSet offerSet, boolean playerOn) {
		Player player = World.getPlayer(username);
		offers.remove(username);
		offers.put(username, offerSet);

		if (player != null && player.getUsername() != null) {
			for (int i = 0; i < offerSet.offers.length; i++) {
				Offer offer = offerSet.offers[i];
				player.setGrandExchangeOffer(offer, i);
			}
			player.sendMessage("One or more of your grand exchange offers has been updated.");
			GrandExchange.updateGrandExchangeBoxes(player);
			save(player);
		} else {
			save(username, offerSet);
		}

	}

	public static String getBestOffer(int itemId, boolean sell) {
		ArrayList<Offer> offersToProcess = new ArrayList<Offer>();

		for (OfferSet offerSet : offers.values()) {
			if (offerSet == null)
				continue;
			for (Offer offer : offerSet.offers) {
				if (offer == null || offer.getItemId() != itemId)
					continue;
				if (sell && offer.getCurrentType() == GrandExchangeType.BUYING) {
					if (offer.getAmountLeft() > 0) {
						offersToProcess.add(offer);
					}
				} else if (!sell && offer.getCurrentType() == GrandExchangeType.SELLING) {
					if (offer.getAmountLeft() > 0) {
						offersToProcess.add(offer);
					}
				}
			}
		}
		if (offersToProcess.isEmpty())
			return "None";
		if (sell) {
			int highest = 0;
			for (Offer offer : offersToProcess) {
				if (offer == null)
					continue;
				if (offer.getPricePerItem() > highest)
					highest = offer.getPricePerItem();
			}
			return NumberFormat.getNumberInstance(Locale.US).format(highest);
		} else {
			int lowest = Integer.MAX_VALUE;
			for (Offer offer : offersToProcess) {
				if (offer == null)
					continue;
				if (offer.getPricePerItem() < lowest)
					lowest = offer.getPricePerItem();
			}
			return NumberFormat.getNumberInstance(Locale.US).format(lowest);
		}
	}
	
	public static List<Offer> getAllOffersOfType(GrandExchangeType type) {
		List<Offer> buyOffers = new ArrayList<Offer>();

		for (OfferSet offerSet : offers.values()) {
			if (offerSet == null)
				continue;
			for (Offer offer : offerSet.offers) {
				if (offer == null)
					continue;
				if (offer.getAmountLeft() > 0) {
					if (offer.getCurrentType() == type) {
						buyOffers.add(offer);
					}
				}
			}
		}
		
		return buyOffers;
	}
}
