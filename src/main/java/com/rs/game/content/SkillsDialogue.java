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
package com.rs.game.content;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.content.skills.util.ReqItem;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Item;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ButtonClickEvent;
import com.rs.plugin.handlers.ButtonClickHandler;

@PluginEventHandler
public final class SkillsDialogue {

	public static final int MAKE_ALL = 0, MAKE_SETS = 1, COOK = 2, ROAST = 3, OFFER = 4, SELL = 5, BAKE = 6, CUT = 7, DEPOSIT = 8, MAKE_INTERVAL = 9, TELEPORT = 10, SELECT = 11, TAKE = 13;

	public static interface ItemNameFilter {

		public String rename(String name);
	}

	public static void sendSkillsDialogue(Player player, int option, String explanation, int maxQuantity, Item[] items, ItemNameFilter filter) {
		int[] itemIds = new int[items.length];
		for (int i = 0; i < items.length; i++)
			itemIds[i] = items[i].getId();
		sendSkillsDialogue(player, option, explanation, maxQuantity, itemIds, filter, true);
	}

	public static void sendSkillsDialogue(Player player, int option, String explanation, int maxQuantity, ReqItem[] items, ItemNameFilter filter) {
		int[] itemIds = new int[items.length];
		for (int i = 0; i < items.length; i++)
			itemIds[i] = items[i].getProduct().getId();
		sendSkillsDialogue(player, option, explanation, maxQuantity, itemIds, filter, true);
	}

	public static void sendSkillsDialogue(Player player, int option, String explanation, int maxQuantity, int[] items, ItemNameFilter filter) {
		sendSkillsDialogue(player, option, explanation, maxQuantity, items, filter, true);
	}

	public static void sendSkillsDialogue(Player player, int option, String explanation, int maxQuantity, int[] items, ItemNameFilter filter, boolean sendQuantitySelector) {
		if (!sendQuantitySelector)
			maxQuantity = -1;
		else if (option != MAKE_SETS && option != MAKE_INTERVAL)
			player.getPackets().setIFRightClickOps(916, 8, -1, 0, 0);
		player.getPackets().setIFText(916, 6, explanation);
		player.getPackets().sendVarc(754, option);
		for (int i = 0; i < 10; i++) {
			if (i >= items.length) {
				player.getPackets().sendVarc(i >= 6 ? (1139 + i - 6) : 755 + i, -1);
				continue;
			}
			player.getPackets().sendVarc(i >= 6 ? (1139 + i - 6) : 755 + i, items[i]);
			String name = ItemDefinitions.getDefs(items[i]).getName();
			if (filter != null)
				name = filter.rename(name);
			player.getPackets().sendVarcString(i >= 6 ? (280 + i - 6) : 132 + i, name);
		}
		setMaxQuantity(player, maxQuantity);
		setQuantity(player, maxQuantity);
		player.getInterfaceManager().sendChatBoxInterface(905);
		player.getInterfaceManager().sendSubSpecific(true, 905, 4, 916);
	}

	public static ButtonClickHandler handleSetQuantityButtons = new ButtonClickHandler(916) {
		@Override
		public void handle(ButtonClickEvent e) {
			if (e.getComponentId() == 10)
				setQuantity(e.getPlayer(), 1, true);
			else if (e.getComponentId() == 11)
				setQuantity(e.getPlayer(), 5, true);
			else if (e.getComponentId() == 12)
				setQuantity(e.getPlayer(), 10, true);
			else if (e.getComponentId() == 8)
				setQuantity(e.getPlayer(), getMaxQuantity(e.getPlayer()), true);
			else if (e.getComponentId() == 24)
				setQuantity(e.getPlayer(), getQuantity(e.getPlayer()) + 1, true);
			else if (e.getComponentId() == 25)
				setQuantity(e.getPlayer(), getQuantity(e.getPlayer()) - 1, true);
		}
	};

	public static void setMaxQuantity(Player player, int maxQuantity) {
		player.getVars().setVarBit(8094, maxQuantity);
	}

	public static void setQuantity(Player player, int quantity) {
		setQuantity(player, quantity, true);
	}

	public static void setQuantity(Player player, int quantity, boolean refresh) {
		int maxQuantity = getMaxQuantity(player);
		if (quantity > maxQuantity)
			quantity = maxQuantity;
		else if (quantity < 0)
			quantity = 0;
		if (refresh)
			player.getVars().setVarBit(8095, quantity);
	}

	public static int getMaxQuantity(Player player) {
		return player.getVars().getVarBit(8094);
	}

	public static int getQuantity(Player player) {
		return player.getVars().getVarBit(8095);
	}

	public static int getItemSlot(int componentId) {
		if (componentId < 14)
			return 0;
		if (componentId == 26)
			return 7;
		if (componentId >= 21)
			return componentId - 13;
		return componentId - 14;
	}

	private SkillsDialogue() {

	}
}
