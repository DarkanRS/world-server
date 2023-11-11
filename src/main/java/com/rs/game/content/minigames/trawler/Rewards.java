package com.rs.game.content.minigames.trawler;

import com.rs.game.content.skills.fishing.Fish;
import com.rs.game.model.item.ItemsContainer;
import com.rs.lib.game.Item;
import com.rs.lib.util.Utils;
import com.rs.utils.drop.DropList;
import com.rs.utils.drop.DropTable;

public class Rewards {
	private enum Rate {
		LOWEST(11, 1),
		LOW(28, 10),
		MEDIUM(63, 43),
		HIGH(150, 201);

		private final int low, high;

		Rate(int low, int high) {
			this.low = low;
			this.high = high;
		}
	}

	public static ItemsContainer<Item> generateRewards(int level, int amount) {
		ItemsContainer<Item> rewards = new ItemsContainer<>(20, true);
		DropTable[] tables = new DropTable[Rate.values().length];
		for (int i = 0;i < Rate.values().length;i++) {
			double perc = (double)level / 99.0D;
			int chance = Utils.clampI((int)((double)Rate.values()[i].low + ((double)Rate.values()[i].high - (double)Rate.values()[i].low) * perc), 0, 256);
			tables[i] = new DropTable(chance, 256, getFishIdAtTier(level, i), 1);
		}
		DropList list = new DropList(tables);
		for (int i = 0;i < amount;i++)
			rewards.addAll(list.genDrop());
		return rewards;
	}

	private static final int[] FISH_ORDER = { Fish.SHRIMP.getFishId(), Fish.SHRIMP.getFishId(), Fish.SARDINES.getFishId(), Fish.ANCHOVIES.getFishId(), Fish.OYSTER.getFishId(), Fish.TUNA.getFishId(), Fish.LOBSTER.getFishId(), Fish.SWORDFISH.getFishId(), Fish.SHARK.getFishId(), 395, 389 };

	public static int getFishIdAtTier(int level, int tier) {
		if (level >= 81)
			return FISH_ORDER[7 + tier];
		if (level >= 79)
			return FISH_ORDER[6 + tier];
		if (level >= 76)
			return FISH_ORDER[5 + tier];
		if (level >= 50)
			return FISH_ORDER[4 + tier];
		if (level >= 40)
			return FISH_ORDER[3 + tier];
		if (level >= 35)
			return FISH_ORDER[2 + tier];
		if (level >= 16)
			return FISH_ORDER[1 + tier];
		return FISH_ORDER[tier];
	}
}
