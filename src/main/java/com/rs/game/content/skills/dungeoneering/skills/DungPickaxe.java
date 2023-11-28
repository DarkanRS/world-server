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
import com.rs.lib.game.Animation;

public enum DungPickaxe {
	NOVITE(16295, 1, 13074, 1),
	BATHUS(16297, 10, 13075, 3),
	MARMAROS(16299, 20, 13076, 5),
	KRATONITE(16301, 30, 13077, 7),
	FRACTITE(16303, 40, 13078, 10),
	ZEPHYRIUM(16305, 50, 13079, 12),
	ARGONITE(16307, 60, 13080, 13),
	KATAGON(16309, 70, 13081, 15),
	GORGONITE(16311, 80, 13082, 16),
	PROMETHIUM(16313, 90, 13083, 17),
	PRIMAL(16315, 99, 13084, 20);

	private final int itemId, level, ticks;
	private final Animation animation;

	DungPickaxe(int itemId, int level, int animId, int ticks) {
		this.itemId = itemId;
		this.level = level;
		this.animation = new Animation(animId);
		this.ticks = ticks;
	}

	public int getItemId() {
		return itemId;
	}

	public int getLevel() {
		return level;
	}

	public int getTicks() {
		return ticks;
	}

	public Animation getAnimation() {
		return animation;
	}

	public static DungPickaxe getBest(Player player) {
		for (int i = DungPickaxe.values().length-1; i >= 0; i--) {
			DungPickaxe def = DungPickaxe.values()[i];
			if (player.getInventory().containsItem(def.getItemId()) || player.getEquipment().getWeaponId() == def.getItemId())
				if (player.getSkills().getLevel(Constants.MINING) >= def.getLevel())
					return def;
		}
		return null;
	}
}
