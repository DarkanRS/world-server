package com.rs.game.grandexchange;

import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.db.WorldDB;
import com.rs.db.collection.GEManager;
import com.rs.game.World;
import com.rs.game.grandexchange.GrandExchange.GrandExchangeType;
import com.rs.game.player.Player;
import com.rs.lib.file.FileManager;
import com.rs.lib.file.JsonFileManager;
import com.rs.lib.util.Logger;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.annotations.ServerStartupEvent;

public class GEHandler {

	public static void processOffers() {
		WorldDB.getGE().execute(() -> {
			ArrayList<Offer> buyOffers = new ArrayList<Offer>();

			for (Offer buyOffer : buyOffers) {
				Offer sellOffer = getBestSellOffer(buyOffer);
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

				updateOffer(buyOffer.getOwner(), buyOffer);
				updateOffer(sellOffer.getOwner(), sellOffer);
				FileManager.writeToFile("GELog.txt", "" + buyOffer.getOwner() + ": Bought " + numberToTransact + " " + ItemDefinitions.getDefs(buyOffer.getItemId()).getName() + " from " + sellOffer.getOwner());
			}
		});
	}

	public static void updateOffer(String username, Offer offer) {
		Player player = World.getPlayer(username);

		if (player != null && player.getUsername() != null) {
			player.setGrandExchangeOffer(offer, offer.getBox());
			player.sendMessage("One or more of your grand exchange offers has been updated.");
			GrandExchange.updateGrandExchangeBoxes(player);
		}
		WorldDB.getGE().save(offer);
	}

	public static void getBestOffer(int itemId, boolean sell, Consumer<String> func) {
		WorldDB.getGE().getBestOffer(itemId, sell, offer -> {
			if (offer == null)
				func.accept("None");
			else
				func.accept(NumberFormat.getNumberInstance(Locale.US).format(offer.getPricePerItem()));
		});
	}
}
