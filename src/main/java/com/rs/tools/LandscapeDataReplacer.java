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

import java.io.IOException;
import java.util.ArrayList;

import com.rs.cache.Cache;
import com.rs.cache.IndexType;
import com.rs.cache.Store;
import com.rs.cache.loaders.ObjectType;
import com.rs.cache.utils.Constants;
import com.rs.game.model.object.GameObject;
import com.rs.game.region.Region;
import com.rs.lib.io.InputStream;
import com.rs.lib.io.OutputStream;
import com.rs.lib.util.MapXTEAs;
import com.rs.lib.util.Utils;

public class LandscapeDataReplacer {

	private static Store NEW;

	public static final void main(String[] args) throws IOException {
		//Cache.init();
		NEW = new Store("C:/Users/John/Desktop/723 cache/");
		MapXTEAs.loadKeys();

		//Fix all broken regions
		//		for (int regionId = 0;regionId < Short.MAX_VALUE;regionId++) {
		//			Region region = new Region(regionId);
		//			region.loadRegionMap();
		//
		//			if (region.isMissingXtea()) {
		//				try {
		//					if (!packMapObjects(region)) {
		//						try {
		//							packPurifiedMapObjects(region);
		//						} catch(Exception e) {
		//							continue;
		//						}
		//					}
		//				} catch(Exception e) {
		//					System.out.println("Failed " + regionId + ": " + e.getMessage());
		//				}
		//			}
		//		}

		int regionId = 12853;
		int regionX = (regionId >> 8) * 64;
		int regionY = (regionId & 0xff) * 64;
		int landArchiveId = NEW.getIndex(IndexType.MAPS).getArchiveId("l" + ((regionX >> 3) / 8) + "_" + ((regionY >> 3) / 8));
		int mapArchiveId = NEW.getIndex(IndexType.MAPS).getArchiveId("m" + ((regionX >> 3) / 8) + "_" + ((regionY >> 3) / 8));


		Cache.STORE.getIndex(IndexType.MAPS).putArchive(mapArchiveId, NEW.getIndex(IndexType.MAPS).getFile(mapArchiveId, 0));
		Cache.STORE.getIndex(IndexType.MAPS).putArchive(landArchiveId, NEW.getIndex(IndexType.MAPS).getFile(landArchiveId, 0));
		//Cache.STORE.getIndex(IndexType.MAPS).putFile(landArchiveId, 0, Constants.GZIP_COMPRESSION, NEW.getIndex(IndexType.MAPS).getFile(landArchiveId, 0), new int[] { 0, 0, 0, 0 }, true, true, -1, -1);
	}


	public static void copyMapObjects(Region from, Region to, boolean compare) {
		if (!compareObjects(from, to) && compare)
			return;
		int fromLandArchiveId = Cache.STORE.getIndex(IndexType.MAPS).getArchiveId("l" + ((((from.getRegionId() >> 8) * 64) >> 3) / 8) + "_" + ((((from.getRegionId() & 0xff) * 64) >> 3) / 8));
		int toLandArchiveId = Cache.STORE.getIndex(IndexType.MAPS).getArchiveId("l" + ((((to.getRegionId() >> 8) * 64) >> 3) / 8) + "_" + ((((to.getRegionId() & 0xff) * 64) >> 3) / 8));

		byte[] fromData = Cache.STORE.getIndex(IndexType.MAPS).getFile(fromLandArchiveId, 0, MapXTEAs.getMapKeys(from.getRegionId()));

		if (fromData == null || toLandArchiveId <= -1) {
			System.out.println("Null data: " + from.getRegionId());
			return;
		}

		if (verifyObjectData(fromData))
			System.out.println(from.getRegionId() + "->" + to.getRegionId() + " - Copied: " + Cache.STORE.getIndex(IndexType.MAPS).putFile(toLandArchiveId, 0, Constants.GZIP_COMPRESSION, fromData, new int[] { 0, 0, 0, 0 }, true, true, -1, -1));
	}

	public static boolean packPurifiedMapObjects(Region region) {
		return packPurifiedMapObjects(region, region);
	}

	public static boolean packPurifiedMapObjects(Region from, Region to) {
		int regionX = (to.getRegionId() >> 8) * 64;
		int regionY = (to.getRegionId() & 0xff) * 64;

		byte[] newLandData = NEW.getIndex(IndexType.MAPS).getFile(((from.getRegionId() >> 8) | (from.getRegionId() & 0xff) << 7), 0);
		int landArchiveId = Cache.STORE.getIndex(IndexType.MAPS).getArchiveId("l" + ((regionX >> 3) / 8) + "_" + ((regionY >> 3) / 8));
		byte[] purified = purifyObjectData(newLandData);

		if (purified != null && verifyObjectData(purified)) {
			boolean success = Cache.STORE.getIndex(IndexType.MAPS).putFile(landArchiveId, 0, Constants.GZIP_COMPRESSION, purified, new int[] { 0, 0, 0, 0 }, true, true, -1, -1);
			System.out.println(from.getRegionId() + " - 764 map data valid: " + Cache.STORE.getIndex(IndexType.MAPS).putFile(landArchiveId, 0, Constants.GZIP_COMPRESSION, purified, new int[] { 0, 0, 0, 0 }, true, true, -1, -1));
			return success;
		}
		System.out.println(from.getRegionId() + " - Failed to purify");
		return false;
	}

	public static boolean packMapObjects(Region region) {
		return packMapObjects(region, region);
	}

	public static boolean packMapObjects(Region from, Region to) {
		int regionX = (to.getRegionId() >> 8) * 64;
		int regionY = (to.getRegionId() & 0xff) * 64;

		byte[] newLandData = NEW.getIndex(IndexType.MAPS).getFile(((from.getRegionId() >> 8) | (from.getRegionId() & 0xff) << 7), 0);
		int landArchiveId = Cache.STORE.getIndex(IndexType.MAPS).getArchiveId("l" + ((regionX >> 3) / 8) + "_" + ((regionY >> 3) / 8));

		boolean success = Cache.STORE.getIndex(IndexType.MAPS).putFile(landArchiveId, 0, Constants.GZIP_COMPRESSION, newLandData, new int[] { 0, 0, 0, 0 }, true, true, -1, -1);
		if (verifyObjectData(newLandData)) {
			System.out.println(from.getRegionId() + " - 764 map data valid: " + success);
			return success;
		}
		System.out.println(from.getRegionId() + " - 764 map data invalid version");
		return false;
	}

	public static byte[] purifyObjectData(byte[] data) {
		ArrayList<GameObject> objects = new ArrayList<>();
		InputStream landStream = new InputStream(data);
		OutputStream fixed = new OutputStream();

		int realSize = 0;

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
				if (localX < 0 || localX >= 64 || localY < 0 || localY >= 64) {
					System.out.println("Skipping: Pos: " + localX + ", " + localY);
					continue;
				}
				int objectPlane = plane;
				if (objectPlane < 0 || objectPlane >= 4 || plane < 0 || plane >= 4) {
					System.out.println("Skipping: Plane: " + objectPlane + ", " + plane);
					continue;
				}
				realSize++;
				if (objectId >= Utils.getObjectDefinitionsSize())
					continue;
				objects.add(new GameObject(objectId, ObjectType.forId(type), rotation, localX, localY, objectPlane));
			}
		}

		System.out.println("Real size: " + realSize + " - Fixed size: " + objects.size());

		int lastLoc = 0;
		int lastObj = -1;
		for (GameObject current : objects) {
			if (lastObj != current.getId()) {
				if (lastLoc != 0) {
					lastLoc = 0;
					fixed.writeSmart(0);
				}
				fixed.writeSum(current.getId() - lastObj);
				lastObj = current.getId();
			}
			int currLoc = (current.getY() + (current.getX() << 6) + (current.getPlane() << 12));
			fixed.writeSmart((currLoc - lastLoc) + 1);
			lastLoc = currLoc;
			fixed.writeByte(current.getRotation() + (current.getType().id << 2));
		}
		fixed.writeSum(0);

		if (!compareObjects(objects, getObjects(fixed.getBuffer())))
			return null;

		return fixed.getBuffer();
	}

	public static boolean verifyObjectData(byte[] data) {
		ArrayList<GameObject> objects = new ArrayList<>();
		InputStream landStream = new InputStream(data);
		boolean valid = true;
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
			if (objectPlane < 0 || objectPlane >= 4 || plane < 0 || plane >= 4)
				continue;
			if (objectId >= Utils.getObjectDefinitionsSize()) {
				System.out.println("Invalid object: " + objectId);
				valid = false;
				continue;
			}
			objects.add(new GameObject(objectId, ObjectType.forId(type), rotation, localX, localY, objectPlane));
			}
		}
		return valid;
	}

	public static boolean compareObjects(Region r1, Region r2) {
		return compareObjects(getObjects(r1), getObjects(r2));
	}

	public static boolean compareObjects(ArrayList<GameObject> objects, ArrayList<GameObject> objects2) {
		if (objects.size() != objects2.size()) {
			System.out.println("Size mismatch");
			return false;
		}
		boolean matches = true;
		for (int i = 0;i < objects.size();i++) {
			GameObject o1 = objects.get(i);
			GameObject o2 = objects2.get(i);
			if (o1.getId() != o2.getId()) {
				matches = false;
				System.out.println("Id mismatch: " + o1.getId() + ", " + o2.getId());
			}
			if (!o1.getTile().isAt(o2.getX(), o2.getY())) {
				matches = false;
				System.out.println("Loc mismatch: " + o1.getTile().getTileHash() + ", " + o2.getTile().getTileHash());
			}
		}
		return matches;
	}

	public static ArrayList<GameObject> getObjects(Region region) {
		byte[] newLandData = NEW.getIndex(IndexType.MAPS).getFile(((region.getRegionId() >> 8) | (region.getRegionId() & 0xff) << 7), 0);
		return getObjects(newLandData);
	}

	public static ArrayList<GameObject> getObjects(byte[] data) {
		ArrayList<GameObject> objects = new ArrayList<>();
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
				int objectPlane = plane;
				if (objectPlane < 0 || objectPlane >= 4 || plane < 0 || plane >= 4)
					continue;
				objects.add(new GameObject(objectId, ObjectType.forId(type), rotation, localX, localY, objectPlane));
			}
		}
		return objects;
	}
}
