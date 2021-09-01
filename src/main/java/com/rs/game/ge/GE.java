package com.rs.game.ge;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.cache.loaders.interfaces.IFTargetParams;
import com.rs.db.WorldDB;
import com.rs.game.World;
import com.rs.game.ge.Offer.State;
import com.rs.game.npc.NPC;
import com.rs.game.player.Player;
import com.rs.lib.game.Item;
import com.rs.lib.net.ClientPacket;
import com.rs.lib.util.Utils;
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
			if (e.getPlayer().getTempB("geLocked"))
				return;
			switch(e.getComponentId()) {
				//Buy box selection
				case 31 -> openBuy(e.getPlayer(), 0);
				case 47 -> openBuy(e.getPlayer(), 1);
				case 63 -> openBuy(e.getPlayer(), 2);
				case 82 -> openBuy(e.getPlayer(), 3);
				case 101 -> openBuy(e.getPlayer(), 4);
				case 120 -> openBuy(e.getPlayer(), 5);
				
				//Sell box selection
				case 32 -> openSell(e.getPlayer(), 0);
				case 48 -> openSell(e.getPlayer(), 1);
				case 64 -> openSell(e.getPlayer(), 2);
				case 83 -> openSell(e.getPlayer(), 3);
				case 102 -> openSell(e.getPlayer(), 4);
				case 121 -> openSell(e.getPlayer(), 5);
				
				case 19 -> clickBox(e.getPlayer(), 0, e.getPacket() != ClientPacket.IF_OP1);
				case 35 -> clickBox(e.getPlayer(), 1, e.getPacket() != ClientPacket.IF_OP1);
				case 51 -> clickBox(e.getPlayer(), 2, e.getPacket() != ClientPacket.IF_OP1);
				case 70 -> clickBox(e.getPlayer(), 3, e.getPacket() != ClientPacket.IF_OP1);
				case 89 -> clickBox(e.getPlayer(), 4, e.getPacket() != ClientPacket.IF_OP1);
				case 108 -> clickBox(e.getPlayer(), 5, e.getPacket() != ClientPacket.IF_OP1);
				
				//Back button
				case 128 -> open(e.getPlayer());
			
				//Amount adjustments
				case 155 -> e.getPlayer().getVars().setVar(VAR_ITEM_AMOUNT, Utils.clampI(e.getPlayer().getVars().getVar(VAR_ITEM_AMOUNT) - 1, 0, Integer.MAX_VALUE));
				case 160, 157 -> e.getPlayer().getVars().setVar(VAR_ITEM_AMOUNT, Utils.clampI(e.getPlayer().getVars().getVar(VAR_ITEM_AMOUNT) + 1, 0, Integer.MAX_VALUE));
				case 162 -> e.getPlayer().getVars().setVar(VAR_ITEM_AMOUNT, Utils.clampI(e.getPlayer().getVars().getVar(VAR_ITEM_AMOUNT) + 10, 0, Integer.MAX_VALUE));
				case 164 -> e.getPlayer().getVars().setVar(VAR_ITEM_AMOUNT, Utils.clampI(e.getPlayer().getVars().getVar(VAR_ITEM_AMOUNT) + 100, 0, Integer.MAX_VALUE));
				case 166 -> e.getPlayer().getVars().setVar(VAR_ITEM_AMOUNT, Utils.clampI(e.getPlayer().getVars().getVar(VAR_ITEM_AMOUNT) + 1000, 0, Integer.MAX_VALUE));
				case 168 -> e.getPlayer().sendInputInteger("Enter amount", amount -> e.getPlayer().getVars().setVar(VAR_ITEM_AMOUNT, Utils.clampI(amount, 0, Integer.MAX_VALUE)));
				
				//Price adjustments
				case 169 -> e.getPlayer().getVars().setVar(VAR_PRICE, Utils.clampI(e.getPlayer().getVars().getVar(VAR_PRICE) - 1, 0, Integer.MAX_VALUE));
				case 171 -> e.getPlayer().getVars().setVar(VAR_PRICE, Utils.clampI(e.getPlayer().getVars().getVar(VAR_PRICE) + 1, 0, Integer.MAX_VALUE));
				case 175 -> e.getPlayer().getVars().setVar(VAR_PRICE, e.getPlayer().getVars().getVar(VAR_MEDIAN_PRICE));
				case 179 -> e.getPlayer().getVars().setVar(VAR_PRICE, Utils.clampI((int) ((double) e.getPlayer().getVars().getVar(VAR_PRICE) * 1.05), 0, Integer.MAX_VALUE));
				case 181 -> e.getPlayer().getVars().setVar(VAR_PRICE, Utils.clampI((int) ((double) e.getPlayer().getVars().getVar(VAR_PRICE) * 0.95), 0, Integer.MAX_VALUE));
				case 177 -> e.getPlayer().sendInputInteger("Enter desired price", price -> e.getPlayer().getVars().setVar(VAR_PRICE, Utils.clampI(price, 0, Integer.MAX_VALUE)));
				
				//Confirm offer
				case 186 -> confirmOffer(e.getPlayer());
				
				//Search item
				case 190 -> e.getPlayer().getPackets().openGESearch(e.getPlayer(), "Grand Exchange Item Search");
				
				default -> System.out.println("Unhandled GE button: " + e.getComponentId());
			}
		}
	};
	
	public static ButtonClickHandler sellInv = new ButtonClickHandler(107) {
		@Override
		public void handle(ButtonClickEvent e) {
			if (e.getPlayer().getTempB("geLocked"))
				return;
			if (e.getComponentId() == 18) {
				Item item = e.getPlayer().getInventory().getItem(e.getSlotId());
				if (item == null)
					return;
				if (item.getDefinitions().isNoted())
					item = new Item(item.getDefinitions().getCertId(), item.getAmount());
				selectItem(e.getPlayer(), item.getId(), item.getAmount());
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
	
	public static void clickBox(Player player, int box, boolean abort) {
		if (abort) {
			Offer offer = player.getGEOffers().get(box);
			if (offer == null)
				return;
			if (offer.getState() == State.FINISHED)
				return;
			offer.setState(State.FINISHED);
			player.setTempB("geLocked", true);
			WorldDB.getGE().save(offer, () -> {
				player.setTempB("geLocked", false);
				updateGE(player);
			});
			return;
		}
		player.getVars().setVar(VAR_CURR_BOX, box);
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
				offer.sendItems(player);
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

	public static void selectItem(Player player, int itemId, int amount) {
		if (!ItemDefinitions.getDefs(itemId).canExchange()) {
			player.sendMessage("That item can't be exchanged.");
			return;
		}
		player.getVars().setVar(VAR_ITEM, itemId);
		player.getVars().setVar(VAR_ITEM_AMOUNT, amount);
		player.getVars().setVar(VAR_PRICE, ItemDefinitions.getDefs(itemId).getHighAlchPrice());
		player.getVars().setVar(VAR_MEDIAN_PRICE, ItemDefinitions.getDefs(itemId).getHighAlchPrice());
		player.getPackets().setIFText(OFFER_SELECTION, 143, ItemExamines.getExamine(new Item(itemId)));
		WorldDB.getGE().getBestOffer(itemId, player.getVars().getVar(VAR_IS_SELLING) == 1, offer -> {
			player.getPackets().setIFText(OFFER_SELECTION, 143, ItemExamines.getExamine(new Item(itemId)) + "<br>" + "Best offer: " + (offer == null ? "None" : Utils.formatNumber(offer.getPrice())));
		});
	}
	
	public static void confirmOffer(Player player) {
		if (player.getVars().getVar(VAR_IS_SELLING) == -1)
			return;
		int box = player.getVars().getVar(VAR_CURR_BOX);
		boolean selling = player.getVars().getVar(VAR_IS_SELLING) == 1;
		int itemId = player.getVars().getVar(VAR_ITEM);
		int amount = player.getVars().getVar(VAR_ITEM_AMOUNT);
		int price = player.getVars().getVar(VAR_PRICE);
		int totalPrice = price * amount;
		if (player.getVars().getVar(VAR_IS_SELLING) == -1)
			return;
		if (box < 0 || box > 5) {
			player.sendMessage("Invalid box selection " + box + ".");
			return;
		}
		if (itemId <= 0)
			return;
		if (totalPrice <= 0 || totalPrice > Integer.MAX_VALUE) {
			player.sendMessage("Invalid total price.");
			return;
		}
		if (amount <= 0) {
			player.sendMessage("Invalid amount.");
			return;
		}
		if (selling) {
			
		} else {
			
		}
		
		Offer offer = new Offer(player.getUsername(), box, selling, itemId, amount, price);
		if (!deleteItems(player, offer)) {
			player.sendMessage("You don't have the items to cover the offer.");
			return;
		}
		player.getGEOffers().put(offer.getBox(), offer);
		updateGE(player);
		player.setTempB("geLocked", true);
		Set<String> ownersNeedUpdate = new HashSet<>();
		WorldDB.getGE().execute(() -> {
			List<Offer> offers = WorldDB.getGE().getBestOffersSync(offer);
			offer.setState(State.STABLE);
			if (offers == null || offers.isEmpty()) {
				WorldDB.getGE().saveSync(offer);
				updateGE(player);
				player.setTempB("geLocked", false);
				return;
			}
			for (Offer other : offers) {
				System.out.println("Processing -->");
				System.out.println(offer);
				System.out.println(other);
				if (offer.process(other)) {
					WorldDB.getGE().saveSync(other);
					ownersNeedUpdate.add(other.getOwner());
				}
			}
			System.out.println("Finalized: " + offer);
			System.out.println("Saving offer...");
			WorldDB.getGE().saveSync(offer);
			System.out.println("Syncing GE and updating.");
			updateGE(player);
			player.setTempB("geLocked", false);
		});
		if (!ownersNeedUpdate.isEmpty()) {
			for (String username : ownersNeedUpdate)
				GE.updateOffers(username);
		}
	}

	private static boolean deleteItems(Player player, Offer offer) {
		if (!offer.isSelling()) {
			if (player.getInventory().removeItems(new Item(995, offer.getPrice() * offer.getAmount())))
				return true;
			return false;
		}
		
		int notedId = ItemDefinitions.getDefs(offer.getItemId()).certId;
		int nonNotedAmt = player.getInventory().getAmountOf(offer.getItemId());
		int notedAmt = player.getInventory().getAmountOf(notedId);
		
		if (nonNotedAmt >= offer.getAmount()) {
			player.getInventory().deleteItem(offer.getItemId(), offer.getAmount());
			return true;
		}
		if (notedAmt >= offer.getAmount()) {
			player.getInventory().deleteItem(notedId, offer.getAmount());
			return true;
		}
		if (notedAmt + nonNotedAmt >= offer.getAmount()) {
			int amtLeft = offer.getAmount();
			player.getInventory().deleteItem(offer.getItemId(), nonNotedAmt);
			amtLeft -= nonNotedAmt;
			player.getInventory().deleteItem(notedId, amtLeft);
			return true;
		}
		return false;
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
					if (prev.get(offer.getBox()) != null && offer.amountLeft() != prev.get(offer.getBox()).amountLeft() || offer.getState() != prev.get(offer.getBox()).getState())
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
			else {
				player.getPackets().updateGESlot(i, offer.getStateHash(), offer.getItemId(), offer.getPrice(), offer.getAmount(), offer.getCompletedAmount());
				offer.sendItems(player);
			}
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
