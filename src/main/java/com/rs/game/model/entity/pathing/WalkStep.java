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
package com.rs.game.model.entity.pathing;

public class WalkStep {

	private final Direction dir;
	private final int x;
    private final int y;
	private boolean clip;

	public WalkStep(Direction dir, int x, int y, boolean clip) {
		this.dir = dir;
		this.x = x;
		this.y = y;
		this.clip = clip;
	}

	public boolean checkClip() {
		return clip;
	}

	public void setCheckClip(boolean clip) {
		this.clip = clip;
	}

	public Direction getDir() {
		return dir;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	@Override
	public String toString() {
		return "[" + x + ", " + y + ", " + dir + ", " + clip + "]";
	}
}
