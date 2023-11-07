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

import com.rs.cache.loaders.ObjectDefinitions;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.Options;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Item;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ItemClickHandler;
import com.rs.plugin.handlers.ItemOnItemHandler;
import com.rs.plugin.handlers.ItemOnObjectHandler;

import java.util.ArrayList;
import java.util.List;

@PluginEventHandler
public class GemBag {

	private static final int[] GEMS = new int[] { 1623, 1621, 1619, 1617, 1631 };

	public static ItemClickHandler handleClickOnGemBag = new ItemClickHandler(new Object[] { 18338, 25352 }, e -> {
		switch(e.getOption()) {
		case "Inspect" -> {
			int total = getTotalGemCount(e.getPlayer());
			e.getPlayer().sendMessage("You have " + total + " gem" + (total > 1 ? "s" : "") + " in your gem bag.");
			e.getPlayer().sendMessage(getGemCount(e.getPlayer(), 0) + " sapphires, " + getGemCount(e.getPlayer(), 1) + " emeralds, " + getGemCount(e.getPlayer(), 2) + " rubies, " + getGemCount(e.getPlayer(), 3) + " diamonds, and " + getGemCount(e.getPlayer(), 4) + " dragonstones.");
		}
		case "Fill" -> {
			for (int gemId : GEMS)
				if (!storeGems(e.getPlayer(), gemId, e.getItem().getId() == 25352))
					break;
		}
		case "Withdraw" -> {
			if (!e.getPlayer().getInventory().hasFreeSlots())
				e.getPlayer().sendMessage("You do not have enough inventory spaces to do that.");
			//TODO cbf since empty works lol
		}
		case "Empty" -> {
			if (!e.getPlayer().getInventory().hasFreeSlots()) {
				e.getPlayer().sendMessage("You do not have enough inventory spaces to do that.");
				return;
			}
			ArrayList<Double> bagContents = e.getPlayer().getO("gemBagContents");
			if (bagContents == null)
				return;
			for (int i = 0;i < GEMS.length;i++) {
				if (!e.getPlayer().getInventory().hasFreeSlots())
					break;
				if (bagContents.get(i).intValue() < 1)
					continue;
				int num = bagContents.get(i).intValue();
				if (num > e.getPlayer().getInventory().getFreeSlots())
					num = e.getPlayer().getInventory().getFreeSlots();
				if (e.getPlayer().getInventory().addItem(new Item(GEMS[i], num), true))
					bagContents.set(i, (double) (bagContents.get(i).intValue() - num));
			}
			e.getPlayer().save("gemBagContents", bagContents);
		}
		}
	});

	public static ItemOnItemHandler handleItemOnGemBag = new ItemOnItemHandler(new int[] { 18338, 25352 }, GEMS, e -> {
		Item bag = e.getUsedWith(GEMS);
		Item gem = e.getUsedWith(18338, 25352);
		if (bag == null)
			return;
		storeGems(e.getPlayer(), gem.getId(), bag.getId() == 25352);
	});

	private static boolean storeGems(Player player, int id, boolean upgrade) {
		ArrayList<Double> bagContents = player.getO("gemBagContents");
		if (bagContents == null)
			bagContents = new ArrayList<>(List.of(0.0, 0.0, 0.0, 0.0, 0.0));
		int maxGems = upgrade ? 500 : 100;
		int current = getTotalGemCount(player);
		int room = 0;

		if (current < maxGems)
			room = maxGems - current;

		if (room == 0) {
			player.sendMessage("Your gem bag is already full.");
			return false;
		}
		int gemsToStore = player.getInventory().getNumberOf(id);
		if (gemsToStore > room)
			gemsToStore = room;
		int gemIndex = switch (id) {
			case 1623 -> 0;
			case 1621 -> 1;
			case 1619 -> 2;
			case 1617 -> 3;
			case 1631 -> 4;
			default -> -1;
		};
		if (gemIndex == -1)
			return false;
		player.getInventory().deleteItem(id, gemsToStore);
		bagContents.set(gemIndex, bagContents.get(gemIndex) + gemsToStore);
		player.save("gemBagContents", bagContents);
		return true;
	}

	public static ItemOnObjectHandler handleGemBagOnObject = new ItemOnObjectHandler(new Object[] { "Bank", "Deposit Box", "Counter", "Bank booth" }, new Object[] { 18338, 25352 }, e -> {
		if (ObjectDefinitions.getDefs(e.getObject().getId()).getName().contains("Bank") || ObjectDefinitions.getDefs(e.getObject().getId()).containsOptionIgnoreCase("bank")) {
			ArrayList<Double> bagContents = e.getPlayer().getO("gemBagContents");
			if (bagContents == null)
				return;
			int stored = 0;
			for (int i = 0;i < GEMS.length;i++) {
				if (bagContents.get(i).intValue() < 1)
					continue;
				if (e.getPlayer().getBank().addItem(new Item(GEMS[i], bagContents.get(i).intValue()), true)) {
					stored += bagContents.get(i);
					bagContents.set(i, 0.0);
				}
			}
			e.getPlayer().save("gemBagContents", bagContents);
			e.getPlayer().sendMessage("You store " + stored + " gems in the bank.");
		}
	});

	public static int getTotalGemCount(Player p) {
		ArrayList<Double> bagContents = p.getO("gemBagContents");
		if (bagContents == null)
			bagContents = new ArrayList<>(List.of(0.0, 0.0, 0.0, 0.0, 0.0));
		int total = 0;
		for (double count : bagContents)
			total += count;
		return total;
	}

	public static int getGemCount(Player p, int gemType) {
		ArrayList<Double> bagContents = p.getO("gemBagContents");
		if (bagContents == null)
			return 0;
		return bagContents.get(gemType).intValue();
	}
}
