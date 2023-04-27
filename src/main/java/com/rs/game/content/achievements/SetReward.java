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
package com.rs.game.content.achievements;

import com.rs.game.content.achievements.AchievementDef.Area;
import com.rs.game.content.achievements.AchievementDef.Difficulty;
import com.rs.game.model.entity.player.Player;

import java.util.HashMap;
import java.util.Map;

public enum SetReward {
	KARAMJA_GLOVES(Area.KARAMJA, new Difficulty[] { Difficulty.EASY, Difficulty.MEDIUM, Difficulty.HARD, Difficulty.ELITE }, new int[] { 11136, 11138, 11140, 19754 }),
	VARROCK_ARMOR(Area.VARROCK, new Difficulty[] { Difficulty.EASY, Difficulty.MEDIUM, Difficulty.HARD, Difficulty.ELITE }, new int[] { 11756, 11757, 11758, 19757 }),
	EXPLORERS_RING(Area.LUMBRIDGE, new Difficulty[] { Difficulty.BEGINNER, Difficulty.EASY, Difficulty.MEDIUM, Difficulty.HARD }, new int[] { 13560, 13561, 13562, 19760 }),
	FREMENNIK_BOOTS(Area.FREMENNIK, new Difficulty[] { Difficulty.EASY, Difficulty.MEDIUM, Difficulty.HARD, Difficulty.ELITE }, new int[] { 14571, 14572, 14573, 19766 }),
	FALADOR_SHIELD(Area.FALADOR, new Difficulty[] { Difficulty.EASY, Difficulty.MEDIUM, Difficulty.HARD, Difficulty.ELITE }, new int[] { 14577, 14578, 14579, 19749 }),
	SEERS_HEADBAND(Area.SEERS, new Difficulty[] { Difficulty.EASY, Difficulty.MEDIUM, Difficulty.HARD, Difficulty.ELITE }, new int[] { 14631, 14662, 14663, 19763 }),
	ARDOUGNE_CLOAK(Area.ARDOUGNE, new Difficulty[] { Difficulty.EASY, Difficulty.MEDIUM, Difficulty.HARD, Difficulty.ELITE }, new int[] { 15345, 15347, 15349, 19748 }),
	MORYTANIA_LEGS(Area.MORYTANIA, new Difficulty[] { Difficulty.EASY, Difficulty.MEDIUM, Difficulty.HARD, Difficulty.ELITE }, new int[] { 24134, 24135, 24136, 24137 });

	private Area area;
	private Difficulty[] diffs;
	private int[] itemIds;

	private static Map<Integer, SetReward> REWARDS = new HashMap<>();
	private static Map<Area, SetReward> AREAS = new HashMap<>();

	static {
		for (SetReward rew : SetReward.values()) {
			for (int id : rew.itemIds)
				REWARDS.put(id, rew);
			AREAS.put(rew.area, rew);
		}
	}

	public static SetReward forId(int id) {
		return REWARDS.get(id);
	}

	public static SetReward forArea(Area area) {
		return AREAS.get(area);
	}

	private SetReward(Area area, Difficulty[] diffs, int[] itemIds) {
		this.area = area;
		this.diffs = diffs;
		this.itemIds = itemIds;
	}

	public int[] getItemIds() {
		return itemIds;
	}

	public boolean hasRequirements(Player player, Area area, Difficulty difficulty) {
		return hasRequirements(player, area, difficulty, true);
	}

	public boolean hasRequirements(Player player, Area area, Difficulty difficulty, boolean print) {
		return AchievementDef.meetsRequirements(player, area, difficulty, print);
	}

	public boolean hasRequirements(Player player, int itemId) {
		for (int i = 0;i < diffs.length;i++) {
			if (itemIds[i] != itemId)
				continue;
			if (!AchievementDef.meetsRequirements(player, area, diffs[i]))
				return false;
		}
		return true;
	}
	public boolean hasRequirements(Player player, int itemId, boolean print) {
		for (int i = 0;i < diffs.length;i++) {
			if (itemIds[i] != itemId)
				continue;
			if (!AchievementDef.meetsRequirements(player, area, diffs[i], print))
				return false;
		}
		return true;
	}
}
