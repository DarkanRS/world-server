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
package com.rs.tools.old;

import java.io.IOException;

import com.rs.cache.Cache;
import com.rs.cache.IndexType;
import com.rs.cache.loaders.ObjectType;
import com.rs.game.model.object.GameObject;
import com.rs.lib.io.InputStream;
import com.rs.lib.util.MapXTEAs;

public class LocationFinder {

	public static int getArchiveId(int regionX, int regionY) {
		return regionX | regionY << 7;
	}

	public static void main(String[] args) throws IOException {
		//Cache.init();
		MapXTEAs.loadKeys();
		//		Region region;
		//		region = new Region(11826);
		//		region.loadRegionMap();
		//		System.out.println(""+region.getAllObjects().size());

		//		for (int regionId = 0; regionId < 16000;regionId++) {
		//for (int y = 0; y < 10000; y += 64) {
		//int regionId = Tile.of(x, y, 0).getRegionId();
		int regionId = 11826;

		int regionX = (regionId >> 8) * 64;
		int regionY = (regionId & 0xff) * 64;

		//int landArchiveId = 2075;
		//int mapArchiveId = 2075;

		int landArchiveId = Cache.STORE.getIndex(IndexType.MAPS).getArchiveId("l" + ((regionX >> 3) / 8) + "_" + ((regionY >> 3) / 8));
		byte[] landContainerData = landArchiveId == -1 ? null : Cache.STORE.getIndex(IndexType.MAPS).getFile(landArchiveId, 0, MapXTEAs.getMapKeys(regionId));
		int mapArchiveId = Cache.STORE.getIndex(IndexType.MAPS).getArchiveId("m" + ((regionX >> 3) / 8) + "_" + ((regionY >> 3) / 8));
		byte[] mapContainerData = mapArchiveId == -1 ? null : Cache.STORE.getIndex(IndexType.MAPS).getFile(mapArchiveId, 0);

		System.out.println("" + landArchiveId + ", " + mapArchiveId);
		System.out.println("" + landContainerData + ", " + mapContainerData);

		//				int landArchiveId = getArchiveId(((regionX >> 3) / 8), ((regionY >> 3) / 8));
		//				byte[] landContainerData = landArchiveId == -1 ? null : Cache.STORE.getIndexes()[5].getFile(landArchiveId, 0);
		//				// int mapArchiveId = Cache.STORE.getIndexes()[5].getArchiveId("m" +
		//				// ((regionX >> 3) / 8) + "_" + ((regionY >> 3) / 8));
		//				int mapArchiveId = getArchiveId(((regionX >> 3) / 8), ((regionY >> 3) / 8));
		//				byte[] mapContainerData = mapArchiveId == -1 ? null : Cache.STORE.getIndexes()[5].getFile(mapArchiveId, 3);

		byte[][][] mapSettings = mapContainerData == null ? null : new byte[4][64][64];
		if (mapContainerData != null) {
			InputStream mapStream = new InputStream(mapContainerData);
			for (int plane = 0; plane < 4; plane++)
				for (int x = 0; x < 64; x++)
					for (int y = 0; y < 64; y++)
						while (true) {
							int value = mapStream.readUnsignedByte();
							if (value == 0)
								break;
							if (value == 1) {
								mapStream.readByte();
								break;
							}
							if (value <= 49)
								mapStream.readByte();
							else if (value <= 81)
								mapSettings[plane][x][y] = (byte) (value - 49);
						}
		} else
			for (int plane = 0; plane < 4; plane++)
				for (int x = 0; x < 64; x++)
					for (int y = 0; y < 64; y++) {
					}
		if (landContainerData != null) {
			InputStream landStream = new InputStream(landContainerData);
			int objectId = -1;
			int incr;
			while ((incr = landStream.readSmart2()) != 0) {
				objectId += incr;
				int location = 0;
				int incr2;
				while ((incr2 = landStream.readUnsignedSmart()) != 0) {
					location += incr2 - 1;
					int localX = (location >> 6 & 0x3f);
					int localY = (location & 0x3f);
					int plane = location >> 12;
			int objectData = landStream.readUnsignedByte();
			int type = objectData >> 2;
							int rotation = objectData & 0x3;
							if (localX < 0 || localX >= 64 || localY < 0 || localY >= 64)
								continue;
							int objectPlane = plane;
							if (mapSettings != null && (mapSettings[1][localX][localY] & 2) == 2)
								objectPlane--;
							if (objectPlane < 0 || objectPlane >= 4 || plane < 0 || plane >= 4)
								continue;
							GameObject obj = new GameObject(objectId, ObjectType.forId(type), rotation, localX + regionX, localY + regionY, objectPlane);
							System.out.println("Object: " + obj.getDefinitions().getName() + ", " + objectId);
				}
			}
		}

		//}
	}
	//	}

}
