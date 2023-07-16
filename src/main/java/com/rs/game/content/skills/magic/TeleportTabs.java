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
package com.rs.game.content.skills.magic;

import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.player.Equipment;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Item;
import com.rs.lib.game.Tile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ItemClickHandler;

import java.util.HashMap;
import java.util.Map;

@PluginEventHandler
public class TeleportTabs {

	public enum TeleTab {
		VARROCK(8007, Tile.of(3217, 3426, 0)),
		LUMBRIDGE(8008, Tile.of(3222, 3218, 0)),
		FALADOR(8009, Tile.of(2965, 3379, 0)),
		CAMELOT(8010, Tile.of(2758, 3478, 0)),
		ARDOUGNE(8011, Tile.of(2660, 3306, 0)),
		WATCHTOWER(8012, Tile.of(2549, 3114, 2)),
		HOUSE(8013, null),

		RIMMINGTON(18809, Tile.of(2954, 3225, 0)),
		TAVERLY(18810, Tile.of(2882, 3451, 0)),
		POLLNIVNEACH(18811, Tile.of(3339, 3004, 0)),
		RELLEKA(18812, Tile.of(2668, 3631, 0)),
		BRIMHAVEN(18813, Tile.of(2757, 3177, 0)),
		YANILLE(18814, Tile.of(2546, 3095, 0)),
		TROLLHEIM(20175, Tile.of(2891, 3676, 0)),

		RUNECRAFTING_GUILD(13598, Tile.of(1696, 5465, 2)),
		AIR_ALTAR(13599, Tile.of(3125, 3406, 0)),
		MIND_ALTAR(13600, Tile.of(2980, 3513, 0)),
		WATER_ALTAR(13601, Tile.of(3184, 3163, 0)),
		EARTH_ALTAR(13602, Tile.of(3303, 3477, 0)),
		FIRE_ALTAR(13603, Tile.of(3309, 3251, 0)),
		BODY_ALTAR(13604, Tile.of(3051, 3441, 0)),
		COSMIC_ALTAR(13605, Tile.of(2407, 4383, 0)),
		CHAOS_ALTAR(13606, Tile.of(2281, 4837, 0)), //Did not feel like adding separate logic to add wilderness controller and a dialogue safety prompt.
		NATURE_ALTAR(13607, Tile.of(2865, 3022, 0)),
		LAW_ALTAR(13608, Tile.of(2857, 3379, 0)),
		DEATH_ALTAR(13609, Tile.of(1864, 4638, 0)),
		BLOOD_ALTAR(13610, Tile.of(3559, 9778, 0)),
		ASTRAL_ALTAR(13611, Tile.of(2150, 3862, 0));

		private static Map<Integer, TeleTab> MAP = new HashMap<>();

		static {
			for (TeleTab t : TeleTab.values())
				MAP.put(t.id, t);
		}

		public static TeleTab forId(int itemId) {
			return MAP.get(itemId);
		}

		private int id;
		private Tile teleToTile;

		private TeleTab (int id, Tile tile) {
			this.id = id;
			teleToTile = tile;
		}

		public int id() {
			return id;
		}

		public Tile tile() {
			return teleToTile;
		}
	}

	public static ItemClickHandler handle = new ItemClickHandler(TeleTab.MAP.keySet().toArray(), e -> {
		switch(e.getOption()) {
		case "Break" -> {
			if (!meetsTabReqs(e.getItem().getId(), e.getPlayer()))
				return;

			if (e.getItem().getId() == 8013) {
				if (!Magic.useHouseTab(e.getPlayer()))
					e.getPlayer().sendMessage("You can't teleport here!");
				else
					e.getPlayer().getInventory().deleteItem(e.getItem().getId(), 1);
				return;
			}

			TeleTab t = TeleTab.forId(e.getItem().getId());

			if (Magic.useTeleTab(e.getPlayer(), t.teleToTile))
				e.getPlayer().getInventory().deleteItem(e.getItem().getId(), 1);
		}
		case "Modify" -> {
			if (!e.getPlayer().isQuestComplete(Quest.LOVE_STORY, "to modify house teleports."))
				return;
			e.getPlayer().sendInputInteger("How many tabs would you like to modify?", num -> {
				if (!e.getPlayer().getInventory().containsItem(e.getItem().getId(), num)) {
					e.getPlayer().sendMessage("You don't have enough tablets to do that.");
					return;
				}
				e.getPlayer().sendOptionDialogue("Which tablet would you like to create?", ops -> {
					ops.add("House teleport", () -> { e.getPlayer().getInventory().deleteItem(e.getItem().getId(), num); e.getPlayer().getInventory().addItem(8013, num); });
					ops.add("Rimmington teleport", () -> { e.getPlayer().getInventory().deleteItem(e.getItem().getId(), num); e.getPlayer().getInventory().addItem(TeleTab.RIMMINGTON.id, num); });
					ops.add("Taverly teleport", () -> { e.getPlayer().getInventory().deleteItem(e.getItem().getId(), num); e.getPlayer().getInventory().addItem(TeleTab.TAVERLY.id, num); });
					ops.add("Pollnivneach teleport", () -> { e.getPlayer().getInventory().deleteItem(e.getItem().getId(), num); e.getPlayer().getInventory().addItem(TeleTab.POLLNIVNEACH.id, num); });
					ops.add("Relleka teleport", () -> { e.getPlayer().getInventory().deleteItem(e.getItem().getId(), num); e.getPlayer().getInventory().addItem(TeleTab.RELLEKA.id, num); });
					ops.add("Brimhaven teleport", () -> { e.getPlayer().getInventory().deleteItem(e.getItem().getId(), num); e.getPlayer().getInventory().addItem(TeleTab.BRIMHAVEN.id, num); });
					ops.add("Yanille teleport", () -> { e.getPlayer().getInventory().deleteItem(e.getItem().getId(), num); e.getPlayer().getInventory().addItem(TeleTab.YANILLE.id, num); });
					ops.add("Trollheim teleport", () -> { e.getPlayer().getInventory().deleteItem(e.getItem().getId(), num); e.getPlayer().getInventory().addItem(TeleTab.TROLLHEIM.id, num); });
				});
			});
			if (!e.getPlayer().getInventory().hasFreeSlots()) {
				e.getPlayer().sendMessage("You don't have enough inventory space to do that.");
				return;
			}
		}
		}
	});

	public static boolean meetsTabReqs(int itemId, Player p) {
		if (itemId == 20175 || (itemId >= 18809 && itemId <= 18814)) {
			if (!p.isQuestComplete(Quest.LOVE_STORY, "to modify house teleports."))
				return false;
		}
		
		if (itemId == 13608) {
			boolean hasEquip = false;
			for (Item item : p.getInventory().getItems().array()) {
				if (item == null)
					continue;
				if (Equipment.getItemSlot(item.getId()) != -1) {
					hasEquip = true;
					break;
				}
			}
			if (p.getEquipment().wearingArmour() || hasEquip) {
				p.sendMessage("The power of Saradomin prevents you from teleporting to Entrana. Please bank all your equippable items.");
				return false;
			}
		}

		if (itemId == 8012 && !p.isQuestComplete(Quest.WATCHTOWER)) {
			p.sendMessage("You must have completed Watchtower to use this teleport.");
			return false;
		}

		if (itemId == 13610 && !p.isQuestComplete(Quest.LEGACY_OF_SEERGAZE)) {
			p.sendMessage("You must have completed Legacy of Seergaze to use this teleport.");
			return false;
		}

		if (itemId == 13609 && !p.isQuestComplete(Quest.MOURNINGS_ENDS_PART_II)) {
			p.sendMessage("You must have completed Mournings End Part II to use this teleport.");
			return false;
		}

		return true;
	}
}
