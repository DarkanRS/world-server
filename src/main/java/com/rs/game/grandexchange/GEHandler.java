package com.rs.game.grandexchange;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

import com.rs.db.WorldDB;
import com.rs.game.World;
import com.rs.game.player.Player;

public class GEHandler {
	
	public static void updateGE(String username) {
		WorldDB.getGE().execute(() -> {
			
		});
	}

	public static void processNewOffer(Offer offer) {
		WorldDB.getGE().execute(() -> {
			List<Offer> offers = WorldDB.getGE().getBestOffersSync(offer);
			if (offers == null || offers.size() <= 0)
				return;
			System.out.println(offers);
			System.out.println(offers.size());
			
//			if (sellOffer == null)
//				continue;
//			int numberToTransact;
//			if (buyOffer.getAmountLeft() >= sellOffer.getAmountLeft()) {
//				numberToTransact = sellOffer.getAmountLeft();
//			} else {
//				numberToTransact = buyOffer.getAmountLeft();
//			}
//
//			int finalPrice = sellOffer.getPricePerItem();
//			int change = Math.abs(buyOffer.getPricePerItem() - sellOffer.getPricePerItem());
//
//			buyOffer.transactBuy(numberToTransact, finalPrice, change);
//			sellOffer.transactSell(numberToTransact, finalPrice);
//
//			updateOffer(buyOffer.getOwner(), buyOffer);
//			updateOffer(sellOffer.getOwner(), sellOffer);
//			FileManager.writeToFile("GELog.txt", "" + buyOffer.getOwner() + ": Bought " + numberToTransact + " " + ItemDefinitions.getDefs(buyOffer.getItemId()).getName() + " from " + sellOffer.getOwner());
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
