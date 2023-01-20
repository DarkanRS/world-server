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
public class Jewelry {

	public static int RING_MOULD = 1592;
	public static int AMULET_MOULD = 1595;
	public static int NECKLACE_MOULD = 1597;
	public static int BRACELET_MOULD = 11065;

	public static int GOLD_BAR = 2357;

	public static int SAPPHIRE = 1607;
	public static int EMERALD = 1605;
	public static int RUBY = 1603;
	public static int DIAMOND = 1601;
	public static int DRAGONSTONE = 1615;
	public static int ONYX = 6573;
	public static int ENCHANTED_GEM = 4155;


	public enum Bling {

		GOLD_RING(5, 15, new Item[] { new Item(RING_MOULD), new Item(GOLD_BAR) }, new Item(1635), 82), SAPP_RING(20, 40, new Item[] { new Item(RING_MOULD), new Item(GOLD_BAR), new Item(SAPPHIRE) }, new Item(1637), 84), EMER_RING(27, 55, new Item[] {
				new Item(RING_MOULD), new Item(GOLD_BAR), new Item(EMERALD) }, new Item(1639), 86), RUBY_RING(34, 70, new Item[] { new Item(RING_MOULD), new Item(GOLD_BAR), new Item(RUBY) }, new Item(1641), 88), DIAM_RING(43, 85, new Item[] {
						new Item(RING_MOULD), new Item(GOLD_BAR), new Item(DIAMOND) }, new Item(1643), 90), DRAG_RING(55, 100, new Item[] { new Item(RING_MOULD), new Item(GOLD_BAR), new Item(DRAGONSTONE) }, new Item(1645), 92), ONYX_RING(67, 115,
								new Item[] { new Item(RING_MOULD), new Item(GOLD_BAR), new Item(ONYX) }, new Item(6575), 94), SLAYER_RING(55, 15, new Item[] { new Item(RING_MOULD), new Item(GOLD_BAR), new Item(ENCHANTED_GEM) }, new Item(13281), 97),

		GOLD_NECK(6, 20, new Item[] { new Item(NECKLACE_MOULD), new Item(GOLD_BAR) }, new Item(1654), 68), SAPP_NECK(22, 55, new Item[] { new Item(NECKLACE_MOULD), new Item(GOLD_BAR), new Item(SAPPHIRE) }, new Item(1656), 70), EMER_NECK(29, 60,
				new Item[] { new Item(NECKLACE_MOULD), new Item(GOLD_BAR), new Item(EMERALD) }, new Item(1658), 72), RUBY_NECK(40, 75, new Item[] { new Item(NECKLACE_MOULD), new Item(GOLD_BAR), new Item(RUBY) }, new Item(1660), 74), DIAM_NECK(56,
						90, new Item[] { new Item(NECKLACE_MOULD), new Item(GOLD_BAR), new Item(DIAMOND) }, new Item(1662), 76), DRAG_NECK(72, 105, new Item[] { new Item(NECKLACE_MOULD), new Item(GOLD_BAR), new Item(DRAGONSTONE) }, new Item(1664), 78), ONYX_NECK(
								82, 120, new Item[] { new Item(NECKLACE_MOULD), new Item(GOLD_BAR), new Item(ONYX) }, new Item(6577), 80),

		GOLD_AMMY(8, 30, new Item[] { new Item(AMULET_MOULD), new Item(GOLD_BAR) }, new Item(1673), 53), SAPP_AMMY(24, 65, new Item[] { new Item(AMULET_MOULD), new Item(GOLD_BAR), new Item(SAPPHIRE) }, new Item(1675), 55), EMER_AMMY(31, 70,
				new Item[] { new Item(AMULET_MOULD), new Item(GOLD_BAR), new Item(EMERALD) }, new Item(1677), 57), RUBY_AMMY(50, 85, new Item[] { new Item(AMULET_MOULD), new Item(GOLD_BAR), new Item(RUBY) }, new Item(1679), 59), DIAM_AMMY(70, 100,
						new Item[] { new Item(AMULET_MOULD), new Item(GOLD_BAR), new Item(DIAMOND) }, new Item(1681), 61), DRAG_AMMY(80, 150, new Item[] { new Item(AMULET_MOULD), new Item(GOLD_BAR), new Item(DRAGONSTONE) }, new Item(1683), 63), ONYX_AMMY(
								90, 165, new Item[] { new Item(AMULET_MOULD), new Item(GOLD_BAR), new Item(ONYX) }, new Item(6579), 65),

		GOLD_BRACE(7, 25, new Item[] { new Item(BRACELET_MOULD), new Item(GOLD_BAR) }, new Item(11069), 33), SAPP_BRACE(23, 60, new Item[] { new Item(BRACELET_MOULD), new Item(GOLD_BAR), new Item(SAPPHIRE) }, new Item(11072), 35), EMER_BRACE(30, 65,
				new Item[] { new Item(BRACELET_MOULD), new Item(GOLD_BAR), new Item(EMERALD) }, new Item(11076), 37), RUBY_BRACE(42, 80, new Item[] { new Item(BRACELET_MOULD), new Item(GOLD_BAR), new Item(RUBY) }, new Item(11085), 39), DIAM_BRACE(
						58, 95, new Item[] { new Item(BRACELET_MOULD), new Item(GOLD_BAR), new Item(DIAMOND) }, new Item(11092), 41), DRAG_BRACE(74, 110, new Item[] { new Item(BRACELET_MOULD), new Item(GOLD_BAR), new Item(DRAGONSTONE) }, new Item(11115), 43), ONYX_BRACE(
								84, 125, new Item[] { new Item(BRACELET_MOULD), new Item(GOLD_BAR), new Item(ONYX) }, new Item(11130), 45);

		private static Map<Integer, Bling> rings = new HashMap<>();

		public static Bling forId(int buttonId) {
			return rings.get(buttonId);
		}

		static {
			for (Bling ring : Bling.values())
				rings.put(ring.getButtonId(), ring);
		}

		private int levelRequired;
		private double experience;
		private Item[] itemsRequired;
		private int buttonId;
		private Item product;

		private Bling(int levelRequired, double experience, Item[] itemsRequired, Item producedBar, int buttonId) {
			this.levelRequired = levelRequired;
			this.experience = experience;
			this.itemsRequired = itemsRequired;
			product = producedBar;
			this.buttonId = buttonId;
		}

		public Item[] getItemsRequired() {
			Item[] req = new Item[itemsRequired.length - 1];
			for (int i = 1; i < itemsRequired.length; i++)
				req[i - 1] = itemsRequired[i];
			return req;
		}

		public Item getMouldRequired() {
			return itemsRequired[0];
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

	public static ButtonClickHandler handleButtons = new ButtonClickHandler(446, e -> {
		Bling bling = Bling.forId(e.getComponentId());
		int numberToMake = getNumberToMake(e.getPacket());
		if (numberToMake == -5)
			e.getPlayer().sendInputInteger("How many would you like to make?", number -> {
				if (bling != null) {
					if (bling.name() == "SLAYER_RING")
						if (!e.getPlayer().hasCraftROS()) {
							e.getPlayer().sendMessage("You have not unlocked the ability to craft this. Purchase the ability from a slayer master.");
							return;
						}
					e.getPlayer().getActionManager().setAction(new JewelryAction(bling, number, e.getPlayer().getTempAttribs().getB("immenseHeatCrafting")));
				} else if (e.getPlayer().hasRights(Rights.DEVELOPER))
					e.getPlayer().sendMessage("JEWELRY: component: " + e.getComponentId() + " packetId: " + e.getPacket());
			});
		else if (bling != null) {
			if (bling.name() == "SLAYER_RING")
				if (!e.getPlayer().hasCraftROS()) {
					e.getPlayer().sendMessage("You have not unlocked the ability to craft this. Purchase the ability from a slayer master.");
					return;
				}
			e.getPlayer().getActionManager().setAction(new JewelryAction(bling, numberToMake, e.getPlayer().getTempAttribs().getB("immenseHeatCrafting")));
		} else if (e.getPlayer().hasRights(Rights.DEVELOPER))
			e.getPlayer().sendMessage("JEWELRY: component: " + e.getComponentId() + " packetId: " + e.getPacket());
		e.getPlayer().closeInterfaces();
	});

	public static void openJewelryInterface(Player player, boolean pyrefiend) {
		player.getInterfaceManager().sendInterface(446);
		player.getPackets().setIFHidden(446, 17, true);
		player.getPackets().setIFHidden(446, 21, true);
		player.getPackets().setIFHidden(446, 26, true);
		player.getPackets().setIFHidden(446, 30, true);

		// Rings
		player.getPackets().setIFItem(446, 81, 1635, 75);
		player.getPackets().setIFItem(446, 83, 1637, 75);
		player.getPackets().setIFItem(446, 85, 1639, 75);
		player.getPackets().setIFItem(446, 87, 1641, 75);
		player.getPackets().setIFItem(446, 89, 1643, 75);
		player.getPackets().setIFItem(446, 91, 1645, 75);
		player.getPackets().setIFItem(446, 93, 6575, 75);
		player.getPackets().setIFItem(446, 96, 13281, 75);

		// Necklaces
		player.getPackets().setIFItem(446, 67, 1654, 75);
		player.getPackets().setIFItem(446, 69, 1656, 75);
		player.getPackets().setIFItem(446, 71, 1658, 75);
		player.getPackets().setIFItem(446, 73, 1660, 75);
		player.getPackets().setIFItem(446, 75, 1662, 75);
		player.getPackets().setIFItem(446, 77, 1664, 75);
		player.getPackets().setIFItem(446, 79, 6577, 75);

		// Amulets
		player.getPackets().setIFItem(446, 52, 1673, 75);
		player.getPackets().setIFItem(446, 54, 1675, 75);
		player.getPackets().setIFItem(446, 56, 1677, 75);
		player.getPackets().setIFItem(446, 58, 1679, 75);
		player.getPackets().setIFItem(446, 60, 1681, 75);
		player.getPackets().setIFItem(446, 62, 1683, 75);
		player.getPackets().setIFItem(446, 64, 6579, 75);

		// Bracelets
		player.getPackets().setIFItem(446, 32, 11069, 75);
		player.getPackets().setIFItem(446, 34, 11071, 75);
		player.getPackets().setIFItem(446, 36, 11076, 75);
		player.getPackets().setIFItem(446, 38, 11085, 75);
		player.getPackets().setIFItem(446, 40, 11092, 75);
		player.getPackets().setIFItem(446, 42, 11115, 75);
		player.getPackets().setIFItem(446, 44, 11130, 75);
		if (pyrefiend) {
			player.getTempAttribs().setB("immenseHeatCrafting", true);
			player.setCloseInterfacesEvent(() -> player.getTempAttribs().removeB("immenseHeatCrafting"));
		}
	}
}
