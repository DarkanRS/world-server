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
package com.rs.game.content.minigames.barrows;

public class Link {

	public enum RoomStatus {
		CLOSED, OPEN, UNDETERMINED;
	}

	private BarrowsRoom roomA;
	private BarrowsRoom roomB;
	private RoomStatus state;

	public Link(BarrowsRoom a, BarrowsRoom b) {
		roomA = a;
		roomB = b;
		state = RoomStatus.UNDETERMINED;
	}

	public BarrowsRoom getOther(BarrowsRoom current) {
		return (current == roomA ? roomB : roomA);
	}

	public BarrowsRoom getRoomA() {
		return roomA;
	}

	public BarrowsRoom getRoomB() {
		return roomB;
	}

	public RoomStatus getState() {
		return state;
	}

	public void setState(RoomStatus state) {
		if (this.state == RoomStatus.UNDETERMINED)
			this.state = state;
	}

	@Override
	public String toString() {
		return "Room " + roomA.getName() + " to Room " + roomB.getName() + " = " + getState();
	}
}
