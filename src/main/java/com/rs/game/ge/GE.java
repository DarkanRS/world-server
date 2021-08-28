package com.rs.game.ge;

import java.util.Map;

import com.rs.cache.loaders.interfaces.IFTargetParams;
import com.rs.db.WorldDB;
import com.rs.game.World;
import com.rs.game.npc.NPC;
import com.rs.game.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.DialogueOptionEvent;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.plugin.handlers.NPCInteractionDistanceHandler;

@PluginEventHandler
public class GE {

	private static final int OFFER_SELECTION = 105;
	private static final int DEPOSIT_INV = 107;
	private static final int COLLECTION_BOX = 109;

	public static void open(Player player) {
		player.getPackets().closeGESearch();
		player.getVars().setVar(1112, -1);
		player.getVars().setVar(1113, -1);
		player.getVars().setVar(1109, -1);
		player.getVars().setVar(1110, 0);
		player.getInterfaceManager().removeInventoryInterface();
		player.getInterfaceManager().sendInterface(OFFER_SELECTION);
	}

	public static void openCollection(Player player) {
		player.getInterfaceManager().sendInterface(COLLECTION_BOX);
		player.getPackets().setIFTargetParams(new IFTargetParams(COLLECTION_BOX, 19, 0, 2).enableRightClickOptions(0, 1));
		player.getPackets().setIFTargetParams(new IFTargetParams(COLLECTION_BOX, 23, 0, 2).enableRightClickOptions(0, 1));
		player.getPackets().setIFTargetParams(new IFTargetParams(COLLECTION_BOX, 27, 0, 2).enableRightClickOptions(0, 1));
		player.getPackets().setIFTargetParams(new IFTargetParams(COLLECTION_BOX, 32, 0, 2).enableRightClickOptions(0, 1));
		player.getPackets().setIFTargetParams(new IFTargetParams(COLLECTION_BOX, 37, 0, 2).enableRightClickOptions(0, 1));
		player.getPackets().setIFTargetParams(new IFTargetParams(COLLECTION_BOX, 42, 0, 2).enableRightClickOptions(0, 1));
		for (Offer offer : player.getGEOffers().values())
			if (offer != null)
				offer.sendCollectionBox(player);
	}

	public static void openBuy(Player player) {
		Object[] o = new Object[] { "Grand Exchange Item Search" };
		player.getVars().setVar(1113, 0);
		player.getPackets().openGESearch(player, o);
	}

	public static void openSell(Player player) {
		player.getVars().setVar(1113, 1);
		player.getInterfaceManager().sendInventoryInterface(DEPOSIT_INV);
		player.getPackets().sendItems(93, player.getInventory().getItems());
		player.getPackets().setIFRightClickOps(DEPOSIT_INV, 18, 0, 27, 0);
		player.getPackets().sendInterSetItemsOptionsScript(DEPOSIT_INV, 18, 93, 4, 7, "Offer");
		player.getInterfaceManager().closeChatBoxInterface();
		player.getPackets().setIFHidden(OFFER_SELECTION, 196, true);
	}

	public static void selectItem(Player player, int itemId) {
//		player.getVars().setVar(1109, id);
//		player.getVars().setVar(1110, amount);
//		player.getVars().setVar(1111, price * amount);
	}

	public static void updatePrice(Player player) {
//		player.getVars().setVar(1110, amount);
//		player.getVars().setVar(1111, price);
	}

	public static void updatePrice(Player player, int itemId, boolean sell) {
//		player.getVars().setVar(1110, amount);
//		player.getVars().setVar(1111, price);
		// player.getPackets().setIFText(OFFER_SELECTION, 143, "Best offer: " +
		// GE.getBestOffer(itemId, sell));
	}

	public static void resetVars(Player player) {
		player.getVars().setVar(1109, -1);
		player.getVars().setVar(1110, 0);
		player.getVars().setVar(1111, 0);
		player.getVars().setVar(1112, -1);
		player.getVars().setVar(1113, 0);
	}

	public static void updateOffers(String username) {
		Player player = World.getPlayer(username);
		if (player != null) {
			WorldDB.getGE().get(username, offers -> {
				boolean diff = false;
				Map<Integer, Offer> prev = player.getGEOffers();
				for (Offer offer : offers) {
					if (offer == null)
						continue;
					if (prev.get(offer.getBox()) != null && offer.amountLeft() != prev.get(offer.getBox()).amountLeft()
							|| offer.getState() != prev.get(offer.getBox()).getState())
						diff = true;
				}
				player.setGEOffers(offers);
				updateGE(player);
				if (diff)
					player.sendMessage("One or more of your Grand Exchange offers has been updated.");
			});
		}
	}

	private static void updateGE(Player player) {
		for (int i = 0; i < 6; i++) {
			Offer offer = player.getGEOffers().get(i);
			if (offer == null)
				player.getPackets().updateGESlot(i, 0, -1, -1, -1, -1);
			else
				player.getPackets().updateGESlot(i, offer.getStateHash(), offer.getItemId(), offer.getPrice(),
						offer.getAmount(), offer.getCompletedAmount());
		}
	}
	
	public static NPCInteractionDistanceHandler clerkDistance = new NPCInteractionDistanceHandler("Grand Exchange clerk") {
		@Override
		public int getDistance(Player player, NPC npc) {
			return 1;
		}
	};

	public static NPCClickHandler handleClerks = new NPCClickHandler("Grand Exchange clerk") {
		@Override
		public void handle(NPCClickEvent e) {
			switch (e.getOption()) {
			case "Talk-to":
				e.getPlayer().sendOptionDialogue("What would you like to do?",
						new String[] { "Open Grand Exchange", "Nothing" }, new DialogueOptionEvent() {
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
