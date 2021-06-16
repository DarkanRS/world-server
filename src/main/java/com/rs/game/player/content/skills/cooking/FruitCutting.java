package com.rs.game.player.content.skills.cooking;

import java.util.HashMap;
import java.util.Map;

import com.rs.game.player.Player;
import com.rs.game.player.actions.Action;

public class FruitCutting extends Action {

	CuttableFruit fruit;
	int option;
	int amount;

	public enum CuttableFruit {
		BANANA(1963, 3162), RED_BANANA(7572, 7574), LEMON(2102, 2104, 2106), ORANGE(2108, 2110, 2112), PINEAPPLE(2114, 2116, 2118), LIME(2120, 2122, 2124), WATERMELON(5982, 5984);

		int fruitId;
		int[] productIds;

		private static Map<Integer, CuttableFruit> items = new HashMap<Integer, CuttableFruit>();

		public static CuttableFruit forId(int itemId) {
			return items.get(itemId);
		}

		static {
			for (CuttableFruit ingredient : CuttableFruit.values()) {
				items.put(ingredient.getFruitId(), ingredient);
			}
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
		this.amount = quantity;
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
		if (player.getInventory().containsItem(fruit.getFruitId(), 1)) {
			player.getInventory().deleteItem(fruit.getFruitId(), 1);
			player.getInventory().addItem(fruit.getProductIds()[option], 1);
		} else {
			player.sendMessage("You have run out of fruit to cut.");
			return -1;
		}
		return 1;
	}

	@Override
	public void stop(Player player) {

	}

}
