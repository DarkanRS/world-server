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
package com.rs.utils.record;

import com.rs.game.World;

public class RecordedAction implements Comparable<RecordedAction> {
	private int time;
	private long tick;
	private long timeLogged;

	public RecordedAction(long timeLogged, int time) {
		this.timeLogged = timeLogged;
		this.time = time;
		this.tick = World.getServerTicks();
	}

	@Override
	public int compareTo(RecordedAction other) {
		return Long.compare(timeLogged, other.timeLogged);
	}

	public int getTime() {
		return time;
	}

	public long getTick() {
		return tick;
	}
	public long getTimeLogged() {
		return timeLogged;
	}
	
	@Override
	public String toString() {
		return "("+time+"ms) " + getClass().getSimpleName() + ": ";
	}
}
