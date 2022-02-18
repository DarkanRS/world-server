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
package com.rs.game.player.content.skills.magic;

import com.rs.game.World;
import com.rs.game.player.Equipment;
import com.rs.game.player.Player;
import com.rs.game.player.content.dialogue.Conversation;
import com.rs.game.player.content.dialogue.Dialogue;
import com.rs.game.player.content.dialogue.Options;
import com.rs.game.player.quests.Quest;
import com.rs.lib.game.Item;
import com.rs.lib.game.WorldTile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ItemClickEvent;
import com.rs.plugin.handlers.ItemClickHandler;

import java.util.HashMap;
import java.util.Map;

@PluginEventHandler
public class TeleportTabs {

	public enum TeleTab {
		VARROCK(8007, new WorldTile(3217, 3426, 0)),
		LUMBRIDGE(8008, new WorldTile(3222, 3218, 0)),
		FALADOR(8009, new WorldTile(2965, 3379, 0)),
		CAMELOT(8010, new WorldTile(2758, 3478, 0)),
		ARDOUGNE(8011, new WorldTile(2660, 3306, 0)),
		WATCHTOWER(8012, new WorldTile(2549, 3114, 2)),
		HOUSE(8013, null),

		RIMMINGTON(18809, new WorldTile(2954, 3225, 0)),
		TAVERLY(18810, new WorldTile(2882, 3451, 0)),
		POLLNIVNEACH(18811, new WorldTile(3339, 3004, 0)),
		RELLEKA(18812, new WorldTile(2668, 3631, 0)),
		BRIMHAVEN(18813, new WorldTile(2757, 3177, 0)),
		YANILLE(18814, new WorldTile(2546, 3095, 0)),
		TROLLHEIM(20175, new WorldTile(2891, 3676, 0)),

		RUNECRAFTING_GUILD(13598, new WorldTile(1696, 5465, 2)),
		AIR_ALTAR(13599, new WorldTile(3125, 3406, 0)),
		MIND_ALTAR(13600, new WorldTile(2980, 3513, 0)),
		WATER_ALTAR(13601, new WorldTile(3184, 3163, 0)),
		EARTH_ALTAR(13602, new WorldTile(3303, 3477, 0)),
		FIRE_ALTAR(13603, new WorldTile(3309, 3251, 0)),
		BODY_ALTAR(13604, new WorldTile(3051, 3441, 0)),
		COSMIC_ALTAR(13605, new WorldTile(2407, 4383, 0)),
		CHAOS_ALTAR(13606, new WorldTile(2281, 4837, 0)), //Did not feel like adding separate logic to add wilderness controller and a dialogue safety prompt.
		NATURE_ALTAR(13607, new WorldTile(2865, 3022, 0)),
		LAW_ALTAR(13608, new WorldTile(2857, 3379, 0)),
		DEATH_ALTAR(13609, new WorldTile(1864, 4638, 0)),
		BLOOD_ALTAR(13610, new WorldTile(3559, 9778, 0)),
		ASTRAL_ALTAR(13611, new WorldTile(2150, 3862, 0));

		private static Map<Integer, TeleTab> MAP = new HashMap<>();

		static {
			for (TeleTab t : TeleTab.values())
				MAP.put(t.id, t);
		}

		public static TeleTab forId(int itemId) {
			return MAP.get(itemId);
		}

		private int id;
		private WorldTile teleToTile;

		private TeleTab (int id, WorldTile tile) {
			this.id = id;
			teleToTile = tile;
		}

		public int id() {
			return id;
		}

		public WorldTile tile() {
			return teleToTile;
		}
	}

	public static ItemClickHandler handle = new ItemClickHandler(TeleTab.MAP.keySet().toArray()) {
		@Override
		public void handle(ItemClickEvent e) {
			if (e.getOption().equalsIgnoreCase("drop")) {
				e.getPlayer().getInventory().deleteItem(e.getSlotId(), e.getItem());
				World.addGroundItem(e.getItem(), new WorldTile(e.getPlayer().getTile()), e.getPlayer());
				e.getPlayer().getPackets().sendSound(2739, 0, 1);
			}
			if (e.getOption().equalsIgnoreCase("break")) {
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

			if (e.getOption().equalsIgnoreCase("modify"))
				e.getPlayer().startConversation(new Conversation(e.getPlayer(), new Dialogue().addOptions(new Options() {
					@Override
					public void create() {
						option("House teleport", (() -> { e.getPlayer().getInventory().deleteItem(e.getItem().getId(), 1); e.getPlayer().getInventory().addItem(8013, 1); }));
						option("Rimmington teleport", (() -> { e.getPlayer().getInventory().deleteItem(e.getItem().getId(), 1); e.getPlayer().getInventory().addItem(TeleTab.RIMMINGTON.id, 1); }));
						option("Taverly teleport", (() -> { e.getPlayer().getInventory().deleteItem(e.getItem().getId(), 1); e.getPlayer().getInventory().addItem(TeleTab.TAVERLY.id, 1); }));
						option("Pollnivneach teleport", (() -> { e.getPlayer().getInventory().deleteItem(e.getItem().getId(), 1); e.getPlayer().getInventory().addItem(TeleTab.POLLNIVNEACH.id); }));
						option("Next", new Dialogue().addOptions(new Options() {
							@Override
							public void create() {
								option("Relleka teleport", (() -> { e.getPlayer().getInventory().deleteItem(e.getItem().getId(), 1); e.getPlayer().getInventory().addItem(TeleTab.RELLEKA.id, 1); }));
								option("Brimhaven teleport", (() -> { e.getPlayer().getInventory().deleteItem(e.getItem().getId(), 1); e.getPlayer().getInventory().addItem(TeleTab.BRIMHAVEN.id, 1); }));
								option("Yanille teleport", (() -> { e.getPlayer().getInventory().deleteItem(e.getItem().getId(), 1); e.getPlayer().getInventory().addItem(TeleTab.YANILLE.id, 1); }));
								option("Trollheim teleport", (() -> { e.getPlayer().getInventory().deleteItem(e.getItem().getId(), 1); e.getPlayer().getInventory().addItem(TeleTab.TROLLHEIM.id, 1); }));
							}
						}));
					}
				})));
		}
	};

	public static boolean meetsTabReqs(int itemId, Player p) {
		if (itemId == 13608) {
			boolean hasEquip = false;
			for (Item item : p.getInventory().getItems().getItems()) {
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

		if (itemId == 8012 && !p.getQuestManager().isComplete(Quest.WATCHTOWER)) {
			p.sendMessage("You must have completed Watchtower to use this teleport.");
			return false;
		}

		if (itemId == 13610 && !p.getQuestManager().isComplete(Quest.LEGACY_OF_SEERGAZE)) {
			p.sendMessage("You must have completed Legacy of Seergaze to use this teleport.");
			return false;
		}

		if (itemId == 13609 && !p.getQuestManager().isComplete(Quest.MOURNINGS_ENDS_PART_II)) {
			p.sendMessage("You must have completed Mournings End Part II to use this teleport.");
			return false;
		}

		return true;
	}
}
