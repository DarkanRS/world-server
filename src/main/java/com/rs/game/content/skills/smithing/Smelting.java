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
package com.rs.game.content.skills.smithing;

import java.util.HashMap;
import java.util.Map;

import com.rs.game.model.entity.player.Equipment;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.actions.PlayerAction;
import com.rs.game.model.object.GameObject;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ItemClickHandler;

@PluginEventHandler
public class Smelting extends PlayerAction {

	public enum SmeltingBar {

		BRONZE(1, 6.2, new Item[] { new Item(436), new Item(438) }, new Item(2349), 0),

		BLURITE(8, 8.0, new Item[] { new Item(668) }, new Item(9467), 1),

		IRON(15, 12.5, new Item[] { new Item(440) }, new Item(2351), 2),

		SILVER(20, 13.7, new Item[] { new Item(442) }, new Item(2355), 3),

		STEEL(30, 17.5, new Item[] { new Item(440), new Item(453, 2) }, new Item(2353), 4),

		GOLD(40, 22.5, new Item[] { new Item(444) }, new Item(2357), 5),

		MITHRIL(50, 30, new Item[] { new Item(447), new Item(453, 4) }, new Item(2359), 6),

		ADAMANT(70, 37.5, new Item[] { new Item(449), new Item(453, 6) }, new Item(2361), 7),

		RUNE(85, 50, new Item[] { new Item(451), new Item(453, 8) }, new Item(2363), 8),

		//		DRAGONBANE(80, 50, new Item[] { new Item(21779) }, new Item(21783, 1), 9),
		//
		//		WALLASALKIBANE(80, 50, new Item[] { new Item(21780) }, new Item(21784, 1), 10),
		//
		//		BASILISKBANE(80, 50, new Item[] { new Item(21781) }, new Item(21785, 1), 11),
		//
		//		ABYSSSALBANE(80, 50, new Item[] { new Item(21782) }, new Item(21786, 1), 12)
		;

		private static Map<Integer, SmeltingBar> bars = new HashMap<>();
		private static Map<Integer, SmeltingBar> forOres = new HashMap<>();

		public static SmeltingBar forId(int buttonId) {
			return bars.get(buttonId);
		}

		public static SmeltingBar forOre(Player player, int oreId) {
			//cast on iron, has coal, create steel.
			if (oreId == 440)
				if (player.getInventory().containsItem(453, 2) || (player.getInventory().containsItem(18339) && player.getI("coalBag") >= 2))
					return SmeltingBar.STEEL;
			//cast on coal, has tertiary.
			if (oreId == 453)
			{
				if (player.getInventory().containsItem(451))
					return SmeltingBar.RUNE;

				if (player.getInventory().containsItem(449))
					return SmeltingBar.ADAMANT;

				if (player.getInventory().containsItem(447))
					return SmeltingBar.MITHRIL;

				if (player.getInventory().containsItem(440))
					return SmeltingBar.STEEL;
			}
			return forOres.get(oreId);
		}

		static {
			for (SmeltingBar bar : SmeltingBar.values())
				bars.put(bar.getButtonId(), bar);

			for (SmeltingBar bar : SmeltingBar.values())
				for (Item item : bar.getItemsRequired()) {
					if (bar.getProducedBar().getId() == 2353)
						continue;
					forOres.put(item.getId(), bar);
				}
		}

		private int levelRequired;
		private double experience;
		private Item[] itemsRequired;
		private int buttonId;
		private Item producedBar;

		private SmeltingBar(int levelRequired, double experience, Item[] itemsRequired, Item producedBar, int buttonId) {
			this.levelRequired = levelRequired;
			this.experience = experience;
			this.itemsRequired = itemsRequired;
			this.producedBar = producedBar;
			this.buttonId = buttonId;
		}

		public Item[] getItemsRequired() {
			return itemsRequired;
		}

		public int getLevelRequired() {
			return levelRequired;
		}

		public Item getProducedBar() {
			return producedBar;
		}

		public double getExperience() {
			return experience;
		}

		public int getButtonId() {
			return buttonId;
		}
	}

	public SmeltingBar bar;
	public GameObject object;
	public int ticks;

	public Smelting(SmeltingBar bar, GameObject object, int ticks) {
		this.object = object;
		this.bar = bar;
		this.ticks = ticks;
	}

	@Override
	public boolean start(Player player) {
		if (bar == null || player == null || object == null)
			return false;

		if (!player.getInventory().containsItem(bar.getItemsRequired()[0].getId(), bar.getItemsRequired()[0].getAmount())) {
			player.simpleDialogue("You need " + bar.getItemsRequired()[0].getDefinitions().getName() + " to create a " + bar.getProducedBar().getDefinitions().getName() + ".");
			return false;
		}
		if (bar.getItemsRequired().length > 1)
			if (!player.getInventory().containsItem(bar.getItemsRequired()[1].getId(), bar.getItemsRequired()[1].getAmount()) && !(bar.getItemsRequired()[1].getId() == 453 && player.getInventory().containsItem(18339) && (player.getI("coalBag")+player.getInventory().getAmountOf(453)) >= bar.getItemsRequired()[1].getAmount())) {
				player.simpleDialogue("You need " + bar.getItemsRequired()[1].getDefinitions().getName() + " to create a " + bar.getProducedBar().getDefinitions().getName() + ".");
				return false;
			}
		if (player.getSkills().getLevel(Constants.SMITHING) < bar.getLevelRequired()) {
			player.simpleDialogue("You need a Smithing level of at least " + bar.getLevelRequired() + " to smelt " + bar.getProducedBar().getDefinitions().getName());
			return false;
		}
		player.sendMessage("You place the required ores and attempt to create a bar of " + bar.getProducedBar().getDefinitions().getName().toLowerCase().replace(" bar", "") + ".", true);
		return true;
	}

	@Override
	public boolean process(Player player) {
		if (bar == null || player == null || object == null)
			return false;
		if (!player.getInventory().containsItem(bar.getItemsRequired()[0].getId(), bar.getItemsRequired()[0].getAmount())) {
			player.simpleDialogue("You need " + bar.getItemsRequired()[0].getDefinitions().getName() + " to create a " + bar.getProducedBar().getDefinitions().getName() + ".");
			return false;
		}
		if (bar.getItemsRequired().length > 1)
			if (!player.getInventory().containsItem(bar.getItemsRequired()[1].getId(), bar.getItemsRequired()[1].getAmount()) && !(bar.getItemsRequired()[1].getId() == 453 && player.getInventory().containsItem(18339) && (player.getI("coalBag")+player.getInventory().getAmountOf(453)) >= bar.getItemsRequired()[1].getAmount())) {
				player.simpleDialogue("You need " + bar.getItemsRequired()[1].getDefinitions().getName() + " to create a " + bar.getProducedBar().getDefinitions().getName() + ".");
				return false;
			}
		if (player.getSkills().getLevel(Constants.SMITHING) < bar.getLevelRequired()) {
			player.simpleDialogue("You need a Smithing level of at least " + bar.getLevelRequired() + " to smelt " + bar.getProducedBar().getDefinitions().getName());
			return false;
		}
		player.faceObject(object);
		return true;
	}

	public boolean isSuccessfull(Player player) {
		if (bar == SmeltingBar.IRON) {
			if ((player.getEquipment().getItem(Equipment.RING) == null) || (player.getEquipment().getItem(Equipment.RING).getId() != 2568))
				return Utils.random(100) <= (player.getSkills().getLevel(Constants.SMITHING) >= 45 ? 80 : 50);
			player.ringOfForgingCharges--;
			if (player.ringOfForgingCharges <= 0) {
				player.getEquipment().deleteSlot(Equipment.RING);
				player.sendMessage("Your ring of forging disintegrates with all of the heat.");
				player.ringOfForgingCharges = 140;
			}
			return true;
		}
		return true;
	}

	@Override
	public int processWithDelay(Player player) {
		ticks--;
		player.setNextAnimation(new Animation(3243));
		for (Item required : bar.getItemsRequired())
			if (required.getId() == 453 && player.getInventory().containsItem(18339) && player.getI("coalBag") > 0) {
				int coalBag = player.getI("coalBag");
				if (coalBag > required.getAmount())
					player.save("coalBag", coalBag - required.getAmount());
				else {
					player.save("coalBag", 0);
					player.getInventory().deleteItem(453, required.getAmount()-coalBag);
				}
			} else
				player.getInventory().deleteItem(required.getId(), required.getAmount());
		if (isSuccessfull(player)) {
			if (bar == SmeltingBar.GOLD && player.getEquipment().getGlovesId() == 776)
				player.getSkills().addXp(Constants.SMITHING, 56.2);
			else
				player.getSkills().addXp(Constants.SMITHING, bar.getExperience());
			player.getInventory().addItem(bar.getProducedBar());
			player.sendMessage("You retrieve a bar of " + bar.getProducedBar().getDefinitions().getName().toLowerCase().replace(" bar", "") + ".", true);
		} else
			player.sendMessage("The ore is too impure and you fail to refine it.", true);
		if (ticks > 0)
			return 2;
		return -1;
	}

	@Override
	public void stop(Player player) {
		setActionDelay(player, 3);
	}

	public static ItemClickHandler checkChargesRingOfForging = new ItemClickHandler(new Object[] { 2568 }, new String[] { "Check" }, e -> e.getPlayer().sendMessage("You have " + e.getPlayer().ringOfForgingCharges + " charges remaining in your ring of forging."));
}
