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
//  Copyright (C) 2021 Trenton Kress
//  This file is part of project: Darkan
//
package com.rs.game.model.entity.player;

import com.rs.cache.loaders.interfaces.IFEvents;
import com.rs.db.WorldDB;
import com.rs.game.content.ItemConstants;
import com.rs.game.model.item.ItemsContainer;
import com.rs.lib.game.Item;
import com.rs.lib.game.Rights;
import com.rs.lib.net.ClientPacket;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ButtonClickEvent;
import com.rs.plugin.handlers.ButtonClickHandler;
import com.rs.utils.EconomyPrices;
import com.rs.utils.ItemConfig;

@PluginEventHandler
public class Trade {

	private Player player, target;
	private ItemsContainer<Item> items;
	private boolean tradeModified;
	private boolean accepted;
	private boolean logged = false;

	public Trade(Player player) {
		this.player = player; // player reference
		items = new ItemsContainer<>(28, false);
	}

	public static ButtonClickHandler handleTradeWindow = new ButtonClickHandler(334, 335, 336) {
		@Override
		public void handle(ButtonClickEvent e) {
			if (e.getInterfaceId() == 334) {
				if (e.getPlayer().isIronMan())
					return;
				if (e.getPlayer().getRights() == Rights.ADMIN) {
					e.getPlayer().sendMessage("Administrators cannot trade.");
					return;
				}
				if (e.getComponentId() == 22)
					e.getPlayer().closeInterfaces();
				else if (e.getComponentId() == 21)
					e.getPlayer().getTrade().accept(false);
			} else if (e.getInterfaceId() == 335) {
				if (e.getPlayer().isIronMan())
					return;
				if (e.getComponentId() == 18)
					e.getPlayer().getTrade().accept(true);
				else if (e.getComponentId() == 20)
					e.getPlayer().closeInterfaces();
				else if (e.getComponentId() == 32) {
					if (e.getPacket() == ClientPacket.IF_OP1)
						e.getPlayer().getTrade().removeItem(e.getSlotId(), 1);
					else if (e.getPacket() == ClientPacket.IF_OP2)
						e.getPlayer().getTrade().removeItem(e.getSlotId(), 5);
					else if (e.getPacket() == ClientPacket.IF_OP3)
						e.getPlayer().getTrade().removeItem(e.getSlotId(), 10);
					else if (e.getPacket() == ClientPacket.IF_OP4)
						e.getPlayer().getTrade().removeItem(e.getSlotId(), Integer.MAX_VALUE);
					else if (e.getPacket() == ClientPacket.IF_OP5)
						e.getPlayer().sendInputInteger("Enter Amount:", num -> e.getPlayer().getTrade().removeItem(e.getSlotId(), num));
					else if (e.getPacket() == ClientPacket.IF_OP6)
						e.getPlayer().getTrade().sendValue(e.getSlotId(), false);
					else if (e.getPacket() == ClientPacket.IF_OP10)
						e.getPlayer().getTrade().sendExamine(e.getSlotId(), false);
				} else if (e.getComponentId() == 35)
					if (e.getPacket() == ClientPacket.IF_OP1)
						e.getPlayer().getTrade().sendValue(e.getSlotId(), true);
					else if (e.getPacket() == ClientPacket.IF_OP10)
						e.getPlayer().getTrade().sendExamine(e.getSlotId(), true);
			} else if (e.getInterfaceId() == 336)
				if (e.getComponentId() == 0)
					if (e.getPacket() == ClientPacket.IF_OP1)
						e.getPlayer().getTrade().addItem(e.getSlotId(), 1);
					else if (e.getPacket() == ClientPacket.IF_OP2)
						e.getPlayer().getTrade().addItem(e.getSlotId(), 5);
					else if (e.getPacket() == ClientPacket.IF_OP3)
						e.getPlayer().getTrade().addItem(e.getSlotId(), 10);
					else if (e.getPacket() == ClientPacket.IF_OP4)
						e.getPlayer().getTrade().addItem(e.getSlotId(), Integer.MAX_VALUE);
					else if (e.getPacket() == ClientPacket.IF_OP5)
						e.getPlayer().sendInputInteger("Enter Amount:", num -> e.getPlayer().getTrade().addItem(e.getSlotId(), num));
					else if (e.getPacket() == ClientPacket.IF_OP6)
						e.getPlayer().getTrade().sendValue(e.getSlotId());
					else if (e.getPacket() == ClientPacket.IF_OP10)
						e.getPlayer().getInventory().sendExamine(e.getSlotId());
		}
	};

	/*
	 * called to both players
	 */
	public void openTrade(Player target) {
		synchronized (this) {
			synchronized (target.getTrade()) {
				if (target.isIronMan() || player.isIronMan()) {
					player.sendMessage("You may not trade an ironman.");
					target.sendMessage("You may not trade an ironman.");
					return;
				}
				if (target.getRights() == Rights.ADMIN || player.getRights() == Rights.ADMIN) {
					player.sendMessage("You may not trade an administrator.");
					target.sendMessage("You may not trade an administrator.");
					return;
				}
				this.target = target;
				player.getPackets().setIFText(335, 17, "Trading With: " + target.getDisplayName());
				player.getPackets().sendVarcString(203, target.getDisplayName());
				sendInterItems();
				sendOptions();
				sendTradeModified();
				refreshFreeInventorySlots();
				refreshTradeWealth();
				refreshStageMessage(true);
				player.getInterfaceManager().sendInterface(335);
				player.getInterfaceManager().sendInventoryInterface(336);
				player.setCloseInterfacesEvent(() -> closeTrade(CloseTradeStage.CANCEL));
			}
		}
	}

	public void removeItem(final int slot, int amount) {
		synchronized (this) {
			if (!isTrading())
				return;
			synchronized (target.getTrade()) {
				Item item = items.get(slot);
				if (item == null)
					return;
				Item[] itemsBefore = items.getItemsCopy();
				if (!item.containsMetaData()) {
					int maxAmount = items.getNumberOf(item);
					if (amount < maxAmount)
						item = new Item(item.getId(), amount);
					else
						item = new Item(item.getId(), maxAmount);
				}
				items.remove(slot, item);
				player.getInventory().addItem(item);
				refreshItems(itemsBefore);
				cancelAccepted();
				setTradeModified(true);
			}
		}
	}

	public void sendFlash(int slot) {
		player.getPackets().sendInterFlashScript(335, 33, 4, 7, slot);
		target.getPackets().sendInterFlashScript(335, 36, 4, 7, slot);
	}

	public void cancelAccepted() {
		boolean canceled = false;
		if (accepted) {
			accepted = false;
			canceled = true;
		}
		if (target.getTrade().accepted) {
			target.getTrade().accepted = false;
			canceled = true;
		}
		if (canceled)
			refreshBothStageMessage(canceled);
	}

	public void addItem(int slot, int amount) {
		synchronized (this) {
			if (!isTrading())
				return;
			synchronized (target.getTrade()) {
				Item item = player.getInventory().getItem(slot);
				if (item == null)
					return;
				if (!player.hasRights(Rights.OWNER) && !ItemConstants.isTradeable(item)) {
					player.sendMessage("That item isn't tradeable.");
					return;
				}
				Item[] itemsBefore = items.getItemsCopy();
				if (!item.containsMetaData()) {
					int maxAmount = player.getInventory().getItems().getNumberOf(item);
					if (amount < maxAmount)
						item = new Item(item.getId(), amount);
					else
						item = new Item(item.getId(), maxAmount);
				}
				items.add(item);
				player.getInventory().deleteItem(slot, item);
				refreshItems(itemsBefore);
				cancelAccepted();
			}
		}
	}

	public void refreshItems(Item[] itemsBefore) {
		int[] changedSlots = new int[itemsBefore.length];
		int count = 0;
		for (int index = 0; index < itemsBefore.length; index++) {
			Item item = items.array()[index];
			if (itemsBefore[index] != item) {
				if (itemsBefore[index] != null && (item == null || item.getId() != itemsBefore[index].getId() || item.getAmount() < itemsBefore[index].getAmount()))
					sendFlash(index);
				changedSlots[count++] = index;
			}
		}
		int[] finalChangedSlots = new int[count];
		System.arraycopy(changedSlots, 0, finalChangedSlots, 0, count);
		refresh(finalChangedSlots);
		refreshFreeInventorySlots();
		refreshTradeWealth();
	}

	public void sendOptions() {
		player.getPackets().sendInterSetItemsOptionsScript(336, 0, 93, 4, 7, "Offer", "Offer-5", "Offer-10", "Offer-All", "Offer-X", "Value<col=FF9040>", "Lend");
		player.getPackets().setIFEvents(new IFEvents(336, 0, 0, 27).enableRightClickOptions(0,1,2,3,4,5,6,9));
		player.getPackets().sendInterSetItemsOptionsScript(335, 32, 90, 4, 7, "Remove", "Remove-5", "Remove-10", "Remove-All", "Remove-X", "Value");
		player.getPackets().setIFEvents(new IFEvents(335, 32, 0, 27).enableRightClickOptions(0,1,2,3,4,5,9));
		player.getPackets().sendInterSetItemsOptionsScript(335, 35, 90, true, 4, 7, "Value");
		player.getPackets().setIFEvents(new IFEvents(335, 35, 0, 27).enableRightClickOptions(0,9));
	}

	public boolean isTrading() {
		return target != null;
	}

	public void setTradeModified(boolean modified) {
		if (modified == tradeModified)
			return;
		tradeModified = modified;
		sendTradeModified();
	}

	public void sendInterItems() {
		player.getPackets().sendItems(90, items);
		target.getPackets().sendItems(90, true, items);
	}

	public void refresh(int... slots) {
		player.getPackets().sendUpdateItems(90, items, slots);
		target.getPackets().sendUpdateItems(90, true, items.array(), slots);
	}

	public void accept(boolean firstStage) {
		synchronized (this) {
			if (!isTrading())
				return;
			synchronized (target.getTrade()) {
				if (target.getTrade().accepted) {
					if (firstStage) {
						if (nextStage())
							target.getTrade().nextStage();
					} else {
						player.setCloseInterfacesEvent(null);
						player.closeInterfaces();
						closeTrade(CloseTradeStage.DONE);
					}
					return;
				}
				accepted = true;
				refreshBothStageMessage(firstStage);
			}
		}
	}

	public void sendValue(int slot, boolean traders) {
		if (!isTrading())
			return;
		Item item = traders ? target.getTrade().items.get(slot) : items.get(slot);
		if (item == null)
			return;
		if (!ItemConstants.isTradeable(item)) {
			player.sendMessage("That item isn't tradeable.");
			return;
		}
		int price = EconomyPrices.getPrice(item.getId());
		player.sendMessage(item.getDefinitions().getName() + ": market price is " + price + " coins.");
	}

	public void sendValue(int slot) {
		Item item = player.getInventory().getItem(slot);
		if (item == null)
			return;
		if (!ItemConstants.isTradeable(item)) {
			player.sendMessage("That item isn't tradeable.");
			return;
		}
		int price = EconomyPrices.getPrice(item.getId());
		player.sendMessage(item.getDefinitions().getName() + ": market price is " + price + " coins.");
	}

	public void sendExamine(int slot, boolean traders) {
		if (!isTrading())
			return;
		Item item = traders ? target.getTrade().items.get(slot) : items.get(slot);
		if (item == null)
			return;
		player.sendMessage(ItemConfig.get(item.getId()).getExamine(item));
		if (player.hasRights(Rights.DEVELOPER))
			player.sendMessage("Item: " + (item.getId() + ", "+item.getMetaData()));
	}

	public boolean nextStage() {
		if (!isTrading())
			return false;
		if (!player.getInventory().hasRoomFor(target.getTrade().items.getItemsNoNull())) {
			player.setCloseInterfacesEvent(null);
			player.closeInterfaces();
			closeTrade(CloseTradeStage.NO_SPACE);
			return false;
		}
		accepted = false;
		player.getInterfaceManager().sendInterface(334);
		player.getInterfaceManager().removeInventoryInterface();
		player.getPackets().setIFHidden(334, 55, !(tradeModified || target.getTrade().tradeModified));
		refreshBothStageMessage(false);
		return true;
	}

	public void refreshBothStageMessage(boolean firstStage) {
		refreshStageMessage(firstStage);
		target.getTrade().refreshStageMessage(firstStage);
	}

	public void refreshStageMessage(boolean firstStage) {
		player.getPackets().setIFText(firstStage ? 335 : 334, firstStage ? 39 : 34, getAcceptMessage(firstStage));
	}

	public String getAcceptMessage(boolean firstStage) {
		if (target == null)
			return "";
		if (accepted)
			return "Waiting for other player...";
		if (target.getTrade().accepted)
			return "Other player has accepted.";
		return firstStage ? "" : "Are you sure you want to make this trade?";
	}

	public void sendTradeModified() {
		player.getVars().setVar(1042, tradeModified ? 1 : 0);
		target.getVars().setVar(1043, tradeModified ? 1 : 0);
	}

	public void refreshTradeWealth() {
		int wealth = getTradeWealth();
		player.getPackets().sendVarc(729, wealth);
		target.getPackets().sendVarc(697, wealth);
	}

	public void refreshFreeInventorySlots() {
		int freeSlots = player.getInventory().getFreeSlots();
		target.getPackets().setIFText(335, 23, "has " + (freeSlots == 0 ? "no" : freeSlots) + " free" + "<br>inventory slots");
	}

	public int getTradeWealth() {
		int wealth = 0;
		for (Item item : items.array()) {
			if (item == null)
				continue;
			wealth += EconomyPrices.getPrice(item.getId()) * item.getAmount();
		}
		return wealth;
	}

	public ItemsContainer<Item> getItems() {
		return items;
	}

	private static enum CloseTradeStage {
		CANCEL, NO_SPACE, DONE
	}

	public void closeTrade(CloseTradeStage stage) {
		synchronized (this) {
			synchronized (target.getTrade()) {
				Player oldTarget = target;
				target = null;
				tradeModified = false;
				accepted = false;
				if (CloseTradeStage.DONE != stage) {
					player.getInventory().getItems().addAll(items);
					player.getInventory().init();
					items.clear();
				} else {
					player.sendMessage("Accepted trade.");
					if (!logged && !oldTarget.getTrade().logged) {
						WorldDB.getLogs().logTrade(player, this, oldTarget, oldTarget.getTrade());
						logged = true;
					}
					player.getInventory().getItems().addAll(oldTarget.getTrade().items);
					player.getInventory().init();
					oldTarget.getTrade().items.clear();
				}
				if (oldTarget.getTrade().isTrading()) {
					oldTarget.setCloseInterfacesEvent(null);
					oldTarget.closeInterfaces();
					oldTarget.getTrade().closeTrade(stage);
					if (CloseTradeStage.CANCEL == stage)
						oldTarget.sendMessage("<col=ff0000>Other player declined trade!");
					else if (CloseTradeStage.NO_SPACE == stage) {
						player.sendMessage("You don't have enough space in your inventory for this trade.");
						oldTarget.sendMessage("Other player doesn't have enough space in their inventory for this trade.");
					}
				}
			}
		}
	}

}
