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
package com.rs.game.content.skills.dungeoneering;

import com.rs.cache.loaders.EnumDefinitions;
import com.rs.cache.loaders.ItemDefinitions;
import com.rs.engine.dialogue.HeadE;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Item;

public class DungeonResourceShop {

	public static final int RESOURCE_SHOP = 956, RESOURCE_SHOP_INV = 957;
	private static final int[] CS2MAPS = { 2989, 2991, 2993, 2987 };

	public static void openResourceShop(final Player player, int complexity) {
		if (complexity <= 1) {
			player.npcDialogue(DungeonConstants.SMUGGLER, HeadE.CALM_TALK, "Sorry, but I don't have anything to sell.");
			return;
		}
		player.getPackets().sendVarc(1320, complexity);
		player.getTempAttribs().setI("DUNG_COMPLEXITY", complexity);
		player.getPackets().setIFRightClickOps(RESOURCE_SHOP, 24, 0, 429, 0, 1, 2, 3, 4);
		player.getPackets().setIFRightClickOps(RESOURCE_SHOP_INV, 0, 0, 27, 0, 1, 2, 3, 4, 5);
		player.getPackets().sendInterSetItemsOptionsScript(RESOURCE_SHOP_INV, 0, 93, 4, 7, "Value", "Sell 1", "Sell 5", "Sell 10", "Sell 50", "Examine");
		player.getInterfaceManager().sendInterface(RESOURCE_SHOP);
		player.getInterfaceManager().sendInventoryInterface(RESOURCE_SHOP_INV);
		player.setCloseInterfacesEvent(() -> player.getTempAttribs().removeI("DUNG_COMPLEXITY"));
	}

	public static void handlePurchaseOptions(Player player, int slotId, int quantity) {
		int complexity = player.getTempAttribs().getI("DUNG_COMPLEXITY");
		if (complexity <= 1) // not error, just hacking
			return;
		int baseMap = CS2MAPS[complexity >= 5 ? 3 : complexity - 2];
		int slot = (slotId - 2) / 5;
		EnumDefinitions map = EnumDefinitions.getEnum(baseMap);
		if (slot >= map.getSize()) {
			slot -= map.getSize();
			map = EnumDefinitions.getEnum(baseMap + 1);
		}
		int item = map.getIntValue(slot);
		if (item == -1)
			return;
		ItemDefinitions def = ItemDefinitions.getDefs(item);
		int value = (int) (def.getValue() * def.getDungShopValueMultiplier());
		if (quantity == -1) {
			player.sendMessage(def.getName() + ": currently costs " + value + " coins.");
			return;
		}
		int coinsCount = player.getInventory().getAmountOf(DungeonConstants.RUSTY_COINS);
		int price = value * quantity;
		if (price > coinsCount) {
			quantity = coinsCount / value;
			price = quantity * value;
			player.sendMessage("You don't have enough money to buy that!");
		}
		int openSlots = player.getInventory().getFreeSlots();
		if (!def.isStackable())
			quantity = quantity > openSlots ? openSlots : quantity;
			if (quantity == 0)
				return;
			if (player.getInventory().addItem(item, quantity))
				player.getInventory().deleteItem(new Item(DungeonConstants.RUSTY_COINS, price));
	}

	public static void handleSellOptions(Player player, int slotId, int itemId, int quantity) {
		Item item = player.getInventory().getItem(slotId);
		if (item == null || itemId != item.getId())
			return;
		if (item.getDefinitions().name.contains("key") || item.getDefinitions().name.contains("(b)") || item.getId() == DungeonConstants.RUSTY_COINS || item.getName().toLowerCase().contains("of kinship") || item.getName().toLowerCase().contains("gatestone")) {
			player.sendMessage("You can't sell this item.");
			return;
		}
		ItemDefinitions def = item.getDefinitions();

		int value = (int) ((def.getValue() * def.getDungShopValueMultiplier()) * 0.3D);
		if (quantity == -1) {
			player.sendMessage(def.getName() + ": shop will buy for " + value + ". Right-click the item to sell.");
			return;
		}
		int itemCount = player.getInventory().getAmountOf(item.getId());
		if (quantity > itemCount)
			quantity = itemCount;
		int price = value * quantity;
		player.getInventory().deleteItem(new Item(item.getId(), quantity));
		player.getInventory().addItem(DungeonConstants.RUSTY_COINS, price);
	}
}
