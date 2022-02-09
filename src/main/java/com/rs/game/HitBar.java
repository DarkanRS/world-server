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
package com.rs.game;

import com.rs.game.player.Player;

public abstract class HitBar {

	public abstract int getType();

	public abstract int getPercentage();

	public int getToPercentage() {
		return getPercentage();
	}

	public int getTimer() {
		return getPercentage() == getToPercentage() ? 0 : 1;
	}

	public int getDelay() {
		return 0;
	}

	public boolean display(Player player) {
		return true;
	}
}
