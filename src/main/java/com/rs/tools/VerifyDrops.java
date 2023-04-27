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

import com.google.gson.JsonIOException;
import com.rs.cache.loaders.ItemDefinitions;
import com.rs.lib.file.JsonFileManager;
import com.rs.utils.drop.DropEntry;
import com.rs.utils.drop.DropSet;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class VerifyDrops {

	private static Map<String, String> NAMES = new HashMap<>();
	private static final int ITEM_TO_SEARCH_FOR = 14642;

	public static void main(String[] args) throws IOException {
		//Cache.init();

		System.out.println("Checking dumps folder...");
		File[] dumpFiles = new File("./dumps/").listFiles();
		for (File f : dumpFiles)
			loadFile(f);
		NAMES.clear();
		System.out.println("Checking drops folder...");
		File[] dropFiles = new File("./data/npcs/drops/").listFiles();
		for (File f : dropFiles)
			loadFile(f);
	}

	public static void loadFile(File f) throws JsonIOException, IOException {
		try {
			if (f.isDirectory()) {
				for (File dir : f.listFiles())
					loadFile(dir);
				return;
			}
			DropSet set = (DropSet) JsonFileManager.loadJsonFile(f, DropSet.class);
			if (set != null) {
				set.getDropList();
				if (set.getNames() != null && set.getNames().length > 0)
					for (String n : set.getNames()) {
						String prev = NAMES.put(n, f.getName());
						if (prev != null) {
							System.err.println("Duplicate table for: " + prev);
							System.err.println("Duplicate table for: " + f.getName());
						}
					}
				for (DropEntry drop : set.getDropList().getDrops()) {
					if (drop.getTable() == null || drop.getTable().getDrops() == null || drop.getTable().getDrops().length <= 0)
						continue;
					if (drop.getTable().getDrops()[0].getId() == ITEM_TO_SEARCH_FOR)
						System.out.println(ItemDefinitions.getDefs(ITEM_TO_SEARCH_FOR).getName() + " in " + f.getName());
				}
				if (set.isOverflowed())
					System.out.println("Overflow in " + f.getName());
			}
		} catch (Exception e) {
			System.err.println("Exception loading: " + f.getName());
			e.printStackTrace();
			System.exit(1);
		}
	}

}
