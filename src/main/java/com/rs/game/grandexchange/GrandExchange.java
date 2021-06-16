package com.rs.game.grandexchange;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.cache.loaders.interfaces.IFTargetParams;
import com.rs.game.player.Player;
import com.rs.game.player.dialogues.SimpleMessage;
import com.rs.lib.game.Item;
import com.rs.lib.net.ClientPacket;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ButtonClickEvent;
import com.rs.plugin.handlers.ButtonClickHandler;

/**
 * Grand exchange button and config handler
 * 
 * @author Trenton
 */
@PluginEventHandler
public class GrandExchange {

	public enum GrandExchangeOfferType {
		ABORTED(-3), 
		RESET_GE(0), 
		RESET_AFTER_BUY(8), 
		RESET_AFTER_SELL(16), 
		SUBMITTING_BUY_OFFER(1), 
		BUYING_PROGRESS(2), 
		BUYING_PROGRESS_2(3), 
		BUYING_PROGRESS_3(4), 
		FINISHED_BUYING(5), 
		BUYING_PROGRESS_4(6), 
		BUYING_PROGRESS_5(7), 
		SUBMIT_SELL_OFFER(9), 
		SELLING_PROGRESS(10), 
		SELLING_PROGRESS_2(11), 
		SELLING_PROGRESS_3(13), 
		SELLING_PROGRESS_4(14), 
		SELLING_PROGRESS_5(15), 
		FINISHED_SELLING(13);

		private int opcode;

		GrandExchangeOfferType(int opcode) {
			this.opcode = opcode;
		}

		public int getOpcode() {
			return opcode;
		}
	}

	public enum GrandExchangeType {
		BUYING, SELLING, ABORTED;
	}

	public enum OfferType {
		BUY, SELL;
	}
	
	public static ButtonClickHandler handleButtons = new ButtonClickHandler(105, 107) {
		@Override
		public void handle(ButtonClickEvent e) {
			if (e.getPlayer().isIronMan()) {
				e.getPlayer().sendMessage("You are not allowed to use the Grand Exchange as an ironman.");
				return;
			}
			switch(e.getInterfaceId()) {
			case 105:
				switch(e.getComponentId()) {
				case 206:
				case 208:
					claim(e.getPlayer());
					break;
				case 19:
				case 35:
				case 51:
				case 70:
				case 89:
				case 108:
					int myBox = getBoxForComponent(e.getComponentId());
					if (myBox > -1) {
						e.getPlayer().geBox = myBox;
						if (e.getPacket() == ClientPacket.IF_OP2) {
							abort(e.getPlayer(), myBox, !e.getPlayer().geBuying);
							return;
						}
						openCollectionInterface(e.getPlayer(), myBox, !e.getPlayer().geBuying);
					}
					break;
				case 31:
				case 47:
				case 63:
				case 82:
				case 101:
				case 120:
					int buyBox = getBoxForComponent(e.getComponentId());
					if (buyBox > -1) {
						displayBuyInterface(e.getPlayer(), buyBox);
						e.getPlayer().getVars().setVar(1112, buyBox);
						e.getPlayer().geBox = buyBox;
					}
					break;
				case 32:
				case 48:
				case 64:
				case 83:
				case 102:
				case 121:
					int sellBox = getBoxForComponent(e.getComponentId());
					if (sellBox > -1) {
						displaySellInterface(e.getPlayer(), sellBox);
						e.getPlayer().geBox = sellBox;
						e.getPlayer().getVars().setVar(1112, sellBox);
					}
					break;
				case 169:
				case 171:
				case 179:
				case 181:
				case 177:
					setPrice(e.getPlayer(), e.getComponentId());
					break;
				case 155:
				case 157:
				case 160:
				case 162:
				case 164:
				case 166:
				case 168:
					setAmount(e.getPlayer(), e.getComponentId());
					break;
				case 186:
					if (!e.getPlayer().geBuying) {
						e.getPlayer().geAwaitingSell = null;
					}
					offerOffer(e.getPlayer());
					break;
				case 190:
					if (e.getPlayer().geBox > -1) {
						displayBuyInterface(e.getPlayer(), e.getPlayer().geBox);
					}
					break;
				case 128:
					open(e.getPlayer());
					break;
				case 200:
					abort(e.getPlayer(), e.getPlayer().geBox, !e.getPlayer().geBuying);
					break;
				}
				break;
			case 107:
				if (e.getPlayer().geBuying)
					return;
				sellItem(e.getPlayer(), e.getPlayer().getInventory().getItem(e.getSlotId()).getId(), e.getPlayer().getInventory().getItem(e.getSlotId()).getAmount());
				break;
			}
			updatePrice(e.getPlayer());
			updateGrandExchangeBoxes(e.getPlayer());
			updateCollectionBox(e.getPlayer());
		}
	};

	public static void claim(Player player) {
		if (player.geBox != -1) {
			if (player.getOfferSet().offers[player.geBox] != null) {
				player.getOfferSet().offers[player.geBox].claimAndClear(player);
				updateGrandExchangeBoxes(player);
				open(player);
			}
		}
	}

	public static int getNotedId(int itemId) {
		ItemDefinitions def = ItemDefinitions.getDefs(itemId);
		if (!def.isNoted() && !def.isStackable())
			return def.getCertId();
		if (def.isNoted() || def.isStackable())
			return itemId;
		return -1;
	}

	public static void abort(Player player, int slot, boolean selling) {
		if (slot < 0)
			return;
		player.getInterfaceManager().closeChatBoxInterface();
		if (player.getOfferSet().offers[slot].getAmountLeft() == 0)
			return;
		player.getOfferSet().offers[slot].abort();
		player.sendMessage("Abort request acknowledged. Please be aware that your offer may have already been completed.");
		updateCollectionBox(player);
		updateGrandExchangeBoxes(player);
	}

	public static void updateCollectionBox(Player player) {
		if (player.geBox < 0)
			return;
		Offer offer = player.getOfferSet().offers[player.geBox];
		if (offer == null)
			return;

		switch (offer.getCurrentType()) {
		case SELLING:
			sendItems(player, new Item[] { new Item(offer.getCashToClaim() > 0 ? 995 : -1, offer.getCashToClaim()) }, offer.getBox());
			break;
		case BUYING:
			sendItems(player, new Item[] { new Item(offer.getItemId(), offer.getAmountProcessed()), new Item(offer.getCashToClaim() > 0 ? 995 : -1, offer.getCashToClaim()) }, offer.getBox());
			break;
		case ABORTED:
			if (offer.getOfferType() == OfferType.BUY) {
				sendItems(player, new Item[] { new Item(offer.getAmountProcessed() == 0 ? -1 : offer.getItemId(), offer.getAmountProcessed()), new Item(offer.getCashToClaim() > 0 ? 995 : -1, offer.getCashToClaim()) }, offer.getBox());
			} else {
				sendItems(player, new Item[] { new Item(offer.getItemId(), offer.getAmountLeft()), new Item(offer.getCashToClaim() > 0 ? 995 : -1, offer.getCashToClaim()) }, offer.getBox());
			}
			break;
		default:
			sendItems(player, new Item[] { new Item(-1, 0) }, offer.getBox());
			break;
		}
		player.getPackets().setIFTargetParams(new IFTargetParams(105, 206, -1, 0).enableRightClickOptions(0,1));
		player.getPackets().setIFTargetParams(new IFTargetParams(105, 208, -1, 0).enableRightClickOptions(0,1));
	}

	public static void openGrandExchangeCollection(Player player) {
		player.getInterfaceManager().sendInterface(109);
		player.getPackets().setIFTargetParams(new IFTargetParams(109, 19, 0, 2).enableRightClickOptions(0,1));
		player.getPackets().setIFTargetParams(new IFTargetParams(109, 23, 0, 2).enableRightClickOptions(0,1));
		player.getPackets().setIFTargetParams(new IFTargetParams(109, 27, 0, 2).enableRightClickOptions(0,1));
		player.getPackets().setIFTargetParams(new IFTargetParams(109, 32, 0, 2).enableRightClickOptions(0,1));
		player.getPackets().setIFTargetParams(new IFTargetParams(109, 36, 0, 2).enableRightClickOptions(0,1));
		player.getPackets().setIFTargetParams(new IFTargetParams(109, 42, 0, 2).enableRightClickOptions(0,1));
	}

	public static void setPrice(Player player, int button) {
		switch (button) {
		case 169:
			if (player.gePrice > 1) {
				player.gePrice--;
			} else {
				player.gePrice = 1;
			}
			break;
		case 171:
			if (player.gePrice < Integer.MAX_VALUE) {
				player.gePrice++;
			} else {
				player.gePrice = 1;
			}
			break;
		case 181:
			if ((player.gePrice * 0.95) > 0) {
				player.gePrice *= 0.95;
			} else {
				player.gePrice = 1;
			}
			break;
		case 179:
			if ((player.gePrice * 1.05) < Integer.MAX_VALUE) {
				int oldPrice = player.gePrice;
				player.gePrice *= 1.05;
				if (oldPrice == player.gePrice)
					player.gePrice++;
			} else {
				player.gePrice = 1;
			}
			break;
		case 177:
			player.getTemporaryAttributes().put("geCustomPrice", true);
			player.getPackets().sendRunScriptReverse(108, new Object[] { "Enter Amount:" });
			break;
		}
		updatePrice(player);
	}

	public static void setAmount(Player player, int button) {
		ItemDefinitions defs = ItemDefinitions.getDefs(player.geItemId);
		switch (button) {
		case 155:
			if (player.geAmount > 1) {
				player.geAmount--;
			} else {
				player.geAmount = 1;
			}
			break;
		case 157:
			if (player.geAmount < Integer.MAX_VALUE) {
				player.geAmount++;
			} else {
				player.geAmount = 1;
			}
			break;
		case 160:
			if (player.geBuying) {
				if (player.getInventory().getNumberOf(player.geItemId) <= player.geAmount && !player.geBuying) {
					return;
				}
				player.geAmount += 1;
			} else {
				player.geAmount = 1;
			}
			break;
		case 162:
			if (player.geBuying) {
				player.geAmount += 10;
			} else {
				if (player.getInventory().getNumberOf(player.geItemId) > 10) {
					player.geAmount = player.geAmount;
				} else {
					player.geAmount = 10;
				}
			}
			break;
		case 164:
			if (player.geBuying) {
				player.geAmount += 100;
			} else {
				if (defs.isNoted()) {
					if (player.getInventory().getItems().getNumberOf(defs.getCertId()) <= 100) {
						player.geAmount = player.geAmount;
					} else {
						player.geAmount = 100;
					}
				} else if (player.getInventory().getItems().getNumberOf(player.geItemId) <= 100) {
					player.geAmount = player.geAmount;
				} else {
					player.geAmount = 100;
				}
			}
			break;
		case 166:
			if (player.geBuying) {
				player.geAmount += 1000;
			} else {
				player.geAmount = player.geAmount;
			}
			break;
		case 168:
			player.getTemporaryAttributes().put("geCustomAmount", true);
			player.getPackets().sendRunScriptReverse(108, new Object[] { "Enter Amount:" });
			break;
		}
		player.getVars().setVar(1110, player.geAmount);
	}

	public static void sellItem(Player player, int itemId, int amount) {
		player.getInterfaceManager().closeChatBoxInterface();
		if (!ItemDefinitions.getDefs(itemId).canExchange()) {
			player.getDialogueManager().execute(new SimpleMessage(), "You can't exchange that item.");
			return;
		}
		ItemDefinitions def = ItemDefinitions.getDefs(itemId);
		if (def == null)
			return;
		if (def.isNoted()) {
			player.geNoteId = def.getId();
			itemId = def.getCertId();
			player.getInterfaceManager().removeInventoryInterface();
			setItem(player, def.getCertId(), ItemDefinitions.getDefs(itemId).getValue(), player.getInventory().getItems().getNumberOf(def.getId()));
			updatePrice(player, itemId, true);
			player.geAmount = player.getInventory().getItems().getNumberOf(def.getId());
		} else {
			amount = player.getInventory().getItems().getNumberOf(itemId);
			player.getInterfaceManager().removeInventoryInterface();
			setItem(player, itemId, ItemDefinitions.getDefs(itemId).getValue(), player.getInventory().getItems().getNumberOf(def.getId()));
			updatePrice(player, itemId, true);
			player.geAmount = player.getInventory().getItems().getNumberOf(def.getId());
		}
	}

	public static void buyItem(Player player, int id) {
		ItemDefinitions def = ItemDefinitions.getDefs(id);
		if (def == null) {
			return;
		}
		player.getInterfaceManager().removeInventoryInterface();
		setItem(player, id, ItemDefinitions.getDefs(id).getValue(), 1);
		updatePrice(player, id, false);
	}

	public static void offerOffer(Player player) {
		if (player.geBox >= 0 && player.geBox <= player.getOfferSet().offers.length) {
			if (player.getOfferSet().offers[player.geBox] != null)
				return;
		}

		if ((player.gePrice * player.geAmount) > Integer.MAX_VALUE) {
			return;
		}

		if (player.gePrice <= 0) {
			player.sendMessage("You can't buy/sell an item for free!");
			return;
		}

		if (player.geAmount <= 0) {
			player.sendMessage("You can't buy/sell zero of an item!");
			return;
		}

		if (player.geItemId <= 0) {
			player.sendMessage("Choose an item to buy/sell first.");
			return;
		}
		if (player.geBuying) {
			if (player.geTotalPrice >= Integer.MAX_VALUE)
				return;
			if (player.geTotalPrice <= 0)
				return;
			player.geAwaitingBuy = new Item(player.geItemId, player.geAmount);
			if (player.geAwaitingBuy == null || player.geAwaitingBuy.getId() < 0 || player.geAwaitingBuy.getAmount() < 0) {
				player.sendMessage("Error generating bought item. Please try again.");
				return;
			}
			if (player.getInventory().containsItem(995, player.geTotalPrice)) {
				player.getInventory().deleteItem(995, player.geTotalPrice);
				Offer offer = new Offer(player.getUsername(), player.geBox, player.geAwaitingBuy.getId(), player.geAwaitingBuy.getAmount(), player.gePrice, OfferType.BUY);
				player.setGrandExchangeOffer(offer, player.geBox);
				GrandExchangeDatabase.updateOfferSet(player.getUsername(), player.getOfferSet(), true);
			} else {
				player.sendMessage("You don't have enough coins to buy that many.");
				return;
			}
		} else {
			player.geAwaitingSell = new Item(player.geItemId, player.geAmount);
			if (player.geAwaitingSell == null || player.geAwaitingSell.getId() < 0 || player.geAwaitingSell.getAmount() < 0) {
				player.sendMessage("Error generating sold item. Please try again.");
				return;
			}

			if (player.geNoteId == -1) {
				if (player.getInventory().containsItem(player.geAwaitingSell.getId(), player.geAwaitingSell.getAmount())) {
					Offer offer = new Offer(player.getUsername(), player.geBox, player.geAwaitingSell.getId(), player.geAwaitingSell.getAmount(), player.gePrice, OfferType.SELL);
					player.getInventory().deleteItem(player.geAwaitingSell.getId(), player.geAwaitingSell.getAmount());
					player.setGrandExchangeOffer(offer, player.geBox);
					GrandExchangeDatabase.updateOfferSet(player.getUsername(), player.getOfferSet(), true);
				} else {
					player.sendMessage("You don't have enough of that item to sell.");
					return;
				}
			} else {
				if (player.getInventory().containsItem(player.geNoteId, player.geAwaitingSell.getAmount())) {
					Offer offer = new Offer(player.getUsername(), player.geBox, player.geAwaitingSell.getId(), player.geAwaitingSell.getAmount(), player.gePrice, OfferType.SELL);
					player.getInventory().deleteItem(player.geNoteId, player.geAwaitingSell.getAmount());
					player.setGrandExchangeOffer(offer, player.geBox);
					GrandExchangeDatabase.updateOfferSet(player.getUsername(), player.getOfferSet(), true);
				} else {
					player.sendMessage("You don't have enough of that item to sell.");
					return;
				}
			}
		}
		open(player);
		updateGrandExchangeBoxes(player);
	}

	public static void openCollectionInterface(Player player, int slot, boolean selling) {
		player.getVars().setVar(1112, slot);
		// player.getPackets().sendItemOnIComponent(105, 143,
		// player.offer[slot].getId(), 1);
		player.getInterfaceManager().closeChatBoxInterface();
		// if (player.offer[slot].getAmountTransacted() != 0)
		// player.getPackets().sendItemOnIComponent(105, 206, selling ? 995 :
		// player.offer[slot].getId(), selling ?
		// Economy.getItemPrice(player.offer[slot].getId()) :
		// player.offer[slot].getAmountTransacted());
		if (!selling)
			player.getPackets().setIFItem(105, 208, 995, 0);
	}

	public static void sendItems(Player player, Item[] items, int box) {
		int key = getComponentForBox(box);
		player.getPackets().sendItems(key, items);
	}

	public static void open(Player player) {
		player.resetGEValues();
		player.getPackets().closeGESearch();
		player.getVars().setVar(1112, -1);
		player.getVars().setVar(1113, -1);
		player.getVars().setVar(1109, -1);
		player.getVars().setVar(1110, 0);
		player.getInterfaceManager().removeInventoryInterface();
		player.getInterfaceManager().sendInterface(105);
	}

	public static void displayBuyInterface(final Player player, int box) {
		player.geBuying = true;
		Object[] o = new Object[] { "Grand Exchange Item Search" };
		player.getVars().setVar(1113, 0);
		player.getPackets().openGESearch(player, o);
	}

	public static void displaySellInterface(Player player, int offerBox) {
		reset(player);
		player.geBuying = false;
		player.getVars().setVar(1113, 1);
		player.getInterfaceManager().sendInventoryInterface(107);
		player.getPackets().sendItems(93, player.getInventory().getItems());
		player.getPackets().setIFRightClickOps(107, 18, 0, 27, 0);
		player.getPackets().sendInterSetItemsOptionsScript(107, 18, 93, 4, 7, "Offer");
		player.getInterfaceManager().closeChatBoxInterface();
		player.getPackets().setIFHidden(105, 196, true);
	}

	public static void setItem(Player player, int id, int price, int amount) {
		player.getVars().setVar(1109, id);
		player.getVars().setVar(1110, amount);
		player.getVars().setVar(1111, price * amount);
		player.geItemId = id;
		player.gePrice = price;
		player.geAmount = amount;
		player.geTotalPrice = price * amount;
	}

	public static void updatePrice(Player player) {
		player.geTotalPrice = player.gePrice * player.geAmount;
		player.getVars().setVar(1110, player.geAmount);
		player.getVars().setVar(1111, player.gePrice);
	}

	public static void updatePrice(Player player, int itemId, boolean sell) {
		player.geTotalPrice = player.gePrice * player.geAmount;
		player.getVars().setVar(1110, player.geAmount);
		player.getVars().setVar(1111, player.gePrice);
		player.getPackets().setIFText(105, 143, "Best offer: " + GrandExchangeDatabase.getBestOffer(itemId, sell));
	}

	public static void reset(Player player) {
		player.getVars().setVar(1109, -1);
		player.getVars().setVar(1110, 0);
		player.getVars().setVar(1111, 0);
		player.getVars().setVar(1112, -1);
		player.getVars().setVar(1113, 0);
		player.resetGEValues();
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

	public static void updateGrandExchangeBoxes(Player player) {
		for (int i = 0; i < player.getOfferSet().offers.length; i++) {
			if (player.getOfferSet().offers[i] != null)
				player.getPackets().updateGESlot(i, player.getOfferSet().offers[i].getProgressOpcode(), player.getOfferSet().offers[i].getItemId(), player.getOfferSet().offers[i].getPricePerItem(), player.getOfferSet().offers[i].getItemAmount(), player.getOfferSet().offers[i].getItemAmount() - player.getOfferSet().offers[i].getAmountLeft());
			else
				player.getPackets().updateGESlot(i, 0, -1, -1, -1, -1);
		}
	}

	public static int getBoxForComponent(int button) {
		switch (button) {
		case 19:
		case 31:
		case 32:
			return 0;
		case 35:
		case 48:
		case 47:
			return 1;
		case 51:
		case 63:
		case 64:
			return 2;
		case 70:
		case 82:
		case 83:
			return 3;
		case 89:
		case 101:
		case 102:
			return 4;
		case 108:
		case 120:
		case 121:
			return 5;
		}
		return -1;
	}
}
