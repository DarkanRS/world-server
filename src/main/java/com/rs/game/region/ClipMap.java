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
package com.rs.game.region;

import com.rs.cache.loaders.ObjectType;
import com.rs.game.World;
import com.rs.lib.game.Tile;

public class ClipMap {

	private int regionX;
	private int regionY;
	private int flags[][][];

	public ClipMap(int regionId) {
		regionX = (regionId >> 8) * 64;
		regionY = (regionId & 0xff) * 64;
		flags = new int[4][64][64];
	}

	public int[][][] getMasks() {
		return flags;
	}

	public int getRegionX() {
		return regionX;
	}

	public int getRegionY() {
		return regionY;
	}



	public void setFlag(int plane, int x, int y, int flag) {
		if (x >= 64 || y >= 64 || x < 0 || y < 0) {
			Tile tile = Tile.of(regionX + x, regionY + y, plane);
			int regionId = tile.getRegionId();
			int newRegionX = (regionId >> 8) * 64;
			int newRegionY = (regionId & 0xff) * 64;
			World.getRegion(tile.getRegionId()).forceGetClipMap().setFlag(plane, tile.getX() - newRegionX, tile.getY() - newRegionY, flag);
			return;
		}
		flags[plane][x][y] = flag;
	}

	public void addFlag(int plane, int x, int y, int flag) {
		if (x >= 64 || y >= 64 || x < 0 || y < 0) {
			Tile tile = Tile.of(regionX + x, regionY + y, plane);
			int regionId = tile.getRegionId();
			int newRegionX = (regionId >> 8) * 64;
			int newRegionY = (regionId & 0xff) * 64;
			World.getRegion(tile.getRegionId()).forceGetClipMap().addFlag(plane, tile.getX() - newRegionX, tile.getY() - newRegionY, flag);
			return;
		}
		flags[plane][x][y] |= flag;
	}

	public void removeFlag(int plane, int x, int y, int flag) {
		if (x >= 64 || y >= 64 || x < 0 || y < 0) {
			Tile tile = Tile.of(regionX + x, regionY + y, plane);
			int regionId = tile.getRegionId();
			int newRegionX = (regionId >> 8) * 64;
			int newRegionY = (regionId & 0xff) * 64;
			World.getRegion(tile.getRegionId()).forceGetClipMap().removeFlag(plane, tile.getX() - newRegionX, tile.getY() - newRegionY, flag);
			return;
		}
		flags[plane][x][y] &= ~flag;
	}

	void removeFlag(int plane, int x, int y, ClipFlag... flags) {
		int flag = 0;
		for (ClipFlag f : flags)
			flag |= f.flag;
		removeFlag(plane, x, y, flag);
	}

	void addFlag(int plane, int x, int y, ClipFlag... flags) {
		int flag = 0;
		for (ClipFlag f : flags)
			flag |= f.flag;
		addFlag(plane, x, y, flag);
	}

}
