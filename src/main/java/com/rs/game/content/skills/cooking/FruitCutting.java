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

import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.actions.PlayerAction;

import java.util.HashMap;
import java.util.Map;

public class FruitCutting extends PlayerAction {

	CuttableFruit fruit;
	int option;
	int amount;

	public enum CuttableFruit {
		BANANA(1963, 3162), RED_BANANA(7572, 7574), LEMON(2102, 2104, 2106), ORANGE(2108, 2110, 2112), PINEAPPLE(2114, 2116, 2118), LIME(2120, 2122, 2124), WATERMELON(5982, 5984);

		int fruitId;
		int[] productIds;

		private static Map<Integer, CuttableFruit> items = new HashMap<>();

		public static CuttableFruit forId(int itemId) {
			return items.get(itemId);
		}

		static {
			for (CuttableFruit ingredient : CuttableFruit.values())
				items.put(ingredient.getFruitId(), ingredient);
		}

		private CuttableFruit(int fruitId, int... productIds) {
			this.fruitId = fruitId;
			this.productIds = productIds;
		}

		public int getFruitId() {
			return fruitId;
		}

		public int[] getProductIds() {
			return productIds;
		}
	}

	public FruitCutting(CuttableFruit fruit, int option, int quantity) {
		this.fruit = fruit;
		this.option = option;
		amount = quantity;
	}

	@Override
	public boolean start(Player player) {

		return true;
	}

	@Override
	public boolean process(Player player) {
		if (!player.getInventory().containsItem(fruit.getFruitId(), 1))
			return false;
		return true;
	}

	@Override
	public int processWithDelay(Player player) {
		if (!player.getInventory().containsItem(fruit.getFruitId(), 1)) {
			player.sendMessage("You have run out of fruit to cut.");
			return -1;
		}
		player.getInventory().deleteItem(fruit.getFruitId(), 1);
		player.getInventory().addItem(fruit.getProductIds()[option], 1);
		return 1;
	}

	@Override
	public void stop(Player player) {

	}

}
