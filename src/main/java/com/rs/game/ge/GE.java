package com.rs.game.ge;

import com.rs.db.WorldDB;
import com.rs.game.World;
import com.rs.game.player.Player;

public class GE {
	
	public static void open(Player player) {
		
	}
	
	public static void selectItem(Player player, int itemId) {
		
	}
	
	public static void updateOffers(String username) {
		Player player = World.getPlayer(username);
		if (player != null) {
			WorldDB.getGE().get(username, o -> {
				boolean diff = false;
				Offer[] offers = o;
				if (offers == null)
					offers = new Offer[6];
				Offer[] prev = player.getOffers();
				for (int i = 0;i < offers.length;i++) {
					if (offers[i] == null || prev[i] == null)
						continue;
					if (offers[i].amountLeft() != prev[i].amountLeft() || offers[i].getState() != prev[i].getState())
						diff = true;
				}
				player.setOffers(offers);
				if (diff)
					player.sendMessage("One or more of your Grand Exchange offers has been updated.");
			});
		}
	}
}
