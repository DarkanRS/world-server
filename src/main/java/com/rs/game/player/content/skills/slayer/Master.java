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
package com.rs.game.player.content.skills.slayer;

import java.util.HashMap;
import java.util.Map;

public enum Master {
	Turael(8480, 3, 1, 1, 1, 3, 10),
	Mazchna(8481, 20, 1, 2, 2, 5, 15),
	Vannaka(1597, 40, 1, 3, 4, 20, 60),
	Chaeldar(1598, 70, 1, 4, 10, 50, 150),
	Sumona(7779, 85, 35, 5, 12, 60, 180),
	Duradel(8466, 100, 50, 6, 15, 75, 225),
	Kuradal(9085, 110, 75, 7, 18, 90, 270);

	public static final Map<Integer, Master> SLAYER_MASTERS = new HashMap<>();

	public static Master getMaster(int id) {
		return SLAYER_MASTERS.get(id);
	}

	public static Master getMasterForId(int npcId) {
		for (Master master : Master.values())
			if (master != null && master.npcId == npcId)
				return master;
		return null;
	}

	static {
		for (Master master : Master.values())
			SLAYER_MASTERS.put(master.npcId, master);
	}

	public int npcId;
	public int requiredCombatLevel;
	public int reqSlayerLevel;
	public int masterID;
	private int points, points10, points50;

	private Master(int npcId, int requiredCombatLevel, int requiredSlayerLevel, int slayerMasterID, int points, int points10, int points50) {
		this.npcId = npcId;
		this.requiredCombatLevel = requiredCombatLevel;
		reqSlayerLevel = requiredSlayerLevel;
		masterID = slayerMasterID;
		this.points = points;
		this.points10 = points10;
		this.points50 = points50;
	}

	public int getPoints() {
		return points;
	}

	public int getPoints10() {
		return points10;
	}

	public int getPoints50() {
		return points50;
	}
}