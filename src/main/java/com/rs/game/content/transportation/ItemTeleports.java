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
package com.rs.game.content.transportation;

import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.quest.Quest;
import com.rs.game.content.skills.magic.Magic;
import com.rs.game.content.skills.magic.TeleType;
import com.rs.game.content.world.areas.burthorpe.HeroesGuild;
import com.rs.game.model.entity.player.Equipment;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Item;
import com.rs.lib.game.Tile;

public class ItemTeleports {

	/*
	 * 1230, 1232 - grand seed pod gfx
	 */

	private static final String[] ITEM_NAMES = {
			"ring of duelling",
			"games necklace",
			"amulet of glory (t",
			"amulet of glory",
			"skills necklace",
			"combat bracelet",
			"digsite",
			"ring of wealth",
			"ring of slaying",
			"lumber yard",
			"nardah",
			"tai bwo wannai",
			"bandit camp",
			"phoenix lair",
			"miscellania"
	};
	private static final String[][] TELEPORT_NAMES = {
			{ "Al Kharid Duel Arena", "Castle Wars Arena", "Mobilizing Armies Command Centre", "Fist Of Guthix", "Nowhere" },
			{ "Burthorpe Games Room", "Barbarian Outpost", "Gamers' Grotto", "Corporeal Beast", "Nowhere" },
			{ "Edgeville", "Karamja", "Draynor Village", "Al Kharid", "Nowhere" },
			{ "Edgeville", "Karamja", "Draynor Village", "Al Kharid", "Nowhere" },
			{ "Fishing Guild", "Mining Guild", "Crafting Guild", "Cooking Guild", "Nowhere" },
			{ "Warrior's Guild", "Champions Guild", "Monastery", "Ranging Guild", "Nowhere" },
			{ "Digsite", "Nowhere" },
			{ "Miscellania", "Grand Exchange", "Nowhere" },
			{ "Sumona's Lair", "Slayer Tower", "Fremennik Slayer Dungeon", "Tarn's Lair", "Nowhere" },
			{ "Lumber Yard", "Nowhere" },
			{ "Nardah", "Nowhere" },
			{ "Tai Bwo Wannai", "Nowhere" },
			{ "Bandit camp", "Nowhere" },
			{ "Phoenix lair", "Nowhere" },
			{ "Miscellania", "Nowhere" }
	};

	private static final Tile[][] COORDINATES = {
			{ Tile.of(3316, 3232, 0), Tile.of(2443, 3089, 0), Tile.of(2416, 2836, 0), Tile.of(1701, 5600, 0) },
			{ Tile.of(2208, 4940, 0), Tile.of(2519, 3571, 0), Tile.of(2970, 9679, 0), Tile.of(2886, 4377, 2), },
			{ Tile.of(3087, 3496, 0), Tile.of(2918, 3176, 0), Tile.of(3105, 3251, 0), Tile.of(3293, 3163, 0) },
			{ Tile.of(3087, 3496, 0), Tile.of(2918, 3176, 0), Tile.of(3105, 3251, 0), Tile.of(3293, 3163, 0) },
			{ Tile.of(2614, 3384, 0), Tile.of(3032, 3337, 0), Tile.of(2933, 3293, 0), Tile.of(3143, 3442, 0) },
			{ Tile.of(2878, 3542, 0), Tile.of(3191, 3363, 0), Tile.of(2607, 3220, 0), Tile.of(2657, 3238, 0) },
			{ Tile.of(3356, 3421, 0) },
			{ Tile.of(2527, 3860, 0), Tile.of(3167, 3492, 0) },
			{ Tile.of(3353, 3006, 0), Tile.of(3427, 3538, 0), Tile.of(2787, 3616, 0), Tile.of(3150, 4666, 0) },
			{ Tile.of(3305, 3489, 0) },
			{ Tile.of(3423, 2914, 0) },
			{ Tile.of(2796, 3082, 0) },
			{ Tile.of(3170, 2982, 0) },
			{ Tile.of(2294, 3626, 0) },
			{ Tile.of(2519, 3860, 0) },
	};


	private static final int[] LOWEST_AMOUNT = { 2566, 3867, 1706, 10362, 11113, 11126, 11190, 2572, 13288, 19480, 19475, 19479, 19476, 19478, 19477 };
	public static final int EMOTE = 9603, GFX = 1684, SCROLL_EMOTE = 14293, SCROLL_GFX = 94;

	private static Dialogue getTransportationDialogue(Player player, Item item, int itemId, String... locations) {
		return new Dialogue().addOptions("Where would you like to teleport to?", (ops) -> {
			for (int i = 0; i < locations.length; i++) {
				final int index = i;
				ops.add(locations[i], () -> {
					if (item != null) {
						ItemTeleports.sendTeleport(player, item, index);
						return;
					}
					ItemTeleports.sendTeleport(player, player.getInventory().getItems().lookup(itemId), index, false, locations.length);
				});
			}
		});
	}

	public static boolean transportationDialogue(Player player, Item item) {
		int index = getIndex(item);
		if (!checkAll(player, item, index, 0, 1))
			return false;
		player.startConversation(getTransportationDialogue(player, null, item.getId(), TELEPORT_NAMES[index]));
		return true;
	}

	public static boolean transportationDialogue(Player player, int itemId) {
		int index = getIndex(new Item(itemId, 1));
		if (index == -1)
			return false;
		player.startConversation(getTransportationDialogue(player, new Item(itemId, 1), itemId, TELEPORT_NAMES[index]));
		return true;
	}

	private static boolean checkAll(Player player, Item item, int index, int optionsIndex, int optionsLength) {
		if (index == -1)
			return false;
		if (optionsIndex >= optionsLength || optionsIndex >= COORDINATES[index].length) {
			player.stopAll(); // nowhere option
			return false;
		}

		if (ITEM_NAMES[index].equals("digsite") && !player.isQuestComplete(Quest.DIG_SITE)) {
			player.sendMessage("You need to complete 'The Dig Site' quest before using this item.");
			return false;
		}

		if (!isScrollTeleport(index) && (item.getId() == 10362 || !item.getName().toLowerCase().contains("("))) {
			player.sendMessage("Your " + item.getName().toLowerCase() + " has ran out of charges. You need to recharge it if you wish it use it once more.");
			return false;
		}
		return true;
	}

	public static void sendItemTransport(Player player, Item item, boolean wearing, int locationIndex) {
		if (!wearing)
			ItemTeleports.transportationDialogue(player, new Item(item.getId(), 1));
		else
			ItemTeleports.sendTeleport(player, player.getEquipment().getItem(Equipment.getItemSlot(item.getId())), locationIndex, true);
	}

	public static void sendTeleport(Player player, Item item, int optionIndex) {
		int index = getIndex(item);
		if (index < 0)
			return;
		if (optionIndex >= COORDINATES[index].length) {
			player.sendMessage("Error handling teleport option. Report this to administrators.");
			return;
		}
		Magic.sendTeleportSpell(player, getFirstEmote(index), -2, getFirstGFX(index), -1, 0, 0, COORDINATES[index][optionIndex], 4, true, TeleType.ITEM, null);
	}

	public static void sendTeleport(Player player, Item item, int optionIndex, boolean equipmentTeleport) {
		sendTeleport(player, item, optionIndex, equipmentTeleport, 4);
	}

	public static void sendTeleport(Player player, Item item, int optionIndex, boolean equipmentTeleport, int optionslength) {
		int index = getIndex(item);
		if (!checkAll(player, item, index, optionIndex, optionslength))
			return;
		if (HeroesGuild.isGloryOrROW(item.getId()))
			player.getTempAttribs().setB("glory", true);
		Magic.sendTeleportSpell(player, getFirstEmote(index), -2, getFirstGFX(index), -1, 0, 0, COORDINATES[index][optionIndex], 4, true, TeleType.ITEM, () -> {
			int newItemId = item.getId() + ((isNegative(index) ? -1 : 1) * (isIncremented(index) ? 2 : 1)), slot = equipmentTeleport ? Equipment.getItemSlot(item.getId()) : player.getInventory().getItems().getThisItemSlot(item);
			if (item.getId() == LOWEST_AMOUNT[index] && destroyOnEmpty(index)) {
				if (equipmentTeleport)
					player.getEquipment().deleteItem(item.getId(), item.getAmount());
				else
					player.getInventory().deleteItem(item.getId(), 1);
				player.sendMessage("Your " + item.getName().toLowerCase().replace(" (1)", "") + " has crumbled to dust.");
			} else {
				if (newItemId == 20651)
					newItemId = 2572;
				item.setId(newItemId);
				if (equipmentTeleport)
					player.getEquipment().refresh(slot);
				else
					player.getInventory().refresh(slot);
			}
		});
	}

	private static boolean isScrollTeleport(int index) {
		return index >= 9 && index <= 14;
	}

	public static int getFirstEmote(int index) {
		if (isScrollTeleport(index))
			return SCROLL_EMOTE;
		return EMOTE;
	}

	public static int getFirstGFX(int index) {
		if (isScrollTeleport(index))
			return SCROLL_GFX;
		return GFX;
	}

	private static int getIndex(Item item) {
		for (int i = 0; i < ITEM_NAMES.length; i++)
			if (item.getName().toLowerCase().contains(ITEM_NAMES[i]))
				return i;
		return -1;
	}

	private static boolean isNegative(int index) {
		return index == 3 || index == 6 || index == 7;
	}

	private static boolean isIncremented(int index) {
		return index == 0 || index == 1 || index == 2 || index == 3 || index == 4 || index == 5 || index == 7;
	}

	private static boolean destroyOnEmpty(int index) {
		return index == 0 || index == 1 || index == 6 || index == 8 || isScrollTeleport(index);
	}
}