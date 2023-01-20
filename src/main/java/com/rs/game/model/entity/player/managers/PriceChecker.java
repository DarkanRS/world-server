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
package com.rs.game.model.entity.player.managers;

import com.rs.game.content.ItemConstants;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.item.ItemsContainer;
import com.rs.lib.game.Item;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ButtonClickHandler;
import com.rs.utils.EconomyPrices;

@PluginEventHandler
public class PriceChecker {

	public static void openPriceCheck(Player player) {
		player.getTempAttribs().setO("pcContainer", new ItemsContainer<>(28, false));
		player.getTempAttribs().setO("pcInvContainer", new ItemsContainer<>(28, false));
		ItemsContainer<Item> pc = player.getTempAttribs().getO("pcContainer");
		ItemsContainer<Item> inv = player.getTempAttribs().getO("pcInvContainer");
		for (int i = 0;i < player.getInventory().getItems().getItemsCopy().length;i++) {
			Item[] items = player.getInventory().getItems().getItemsCopy();
			inv.set(i, items[i] == null ? null : new Item(items[i]));
		}
		player.getInterfaceManager().sendInterface(206);
		player.getInterfaceManager().sendInventoryInterface(207);
		sendInterItems(player);
		sendOptions(player);
		player.getPackets().sendVarc(728, 0);
		for (int i = 0; i < pc.getSize(); i++)
			player.getPackets().sendVarc(700 + i, 0);
		player.setCloseInterfacesEvent(() -> player.getInventory().init());
	}

	public static int getSlotId(int clickSlotId) {
		return clickSlotId / 2;
	}

	public static void removeItem(Player player, int clickSlotId, int amount) {
		ItemsContainer<Item> pc = player.getTempAttribs().getO("pcContainer");
		ItemsContainer<Item> inv = player.getTempAttribs().getO("pcInvContainer");
		if (pc == null || inv == null)
			return;
		int slot = getSlotId(clickSlotId);
		Item item = pc.get(slot);
		if (item == null)
			return;
		Item[] itemsBefore = pc.getItemsCopy();
		int maxAmount = pc.getNumberOf(item);
		if (amount < maxAmount)
			item = new Item(item.getId(), amount);
		else
			item = new Item(item.getId(), maxAmount);
		pc.remove(slot, item);
		inv.add(item);
		refreshItems(player, itemsBefore);
	}

	public static void addItem(Player player, int slot, int amount) {
		ItemsContainer<Item> pc = player.getTempAttribs().getO("pcContainer");
		ItemsContainer<Item> inv = player.getTempAttribs().getO("pcInvContainer");
		if (pc == null || inv == null)
			return;
		Item item = inv.get(slot);
		if (item == null)
			return;
		if (!ItemConstants.isTradeable(item)) {
			player.sendMessage("That item isn't tradeable.");
			return;
		}
		Item[] itemsBefore = pc.getItemsCopy();
		int maxAmount = inv.getNumberOf(item);
		if (amount < maxAmount)
			item = new Item(item.getId(), amount);
		else
			item = new Item(item.getId(), maxAmount);
		pc.add(item);
		inv.remove(item);
		refreshItems(player, itemsBefore);
	}

	public static void refreshItems(Player player, Item[] itemsBefore) {
		ItemsContainer<Item> pc = player.getTempAttribs().getO("pcContainer");
		ItemsContainer<Item> inv = player.getTempAttribs().getO("pcInvContainer");
		if (pc == null || inv == null)
			return;
		int totalPrice = 0;
		int[] changedSlots = new int[itemsBefore.length];
		int count = 0;
		for (int index = 0; index < itemsBefore.length; index++) {
			Item item = pc.array()[index];
			if (item != null)
				totalPrice += EconomyPrices.getPrice(item.getId()) * item.getAmount();
			if (itemsBefore[index] != item) {
				changedSlots[count++] = index;
				player.getPackets().sendVarc(700 + index, item == null ? 0 : EconomyPrices.getPrice(item.getId()));
			}

		}
		int[] finalChangedSlots = new int[count];
		System.arraycopy(changedSlots, 0, finalChangedSlots, 0, count);
		refresh(player, finalChangedSlots);
		player.getPackets().sendVarc(728, totalPrice);
	}

	public static void refresh(Player player, int... slots) {
		ItemsContainer<Item> pc = player.getTempAttribs().getO("pcContainer");
		ItemsContainer<Item> inv = player.getTempAttribs().getO("pcInvContainer");
		if (pc == null || inv == null)
			return;
		player.getPackets().sendUpdateItems(90, pc, slots);
		player.getPackets().sendItems(93, inv);
	}

	public static void sendOptions(Player player) {
		player.getPackets().setIFRightClickOps(206, 15, 0, 54, 0, 1, 2, 3, 4, 5, 6);
		player.getPackets().setIFRightClickOps(207, 0, 0, 27, 0, 1, 2, 3, 4, 5);
		player.getPackets().sendInterSetItemsOptionsScript(207, 0, 93, 4, 7, "Add", "Add-5", "Add-10", "Add-All", "Add-X", "Examine");
	}

	public static void sendInterItems(Player player) {
		ItemsContainer<Item> pc = player.getTempAttribs().getO("pcContainer");
		ItemsContainer<Item> inv = player.getTempAttribs().getO("pcInvContainer");
		if (pc == null || inv == null)
			return;
		player.getPackets().sendItems(90, pc);
		player.getPackets().sendItems(93, inv);
	}

	public static ButtonClickHandler handleButtons = new ButtonClickHandler(new Object[] { 206, 207 }, e -> {
		if (e.getInterfaceId() == 206) {
			if (e.getComponentId() == 15)
				switch(e.getPacket()) {
				case IF_OP1:
					removeItem(e.getPlayer(), e.getSlotId(), 1);
					break;
				case IF_OP2:
					removeItem(e.getPlayer(), e.getSlotId(), 5);
					break;
				case IF_OP3:
					removeItem(e.getPlayer(), e.getSlotId(), 10);
					break;
				case IF_OP4:
					removeItem(e.getPlayer(), e.getSlotId(), Integer.MAX_VALUE);
					break;
				case IF_OP5:
					e.getPlayer().sendInputInteger("Enter Amount:", amount -> removeItem(e.getPlayer(), e.getSlotId(), amount));
					break;
				default:
					break;
				}
		} else if (e.getInterfaceId() == 207)
			if (e.getComponentId() == 0)
				switch(e.getPacket()) {
				case IF_OP1:
					addItem(e.getPlayer(), e.getSlotId(), 1);
					break;
				case IF_OP2:
					addItem(e.getPlayer(), e.getSlotId(), 5);
					break;
				case IF_OP3:
					addItem(e.getPlayer(), e.getSlotId(), 10);
					break;
				case IF_OP4:
					addItem(e.getPlayer(), e.getSlotId(), Integer.MAX_VALUE);
					break;
				case IF_OP5:
					e.getPlayer().sendInputInteger("Enter Amount:", amount -> addItem(e.getPlayer(), e.getSlotId(), amount));
					break;
				case IF_OP6:
					e.getPlayer().getInventory().sendExamine(e.getSlotId());
					break;
				default:
					break;
				}
	});
}
