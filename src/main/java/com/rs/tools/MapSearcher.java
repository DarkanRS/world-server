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
package com.rs.tools;

import com.rs.cache.Cache;
import com.rs.cache.IndexType;
import com.rs.cache.loaders.ObjectType;
import com.rs.game.World;
import com.rs.game.model.object.GameObject;
import com.rs.lib.io.InputStream;
import com.rs.lib.util.MapXTEAs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapSearcher {

	private static int SEARCH = 27241;

	public static void main(String[] args) throws IOException, InterruptedException {
		//Cache.init();
		MapXTEAs.loadKeys();

		for (GameObject obj : getObjectsById(SEARCH))
			System.out.println(obj);
		//System.out.println("PITFALLS.add("+obj.getTileHash()+");");
	}

	public static void removeAllObjects(int id) {
		for (GameObject obj : getObjectsById(id))
			World.removeObject(obj);
	}

	public static List<GameObject> getObjectsById(int id) {
		List<GameObject> objects = new ArrayList<>();
		for (int regionId = 0; regionId < 30000; regionId++) {
			if (regionId == 18754)
				continue;
			int regionX = (regionId >> 8) * 64;
			int regionY = (regionId & 0xff) * 64;
			int landArchiveId = Cache.STORE.getIndex(IndexType.MAPS).getArchiveId("l" + ((regionX >> 3) / 8) + "_" + ((regionY >> 3) / 8));
			byte[] data = landArchiveId == -1 ? null : Cache.STORE.getIndex(IndexType.MAPS).getFile(landArchiveId, 0, MapXTEAs.getMapKeys(regionId));
			if (data == null)
				continue;

			InputStream landStream = new InputStream(data);
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
					//					ObjectDefinitions def = ObjectDefinitions.getDefs(objectId);
					if (objectId != id)
						continue;
					//					if (def.ignoreAltClip)
					//						continue;
					objects.add(new GameObject(objectId, ObjectType.forId(type), rotation, (regionX + localX), (regionY + localY), plane));
				}
			}
		}
		return objects;
	}

}
