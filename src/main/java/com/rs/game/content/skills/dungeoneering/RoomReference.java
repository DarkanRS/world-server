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
package com.rs.game.content.skills.dungeoneering;

public class RoomReference {

	private final int x, y;

	public RoomReference(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public int getRoomX() {
		return x;
	}

	public int getRoomY() {
		return y;
	}

	public int getBaseX() {
		return x * 16;
	}

	public int getBaseY() {
		return y * 16;
	}

	@Override
	public boolean equals(Object object) {
		if (object instanceof RoomReference rRef)
			return x == rRef.x && y == rRef.y;
		return false;
	}

	@Override
	public String toString() {
		return "[RoomReference][" + x + "][" + y + "]";
	}
}
