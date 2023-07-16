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

@PluginEventHandler
public class GemBag {

	public static ItemClickHandler handleClickOnGemBag = new ItemClickHandler(new Object[] { 18338 }, e -> {
		int current = getNumGemsInBag(e.getPlayer());
		int sapphire = e.getPlayer().getI("gemBagSapphire");
		int emerald = e.getPlayer().getI("gemBagEmerald");
		int ruby = e.getPlayer().getI("gemBagRuby");
		int diamond = e.getPlayer().getI("gemBagDiamond");

		if (current == 0) {
			e.getPlayer().sendMessage("You do not have any gems in your gem bag.");
			return;
		}

		switch(e.getOption()) {
		case "Inspect":
			e.getPlayer().sendMessage("You have " + current + " gem"  + (current > 1 ? "s" : "") + " in your gem bag.");
			e.getPlayer().sendMessage(sapphire + " sapphires, " + emerald + " emeralds, " + ruby + " rubies, " + diamond + " diamonds.");
			break;
		case "Withdraw":
			if (!e.getPlayer().getInventory().hasFreeSlots()) {
				e.getPlayer().sendMessage("You do not have enough inventory spaces to do that.");
				return;
			}

			e.getPlayer().startConversation(new Dialogue().addOptions(new Options() {
				@Override
				public void create() {
					if (sapphire > 0)
						option("Withdraw sapphires", () -> {
							int numToWithdraw = (sapphire > e.getPlayer().getInventory().getFreeSlots() ? e.getPlayer().getInventory().getFreeSlots() : sapphire);
							e.getPlayer().getInventory().addItem(1623, numToWithdraw);
							e.getPlayer().save("gemBagSapphire", sapphire-numToWithdraw);
						});
					if (emerald > 0)
						option("Withdraw emerald", () -> {
							int numToWithdraw = (emerald > e.getPlayer().getInventory().getFreeSlots() ? e.getPlayer().getInventory().getFreeSlots() : emerald);
							e.getPlayer().getInventory().addItem(1621, numToWithdraw);
							e.getPlayer().save("gemBagEmerald", emerald-numToWithdraw);
						});
					if (ruby > 0)
						option("Withdraw rubies", () -> {
							int numToWithdraw = (ruby > e.getPlayer().getInventory().getFreeSlots() ? e.getPlayer().getInventory().getFreeSlots() : ruby);
							e.getPlayer().getInventory().addItem(1619, numToWithdraw);
							e.getPlayer().save("gemBagRuby", ruby-numToWithdraw);
						});
					if (diamond > 0)
						option("Withdraw diamonds", () -> {
							int numToWithdraw = (diamond > e.getPlayer().getInventory().getFreeSlots() ? e.getPlayer().getInventory().getFreeSlots() : diamond);
							e.getPlayer().getInventory().addItem(1617, numToWithdraw);
							e.getPlayer().save("gemBagDiamond", diamond-numToWithdraw);
						});
				}
			}));
			break;
		case "Empty":
			if (!e.getPlayer().getInventory().hasFreeSlots()) {
				e.getPlayer().sendMessage("You do not have enough inventory spaces to do that.");
				return;
			}

			int withdraw = (diamond > e.getPlayer().getInventory().getFreeSlots() ? e.getPlayer().getInventory().getFreeSlots() : diamond);
			if (withdraw > 0) {
				e.getPlayer().getInventory().addItem(1617, withdraw);
				e.getPlayer().save("gemBagDiamond", diamond-withdraw);
				e.getPlayer().sendMessage("You empty out " + withdraw + " diamon" + (withdraw > 1 ? "s" : "") +  " from your gem bag.");
			}

			withdraw = (ruby > e.getPlayer().getInventory().getFreeSlots() ? e.getPlayer().getInventory().getFreeSlots() : ruby);
			if (withdraw > 0) {
				e.getPlayer().getInventory().addItem(1619, withdraw);
				e.getPlayer().save("gemBagRuby", ruby-withdraw);
				e.getPlayer().sendMessage("You empty out " + withdraw +  (withdraw > 1 ? " rubies" : " ruby") + " from your gem bag.");
			}

			withdraw = (emerald > e.getPlayer().getInventory().getFreeSlots() ? e.getPlayer().getInventory().getFreeSlots() : emerald);
			if (withdraw > 0) {
				e.getPlayer().getInventory().addItem(1621, withdraw);
				e.getPlayer().save("gemBagEmerald", emerald-withdraw);
				e.getPlayer().sendMessage("You empty out " + withdraw + " emerald"  + (withdraw > 1 ? "s" : "") + " from your gem bag.");
			}

			withdraw = (sapphire > e.getPlayer().getInventory().getFreeSlots() ? e.getPlayer().getInventory().getFreeSlots() : sapphire);
			if (withdraw > 0) {
				e.getPlayer().getInventory().addItem(1623, withdraw);
				e.getPlayer().save("gemBagSapphire", sapphire-withdraw);
				e.getPlayer().sendMessage("You empty out " + withdraw + " sapphire" + (withdraw > 1 ? "s" : "") + " from your gem bag.");
			}
			break;
		}
	});

	public static ItemOnItemHandler handleItemOnGemBag = new ItemOnItemHandler(18338, new int[] { 1623, 1621, 1619, 1617 }, e -> {
		int current = getNumGemsInBag(e.getPlayer());
		int sapphire = e.getPlayer().getI("gemBagSapphire");
		int emerald = e.getPlayer().getI("gemBagEmerald");
		int ruby = e.getPlayer().getI("gemBagRuby");
		int diamond = e.getPlayer().getI("gemBagDiamond");
		int room = 0;

		if (current < 100)
			room = 100 - current;

		if (room == 0) {
			e.getPlayer().sendMessage("Your gem bag is already full.");
			return;
		}

		int gemsToStore = 0;
		switch (e.getUsedWith(18338).getId()) {
		case 1623:
			gemsToStore = e.getPlayer().getInventory().getNumberOf(1623);
			if (gemsToStore > room)
				gemsToStore = room;

			e.getPlayer().getInventory().deleteItem(1623, gemsToStore);
			e.getPlayer().save("gemBagSapphire", sapphire+gemsToStore);
			e.getPlayer().sendMessage("You store " + gemsToStore + " sapphire" + (gemsToStore > 1 ? "s" : "") + " in your gem bag.");
			break;
		case 1621:
			gemsToStore = e.getPlayer().getInventory().getNumberOf(1621);
			if (gemsToStore > room)
				gemsToStore = room;

			e.getPlayer().getInventory().deleteItem(1621, gemsToStore);
			e.getPlayer().save("gemBagEmerald", emerald+gemsToStore);
			e.getPlayer().sendMessage("You store " + gemsToStore + " emerald" + (gemsToStore > 1 ? "s" : "") + " in your gem bag.");
			break;
		case 1619:
			gemsToStore = e.getPlayer().getInventory().getNumberOf(1619);
			if (gemsToStore > room)
				gemsToStore = room;

			e.getPlayer().getInventory().deleteItem(1619, gemsToStore);
			e.getPlayer().save("gemBagRuby", ruby+gemsToStore);
			e.getPlayer().sendMessage("You store " + gemsToStore + (gemsToStore > 1 ? " rubies" : " ruby") + " in your gem bag.");
			break;
		case 1617:
			gemsToStore = e.getPlayer().getInventory().getNumberOf(1617);
			if (gemsToStore > room)
				gemsToStore = room;

			e.getPlayer().getInventory().deleteItem(1617, gemsToStore);
			e.getPlayer().save("gemBagDiamond", diamond+gemsToStore);
			e.getPlayer().sendMessage("You store " + gemsToStore + " diamond" + (gemsToStore > 1 ? "s" : "") +  "in your gem bag.");
			break;
		default:
			break;
		}
	});

	public static ItemOnObjectHandler handleGemBagOnObject = new ItemOnObjectHandler(new Object[] { "Bank", "Deposit Box", "Counter" }, e -> {
		if (e.getItem().getId() != 18338)
			return;
		if (e.isAtObject())
			if (ObjectDefinitions.getDefs(e.getObject().getId()).getName().contains("Bank") || ObjectDefinitions.getDefs(e.getObject().getId()).containsOptionIgnoreCase("bank")) {
				e.getPlayer().sendMessage("You store " + getNumGemsInBag(e.getPlayer()) + " gems in the bank.");
				int sapphire = e.getPlayer().getI("gemBagSapphire");
				int emerald = e.getPlayer().getI("gemBagEmerald");
				int ruby = e.getPlayer().getI("gemBagRuby");
				int diamond = e.getPlayer().getI("gemBagDiamond");

				e.getPlayer().getBank().addItem(new Item(1623, sapphire), true);
				e.getPlayer().getBank().addItem(new Item(1621, emerald), true);
				e.getPlayer().getBank().addItem(new Item(1619, ruby), true);
				e.getPlayer().getBank().addItem(new Item(1617, diamond), true);

				e.getPlayer().save("gemBagSapphire", 0);
				e.getPlayer().save("gemBagEmerald", 0);
				e.getPlayer().save("gemBagRuby", 0);
				e.getPlayer().save("gemBagDiamond", 0);
			}
	});

	//Lmao... forgot I need to add checks to make sure this doesn't trigger when someone is withdrawing from the gem bag.
	//	@ItemAddedToInventoryHandler(ids = {1617, 1619, 1621, 1623})
	//	public static void handleGemAddedToInventory(ItemAddedToInventoryEvent e) {
	//		int amtToAdd = e.getItem().getAmount();
	//		int totalGems = getNumGemsInBag(e.getPlayer());
	//		int sapphire = e.getPlayer().getInt("gemBagSapphire");
	//		int emerald = e.getPlayer().getInt("gemBagEmerald");
	//		int ruby = e.getPlayer().getInt("gemBagRuby");
	//		int diamond = e.getPlayer().getInt("gemBagDiamond");
	//		int qty = 0;
	//
	//		switch (e.getItem().getId()) {
	//			case 1623:
	//				if ((totalGems + amtToAdd) > 100)
	//					amtToAdd = 100-totalGems;
	//				e.getPlayer().save("gemBagSapphire", sapphire + amtToAdd);
	//				e.getPlayer().getInventory().deleteItem(1623,  amtToAdd);
	//				qty = ((totalGems + amtToAdd) > 100 ? 100-totalGems : amtToAdd);
	//				e.getPlayer().sendMessage(String.valueOf(qty) + (qty > 1 ? " sapphires are" : " sapphire is") + " added to your gem bag.");
	//				break;
	//			case 1621:
	//				if ((totalGems + amtToAdd) > 100)
	//					amtToAdd = 100-totalGems;
	//				e.getPlayer().save("gemBagEmerald", emerald + amtToAdd);
	//				e.getPlayer().getInventory().deleteItem(1621,  amtToAdd);
	//				qty = ((totalGems + amtToAdd) > 100 ? 100-totalGems : amtToAdd);
	//				e.getPlayer().sendMessage(String.valueOf(qty) + (qty > 1 ? " emeralds are" : " emerald is") + " added to your gem bag.");
	//				break;
	//			case 1619:
	//				if ((totalGems + amtToAdd) > 100)
	//					amtToAdd = 100-totalGems;
	//				e.getPlayer().save("gemBagRuby", ruby + amtToAdd);
	//				e.getPlayer().getInventory().deleteItem(1619,  amtToAdd);
	//				qty = ((totalGems + amtToAdd) > 100 ? 100-totalGems : amtToAdd);
	//				e.getPlayer().sendMessage(String.valueOf(qty) + (qty > 1 ? " rubies are" : " ruby is") + " added to your gem bag.");
	//				break;
	//			case 1617:
	//				if ((totalGems + amtToAdd) > 100)
	//					amtToAdd = 100-totalGems;
	//				e.getPlayer().save("gemBagDiamond", diamond + amtToAdd);
	//				e.getPlayer().getInventory().deleteItem(1617,  amtToAdd);
	//				e.getPlayer().sendMessage(((totalGems + amtToAdd) > 100 ? String.valueOf(100-totalGems) : String.valueOf(amtToAdd)) + " diamond(s) are added to your gem bag.");
	//				qty = ((totalGems + amtToAdd) > 100 ? 100-totalGems : amtToAdd);
	//				e.getPlayer().sendMessage(String.valueOf(qty) + (qty > 1 ? " diamonds are" : " diamond is") + " added to your gem bag.");
	//				break;
	//		}
	//	}

	public static int getNumGemsInBag(Player p) {
		int sapphire = p.getI("gemBagSapphire");
		int emerald = p.getI("gemBagEmerald");
		int ruby = p.getI("gemBagRuby");
		int diamond = p.getI("gemBagDiamond");

		if (sapphire == -1) {
			sapphire = 0;
			p.save("gemBagSapphire", sapphire);
		}
		if (emerald == -1) {
			emerald = 0;
			p.save("gemBagEmerald", emerald);
		}
		if (ruby == -1) {
			ruby = 0;
			p.save("gemBagRuby", ruby);
		}
		if (diamond == -1) {
			diamond = 0;
			p.save("gemBagDiamond", diamond);
		}

		return sapphire + emerald + ruby + diamond;
	}
}
