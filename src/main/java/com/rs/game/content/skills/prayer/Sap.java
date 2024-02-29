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
package com.rs.game.content.skills.prayer;

import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.player.Player;

public enum Sap {
	WARRIOR(Prayer.SAP_WARRIOR, 2214, 2215, 2216),
	RANGE(Prayer.SAP_RANGE, 2217, 2218, 2219),
	MAGE(Prayer.SAP_MAGE, 2220, 2221, 2222),
	SPIRIT(Prayer.SAP_SPIRIT, 2223, 2224, 2225);

	private final Prayer prayer;

	private final int spotAnimStart;
    private final int projAnim;
    private final int spotAnimHit;

	private Sap(Prayer prayer, int spotAnimStart, int projAnim, int spotAnimHit) {
		this.prayer = prayer;
		this.spotAnimStart = spotAnimStart;
		this.projAnim = projAnim;
		this.spotAnimHit = spotAnimHit;
	}

	public Prayer getPrayer() {
		return prayer;
	}

	public int getSpotAnimStart() {
		return spotAnimStart;
	}

	public int getProjAnim() {
		return projAnim;
	}

	public int getSpotAnimHit() {
		return spotAnimHit;
	}

	public void activate(Player player, Entity target) {
		//	if (target.getPrayer().reachedMax(0)) {
		//		target.sendMessage("Your opponent has been weakened so much that your sap curse has no effect.", true);
		//	} else {
		//		target.prayer.increaseLeechBonus(0);
		//		target.sendMessage("Your curse drains Attack from the enemy, boosting your Attack.", true);
		//	}
	}
}
