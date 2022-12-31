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
package com.rs.game.content.pets;

import com.rs.game.model.entity.player.Player;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;

public class Incubator {

	public static enum Egg {
		PEGUIN_EGG(30, 12483, 12481),
		RAVEN_EGG(50, 11964, 12484),
		BIRD_EGG_ZAMMY(70, 5076, 12506),
		BIRD_EGG_SARA(70, 5077, 12503),
		BIRD_EGG_GUTHIX(70, 5078, 12509),
		VULTURE_EGG(85, 11965, 12498),
		CHAMELEON_EGG(90, 12494, 12492),
		RED_DRAGON_EGG(99, 12477, 12469),
		BLUE_DRAGON_EGG(99, 12478, 12471),
		GREEN_DRAGON_EGG(99, 12479, 12473),
		BLACK_DRAGON_EGG(99, 12480, 12475);

		private int summoningLevel, eggId, petId;

		private Egg(int summoningLevel, int eggId, int petId) {
			this.summoningLevel = summoningLevel;
			this.eggId = eggId;
			this.petId = petId;
		}
	}

	public static Egg getEgg(int itemId) {
		for (Egg egg : Egg.values())
			if (egg.eggId == itemId)
				return egg;
		return null;
	}

	public static boolean useEgg(Player player, int itemId) {
		Egg egg = getEgg(itemId);
		if (egg == null)
			return false;
		if (player.getSkills().getLevelForXp(Constants.SUMMONING) < egg.summoningLevel) {
			player.sendMessage("You need a level of " + egg.summoningLevel + " summoning to hatch this egg.");
			return true;
		}
		player.lock(1);
		player.setNextAnimation(new Animation(833));
		player.getInventory().deleteItem(itemId, 1);
		player.getInventory().addItem(egg.petId, 1);
		player.sendMessage("You put the egg in the incubator and it hatches.");
		return true;
	}
}
