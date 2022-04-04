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

import com.rs.game.content.ItemConstants;
import com.rs.game.content.skills.summoning.Summoning.Pouches;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.WorldTile;

public class Packyak extends Familiar {

	public Packyak(Player owner, Pouches pouch, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea) {
		super(owner, pouch, tile, mapAreaNameHash, false);
	}

	@Override
	public int getSpecialAmount() {
		return 12;
	}

	@Override
	public String getSpecialName() {
		return "Winter Storage";
	}

	@Override
	public String getSpecialDescription() {
		return "Use special move on an item in your inventory to send it to your bank.";
	}

	@Override
	public SpecialAttack getSpecialAttack() {
		return SpecialAttack.ITEM;
	}

	@Override
	public int getBOBSize() {
		return 30;
	}

	@Override
	public boolean isAgressive() {
		return false;
	}

	@Override
	public boolean submitSpecial(Object object) {
		int slotId = (Integer) object;
		if (ItemConstants.isDungItem(getOwner().getInventory().getItem(slotId).getId())) {
			getOwner().sendMessage("I don't know how you managed to get a yak in here, but do you think");
			getOwner().sendMessage("the owner is retarded enough to let you bank a dung item with this scroll?");
			return false;
		}
		if (getSpecialEnergy() <= getSpecialAmount()) {
			getOwner().sendMessage("Your familiar does not have enough special energy.");
			return false;
		}
		if (getOwner().getBank().hasBankSpace()) {
			getOwner().incrementCount("Items banked with yak");
			getOwner().getInventory().deleteItem(12435, 1);
			getOwner().getBank().depositItem(slotId, 1, true);
			getOwner().sendMessage("Your pack yak has sent an item to your bank.");
			getOwner().setNextSpotAnim(new SpotAnim(1316));
			getOwner().setNextAnimation(new Animation(7660));
			drainSpecial(getSpecialAmount());
		}
		return true;
	}
}
