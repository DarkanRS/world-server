package com.rs.game.ge;

import java.util.Map;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.cache.loaders.interfaces.IFTargetParams;
import com.rs.db.WorldDB;
import com.rs.game.World;
import com.rs.game.npc.NPC;
import com.rs.game.player.Player;
import com.rs.lib.game.Item;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ButtonClickEvent;
import com.rs.plugin.events.DialogueOptionEvent;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.handlers.ButtonClickHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.plugin.handlers.NPCInteractionDistanceHandler;
import com.rs.utils.ItemExamines;

@PluginEventHandler
public class GE {

	private static final int OFFER_SELECTION = 105;
	private static final int DEPOSIT_INV = 107;
	private static final int COLLECTION_BOX = 109;
	
	private static final int VAR_ITEM = 1109;
	private static final int VAR_ITEM_AMOUNT = 1110;
	private static final int VAR_PRICE = 1111;
	private static final int VAR_CURR_BOX = 1112;
	private static final int VAR_IS_SELLING = 1113;
	private static final int VAR_MEDIAN_PRICE = 1114;
	
	public static ButtonClickHandler mainInterface = new ButtonClickHandler(105) {
		@Override
		public void handle(ButtonClickEvent e) {
			switch(e.getComponentId()) {
				case 31 -> openBuy(e.getPlayer(), 0);
				case 47 -> openBuy(e.getPlayer(), 1);
				case 63 -> openBuy(e.getPlayer(), 2);
				case 82 -> openBuy(e.getPlayer(), 3);
				case 101 -> openBuy(e.getPlayer(), 4);
				case 120 -> openBuy(e.getPlayer(), 5);
				
				case 32 -> openSell(e.getPlayer(), 0);
				case 48 -> openSell(e.getPlayer(), 1);
				case 64 -> openSell(e.getPlayer(), 2);
				case 83 -> openSell(e.getPlayer(), 3);
				case 102 -> openSell(e.getPlayer(), 4);
				case 121 -> openSell(e.getPlayer(), 5);
				case 128 -> open(e.getPlayer());
				case 179 -> e.getPlayer().getVars().setVar(VAR_PRICE, (int) ((double) e.getPlayer().getVars().getVar(VAR_PRICE) * 1.05));
				case 190 -> e.getPlayer().getPackets().openGESearch(e.getPlayer(), "Grand Exchange Item Search");
				default -> System.out.println("Unhandled GE button: " + e.getComponentId());
			}
		}
	};

	public static void open(Player player) {
		player.getPackets().closeGESearch();
		resetVars(player);
		if (player.getInterfaceManager().containsInventoryInter())
			player.getInterfaceManager().removeInventoryInterface();
		if (!player.getInterfaceManager().containsInterface(OFFER_SELECTION))
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

	public static void openBuy(Player player, int box) {
		player.getVars().setVar(VAR_CURR_BOX, box);
		player.getVars().setVar(VAR_IS_SELLING, 0);
		player.getVars().setVar(VAR_PRICE, 1); //TODO check if even is needed
		player.getPackets().openGESearch(player, "Grand Exchange Item Search");
	}

	public static void openSell(Player player, int box) {
		player.getVars().setVar(VAR_CURR_BOX, box);
		player.getVars().setVar(VAR_IS_SELLING, 1);
		player.getInterfaceManager().sendInventoryInterface(DEPOSIT_INV);
		player.getPackets().sendItems(93, player.getInventory().getItems());
		player.getPackets().setIFRightClickOps(DEPOSIT_INV, 18, 0, 27, 0);
		player.getPackets().sendInterSetItemsOptionsScript(DEPOSIT_INV, 18, 93, 4, 7, "Offer");
		player.getInterfaceManager().closeChatBoxInterface();
		player.getPackets().setIFHidden(OFFER_SELECTION, 196, true);
	}

	public static void selectItem(Player player, int itemId) {
		player.getVars().setVar(VAR_ITEM, itemId);
		player.getVars().setVar(VAR_ITEM_AMOUNT, 1);
		player.getVars().setVar(VAR_PRICE, ItemDefinitions.getDefs(itemId).getHighAlchPrice());
		player.getVars().setVar(VAR_MEDIAN_PRICE, ItemDefinitions.getDefs(itemId).getHighAlchPrice());
		player.getPackets().setIFText(OFFER_SELECTION, 143, ItemExamines.getExamine(new Item(itemId)) + "<br>" + "Best offer: " + GE.getBestOffer(itemId, player.getVars().getVar(VAR_IS_SELLING) == 1));
		player.getPackets().sendRunScript(621);
	}

	private static int getBestOffer(int itemId, boolean selling) {
		return 1;
	}

	public static void updatePrice(Player player) {
//		player.getVars().setVar(VAR_ITEM_AMOUNT, amount);
//		player.getVars().setVar(VAR_TOTAL_PRICE, price);
	}

	public static void updatePrice(Player player, int itemId, boolean sell) {
//		player.getVars().setVar(VAR_ITEM_AMOUNT, amount);
//		player.getVars().setVar(VAR_TOTAL_PRICE, price);
	}

	public static void resetVars(Player player) {
		player.getVars().setVar(VAR_CURR_BOX, -1);
		player.getVars().setVar(VAR_IS_SELLING, -1);
		player.getVars().setVar(VAR_ITEM, -1);
		player.getVars().setVar(VAR_ITEM_AMOUNT, 0);
		player.getVars().setVar(VAR_PRICE, 0);
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
				if (diff) {
					//TODO sound effect
					player.sendMessage("One or more of your Grand Exchange offers has been updated.");
				}
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
