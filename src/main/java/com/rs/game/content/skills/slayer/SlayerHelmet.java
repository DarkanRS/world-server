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
package com.rs.game.content.skills.slayer;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.lib.Constants;
import com.rs.lib.game.Item;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ItemOnItemEvent;
import com.rs.plugin.handlers.ItemClickHandler;
import com.rs.plugin.handlers.ItemOnItemHandler;

@PluginEventHandler
public class SlayerHelmet  {
	static int[] SLAYER_HELMET_PARTS = { 4166, 4164, 4551, 4168 };
	static int[] FULL_SLAYER_HELMET_PARTS = { 15490, 15488 };
	static int SLAYER_HELMET = 13263;
	static int FULL_SLAYER_HELMET = 15492;

	public static ItemClickHandler dismantle = new ItemClickHandler(new Object[] { FULL_SLAYER_HELMET, SLAYER_HELMET }, new String[] { "Disassemble" }, e -> {
		if (e.getItem().getId() == FULL_SLAYER_HELMET) {
			if (e.getPlayer().getInventory().getFreeSlots() < 7) {
				e.getPlayer().sendMessage("You don't have enough space in your inventory to dissassemble the helmet.");
				return;
			}
			e.getPlayer().getInventory().deleteItem(15492, 1);
			for (int parts : FULL_SLAYER_HELMET_PARTS)
				e.getPlayer().getInventory().addItem(parts, 1, true);
			for (int parts : SLAYER_HELMET_PARTS)
				e.getPlayer().getInventory().addItem(parts, 1, true);
			e.getPlayer().getInventory().addItem(8921, 1);
			return;
		}
		if (e.getItem().getId() == SLAYER_HELMET) {
			if (e.getPlayer().getInventory().getFreeSlots() < 5) {
				e.getPlayer().sendMessage("You don't have enough space in your inventory to dissassemble the helmet.");
				return;
			}
			e.getPlayer().getInventory().deleteItem(13263, 1);
			for (int parts : SLAYER_HELMET_PARTS)
				e.getPlayer().getInventory().addItem(parts, 1, true);
			e.getPlayer().getInventory().addItem(8921, 1);
			return;
		}
	});

	public static ItemOnItemHandler craftNormal = new ItemOnItemHandler(SLAYER_HELMET_PARTS, SLAYER_HELMET_PARTS, e -> craft(e));
	public static ItemOnItemHandler craftFull = new ItemOnItemHandler(SLAYER_HELMET, FULL_SLAYER_HELMET_PARTS, e -> craft(e));

	public static boolean craft(ItemOnItemEvent e) {

		boolean CRAFT_FULL_SLAYER_HELMET = true;

		if (!SlayerHelmet.isSlayerHelmComponent(e.getItem1().getId()) || !isSlayerHelmComponent(e.getItem2().getId()))
			return false;
		if (!e.getPlayer().isSlayerHelmCreation()) {
			e.getPlayer().sendMessage("You don't know what to do with these parts. You should talk to an expert, perhaps they know how to assemble these parts.");
			return true;
		}
		if (e.getPlayer().getSkills().getLevel(Constants.CRAFTING) < 55) {
			e.getPlayer().sendMessage("You need a Crafting level of 55 in order to assemble a slayer helmet.");
			return true;
		}

		//Determine if the player has the necessary items to craft a full slayer helmet
		for (int parts : FULL_SLAYER_HELMET_PARTS)
			if (!e.getPlayer().getInventory().containsItem(parts, 1))
				CRAFT_FULL_SLAYER_HELMET = false;

		//Verify the player has all normal slayer helmet parts
		for (int parts : SLAYER_HELMET_PARTS)
			if (!e.getPlayer().getInventory().containsItem(parts, 1)) {
				if (!CRAFT_FULL_SLAYER_HELMET || !e.getPlayer().getInventory().containsItem(SLAYER_HELMET, 1))
					return false;
			}

		if ((!e.getPlayer().getInventory().containsItem("black mask") && !CRAFT_FULL_SLAYER_HELMET) || (!e.getPlayer().getInventory().containsItem("black mask") && !e.getPlayer().getInventory().containsItem(SLAYER_HELMET)))
			return false;

		if (!CRAFT_FULL_SLAYER_HELMET) {
			for (int parts : SLAYER_HELMET_PARTS)
				e.getPlayer().getInventory().deleteItem(parts, 1);

			for (int i = 8901; i <= 8922; i++)
				if (!ItemDefinitions.getDefs(i).isNoted() && e.getPlayer().getInventory().containsItem(i)) {
					e.getPlayer().getInventory().deleteItem(i, 1);
					break;
				}

			e.getPlayer().getInventory().addItem(new Item(SLAYER_HELMET, 1));
			e.getPlayer().sendMessage("You combine all parts of the helmet.");
			return true;
		}
		if (e.getPlayer().getInventory().containsItem(SLAYER_HELMET, 1)) {
			e.getPlayer().getInventory().deleteItem(SLAYER_HELMET, 1);
			for (int parts : FULL_SLAYER_HELMET_PARTS)
				e.getPlayer().getInventory().deleteItem(parts, 1);
			e.getPlayer().getInventory().addItem(new Item(15492, 1));
			e.getPlayer().sendMessage("You attach two parts to your slayer helmet.");
			return true;
		}
		for (int parts : SLAYER_HELMET_PARTS)
			e.getPlayer().getInventory().deleteItem(parts, 1);
		for (int parts : FULL_SLAYER_HELMET_PARTS)
			e.getPlayer().getInventory().deleteItem(parts, 1);

		for (int i = 8901; i <= 8922; i++)
			if (!ItemDefinitions.getDefs(i).isNoted() && e.getPlayer().getInventory().containsItem(i)) {
				e.getPlayer().getInventory().deleteItem(i, 1);
				break;
			}

		e.getPlayer().getInventory().addItem(new Item(15492, 1));
		e.getPlayer().sendMessage("You combine all parts of the helmet.");
		return true;
	}

	public static boolean isSlayerHelmComponent(int itemId) {
		for (int parts : SLAYER_HELMET_PARTS)
			if (itemId == parts)
				return true;
		for (int parts : FULL_SLAYER_HELMET_PARTS)
			if (itemId == parts)
				return true;
		if ((itemId >= 8901 && itemId <= 8922 && !ItemDefinitions.getDefs(itemId).isNoted()) || (itemId == 13263))
			return true;
		return false;
	}
}