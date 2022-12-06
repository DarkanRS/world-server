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
package com.rs.utils;

import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;

/*
 * @author Dragonkk/Alex
 */
public class RegionUtils {

	private static interface StructureEncoder {

		public abstract int encode(int x, int y, int plane);

	}

	public static enum Structure {

		TILE(null, 1,1, (x, y, plane) -> y | (x << 14) | (plane << 28)),
		CHUNK(TILE, 8, 8, (x, y, plane) -> (x << 14) | (y << 3) | (plane << 24)),
		REGION(CHUNK, 8, 8, (x, y, plane) -> ((x << 8) | y  | (plane << 16))),
		MAP(REGION, 255, 255);

		private Structure child;
		private int width, height;
		private StructureEncoder encoder;

		/*
		 * width * height squares.
		 * For instance 4x4:
		 * S S S S
		 * S S S S
		 * S S S S
		 */

		private Structure(Structure child, int width, int height, StructureEncoder encode) {
			this.child = child;
			this.width = width;
			this.height = height;
			encoder = encode;
		}

		private Structure(Structure child, int width, int height) {
			this(child, width, height, null);
		}

		public int getWidth() {
			int x = width;
			Structure nextChild = child;
			while(nextChild != null) {
				x *= nextChild.width;
				nextChild = nextChild.child;
			}
			return x;
		}

		public int getChildWidth() {
			return width;
		}

		public int getHeight() {
			int y = height;
			Structure nextChild = child;
			while(nextChild != null) {
				y *= nextChild.height;
				nextChild = nextChild.child;
			}
			return y;
		}


		public int encode(int x, int y) {
			return encode(x, y, 0);
		}

		public int encode(int x, int y, int plane) {
			return encoder == null ? -1 : encoder.encode(x, y, plane);
		}

		public int getChildHeight() {
			return width;
		}

		@Override
		public String toString() {
			return Utils.formatPlayerNameForDisplay(name());
		}
	}

	public static final class Area {

		private Structure structure;
		private int x, y, width, height;

		public Area(Structure structure, int x, int y, int width, int height) {
			this.structure = structure;
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
		}

		public int getX() {
			return x;
		}

		public int getY() {
			return y;
		}

		public int getMapX() {
			return x * structure.getWidth();
		}

		public int getMapY() {
			return y * structure.getHeight();
		}

		public int getMapWidth() {
			return width *  structure.getWidth();
		}

		public int getMapHeight() {
			return height *  structure.getHeight();
		}

		public Structure getStructure() {
			return structure;
		}

		public WorldTile getRandomTile() {
			return WorldTile.of(x + Utils.random(width), y + Utils.random(height), 0);
		}

		@Override
		public int hashCode() {
			return structure.encode(x, y, 0);
		}

		@Override
		public String toString() {
			return "Structure: "+structure.toString() + ", x: " + x + ", y: " + y + ", width: " + width + ", height: " + height;
		}
	}

	public static Area getArea(WorldTile min, WorldTile max) {
		return getArea(Structure.TILE, min.getX(), min.getY(), max.getX(), max.getY());
	}

	public static Area getArea(int minX, int minY, int maxX, int maxY) {
		return getArea(Structure.TILE, minX, minY, maxX, maxY);
	}

	public static Area getArea(Structure structure, int minX, int minY, int maxX, int maxY) {
		return new Area(structure, minX, minY, maxX-minY, maxY-minY);
	}

	/*
	 * returns converted area
	 */
	public static Area convert(Structure to, Area area) {
		int x = area.getMapX() / to.getWidth();
		int y = area.getMapY() / to.getHeight();
		int width = area.getMapWidth() / to.getWidth();
		int height = area.getMapHeight() / to.getHeight();
		return new Area(to, x, y, width, height);
	}

	/*
	 * converted pos
	 * return converted x and y
	 */
	public static int[] convert(Structure from, Structure to, int... xy) {
		return new int[] {xy[0] * from.getWidth() / to.getWidth(), xy[1] * from.getHeight() / to.getHeight()};
	}

	public static int encode(Structure structure, int... xyp) {
		return structure.encode(xyp[0], xyp[1], xyp.length == 3 ? xyp[2] : 0);
	}



}