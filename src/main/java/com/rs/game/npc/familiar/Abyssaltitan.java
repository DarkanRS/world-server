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
package com.rs.game.npc.familiar;

import com.rs.game.player.Player;
import com.rs.game.player.content.skills.summoning.Summoning.Pouches;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.WorldTile;

public class Abyssaltitan extends Familiar {

	public Abyssaltitan(Player owner, Pouches pouch, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea) {
		super(owner, pouch, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
	}

	@Override
	public String getSpecialName() {
		return "Essence Shipment";
	}

	@Override
	public String getSpecialDescription() {
		return "Sends all your inventory and beast's essence to your bank.";
	}

	@Override
	public int getBOBSize() {
		return 7;
	}

	@Override
	public int getSpecialAmount() {
		return 6;
	}

	@Override
	public SpecialAttack getSpecialAttack() {
		return SpecialAttack.CLICK;
	}

	@Override
	public boolean submitSpecial(Object object) {
		if (getOwner().getBank().hasBankSpace()) {
			if (getBob().getBeastItems().getUsedSlots() == 0) {
				getOwner().sendMessage("You clearly have no essence.");
				return false;
			}
			getOwner().getBank().depositAllBob(false);
			getOwner().setNextSpotAnim(new SpotAnim(1316));
			getOwner().setNextAnimation(new Animation(7660));
			return true;
		}
		return false;
	}
}
