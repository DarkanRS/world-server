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
package com.rs.game.content.skills.herblore;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.engine.dialogue.Dialogue;
import com.rs.game.content.Potions.Potion;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Item;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

import java.util.ArrayList;
import java.util.HashMap;

@PluginEventHandler
public class BobBarter {

	public static NPCClickHandler handleBobBarter = new NPCClickHandler(new Object[] { 6524 }, e -> {
		if (e.getOption().equalsIgnoreCase("decant"))
			decant(e.getPlayer());
		else if (e.getOption().equalsIgnoreCase("decant-x")) {
			Dialogue decantChoices = new Dialogue();
			decantChoices.addOption("Decant to dose", "1 dose", "2 dose", "3 dose", "4 dose");
			decantChoices.addSimple("Bob decant's your potions into 1 doses", () -> decant(e.getPlayer(), 1));
			decantChoices.addSimple("Bob decant's your potions into 2 doses", () -> decant(e.getPlayer(), 2));
			decantChoices.addSimple("Bob decant's your potions into 3 doses", () -> decant(e.getPlayer(), 3));
			decantChoices.addSimple("Bob decant's your potions into 4 doses", () -> decant(e.getPlayer(), 4));
			e.getPlayer().startConversation(decantChoices);
		}
	});

	public static void decant(Player p) {
		decant(p, 4);
	}

	public static void decant(Player p, int dose) {
		ArrayList<Integer> checkedPotions = new ArrayList<>();
		HashMap<String, ArrayList<Integer>> potionDoses = new HashMap<>();
		Potion potion;

		for (Item i : p.getInventory().getItems().array()) {
			potion = null;
			if (i == null)
				continue;
			ItemDefinitions itemdefs = ItemDefinitions.getDefs(i.getId());

			if (checkedPotions.contains(i.getId()) && !itemdefs.isNoted()) {
				p.getInventory().deleteItem(i);
				continue;
			}

			for (Potion potions : Potion.values())
				if (potions != null && potions.isVial()) {
					int[] potIds = potions.getIds();
					for (int potId : potIds)
						if (itemdefs.isNoted()) {
							if (potId == itemdefs.getCertId()) {
								potion = potions;
								checkedPotions.add(i.getId());
							}
						} else if (potId == i.getId()) {
							potion = potions;
							checkedPotions.add(i.getId());
						}
				}

			if (potion == null || !potion.isVial())
				continue;

			String potionName = getNameWithoutDoses(i.getName());
			int doses = p.getInventory().getNumberOf(i.getId()) * getDosage(i.getName());
			//Logger.debug("doses " + doses);
			if (potionDoses.containsKey(potionName)) {
				ArrayList<Integer> temp = potionDoses.get(potionName);
				doses += temp.get(0);
			}
			ArrayList<Integer> qtyAndIds = new ArrayList<>();
			qtyAndIds.add(doses);
			for (int potId : potion.getIds())
				qtyAndIds.add(potId);

			potionDoses.put(potionName, qtyAndIds);
			p.getInventory().deleteItem(i);
		}

		if (potionDoses == new HashMap<String, ArrayList<Integer>>() || potionDoses.isEmpty())
			p.sendMessage("You don't have any potions to decant.");
		else
			for (String name : potionDoses.keySet()) {
				ArrayList<Integer> pot = potionDoses.get(name);
				int totalDoses = pot.get(0);
				int fullQty = totalDoses / dose;
				int partialDose = totalDoses % dose;
				//				Logger.debug("Making " + dose + " dose " + name + " potions.");
				//				Logger.debug("Total doses available: " + totalDoses + ", converting to " + fullQty + " potions with a remaining " + partialDose + " dose");
				//
				//				Logger.debug("item id full: " + ((int)(pot.get(pot.size()-dose))+1));
				ItemDefinitions fullDef = ItemDefinitions.getDefs(((pot.get(pot.size()-dose))));
				if (fullDef.name.contains(name) && fullQty > 0)
					p.getInventory().addItemDrop(fullDef.getCertId() == -1 ? fullDef.getId() : fullDef.getCertId(), fullQty);
				if (partialDose > 0) {
					//Logger.debug("item id partial: " + ((int)(pot.get(pot.size()-partialDose))));
					ItemDefinitions partialDef = ItemDefinitions.getDefs((pot.get(pot.size()-partialDose)));
					if (partialDef.name.contains(name))
						p.getInventory().addItemDrop(partialDef.getCertId() == -1 ? partialDef.getId() : partialDef.getCertId(), 1);
				}
			}
	}

	public static String getNameWithoutDoses(String itemName) {
		int end = itemName.indexOf("(") - 1;
		if (end < 0)
			return "";
		return itemName.substring(0, end);
	}

	public static int getDosage(String itemName) {
		int start = itemName.indexOf("(") + 1;
		int end = start+1;
		return Integer.parseInt(itemName.substring(start, end));
	}
}