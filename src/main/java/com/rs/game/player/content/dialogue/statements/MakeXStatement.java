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
package com.rs.game.player.content.dialogue.statements;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.player.Player;
import com.rs.game.player.content.dialogue.impl.skilling.MakeXItem;

public class MakeXStatement implements Statement {

	public enum MakeXType {
		MAKE,
		MAKE_SET,
		COOK,
		ROAST,
		OFFER,
		SELL,
		BAKE,
		CUT,
		DEPOSIT,
		MAKE2,
		TELEPORT,
		SELECT,
		MAKE_SET2,
		TAKE,
		RETURN,
		HEAT,
		ADD
	}

	private MakeXType type;
	private int maxQuantity = -1;
	private String question;
	private int[] items;
	private String[] options;

	public MakeXStatement(MakeXType type, int maxQuantity, String question, int[] items, String[] options) {
		this.type = type;
		this.maxQuantity = maxQuantity;
		this.question = question;
		this.items = items;
		this.options = options;
	}

	public MakeXStatement(MakeXItem[] items, int maxQuantity) {
		this(MakeXType.MAKE, maxQuantity, "How many would you like to make?", null, null);
		int[] itemIds = new int[items.length];
		for (int i = 0;i < items.length;i++)
			itemIds[i] = items[i].getItemId();
		this.items = itemIds;
	}

	public MakeXStatement(int[] items, int maxQuantity) {
		this(MakeXType.MAKE, maxQuantity, "How many would you like to make?", items, null);
	}

	public MakeXStatement(int[] items, String[] options) {
		this(MakeXType.SELECT, -1, "Select an item.", items, options);
	}

	public MakeXStatement(int[] items) {
		this(MakeXType.SELECT, -1, "Select an item.", items, null);
	}

	@Override
	public void send(Player player) {
		player.getPackets().setIFRightClickOps(916, 8, -1, 0, 0); // unlocks all option
		player.getPackets().setIFText(916, 6, question);
		player.getPackets().sendVarc(754, type.ordinal());
		for (int i = 0; i < 10; i++) {
			if (i >= items.length) {
				player.getPackets().sendVarc(i >= 6 ? (1139 + i - 6) : 755 + i, -1);
				continue;
			}
			player.getPackets().sendVarc(i >= 6 ? (1139 + i - 6) : 755 + i, items[i]);
			player.getPackets().sendVarcString(i >= 6 ? (280 + i - 6) : 132 + i, options != null ? options[i] : ItemDefinitions.getDefs(items[i]).getName());
		}
		setMaxQuantity(player, maxQuantity);
		setQuantity(player, maxQuantity);
		player.getInterfaceManager().sendChatBoxInterface(905);
		player.getInterfaceManager().setInterface(true, 905, 4, 916);
	}

	@Override
	public int getOptionId(int componentId) {
		if (componentId < 14)
			return 0;
		if (componentId == 26)
			return 7;
		if (componentId >= 21)
			return componentId - 13;
		return componentId - 14;
	}

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

}
