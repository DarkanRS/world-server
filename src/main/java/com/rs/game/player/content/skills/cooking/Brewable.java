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
package com.rs.game.player.content.skills.cooking;

import java.util.HashMap;
import java.util.Map;

public enum Brewable {
	KELDA_STOUT(new int[] { 6118, 6118 }, null, 17, 6113, 1, 1),
	DWARVEN_STOUT(new int[] { 1913, 5747 }, new int[] { 5777, 5857 }, 19, 5994, 4, 215),
	ASGARNIAN_ALE(new int[] { 1905, 5739 }, new int[] { 5785, 5865 }, 24, 5996, 4, 248),
	GREENMANS_ALE(new int[] { 1909, 5743 }, new int[] { 5793, 5873 }, 29, 255, 4, 281),
	WIZARD_MIND_BOMB(new int[] { 1907, 5741 }, new int[] { 5801, 5881 }, 34, 5998, 4, 314),
	DRAGON_BITTER(new int[] { 1911, 5745 }, new int[] { 5809, 5889 }, 39, 6000, 4, 347),
	MOONLIGHT_MEAD(new int[] { 5763, 5749 }, new int[] { 5817, 5897 }, 44, 6004, 4, 380),
	AXEMANS_FOLLY(new int[] { 5751, 5753 }, new int[] { 5825, 5905 }, 49, 6043, 1, 413),
	CHEFS_DELIGHT(new int[] { 5755, 5757 }, new int[] { 5833, 5913 }, 54, 1975, 4, 446),
	SLAYERS_RESPITE(new int[] { 5759, 5761 }, new int[] { 5841, 5921 }, 59, 6002, 4, 479),
	CIDER(new int[] { 5763, 5765 }, new int[] { 5849, 5929 }, 14, 5992, 4, 182);

	private int[] beerGlassId;
	private int[] calquatId;
	private int levelRequirement;
	private int ingredient;
	private int ingredientAmount;
	private int xp;

	private static Map<Integer, Brewable> MAP = new HashMap<>();

	public static Brewable forId(int itemId) {
		return MAP.get(itemId);
	}

	static {
		for (final Brewable brewable : Brewable.values())
			MAP.put(brewable.ingredient, brewable);
	}

	private Brewable(int[] beerGlassId, int[] calquatId, int levelRequirement, int ingredient, int ingredientAmount, int xp) {
		this.beerGlassId = beerGlassId;
		this.calquatId = calquatId;
		this.levelRequirement = levelRequirement;
		this.ingredient = ingredient;
		this.ingredientAmount = ingredientAmount;
		this.xp = xp;
	}

	public int getLevelRequirement() {
		return levelRequirement;
	}

	public int getBeerGlassId(boolean mature) {
		return beerGlassId[mature ? 1 : 0];
	}

	public int getCalquatId(boolean mature) {
		return calquatId[mature ? 1 : 0];
	}

	public int getIngredient() {
		return ingredient;
	}

	public int getIngredientAmount() {
		return ingredientAmount;
	}

	public int getXp() {
		return xp;
	}

	public int getVatVal(int stage) {
		if (this == KELDA_STOUT)
			return 68 + stage;
		return (ordinal() * 6) + 4 + stage;
	}

	public int getBarrelVal(boolean mature) {
		if (this == KELDA_STOUT)
			return 3;
		return (ordinal() * 8) + (mature ? 128 : 0);
	}
}
