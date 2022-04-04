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
package com.rs.game.content.skills.summoning.familiars;

import com.rs.game.content.skills.summoning.Summoning.Pouches;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.WorldTile;

public class Unicornstallion extends Familiar {

	public Unicornstallion(Player owner, Pouches pouch, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea) {
		super(owner, pouch, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
	}

	@Override
	public String getSpecialName() {
		return "Healing Aura";
	}

	@Override
	public String getSpecialDescription() {
		return "Heals 15% of your health points.";
	}

	@Override
	public int getBOBSize() {
		return 0;
	}

	@Override
	public int getSpecialAmount() {
		return 20;
	}

	@Override
	public boolean isAgressive() {
		return false;
	}

	@Override
	public SpecialAttack getSpecialAttack() {
		return SpecialAttack.CLICK;
	}

	@Override
	public boolean submitSpecial(Object object) {
		Player player = (Player) object;
		if (player.getHitpoints() == player.getMaxHitpoints()) {
			player.sendMessage("You need to have at least some damage before being able to heal yourself.");
			return false;
		}
		player.setNextAnimation(new Animation(7660));
		player.setNextSpotAnim(new SpotAnim(1300));
		int percentHealed = player.getMaxHitpoints() / 15;
		player.heal(percentHealed);
		return true;
	}

}
