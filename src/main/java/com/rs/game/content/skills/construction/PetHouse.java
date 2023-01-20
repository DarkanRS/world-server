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
package com.rs.game.content.skills.construction;

import com.rs.game.content.pets.Pets;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.item.ItemsContainer;
import com.rs.lib.game.Item;
import com.rs.lib.game.Rights;
import com.rs.lib.net.ClientPacket;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ButtonClickHandler;

@PluginEventHandler
public class PetHouse {

	private static final int ITEMS_KEY = 540;
	private static final int INTERFACE_ID = 879;
	private static final int INV_INTERFACE_ID = 878;

	private transient Player player;
	private ItemsContainer<Item> pets;

	public PetHouse() {
		pets = new ItemsContainer<>(72, false);
	}

	public static ButtonClickHandler handleInvInterface = new ButtonClickHandler(INV_INTERFACE_ID, e -> {
		if (e.getComponentId() == 0)
			if (e.getPacket() == ClientPacket.IF_OP1)
				e.getPlayer().getHouse().getPetHouse().addItem(e.getSlotId());
			else if (e.getPacket() == ClientPacket.IF_OP2)
				e.getPlayer().getInventory().sendExamine(e.getSlotId());
	});

	public static ButtonClickHandler handleInterfaceButtons = new ButtonClickHandler(INTERFACE_ID, e -> {
		if (e.getComponentId() == 13)
			if (e.getPacket() == ClientPacket.IF_OP1)
				e.getPlayer().getHouse().getPetHouse().removeItem(e.getSlotId());
	});

	public void open() {
		player.getInterfaceManager().sendInterface(INTERFACE_ID);
		player.getInterfaceManager().sendInventoryInterface(INV_INTERFACE_ID);
		player.getPackets().setIFRightClickOps(INV_INTERFACE_ID, 0, 0, 27, 0, 1);
		player.getPackets().sendInterSetItemsOptionsScript(INV_INTERFACE_ID, 0, 93, 4, 7, "Store", "Examine");
		player.getPackets().setIFRightClickOps(INTERFACE_ID, 13, 0, ITEMS_KEY, 0, 1);
		player.getPackets().sendInterSetItemsOptionsScript(INTERFACE_ID, 13, ITEMS_KEY, 12, 6, "Withdraw", "Examine");
		refresh();
	}

	public void refresh() {
		player.getPackets().sendItems(ITEMS_KEY, pets);
		player.getPackets().sendItems(93, player.getInventory().getItems());
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public void removeItem(int slot) {
		Item item = pets.get(slot);
		if (item == null)
			return;
		item = new Item(item.getId(), 1);
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
		player.getHouse().removePet(item, true);
		pets.remove(slot, item);
		pets.shift();
		player.getInventory().addItem(item);
		refresh();
	}

	public void addItem(int slot) {
		int usedSlots = pets.getSize()-pets.freeSlots();
		if (usedSlots >= 25 && !player.hasRights(Rights.ADMIN)) {
			player.sendMessage("You may only hold up to 25 pets.");
			return;
		}
		Item item = player.getInventory().getItem(slot);
		if (item == null)
			return;
		if (Pets.forId(item.getId()) == null) {
			player.sendMessage("You cannot store this item.");
			return;
		}
		item = new Item(item.getId(), 1);
		int freeSpace = pets.getFreeSlots();
		if (!item.getDefinitions().isStackable()) {
			if (freeSpace == 0) {
				player.sendMessage("Not enough space in your Familiar Inventory.");
				return;
			}

			if (freeSpace < item.getAmount()) {
				item.setAmount(freeSpace);
				player.sendMessage("Not enough space in your Familiar Inventory.");
			}
		} else if (freeSpace == 0 && !pets.containsOne(item)) {
			player.sendMessage("Not enough space in your Familiar Inventory.");
			return;
		}
		player.getHouse().addPet(item, true);
		pets.add(item);
		pets.shift();
		player.getInventory().deleteItem(slot, item);
		refresh();
	}

	public ItemsContainer<Item> getPets() {
		return pets;
	}

	public boolean contains(int itemId) {
		if (pets.containsOne(new Item(itemId, 1)))
			return true;
		return false;
	}

}
