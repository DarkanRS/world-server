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
package com.rs.game.pathing;

/**
 * @author Mangis
 *
 */
public class FixedTileStrategy extends RouteStrategy {

	private int x;
	private int y;

	public FixedTileStrategy(int x, int y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public boolean canExit(int currentX, int currentY, int sizeXY, int[][] clip, int clipBaseX, int clipBaseY) {
		return currentX == x && currentY == y;
	}

	@Override
	public int getApproxDestinationX() {
		return x;
	}

	@Override
	public int getApproxDestinationY() {
		return y;
	}

	@Override
	public int getApproxDestinationSizeX() {
		return 1;
	}

	@Override
	public int getApproxDestinationSizeY() {
		return 1;
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof FixedTileStrategy strategy))
			return false;
		return x == strategy.x && y == strategy.y;
	}
}
