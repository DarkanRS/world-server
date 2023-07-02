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
package com.rs.game.content.skills.mining;

import com.rs.game.model.entity.player.Player;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;

public enum Pickaxe {
	BRONZE(1265, 1, 625, 8),
	BRONZE_G(20780, 1, 234, 8),
	IRON(1267, 1, 626, 7),
	IRON_G(20781, 1, 235, 7),
	STEEL(1269, 6, 627, 6),
	STEEL_G(20782, 6, 236, 6),
	MITHRIL(1273, 21, 629, 5),
	MITHRIL_G(20784, 21, 238, 5),
	ADAMANT(1271, 31, 628, 4),
	ADAMANT_G(20783, 31, 237, 4),
	RUNE(1275, 41, 624, 3),
	RUNE_G(20785, 41, 249, 3),
	DRAGON(15259, 61, 12189, 3),
	DRAGON_G(20786, 61, 250, 3),
	INFERNO_ADZE(13661, 61, 10222, 3);

	private int itemId, level, ticks;
	private int animId;

	private Pickaxe(int itemId, int level, int animId, int ticks) {
		this.itemId = itemId;
		this.level = level;
		this.animId = animId;
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

	public int getAnimId() {
		return animId;
	}

	public static Pickaxe getBest(Player player) {
		for (int i = Pickaxe.values().length-1; i >= 0; i--) {
			Pickaxe def = Pickaxe.values()[i];
			if (player.getInventory().containsItem(def.itemId) || player.getEquipment().getWeaponId() == def.itemId)
				if (player.getSkills().getLevel(Constants.MINING) >= def.level)
					return def;
		}
		return null;
	}
}
