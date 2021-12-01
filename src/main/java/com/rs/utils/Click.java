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
package com.rs.utils;

public class Click {
	
	private int x;
	private int y;
	private int time;
	private long timeMillis;
	
	public Click(int x, int y, int time, long timeMillis) {
		this.x = x;
		this.y = y;
		this.time = time;
		this.timeMillis = timeMillis;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getTime() {
		return time;
	}
	
	public boolean sameSpot(Click click2) {
		return click2.x == this.x && click2.y == this.y;
	}
	
	public boolean sameTime(Click click2) {
		return click2.time == this.time;
	}
	
	public long getTimeMillis() {
		return timeMillis;
	}
	
	@Override
	public String toString() {
		return "["+x+", "+y+"]: " + time+"ms";
	}
	
}
