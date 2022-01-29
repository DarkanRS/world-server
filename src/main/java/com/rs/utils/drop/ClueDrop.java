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
package com.rs.utils.drop;

import java.util.HashSet;
import java.util.Set;

public class ClueDrop {

	private Set<Integer> combatLevels = new HashSet<>();
	private int weight;

	public ClueDrop(int weight, int... combatLevels) {
		this.weight = weight;
		for (int combatLevel : combatLevels)
			this.combatLevels.add(combatLevel);
	}

	public Set<Integer> getCombatLevels() {
		return combatLevels;
	}

	public int getWeight() {
		return weight;
	}

	public boolean validCombatLevel(int combatLevel) {
		if (combatLevels.isEmpty())
			return true;
		return combatLevels.contains(combatLevel);
	}

}
