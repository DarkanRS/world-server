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
package com.rs.game.content.skills.crafting;

import java.util.HashMap;
import java.util.Map;

import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Item;
import com.rs.lib.game.Rights;
import com.rs.lib.net.ClientPacket;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ButtonClickHandler;

@PluginEventHandler
public class Silver {

	public static final int SILVER_BAR = 2355;

	public enum SilverItems {

		HOLY_SYMBOL(16, 50, 1599, new Item(1714, 1), 16),
		UNHOLY_SYMBOL(17, 50, 1594, new Item(1720, 1), 23),
		SILVER_SICKLE(18, 50, 2976, new Item(2961, 1), 30),
		LIGHTNING_ROD(20, 50, 4200, new Item(4201, 1), 37),
		DEMONIC_SIGIL(20, 50, 6747, new Item(6748, 1), 59),
		SILVER_BOLTS(21, 50, 9434, new Item(9382, 10), 66),
		TIARA(23, 52.5, 5523, new Item(5525), 44);

		private static Map<Integer, SilverItems> rings = new HashMap<>();

		public static SilverItems forId(int buttonId) {
			return rings.get(buttonId);
		}

		static {
			for (SilverItems ring : SilverItems.values())
				rings.put(ring.getButtonId(), ring);
		}

		private int levelRequired;
		private double experience;
		private int mould;
		private int buttonId;
		private Item product;

		private SilverItems(int levelRequired, double experience, int itemsRequired, Item producedBar, int buttonId) {
			this.levelRequired = levelRequired;
			this.experience = experience;
			mould = itemsRequired;
			product = producedBar;
			this.buttonId = buttonId;
		}

		public int getMouldRequired() {
			return mould;
		}

		public int getLevelRequired() {
			return levelRequired;
		}

		public Item getProduct() {
			return product;
		}

		public double getExperience() {
			return experience;
		}

		public int getButtonId() {
			return buttonId;
		}
	}

	public static void openSilverInterface(Player player) {
		player.getInterfaceManager().sendInterface(438);
		for (SilverItems item : SilverItems.values())
			player.getPackets().setIFItem(438, item.getButtonId()+1, item.getProduct().getId(), item.getProduct().getAmount());
	}

	public static ButtonClickHandler handleButtons = new ButtonClickHandler(438, e -> {
		e.getPlayer().closeInterfaces();
		SilverItems silver = SilverItems.forId(e.getComponentId());
		int numberToMake = getNumberToMake(e.getPacket());
		if (numberToMake == -5)
			e.getPlayer().sendInputInteger("How many would you like to make?", number -> {
				if (silver != null)
					e.getPlayer().getActionManager().setAction(new SilverCraftingAction(silver, number));
				else if (e.getPlayer().hasRights(Rights.DEVELOPER))
					e.getPlayer().sendMessage("SILVER: component: " + e.getComponentId() + " packetId: " + e.getPacket());
				else
					e.getPlayer().sendMessage("You are unable to craft this item at the moment.");
			});
		else if (silver != null)
			e.getPlayer().getActionManager().setAction(new SilverCraftingAction(silver, numberToMake));
		else if (e.getPlayer().hasRights(Rights.DEVELOPER))
			e.getPlayer().sendMessage("SILVER: component: " + e.getComponentId() + " packetId: " + e.getPacket());
		else
			e.getPlayer().sendMessage("You are unable to craft this item at the moment.");
	});

	public static int getNumberToMake(ClientPacket packetId) {
		switch (packetId) {
		case IF_OP1:
			return 1;
		case IF_OP2:
			return 5;
		case IF_OP3:
			return 28;
		case IF_OP4:
			return -5;
		default:
			return 1;
		}
	}

}
