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
//  Copyright © 2021 Trenton Kress
//  This file is part of project: Darkan
//
package com.rs.game.player.dialogues;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.player.Player;
import com.rs.game.player.actions.Action;
import com.rs.game.player.content.SkillsDialogue;
import com.rs.game.player.content.SkillsDialogue.ItemNameFilter;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.Constants;
import com.rs.lib.game.Item;

public class TanningD extends Dialogue {

	public static final int[][] TANNING_PRICES = { { 1, 3, 15, 20, 20, 20, 20, 20, 20 }, { 2, 5, 25, 45, 45, 45, 45, 45, 45 } };
	public static final int[] INGREDIENT = { 1739, 1739, 6287, 1753, 1751, 1749, 1747, 24372 };
	public static final int[] PRODUCT = { 1741, 1743, 6289, 1745, 2505, 2507, 2509, 24374 };
	public static final int[] LEVELS = { 1, 28, 45, 45, 57, 66, 73, 79, 87 };

	private boolean isCanfis;
	private int npcId;

	@Override
	public void start() {
		npcId = (int) parameters[0];
		isCanfis = npcId == 1041;
		SkillsDialogue.sendSkillsDialogue(player, SkillsDialogue.MAKE_ALL, "How many hides would you like to tan?<br>Choose a number, then click the hide to begin.", 28, PRODUCT, new ItemNameFilter() {
			int count = 0;

			@Override
			public String rename(String name) {
				int levelRequired = LEVELS[count++];
				if (player.getSkills().getLevel(Constants.CRAFTING) < levelRequired)
					name = "<col=ff0000>" + name + "<br><col=ff0000>Level " + levelRequired;
				return name;
			}
		});
	}

	@Override
	public void run(int interfaceId, final int componentId) {
		final int componentIndex = SkillsDialogue.getItemSlot(componentId);
		if (componentIndex > INGREDIENT.length) {
			end();
			return;
		}
		player.getActionManager().setAction(new Action() {

			int ticks, price = TANNING_PRICES[isCanfis ? 1 : 0][componentIndex];

			@Override
			public boolean start(final Player player) {
				int leatherAmount = player.getInventory().getAmountOf(INGREDIENT[componentIndex]);
				if (leatherAmount == 0) {
					end();
					WorldTasks.schedule(new WorldTask() {

						@Override
						public void run() {
							player.getDialogueManager().execute(new SimpleNPCMessage(), npcId, "Ahhh... novice mistake, you must bring me at least one " + ItemDefinitions.getDefs(INGREDIENT[componentIndex]).getName().toLowerCase() + " in order to tan " + ItemDefinitions.getDefs(PRODUCT[componentIndex]).getName().toLowerCase() + ".");
						}
					});
					return false;
				}
				int requestedAmount = SkillsDialogue.getQuantity(player);
				int maximumQuantity = price == 0 ? requestedAmount : player.getInventory().getNumberOf(995) / price;
				if (maximumQuantity > 0) {
					if (requestedAmount > maximumQuantity)
						requestedAmount = maximumQuantity;
					if (requestedAmount > leatherAmount)
						requestedAmount = leatherAmount;
					ticks = requestedAmount;
					return true;
				}
				return false;
			}

			@Override
			public boolean process(Player player) {
				if (player.getInventory().getNumberOf(995) < price) {
					end();
					player.getDialogueManager().execute(new SimpleNPCMessage(), npcId, "Oh no, it looks like you've ran out of coins. Come back later once you have " + price + " coins.");
					return false;
				}
				return ticks > 0;
			}

			@Override
			public int processWithDelay(Player player) {
				ticks--;
				player.getInventory().deleteItem(new Item(INGREDIENT[componentIndex], 1));
				player.getInventory().addItem(new Item(PRODUCT[componentIndex], 1));
				if (price != 0)
					player.getInventory().deleteItem(new Item(995, price));
				return 0;
			}

			@Override
			public void stop(Player player) {
				setActionDelay(player, 3);
			}
		});
		end();
	}

	@Override
	public void finish() {
	}
}
