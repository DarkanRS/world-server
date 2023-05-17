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
import com.rs.cache.Store;
import com.rs.cache.loaders.ItemDefinitions;
import com.rs.lib.util.Utils;

import java.io.IOException;
import java.util.HashMap;

public class ItemDefReplacer {

	private static Store NEW;

	private static HashMap<Integer, Integer> PACKED_MAP = new HashMap<>();

	public static final void main(String[] args) throws IOException {
		//Cache.init();
		NEW = new Store("D:/RSPS/cache_742_cleaned/");

		System.out.println("Packing...");
		for (int itemId = 0;itemId < Utils.getItemDefinitionsSize(NEW);itemId++) {
			//			replaceItem(itemId);
			ItemDefinitions def = ItemDefinitions.getItemDefinitions(NEW, itemId, false);
			if (def.wearPos != -1 || def.wearPos2 != -1 || def.wearPos3 != -1)
				if (def.getName().contains("chainbody"))
					System.out.println(itemId + " (" + def.getName() + ") | " + def.wearPos + " | " + def.wearPos2 + " | " + def.wearPos3);
		}

		//		Cache.STORE.getIndex(IndexType.ITEMS).rewriteTable();
		//		Cache.STORE.getIndex(IndexReference.MODELS).rewriteTable();
	}

	public static void replace100Name(int itemId) {
		ItemDefinitions def = ItemDefinitions.getDefs(itemId);
		if (def.name.endsWith(" 100")) {
			def.name = def.name.replace(" 100", " (deg)");
			def.write(Cache.STORE);
			System.out.println("Name replaced with: " + def.name);
		}
	}

	public static void replaceItem(int itemId) {
		ItemDefinitions def = ItemDefinitions.getItemDefinitions(NEW, itemId);
		ItemDefinitions old = ItemDefinitions.getDefs(itemId);
		if (old.wearPos2 == 5 || def.wearPos2 == 5 || (old.wearPos2 == def.wearPos2 && old.wearPos3 == def.wearPos3))
			return;
		if ((!def.containsOption("Wear") && !def.containsOption("Equip") && !def.containsOption("Wield")) || (!old.containsOption("Wear") && !old.containsOption("Equip") && !old.containsOption("Wield")))
			return;
		System.out.println("Packing: " + itemId + " (" + def.name + ") (" + old.wearPos2 + "->" + def.wearPos2 + ")" + "(" + old.wearPos3 + "->" + def.wearPos3 + ")");
		packModels(old, def);
		old.write(Cache.STORE);
		System.out.println("Packed: " + itemId + " (" + def.name + ")");
	}

	public static int packModel(Store cache, int modelId) {
		if (modelId == -1)
			return -1;
		if (PACKED_MAP.get(modelId) != null)
			return PACKED_MAP.get(modelId);
		int archiveId = cache.getIndex(IndexType.MODELS).getLastArchiveId()+1;
		System.out.println("Packing model: " + modelId + " to " + archiveId);
		PACKED_MAP.put(modelId, archiveId);
		if(cache.getIndex(IndexType.MODELS).putFile(archiveId, 0, NEW.getIndex(IndexType.MODELS).getFile(modelId, 0)))
			return archiveId;
		throw new RuntimeException();
	}

	public static void packModels(ItemDefinitions to, ItemDefinitions from) {
		//		to.modelId = packModel(Cache.STORE, from.modelId);
		//		to.maleEquip1 = packModel(Cache.STORE, from.maleEquip1);
		//		to.maleEquip2 = packModel(Cache.STORE, from.maleEquip2);
		//		to.maleEquip3 = packModel(Cache.STORE, from.maleEquip3);
		//		to.femaleEquip1 = packModel(Cache.STORE, from.femaleEquip1);
		//		to.femaleEquip2 = packModel(Cache.STORE, from.femaleEquip2);
		//		to.femaleEquip3 = packModel(Cache.STORE, from.femaleEquip3);
		//		to.maleHead1 = packModel(Cache.STORE, from.maleHead1);
		//		to.maleHead2 = packModel(Cache.STORE, from.maleHead2);
		//		to.femaleHead1 = packModel(Cache.STORE, from.femaleHead1);
		//		to.femaleHead2 = packModel(Cache.STORE, from.femaleHead2);
		//		to.modelZoom = from.modelZoom;
		//		to.modelOffsetX = from.modelOffsetX;
		//		to.modelOffsetY = from.modelOffsetY;
		//		to.modelRotationX = from.modelRotationX;
		//		to.modelRotationY = from.modelRotationY;
		//		to.modelRotationZ = from.modelRotationZ;
		//		to.originalModelColors = from.originalModelColors;
		//		to.modifiedModelColors = from.modifiedModelColors;
		//		to.originalTextureIds = from.originalTextureIds;
		//		to.modifiedTextureIds = from.modifiedTextureIds;
	}
}
