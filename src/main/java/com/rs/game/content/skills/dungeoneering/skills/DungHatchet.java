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
package com.rs.game.content.skills.dungeoneering.skills;

import com.rs.game.model.entity.player.Player;
import com.rs.lib.Constants;

public enum DungHatchet {
	NOVITE(16361, 1, 1.0, 13118),
	BATHUS(16363, 10, 1.1, 13119),
	MARMAROS(16365, 20, 1.2, 13120),
	KRATONITE(16367, 30, 1.3, 13121),
	FRACTITE(16369, 40, 1.4, 13122),
	ZEPHYRIUM(16371, 50, 1.5, 13123),
	ARGONITE(16373, 60, 1.6, 13124),
	KATAGON(16373, 70, 1.7, 13125),
	GORGONITE(16375, 80, 1.8, 13126),
	PROMETHIUM(16379, 90, 1.9, 13127),
	PRIMAL(16381, 99, 2.0, 13128);

	private final int itemId, useLevel, emoteId;
	private final double toolMod;

	DungHatchet(int itemId, int useLevel, double toolMod, int emoteId) {
		this.itemId = itemId;
		this.useLevel = useLevel;
		this.toolMod = toolMod;
		this.emoteId = emoteId;
	}

	public int getItemId() {
		return itemId;
	}

	public int getUseLevel() {
		return useLevel;
	}

	public double getToolMod() {
		return toolMod;
	}

	public int getEmoteId() {
		return emoteId;
	}

	public static DungHatchet getHatchet(Player player) {
		for (int i = DungHatchet.values().length-1; i >= 0; i--) {
			DungHatchet def = DungHatchet.values()[i];
			if (player.getInventory().containsItem(def.getItemId()) || player.getEquipment().getWeaponId() == def.getItemId())
				if (player.getSkills().getLevel(Constants.WOODCUTTING) >= def.getUseLevel())
					return def;
		}
		return null;
	}
}
