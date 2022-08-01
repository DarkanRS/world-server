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

import java.util.HashMap;

import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Item;
import com.rs.net.decoders.handlers.InventoryOptionsHandler;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ItemClickEvent;
import com.rs.plugin.handlers.ItemClickHandler;

@PluginEventHandler
public class Toolbelt {

	public enum Tools {

		//General
		PICKAXE(10259, 1265),
		HAMMER(10260, 2347),
		CHISEL(10247, 1755),
		HATCHET(10261, 1351),
		KNIFE(10244, 946),
		TINDERBOX(10262, 590),
		SAW(10264, 8794),
		PESTLE_AND_MORTAR(10275, 233),
		MACHETE(10278, 975),
		WATCH(10280, 2575),
		CHART(10281, 2576),
		SHEARS(10245, 1735),
		NOOSE_WAND(10283, 10150),

		//Fishing
		CRAYFISH_CAGE(10253, 13431),
		FISHING_ROD(10254, 307),
		SMALL_FISHING_NET(10258, 303),
		BIG_FISHING_NET(10277, 305),
		FLY_FISHING_ROD(10255, 309),
		HARPOON(10256, 311),
		LOBSTER_POT(10257, 301),
		BARBARIAN_ROD(10279, 11323),

		//Crafting
		NEEDLE(10250, 1733),
		GLASSBLOWING_PIPE(10268, 1785),
		AMULET_MOULD(10246, 1595),
		BRACELET_MOULD(10267, 11065),
		NECKLACE_MOULD(10249, 1597),
		RING_MOULD(10251, 1592),
		TIARA_MOULD(10252, 5523),
		AMMO_MOULD(10265, 4),
		BOLT_MOULD(10266, 9434),
		HOLY_MOULD(10248, 1599),
		UNHOLY_MOULD(10270, 1594),
		SICKLE_MOULD(10269, 2976),
		CHAIN_LINK_MOULD(10282, 13153),

		//Farming
		RAKE(10273, 5341),
		SEED_DIBBER(10271, 5343),
		SPADE(10276, 952),
		GARDENING_TROWEL(10272, 5325),
		SECATEURS(10274, 5329),

		//Dungeoneering
		DUNG_PICKAXE(4293, 16295, 16297, 16299, 16301, 16303, 16305, 16307, 16309, 16311, 16313, 16315),
		DUNG_HATCHET(11047, 16361, 16363, 16365, 16367, 16369, 16371, 16373, 16375, 16377, 16379, 16381),
		DUNG_KNIFE(11051, 17754),
		DUNG_HAMMER(11048, 17883),
		DUNG_CHISEL(11053, 17444),
		DUNG_FLY_FISHING_ROD(11050, 17794),
		DUNG_NEEDLE(11052, 17446),
		DUNG_TINDERBOX(11049, 17678);

		private static HashMap<Integer, Tools> MAP = new HashMap<>();

		static {
			for (Tools tool : Tools.values())
				for (int itemId : tool.itemIds)
					MAP.put(itemId, tool);
		}

		public static Tools forId(int itemId) {
			return MAP.get(itemId);
		}

		private int varpBit;
		private int[] itemIds;

		public void sendUnlocked(Player player, int value) {
			player.getVars().setVarBit(varpBit, value);
		}

		private Tools(int varpBit, int... itemIds) {
			this.varpBit = varpBit;
			this.itemIds = itemIds;
		}

		public int getValue(int itemId) {
			for (int i = 0;i < itemIds.length;i++)
				if (itemIds[i] == itemId)
					return i+1;
			return 0;
		}

		public boolean contains(int value, int itemId) {
			int index = value-1;
			if (index < 0)
				return false;
			for (int i = 0;i <= index;i++)
				if (itemIds[i] == itemId)
					return true;
			return false;
		}
	}

	public static ItemClickHandler handleToolbeltOps = new ItemClickHandler(new String[] { "Add-to-toolbelt", "Carve", "Light", "Grind", "Powder", "Squeeze", "Craft", "Fletch", "Gut" }) {
		@Override
		public void handle(ItemClickEvent e) {
			System.out.println();
			switch(e.getOption()) {
			case "Add-to-toolbelt":
				if (e.getPlayer().addToolbelt(e.getItem().getId())) {
					e.getPlayer().getInventory().deleteItem(e.getItem().getId(), 1);
					refreshToolbelt(e.getPlayer());
				}
				break;
			case "Light":
				InventoryOptionsHandler.handleItemOnItem(e.getPlayer(), new Item(Tools.TINDERBOX.itemIds[0], 1), e.getItem(), -1, e.getSlotId());
				break;
			case "Grind":
			case "Powder":
			case "Squeeze":
				InventoryOptionsHandler.handleItemOnItem(e.getPlayer(), new Item(Tools.PESTLE_AND_MORTAR.itemIds[0], 1), e.getItem(), -1, e.getSlotId());
				break;
			case "Craft":
			case "Carve":
				if (InventoryOptionsHandler.handleItemOnItem(e.getPlayer(), new Item(Tools.KNIFE.itemIds[0], 1), e.getItem(), -1, e.getSlotId()) || InventoryOptionsHandler.handleItemOnItem(e.getPlayer(), new Item(Tools.CHISEL.itemIds[0], 1), e.getItem(), -1, e.getSlotId()))
					return;
				if (InventoryOptionsHandler.handleItemOnItem(e.getPlayer(), new Item(Tools.NEEDLE.itemIds[0], 1), e.getItem(), -1, e.getSlotId()))
					return;
				else if (InventoryOptionsHandler.handleItemOnItem(e.getPlayer(), new Item(Tools.GLASSBLOWING_PIPE.itemIds[0], 1), e.getItem(), -1, e.getSlotId()))
					return;
				else if (InventoryOptionsHandler.handleItemOnItem(e.getPlayer(), new Item(Tools.DUNG_NEEDLE.itemIds[0], 1), e.getItem(), -1, e.getSlotId()))
					return;
				else if (InventoryOptionsHandler.handleItemOnItem(e.getPlayer(), new Item(Tools.DUNG_KNIFE.itemIds[0], 1), e.getItem(), -1, e.getSlotId()))
					return;
				else if (InventoryOptionsHandler.handleItemOnItem(e.getPlayer(), new Item(Tools.DUNG_CHISEL.itemIds[0], 1), e.getItem(), -1, e.getSlotId()))
					return;
				break;
			case "Fletch":
				if (InventoryOptionsHandler.handleItemOnItem(e.getPlayer(), new Item(Tools.DUNG_KNIFE.itemIds[0], 1), e.getItem(), -1, e.getSlotId()))
					return;
				if (InventoryOptionsHandler.handleItemOnItem(e.getPlayer(), new Item(Tools.DUNG_CHISEL.itemIds[0], 1), e.getItem(), -1, e.getSlotId()))
					return;
				if (InventoryOptionsHandler.handleItemOnItem(e.getPlayer(), new Item(Tools.KNIFE.itemIds[0], 1), e.getItem(), -1, e.getSlotId()))
					return;
				else if (InventoryOptionsHandler.handleItemOnItem(e.getPlayer(), new Item(Tools.CHISEL.itemIds[0], 1), e.getItem(), -1, e.getSlotId()))
					return;
				break;
			case "Gut":
				InventoryOptionsHandler.handleItemOnItem(e.getPlayer(), new Item(Tools.KNIFE.itemIds[0], 1), e.getItem(), -1, e.getSlotId());
				break;
			}
		}
	};

	public static void refreshToolbelt(Player player) {
		if (player.getToolbelt() == null)
			return;
		for (Tools tool : player.getToolbelt().keySet())
			tool.sendUnlocked(player, player.getToolValue(tool));
	}

}
