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
package com.rs.game.player.content.skills.mining;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum RockType {

	ESSENCE(-1, Ore.RUNE_ESSENCE, Ore.PURE_ESSENCE),
	CLAY(2, Ore.CLAY),
	COPPER(4, Ore.COPPER),
	TIN(4, Ore.TIN),
	BLURITE(40, Ore.BLURITE),
	LIMESTONE(40, Ore.LIMESTONE),
	IRON(9, Ore.IRON),
	DAEYALT(40, Ore.DAEYALT),
	ELEMENTAL(0, Ore.ELEMENTAL),
	SILVER(100, Ore.SILVER),
	GEM(60, Ore.GEM),
	COAL(50, Ore.COAL),
	GOLD(100, Ore.GOLD),
	PERFECT_GOLD(100, Ore.PERFECT_GOLD),
	LUNAR(100, Ore.LUNAR),
	MITHRIL(200, Ore.MITHRIL),
	GRANITE(20, Ore.GRANITE_500G, Ore.GRANITE_2KG, Ore.GRANITE_5KG),
	SANDSTONE(20, Ore.SANDSTONE_10KG, Ore.SANDSTONE_5KG, Ore.SANDSTONE_2KG, Ore.SANDSTONE_1KG),
	ADAMANT(4, Ore.ADAMANT),
	RUNE(12, Ore.RUNE),
	CONC_COAL(-1, Ore.CONCENTRATED_COAL),
	CONC_GOLD(-1, Ore.CONCENTRATED_GOLD),
	LIVING_MINERALS(0, Ore.LIVING_MINERALS),
	RED_SANDSTONE(-1, Ore.RED_SANDSTONE),

	;

	private int respawnTime;
	private List<Ore> ores;

	private RockType(int respawnTime, Ore... ores) {
		this.respawnTime = respawnTime;
		this.ores = new ArrayList<>(Arrays.asList(ores));
		this.ores.sort((f1, f2) -> {
			return f2.getLevel() == f1.getLevel() ? Double.compare(f2.getXp(), f1.getXp()) : f2.getLevel()-f1.getLevel();
		});
	}

	public List<Ore> getOres() {
		return ores;
	}

	public int getLevel() {
		return ores.get(ores.size()-1).getLevel();
	}

	public int getRespawnTime() {
		return respawnTime;
	}

	public boolean depletes() {
		return respawnTime != -1;
	}
}
