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
package com.rs.game.pathing;

/**
 * @author Mangis
 * 
 */
public class RouteFinder {

	public static final int WALK_ROUTEFINDER = 0;
	private static int lastUsed;

	public static int findRoute(int type, int srcX, int srcY, int srcZ, int srcSizeXY, RouteStrategy strategy, boolean findAlternative) {
		switch (lastUsed = type) {
		case WALK_ROUTEFINDER:
			return WalkRouteFinder.findRoute(srcX, srcY, srcZ, srcSizeXY, strategy, findAlternative);
		default:
			throw new RuntimeException("Unknown routefinder type.");
		}
	}

	public static int[] getLastPathBufferX() {
		switch (lastUsed) {
		case WALK_ROUTEFINDER:
			return WalkRouteFinder.getLastPathBufferX();
		default:
			throw new RuntimeException("Unknown routefinder type.");
		}
	}

	public static int[] getLastPathBufferY() {
		switch (lastUsed) {
		case WALK_ROUTEFINDER:
			return WalkRouteFinder.getLastPathBufferY();
		default:
			throw new RuntimeException("Unknown routefinder type.");
		}
	}

	public static boolean lastIsAlternative() {
		switch (lastUsed) {
		case WALK_ROUTEFINDER:
			return WalkRouteFinder.lastIsAlternative();
		default:
			throw new RuntimeException("Unknown routefinder type.");
		}
	}
}
