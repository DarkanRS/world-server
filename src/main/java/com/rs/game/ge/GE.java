// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.
//
//  Copyright © 2021 Trenton Kress
//  This file is part of project: Darkan
//
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
	private static final int SPECIAL_DEPOSIT_INV = 107;
	private static final int COLLECTION_BOX = 109;

	private static final int VAR_ITEM = 1109;
	private static final int VAR_ITEM_AMOUNT = 1110;
	private static final int VAR_FOR_PRICE_TEXT = 1111;
	private static final int VAR_CURR_BOX = 1112;
	private static final int VAR_IS_SELLING = 1113;
	private static final int VAR_MEDIAN_PRICE = 1114;

	public enum GrandExchangeType {
		BUYING, SELLING, ABORTED;
	}
	public enum OfferType {
		BUY, SELL;
	}

	public static ButtonClickHandler mainInterface = new ButtonClickHandler(105) {
		@Override
		public void handle(ButtonClickEvent e) {;
		if (e.getPlayer().isIronMan()) {
			e.getPlayer().sendMessage("Ironmen stand alone.");
			return;
		}
		if (e.getPlayer().getTempAttribs().getB("geLocked"))
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
		case 200 -> clickBox(e.getPlayer(), e.getPlayer().getVars().getVar(VAR_CURR_BOX), true);

		//Back button
		case 128 -> open(e.getPlayer());

		case 206, 208 -> collectItems(e.getPlayer(), e.getPlayer().getVars().getVar(VAR_CURR_BOX), e.getComponentId() == 206 ? 0 : 1);

		//Amount adjustments
		case 155 -> e.getPlayer().getVars().setVar(VAR_ITEM_AMOUNT, Utils.clampI(e.getPlayer().getVars().getVar(VAR_ITEM_AMOUNT) - 1, 0, Integer.MAX_VALUE));
		case 157 -> e.getPlayer().getVars().setVar(VAR_ITEM_AMOUNT, Utils.clampI(e.getPlayer().getVars().getVar(VAR_ITEM_AMOUNT) + 1, 0, Integer.MAX_VALUE));
		case 160 -> e.getPlayer().getVars().setVar(VAR_ITEM_AMOUNT, e.getPlayer().getVars().getVar(VAR_IS_SELLING) == 1 ? 1 : Utils.clampI(e.getPlayer().getVars().getVar(VAR_ITEM_AMOUNT) + 1, 0, Integer.MAX_VALUE));
		case 162 -> e.getPlayer().getVars().setVar(VAR_ITEM_AMOUNT, e.getPlayer().getVars().getVar(VAR_IS_SELLING) == 1 ? 10 : Utils.clampI(e.getPlayer().getVars().getVar(VAR_ITEM_AMOUNT) + 10, 0, Integer.MAX_VALUE));
		case 164 -> e.getPlayer().getVars().setVar(VAR_ITEM_AMOUNT, e.getPlayer().getVars().getVar(VAR_IS_SELLING) == 1 ? 100 : Utils.clampI(e.getPlayer().getVars().getVar(VAR_ITEM_AMOUNT) + 100, 0, Integer.MAX_VALUE));
		case 166 -> e.getPlayer().getVars().setVar(VAR_ITEM_AMOUNT, e.getPlayer().getVars().getVar(VAR_IS_SELLING) == 1 ? e.getPlayer().getInventory().getAmountOf(e.getPlayer().getVars().getVar(VAR_ITEM)) : Utils.clampI(e.getPlayer().getVars().getVar(VAR_ITEM_AMOUNT) + 1000, 0, Integer.MAX_VALUE));
		case 168 -> e.getPlayer().sendInputInteger("Enter amount", amount -> e.getPlayer().getVars().setVar(VAR_ITEM_AMOUNT, Utils.clampI(amount, 0, Integer.MAX_VALUE)));

		//Price adjustments
		case 169 -> e.getPlayer().getVars().setVar(VAR_FOR_PRICE_TEXT, Utils.clampI(e.getPlayer().getVars().getVar(VAR_FOR_PRICE_TEXT) - 1, 0, Integer.MAX_VALUE));
		case 171 -> e.getPlayer().getVars().setVar(VAR_FOR_PRICE_TEXT, Utils.clampI(e.getPlayer().getVars().getVar(VAR_FOR_PRICE_TEXT) + 1, 0, Integer.MAX_VALUE));
		case 175 -> e.getPlayer().getVars().setVar(VAR_FOR_PRICE_TEXT, e.getPlayer().getVars().getVar(VAR_MEDIAN_PRICE));
		case 179 -> e.getPlayer().getVars().setVar(VAR_FOR_PRICE_TEXT, Utils.clampI((int) (e.getPlayer().getVars().getVar(VAR_FOR_PRICE_TEXT) * 1.05), 0, Integer.MAX_VALUE));
		case 181 -> e.getPlayer().getVars().setVar(VAR_FOR_PRICE_TEXT, Utils.clampI((int) (e.getPlayer().getVars().getVar(VAR_FOR_PRICE_TEXT) * 0.95), 0, Integer.MAX_VALUE));
		case 177 -> e.getPlayer().sendInputInteger("Enter desired price", price -> e.getPlayer().getVars().setVar(VAR_FOR_PRICE_TEXT, Utils.clampI(price, 0, Integer.MAX_VALUE)));

		//Confirm offer
		case 186 -> confirmOffer(e.getPlayer());

		//Search item
		case 190 -> e.getPlayer().getPackets().openGESearch(e.getPlayer(), "Grand Exchange Item Search");

		default -> System.out.println("Unhandled GE button: " + e.getComponentId() + ", " + e.getSlotId());
		}
		}
	};

	public static ButtonClickHandler sellInv = new ButtonClickHandler(SPECIAL_DEPOSIT_INV) {
		@Override
		public void handle(ButtonClickEvent e) {
			if (e.getPlayer().isIronMan()) {
				e.getPlayer().sendMessage("Ironmen stand alone.");
				return;
			}
			if (e.getPlayer().getTempAttribs().getB("geLocked"))
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

	public static ButtonClickHandler collBox = new ButtonClickHandler(COLLECTION_BOX) {
		@Override
		public void handle(ButtonClickEvent e) {
			if (e.getPlayer().isIronMan()) {
				e.getPlayer().sendMessage("Ironmen stand alone.");
				return;
			}
			if (e.getPlayer().getTempAttribs().getB("geLocked"))
				return;
			switch(e.getComponentId()) {
			case 19 -> collectItems(e.getPlayer(), 0, e.getSlotId() / 2);
			case 23 -> collectItems(e.getPlayer(), 1, e.getSlotId() / 2);
			case 27 -> collectItems(e.getPlayer(), 2, e.getSlotId() / 2);
			case 32 -> collectItems(e.getPlayer(), 3, e.getSlotId() / 2);
			case 37 -> collectItems(e.getPlayer(), 4, e.getSlotId() / 2);
			case 42 -> collectItems(e.getPlayer(), 5, e.getSlotId() / 2);
			default -> System.out.println("Unhandled collection box button: " + e.getComponentId() + ", " + e.getSlotId());
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

	public static void collectItems(Player player, int box, int slot) {
		Offer offer = player.getGEOffers().get(box);
		if (offer == null)
			return;
		Item item = offer.getProcessedItems().get(slot).clone();
		if (item == null)
			return;
		if (!item.getDefinitions().isStackable() && item.getAmount() > player.getInventory().getFreeSlots())
			item.setAmount(player.getInventory().getFreeSlots());
		if (item.getAmount() <= 0 || !player.getInventory().hasRoomFor(item)) {
			player.sendMessage("Not enough space in your inventory.");
			return;
		}
		final Item toRemove = item;
		offer.getProcessedItems().remove(toRemove);
		ItemDefinitions defs = toRemove.getDefinitions();
		player.getTempAttribs().setB("geLocked", true);
		if (offer.getProcessedItems().isEmpty() && offer.getState() != State.STABLE)
			WorldDB.getGE().remove(offer.getOwner(), box, () -> {
				player.getGEOffers().remove(box);
				//Turn to note upon withdrawel
				if (toRemove.getAmount() > 1 && !defs.isNoted() && defs.getCertId() != -1 && toRemove.getMetaData() == null)
					toRemove.setId(defs.getCertId());
				player.getInventory().addItemDrop(toRemove);
				player.getTempAttribs().setB("geLocked", false);
				updateGE(player);
			});
		else
			WorldDB.getGE().save(offer, () -> {
				//Turn to note upon withdrawel
				if (toRemove.getAmount() > 1 && !defs.isNoted() && defs.getCertId() != -1 && toRemove.getMetaData() == null)
					toRemove.setId(defs.getCertId());
				player.getInventory().addItemDrop(toRemove);
				player.getTempAttribs().setB("geLocked", false);
				updateGE(player);
			});
	}

	public static void clickBox(Player player, int box, boolean abort) {
		Offer offer = player.getGEOffers().get(box);
		if (offer == null)
			return;
		if (abort) {
			if (offer.getState() == State.FINISHED)
				return;
			player.getTempAttribs().setB("geLocked", true);
			WorldDB.getGE().remove(player.getUsername(), offer.getBox(), () -> {
				player.getTempAttribs().setB("geLocked", false);
				offer.abort();
				updateGE(player);
			});
			return;
		}
		player.getVars().setVar(VAR_CURR_BOX, box);
		player.getPackets().setIFText(OFFER_SELECTION, 143, ItemExamines.getExamine(new Item(offer.getItemId())));
		player.getPackets().setIFTargetParams(new IFTargetParams(OFFER_SELECTION, 206, -1, 0).enableRightClickOptions(0,1));
		player.getPackets().setIFTargetParams(new IFTargetParams(OFFER_SELECTION, 208, -1, 0).enableRightClickOptions(0,1));
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
		player.getPackets().openGESearch(player, "Grand Exchange Item Search");
	}

	public static void openSell(Player player, int box) {
		player.getVars().setVar(VAR_CURR_BOX, box);
		player.getVars().setVar(VAR_IS_SELLING, 1);
		player.getInterfaceManager().sendInventoryInterface(SPECIAL_DEPOSIT_INV);
		player.getPackets().sendItems(93, player.getInventory().getItems());
		player.getPackets().setIFRightClickOps(SPECIAL_DEPOSIT_INV, 18, 0, 27, 0);
		player.getPackets().sendInterSetItemsOptionsScript(SPECIAL_DEPOSIT_INV, 18, 93, 4, 7, "Offer");
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
		player.getVars().setVar(VAR_FOR_PRICE_TEXT, ItemDefinitions.getDefs(itemId).getHighAlchPrice());
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
		int price = player.getVars().getVar(VAR_FOR_PRICE_TEXT);
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
		Offer offer;
		if (selling)
			offer = new Offer(player.getUsername(), box, selling, itemId, amount, price, OfferType.SELL);
		else
			offer = new Offer(player.getUsername(), box, selling, itemId, amount, price, OfferType.BUY);


		if (!deleteItems(player, offer)) {
			player.sendMessage("You don't have the items to cover the offer.");
			return;
		}
		resetVars(player);
		player.getGEOffers().put(offer.getBox(), offer);
		updateGE(player);
		player.getTempAttribs().setB("geLocked", true);
		WorldDB.getGE().execute(() -> {
			Set<String> ownersNeedUpdate = new HashSet<>();
			List<Offer> offers = WorldDB.getGE().getBestOffersSync(offer);
			offer.setState(State.STABLE);
			if (offers == null || offers.isEmpty()) {
				WorldDB.getGE().saveSync(offer);
				updateGE(player);
				player.getTempAttribs().setB("geLocked", false);
				return;
			}
			for (Offer other : offers)
				if (offer.process(other)) {
					WorldDB.getGE().saveSync(other);
					ownersNeedUpdate.add(other.getOwner());
				}
			if (offer.getState() == State.STABLE)
				WorldDB.getGE().saveSync(offer);
			updateGE(player);
			player.getTempAttribs().setB("geLocked", false);
			if (!ownersNeedUpdate.isEmpty())
				for (String username : ownersNeedUpdate)
					GE.updateOffers(username);
		});
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
		player.getVars().setVar(VAR_FOR_PRICE_TEXT, 0);
	}

	public static void updateOffers(String username) {
		Player player = World.getPlayer(username);
		if (player != null)
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
				if (diff)
					if (player.getTempAttribs().getL("GENotificationTime") == 0) {
						player.getTempAttribs().setL("GENotificationTime", System.currentTimeMillis());
						player.getPackets().sendSound(4042, 0, 1);
						player.sendMessage("One or more of your grand exchange offers has been updated.");
					} else if ((System.currentTimeMillis() - player.getTempAttribs().getL("GENotificationTime")) > 1000*60*1) { //1 minute
						player.getTempAttribs().setL("GENotificationTime", System.currentTimeMillis());
						player.getPackets().sendSound(4042, 0, 1);
						player.sendMessage("One or more of your grand exchange offers has been updated.");
					}
			});
	}

	private static void updateGE(Player player) {
		updateCollectionBox(player);
		for (int i = 0; i < 6; i++) {
			Offer offer = player.getGEOffers().get(i);
			if (offer == null)
				player.getPackets().updateGESlot(i, 0, -1, -1, -1, -1, -1);
			else {
				player.getPackets().updateGESlot(i, offer.getStateHash(), offer.getItemId(), offer.getPrice(), offer.getAmount(), offer.getCompletedAmount(), offer.getTotalGold());
				offer.sendItems(player);
			}
		}
	}

	private static void updateCollectionBox(Player player) {
		for (int box = 0; box < 6; box++) {
			Offer offer = player.getGEOffers().get(box);
			if (offer == null) {
				sendItems(player, new Item[]{new Item(-1, 0)}, box);
				continue;
			}
			switch(offer.getCurrentType()) {
			case SELLING:
				sendItems(player, new Item[] { new Item(offer.getTotalGold() > 0 ? 995 : -1, offer.getTotalGold()) }, offer.getBox());
				break;
			case BUYING:
				sendItems(player, new Item[] { new Item(offer.getCompletedAmount() == 0 ? -1 : offer.getItemId(), offer.getCompletedAmount()), new Item(offer.getTotalGold() > 0 ? 995 : -1, offer.getTotalGold()) }, offer.getBox());
				break;
			case ABORTED:
				if (offer.getOfferType() == OfferType.BUY)
					sendItems(player, new Item[] { new Item(offer.getCompletedAmount() == 0 ? -1 : offer.getItemId(), offer.getCompletedAmount()), new Item(offer.getTotalGold() > 0 ? 995 : -1, offer.getTotalGold()) }, offer.getBox());
				else
					sendItems(player, new Item[] { new Item(offer.getItemId(), offer.getAmount()), new Item(offer.getTotalGold() > 0 ? 995 : -1, offer.getTotalGold()) }, offer.getBox());
				break;
			}
		}
	}

	public static void sendItems(Player player, Item[] items, int box) {
		int iComp = getComponentForBox(box);
		player.getPackets().sendItems(iComp, items);
	}

	public static int getComponentForBox(int box) {
		switch (box) {
		case 0:
			return 523;
		case 1:
			return 524;
		case 2:
			return 525;
		case 3:
			return 526;
		case 4:
			return 527;
		case 5:
			return 528;
		}
		return -1;
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
