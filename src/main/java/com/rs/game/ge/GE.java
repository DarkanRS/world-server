package com.rs.game.ge;

import com.rs.db.WorldDB;
import com.rs.game.World;
import com.rs.game.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.DialogueOptionEvent;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
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
	
	public static NPCClickHandler handleClerks = new NPCClickHandler("Grand Exchange clerk") {
		@Override
		public void handle(NPCClickEvent e) {
			switch(e.getOption()) {
			case "Talk-to":
				e.getPlayer().sendOptionDialogue("What would you like to do?", new String[] { "Open Grand Exchange", "Nothing" }, new DialogueOptionEvent() {
					@Override
					public void run(Player player) {
						if (getOption() == 1)
							GE.open(player);
					}
				});
				break;
			case "Exchange":
				GE.open(e.getPlayer());
				break;
			case "History":
				break;
			case "Sets":
				Sets.open(e.getPlayer());
				break;
			}
		}
	};
}
