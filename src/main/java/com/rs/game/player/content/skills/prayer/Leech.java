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
//  Copyright © 2021 Trenton Kress
//  This file is part of project: Darkan
//
package com.rs.game.player.content.skills.prayer;

import com.rs.game.Entity;
import com.rs.game.player.Player;

public enum Leech {
	ATTACK(Prayer.LEECH_ATTACK, 2231, 2232),
	STRENGTH(Prayer.LEECH_STRENGTH, 2248, 2250),
	DEFENSE(Prayer.LEECH_DEFENSE, 2244, 2246),
	RANGED(Prayer.LEECH_RANGE, 2236, 2238),
	MAGIC(Prayer.LEECH_MAGIC, 2240, 2242),
	SPECIAL(Prayer.LEECH_SPECIAL, 2256, 2258),
	ENERGY(Prayer.LEECH_ENERGY, 2252, 2254);

	private Prayer prayer;

	private int projAnim, spotAnimHit;

	private Leech(Prayer prayer, int projAnim, int spotAnimHit) {
		this.prayer = prayer;
		this.projAnim = projAnim;
		this.spotAnimHit = spotAnimHit;
	}

	public Prayer getPrayer() {
		return prayer;
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
