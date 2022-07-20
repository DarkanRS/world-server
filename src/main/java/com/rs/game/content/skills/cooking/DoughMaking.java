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
package com.rs.game.content.skills.cooking;

import com.rs.game.content.dialogue.Conversation;
import com.rs.game.content.dialogue.statements.MakeXStatement;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.actions.PlayerAction;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ItemOnItemEvent;
import com.rs.plugin.handlers.ItemOnItemHandler;

@PluginEventHandler
public class DoughMaking  {

	public static int POT_OF_FLOUR = 1933;
	public static int JUG_OF_WATER = 1937;
	public static int BOWL_OF_WATER = 1921;
	public static int BUCKET_OF_WATER = 1929;

	static class DoughMakeD extends Conversation {

		public DoughMakeD(Player player) {
			super(player);
			
			int[] items = new int[] { 2307, 1953, 2283, 1863 };
			var makeX = addNext(new MakeXStatement(items, getMaxAmount()));
			for (int itemId : items)
				makeX.addNext(() -> player.getActionManager().setAction(new DoughMakeAction(itemId)));
		}

		public int getAmountOfWaterItems() {
			return player.getInventory().getAmountOf(JUG_OF_WATER) + player.getInventory().getAmountOf(BOWL_OF_WATER) + player.getInventory().getAmountOf(BUCKET_OF_WATER);
		}

		public int getMaxAmount() {
			int min = player.getInventory().getAmountOf(POT_OF_FLOUR);
			if (min > getAmountOfWaterItems())
				min = getAmountOfWaterItems();
			return min;
		}
	}

	static class DoughMakeAction extends PlayerAction {

		private int doughId;

		public DoughMakeAction(int doughId) {
			this.doughId = doughId;
		}

		@Override
		public boolean start(Player player) {
			if (!player.getInventory().hasFreeSlots() || !player.getInventory().containsItem(POT_OF_FLOUR, 1) || !player.getInventory().containsItem(POT_OF_FLOUR, 1) || !player.getInventory().containsItem(POT_OF_FLOUR, 1)
					|| !player.getInventory().containsItem(POT_OF_FLOUR, 1))
				return false;
			return true;
		}

		@Override
		public boolean process(Player player) {
			if (!player.getInventory().hasFreeSlots() || !player.getInventory().containsItem(POT_OF_FLOUR, 1) || !player.getInventory().containsItem(POT_OF_FLOUR, 1) || !player.getInventory().containsItem(POT_OF_FLOUR, 1)
					|| !player.getInventory().containsItem(POT_OF_FLOUR, 1))
				return false;
			return true;
		}

		@Override
		public int processWithDelay(Player player) {
			if (!player.getInventory().hasFreeSlots()) {
				player.sendMessage("You don't have enough inventory space.");
				stop(player);
			} else {
				if (player.getInventory().containsItem(BOWL_OF_WATER, 1)) {
					player.getInventory().deleteItem(BOWL_OF_WATER, 1);
					player.getInventory().addItem(1923, 1);
				} else if (player.getInventory().containsItem(BUCKET_OF_WATER, 1)) {
					player.getInventory().deleteItem(BUCKET_OF_WATER, 1);
					player.getInventory().addItem(1925, 1);
				} else if (player.getInventory().containsItem(JUG_OF_WATER, 1)) {
					player.getInventory().deleteItem(JUG_OF_WATER, 1);
					player.getInventory().addItem(1935, 1);
				}
				player.getInventory().deleteItem(POT_OF_FLOUR, 1);
				player.getInventory().addItem(1931, 1);
				player.getInventory().addItem(doughId, 1);
			}
			return 2;
		}

		@Override
		public void stop(Player player) {

		}

	}

	public static ItemOnItemHandler makeDough = new ItemOnItemHandler(POT_OF_FLOUR, new int[] { BUCKET_OF_WATER, BOWL_OF_WATER, JUG_OF_WATER }) {
		@Override
		public void handle(ItemOnItemEvent e) {
			e.getPlayer().startConversation(new DoughMakeD(e.getPlayer()));
		}
	};

}
