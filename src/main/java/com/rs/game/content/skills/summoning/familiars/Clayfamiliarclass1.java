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

import com.rs.game.content.controllers.StealingCreationController;
import com.rs.game.content.minigames.creations.Score;
import com.rs.game.content.skills.summoning.Summoning.Pouches;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.WorldTile;

public class Clayfamiliarclass1 extends Familiar {

	public Clayfamiliarclass1(Player owner, Pouches pouch, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea) {
		super(owner, pouch, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
	}

	@Override
	public String getSpecialName() {
		return "Clay Deposit";
	}

	@Override
	public String getSpecialDescription() {
		return "Deposit all items in the beast of burden's possession in exchange for points.";
	}

	@Override
	public int getBOBSize() {
		return 1;
	}

	@Override
	public int getSpecialAmount() {
		return 30;
	}

	@Override
	public SpecialAttack getSpecialAttack() {
		return SpecialAttack.CLICK;
	}

	@Override
	public boolean submitSpecial(Object object) {
		if (getOwner().getControllerManager().getController() == null || !(getOwner().getControllerManager().getController() instanceof StealingCreationController)) {
			dissmissFamiliar(false);
			return false;
		}
		getOwner().setNextSpotAnim(new SpotAnim(1316));
		getOwner().setNextAnimation(new Animation(7660));
		StealingCreationController sc = (StealingCreationController) getOwner().getControllerManager().getController();
		Score score = sc.getGame().getScore(getOwner());
		if (score == null)
			return false;
		for (Item item : getBob().getBeastItems().array()) {
			if (item == null)
				continue;
			sc.getGame().sendItemToBase(getOwner(), item, sc.getTeam(), true, false);
		}
		return true;
	}
}
