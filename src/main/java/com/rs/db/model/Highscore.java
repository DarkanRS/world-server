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
package com.rs.db.model;

import com.rs.game.model.entity.player.Player;

public class Highscore {

	private String username;
	private String displayName;
	private boolean ironman;
	private int totalLevel;
	private long totalXp;
	private int[] xp;

	public Highscore(Player player) {
		if (player.getDisplayName() == null)
			throw new RuntimeException("No display name found for player: " + player.getUsername());
		username = player.getUsername();
		displayName = player.getDisplayName();
		ironman = player.isIronMan();
		totalLevel = player.getSkills().getTotalLevel();
		totalXp = player.getSkills().getTotalXp();
		xp = player.getSkills().getXpInt();
	}

	public boolean isIronman() {
		return ironman;
	}

	public int getTotalLevel() {
		return totalLevel;
	}

	public long getTotalXp() {
		return totalXp;
	}

	public int[] getXp() {
		return xp;
	}

	public String getUsername() {
		return username;
	}

	public String getDisplayName() {
		return displayName;
	}
}
