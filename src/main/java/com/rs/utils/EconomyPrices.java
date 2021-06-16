package com.rs.utils;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.player.content.ItemConstants;
import com.rs.lib.game.Item;

public final class EconomyPrices {

	public static int getPrice(int itemId) {
		ItemDefinitions defs = ItemDefinitions.getDefs(itemId);
		if (defs.isNoted())
			itemId = defs.getCertId();
		else if (defs.isLended())
			itemId = defs.getLendId();
		if (!ItemConstants.isTradeable(new Item(itemId, 1)))
			return 0;
		if (itemId == 12183)
			return 25;
		if (itemId == 995) // TODO after here
			return 1;
		return defs.getHighAlchPrice(); // TODO get price from real item from saved
									// prices from ge
	}

	private EconomyPrices() {

	}
}
