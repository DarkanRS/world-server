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

import com.rs.game.content.skills.summoning.Summoning.Pouch;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.WorldTile;

public class Spiritterrorbird extends Familiar {

	public Spiritterrorbird(Player owner, Pouch pouch, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea) {
		super(owner, pouch, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
	}

	@Override
	public String getSpecialName() {
		return "Tireless Run";
	}

	@Override
	public String getSpecialDescription() {
		return "Restores the player's run energy, by half the players agility level rounded up.";
	}

	@Override
	public int getBOBSize() {
		return 12;
	}

	@Override
	public int getSpecialAmount() {
		return 8;
	}

	@Override
	public SpecialAttack getSpecialAttack() {
		return SpecialAttack.CLICK;
	}

	@Override
	public boolean submitSpecial(Object object) {
		Player player = (Player) object;
		if (player.getRunEnergy() == 100) {
			player.sendMessage("This wouldn't effect you at all.");
			return false;
		}
		int newLevel = getOwner().getSkills().getLevel(Constants.AGILITY) + 2;
		int runEnergy = (int) (player.getRunEnergy() + (Math.round(newLevel / 2)));
		if (newLevel > getOwner().getSkills().getLevelForXp(Constants.AGILITY) + 2)
			newLevel = getOwner().getSkills().getLevelForXp(Constants.AGILITY) + 2;
		setNextAnimation(new Animation(8229));
		player.setNextSpotAnim(new SpotAnim(1300));
		player.setNextAnimation(new Animation(7660));
		player.getSkills().set(Constants.AGILITY, newLevel);
		player.setRunEnergy(runEnergy > 100 ? 100 : runEnergy);
		return true;
	}
}
