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
package com.rs.game.content.world.unorganized_dialogue;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.statements.MakeXStatement;
import com.rs.engine.dialogue.statements.MakeXStatement.MakeXType;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.actions.PlayerAction;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;

public class LeatherCraftingD extends Conversation {

	public static final int DUNG_NEEDLE = 17446;

	public static final int[] BASE_LEATHER = { 1741, 1743, 1745, 2505, 2507, 2509, 24374, 22451, 22449, 22450, 6289, 10820 };

	public static final double[][] EXPERIENCE = {
			{ 13.8, 16.25, 18.5, 22, 25, 27, 37 },
			{ 35 },
			{ 62, 124, 186 },
			{ 70, 140, 210 },
			{ 78, 156, 234 },
			{ 86, 172, 258 },
			{ 94, 188, 282 },
			{ 100, 300, 1000 },
			{ 10, 20, 30 },
			{ 50, 150, 500 },
			{ 30, 35, 45, 50, 55 },
			{ 32, 32 }
	};

	private int[][] LEVELS = {
			{ 1, 7, 9, 11, 14, 18, 38 },
			{ 28 },
			{ 57, 60, 63 },
			{ 66, 68, 71 },
			{ 73, 75, 77 },
			{ 79, 82, 84 },
			{ 87, 89, 93 },
			{ 86, 92, 98 },
			{ 3, 12, 21 },
			{ 65, 72, 78 },
			{ 45, 47, 48, 51, 53 },
			{ 43, 46 }
	};

	private static final Item[][] POTENTIAL_PRODUCTS = {
			{ new Item(1059, 1), new Item(1061, 1), new Item(1167, 1), new Item(1063, 1), new Item(1129, 1), new Item(1095, 1), new Item(1169, 1) },
			{ new Item(1131, 1) },
			{ new Item(1065, 1), new Item(1099, 2), new Item(1135, 3) },
			{ new Item(2487, 1), new Item(2493, 2), new Item(2499, 3) },
			{ new Item(2489, 1), new Item(2495, 2), new Item(2501, 3) },
			{ new Item(2491, 1), new Item(2497, 2), new Item(2503, 3) },
			{ new Item(24376, 1), new Item(24379, 2), new Item(24382, 3) },
			{ new Item(22482, 500), new Item(22486, 1500), new Item(22490, 5000) },
			{ new Item(22458, 300), new Item(22462, 1000), new Item(22466, 2500) },
			{ new Item(22470, 400), new Item(22474, 1200), new Item(22478, 3500) },
			{ new Item(6328, 6), new Item(6330, 8), new Item(6326, 5), new Item(6324, 12), new Item(6322, 15) },
			{ new Item(10824, 1), new Item(10822, 2) }
	};
	private static final Item[][] REQUIRED_BASE_ITEMS = { null, null, null, null, null, null, null,
			new Item[] { new Item(22452, 1), new Item(22454, 1), new Item(22456, 1) },
			new Item[] { new Item(22452, 1), new Item(22454, 1), new Item(22456, 1) },
			new Item[] { new Item(22452, 1), new Item(22454, 1), new Item(22456, 1) }, null, null
	};
	
	public LeatherCraftingD(Player player, int index) {
		super(player);
		
		int[] ids = new int[POTENTIAL_PRODUCTS[index].length];
		String[] names = new String[POTENTIAL_PRODUCTS[index].length];
		for (int i = 0; i < ids.length; i++) {
			ids[i] = POTENTIAL_PRODUCTS[index][i].getId();
			if (player.getSkills().getLevel(Constants.CRAFTING) < LEVELS[index][i])
				names[i] = "<col=ff0000>" + ItemDefinitions.getDefs(ids[i]).name + "<br><col=ff0000>Level " + LEVELS[index][i];
			else
				names[i] = ItemDefinitions.getDefs(ids[i]).name;
		}
		
		Dialogue makeX = addNext(new MakeXStatement(MakeXType.MAKE, 28, "Choose how many you wish to make,<br>then click on the item to begin.", ids, names));
		
		for (int i = 0; i < ids.length; i++) {
			final int componentIndex = i;
			makeX.addNext(() -> {
				player.getActionManager().setAction(new PlayerAction() {

					int ticks;

					@Override
					public boolean start(final Player player) {
						if (!checkAll(player))
							return false;
						int leatherAmount = player.getInventory().getAmountOf(BASE_LEATHER[index]);
						int requestedAmount = MakeXStatement.getQuantity(player);
						if (requestedAmount > leatherAmount)
							requestedAmount = leatherAmount;
						setTicks(requestedAmount);
						return true;
					}

					public void setTicks(int ticks) {
						this.ticks = ticks;
						player.getInventory().deleteItem(1734, 1);
					}

					public boolean checkAll(Player player) {
						final int levelReq = LEVELS[index][componentIndex];
						if (player.getSkills().getLevel(Constants.CRAFTING) < levelReq) {
							player.sendMessage("You need a Crafting level of " + levelReq + " to craft this hide.");
							return false;
						}
						if (player.getInventory().getItems().getNumberOf(BASE_LEATHER[index]) < POTENTIAL_PRODUCTS[index][componentIndex].getAmount()) {
							player.sendMessage("You don't have enough hides in your inventory.");
							return false;
						}
						if (!player.getInventory().containsOneItem(1734)) {
							player.sendMessage("You need a thread in order to bind the tanned hides together.");
							return false;
						}
						if (!player.getInventory().containsItem(1733, 1)) {
							player.sendMessage("You need a needle in order to bind the tanned hides together.");
							return false;
						}
						Item[] extraItems = REQUIRED_BASE_ITEMS[index];
						if (extraItems != null) {
							Item item = extraItems[componentIndex];
							if (!player.getInventory().containsItem(item.getId(), item.getAmount())) {
								player.sendMessage("You need a " + item.getName().toLowerCase() + ".");
								return false;
							}
						}
						return true;
					}

					@Override
					public boolean process(Player player) {
						return checkAll(player) && ticks > 0;
					}

					@Override
					public int processWithDelay(Player player) {
						ticks--;
						if (ticks % 4 == 0)
							player.getInventory().deleteItem(1734, 1);
						Item item = POTENTIAL_PRODUCTS[index][componentIndex];
						player.getInventory().deleteItem(new Item(BASE_LEATHER[index], item.getAmount()));
						player.getInventory().addItem(item.getId(), 1);
						player.getSkills().addXp(Constants.CRAFTING, EXPERIENCE[index][componentIndex]);
						Item[] extraItems = REQUIRED_BASE_ITEMS[index];
						if (extraItems != null)
							player.getInventory().deleteItem(extraItems[componentIndex]);
						player.setNextAnimation(new Animation(1249));
						return 3;
					}

					@Override
					public void stop(Player player) {
						setActionDelay(player, 3);
					}
				});
			});
		}
		
		create();
	}

	public static int getIndex(int requestedId) {
		for (int index = 0; index < BASE_LEATHER.length; index++) {
			int baseId = BASE_LEATHER[index];
			if (requestedId == baseId)
				return index;
		}
		return -1;
	}

	public static boolean isExtraItem(int requestedId) {
		for (Item[] items : REQUIRED_BASE_ITEMS) {
			if (items == null)
				continue;
			for (Item item : items)
				if (item.getId() == requestedId)
					return true;
		}
		return false;
	}

}
