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
package com.rs.game.model.entity;

import com.rs.game.model.entity.pathing.Direction;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Utils;
import com.rs.utils.WorldUtil;

public class ForceMovement {
	private Tile start;
	private Tile[] path;
	private int startClientCycles;
	private int speedClientCycles;
	private int direction;

	public ForceMovement(Tile start, Tile[] path, int startClientCycles, int speedClientCycles, int direction) {
		this.start = start;
		this.path = path;
		this.startClientCycles = startClientCycles;
		if (path.length < 2)
			this.path = new Tile[] { start, path[0] };
		this.speedClientCycles = speedClientCycles;
		this.direction = direction;
	}

	public ForceMovement(Tile start, Tile destination, int startClientCycles, int speedClientCycles, int direction) {
		this(start, new Tile[] { destination }, startClientCycles, speedClientCycles, direction);
	}

	public ForceMovement(Tile start, Tile destination, int startClientCycles, int speedClientCycles, Direction direction) {
		this(start, destination, startClientCycles, speedClientCycles, WorldUtil.getAngleTo(direction));
	}

	public ForceMovement(Tile start, Tile destination, int startClientCycles, int speedClientCycles) {
		this(start, destination, startClientCycles, speedClientCycles, Utils.getAngleTo(start, destination));
	}

	public int getTickDuration() {
		return (int) Math.ceil((double) Math.max(startClientCycles, speedClientCycles) / 30.0);
	}

	public int getDiffX1() {
		return path[0].getX() - start.getX();
	}

	public int getDiffX2() {
		if (path.length <= 1)
			return 0;
		return path[1].getX() - start.getX();
	}

	public int getDiffY1() {
		return path[0].getY() - start.getY();
	}

	public int getDiffY2() {
		if (path.length <= 1)
			return 0;
		return path[1].getY() - start.getY();
	}

	public Tile getStart() {
		return start;
	}

	public Tile[] getPath() {
		return path;
	}

	public int getStartClientCycles() {
		return startClientCycles;
	}

	public int getSpeedClientCycles() {
		return speedClientCycles;
	}

	public int getDirection() {
		return direction;
	}
}
