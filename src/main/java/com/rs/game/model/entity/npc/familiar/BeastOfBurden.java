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
package com.rs.game.model.entity.npc.familiar;

import com.rs.game.World;
import com.rs.game.content.ItemConstants;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.item.ItemsContainer;
import com.rs.lib.game.Item;
import com.rs.lib.game.WorldTile;
import com.rs.lib.net.ClientPacket;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ButtonClickEvent;
import com.rs.plugin.handlers.ButtonClickHandler;

@PluginEventHandler
public class BeastOfBurden {

	private static final int ITEMS_KEY = 530;

	private transient Player player;
	private transient Familiar familiar;

	private ItemsContainer<Item> beastItems;

	public BeastOfBurden(int size) {
		beastItems = new ItemsContainer<>(size, false);
	}

	public void setEntitys(Player player, Familiar familiar) {
		this.player = player;
		this.familiar = familiar;
	}

	public static ButtonClickHandler handleInvInter = new ButtonClickHandler(665) {
		@Override
		public void handle(ButtonClickEvent e) {
			if (e.getPlayer().getFamiliar() == null || e.getPlayer().getFamiliar().getBob() == null)
				return;
			if (e.getComponentId() == 0)
				if (e.getPacket() == ClientPacket.IF_OP1)
					e.getPlayer().getFamiliar().getBob().addItem(e.getSlotId(), 1);
				else if (e.getPacket() == ClientPacket.IF_OP2)
					e.getPlayer().getFamiliar().getBob().addItem(e.getSlotId(), 5);
				else if (e.getPacket() == ClientPacket.IF_OP3)
					e.getPlayer().getFamiliar().getBob().addItem(e.getSlotId(), 10);
				else if (e.getPacket() == ClientPacket.IF_OP4)
					e.getPlayer().getFamiliar().getBob().addItem(e.getSlotId(), Integer.MAX_VALUE);
				else if (e.getPacket() == ClientPacket.IF_OP5)
					e.getPlayer().sendInputInteger("Enter Amount:", num -> e.getPlayer().getFamiliar().getBob().addItem(e.getSlotId(), num));
				else if (e.getPacket() == ClientPacket.IF_OP6)
					e.getPlayer().getInventory().sendExamine(e.getSlotId());
		}
	};

	public static ButtonClickHandler handleInter = new ButtonClickHandler(671) {
		@Override
		public void handle(ButtonClickEvent e) {
			if (e.getPlayer().getFamiliar() == null || e.getPlayer().getFamiliar().getBob() == null)
				return;
			if (e.getComponentId() == 27) {
				if (e.getPacket() == ClientPacket.IF_OP1)
					e.getPlayer().getFamiliar().getBob().removeItem(e.getSlotId(), 1);
				else if (e.getPacket() == ClientPacket.IF_OP2)
					e.getPlayer().getFamiliar().getBob().removeItem(e.getSlotId(), 5);
				else if (e.getPacket() == ClientPacket.IF_OP3)
					e.getPlayer().getFamiliar().getBob().removeItem(e.getSlotId(), 10);
				else if (e.getPacket() == ClientPacket.IF_OP4)
					e.getPlayer().getFamiliar().getBob().removeItem(e.getSlotId(), Integer.MAX_VALUE);
				else if (e.getPacket() == ClientPacket.IF_OP5)
					e.getPlayer().sendInputInteger("Enter Amount:", num -> e.getPlayer().getFamiliar().getBob().removeItem(e.getSlotId(), num));
			} else if (e.getComponentId() == 29)
				e.getPlayer().getFamiliar().takeBob();
		}
	};

	public void open() {
		player.getInterfaceManager().sendInterface(671);
		player.getInterfaceManager().sendInventoryInterface(665);
		sendInterItems();
		sendOptions();
	}

	public void dropBob() {
		if (familiar == null)
			return;
		int size = familiar.getSize();
		WorldTile tile = new WorldTile(familiar.getCoordFaceX(size), familiar.getCoordFaceY(size), familiar.getPlane());
		for (int i = 0; i < beastItems.getSize(); i++) {
			Item item = beastItems.get(i);
			if (item != null)
				World.addGroundItem(item, tile, player, false, -1);
		}
		beastItems.reset();
	}

	public void takeBob() {
		Item[] itemsBefore = beastItems.getItemsCopy();
		for (int i = 0; i < beastItems.getSize(); i++) {
			Item item = beastItems.get(i);
			if (item != null) {
				if (!player.getInventory().addItem(item))
					break;
				beastItems.remove(i, item);
			}
		}
		beastItems.shift();
		refreshItems(itemsBefore);
	}

	public void removeItem(int slot, int amount) {
		Item item = beastItems.get(slot);
		if (item == null)
			return;
		Item[] itemsBefore = beastItems.getItemsCopy();
		int maxAmount = beastItems.getNumberOf(item);
		if (amount < maxAmount)
			item = new Item(item.getId(), amount);
		else
			item = new Item(item.getId(), maxAmount);
		int freeSpace = player.getInventory().getFreeSlots();
		if (!item.getDefinitions().isStackable()) {
			if (freeSpace == 0) {
				player.sendMessage("Not enough space in your inventory.");
				return;
			}
			if (freeSpace < item.getAmount()) {
				item.setAmount(freeSpace);
				player.sendMessage("Not enough space in your inventory.");
			}
		} else if (freeSpace == 0 && !player.getInventory().containsItem(item.getId(), 1)) {
			player.sendMessage("Not enough space in your inventory.");
			return;
		}
		beastItems.remove(slot, item);
		beastItems.shift();
		player.getInventory().addItem(item);
		refreshItems(itemsBefore);
	}

	public void addItem(int slot, int amount) {
		Item item = player.getInventory().getItem(slot);
		if (item == null)
			return;
		if (!ItemConstants.isTradeable(item) || item.getId() == 4049 || (familiar.canStoreEssOnly() && item.getId() != 1436 && item.getId() != 7936) || item.getDefinitions().getValue() > 50000) {
			player.sendMessage("You cannot store this item.");
			return;
		}
		Item[] itemsBefore = beastItems.getItemsCopy();
		int maxAmount = player.getInventory().getItems().getNumberOf(item);
		if (amount < maxAmount)
			item = new Item(item.getId(), amount);
		else
			item = new Item(item.getId(), maxAmount);
		int freeSpace = beastItems.getFreeSlots();
		if (!item.getDefinitions().isStackable()) {
			if (freeSpace == 0) {
				player.sendMessage("Not enough space in your Familiar Inventory.");
				return;
			}

			if (freeSpace < item.getAmount()) {
				item.setAmount(freeSpace);
				player.sendMessage("Not enough space in your Familiar Inventory.");
			}
		} else if (freeSpace == 0 && !beastItems.containsOne(item)) {
			player.sendMessage("Not enough space in your Familiar Inventory.");
			return;
		}
		beastItems.add(item);
		beastItems.shift();
		player.getInventory().deleteItem(slot, item);
		refreshItems(itemsBefore);
	}

	public void refreshItems(Item[] itemsBefore) {
		int[] changedSlots = new int[itemsBefore.length];
		int count = 0;
		for (int index = 0; index < itemsBefore.length; index++) {
			Item item = beastItems.array()[index];
			if (itemsBefore[index] != item)
				changedSlots[count++] = index;

		}
		int[] finalChangedSlots = new int[count];
		System.arraycopy(changedSlots, 0, finalChangedSlots, 0, count);
		refresh(finalChangedSlots);
	}

	public void refresh(int... slots) {
		player.getPackets().sendUpdateItems(ITEMS_KEY, beastItems, slots);
	}

	public void sendOptions() {
		player.getPackets().setIFRightClickOps(665, 0, 0, 27, 0, 1, 2, 3, 4, 5);
		player.getPackets().sendInterSetItemsOptionsScript(665, 0, 93, 4, 7, "Store", "Store-5", "Store-10", "Store-All", "Store-X", "Examine");
		player.getPackets().setIFRightClickOps(671, 27, 0, ITEMS_KEY, 0, 1, 2, 3, 4, 5);
		player.getPackets().sendInterSetItemsOptionsScript(671, 27, ITEMS_KEY, 6, 5, "Withdraw", "Withdraw-5", "Withdraw-10", "Withdraw-All", "Withdraw-X", "Examine");
	}

	public boolean containsOneItem(int... itemIds) {
		for (int itemId : itemIds)
			if (beastItems.containsOne(new Item(itemId, 1)))
				return true;
		return false;
	}

	public void sendInterItems() {
		player.getPackets().sendItems(ITEMS_KEY, beastItems);
		player.getPackets().sendItems(93, player.getInventory().getItems());
	}

	public ItemsContainer<Item> getBeastItems() {
		return beastItems;
	}
}
