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
package com.rs.tools;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonIOException;
import com.rs.cache.loaders.NPCDefinitions;
import com.rs.game.npc.combat.NPCCombatDefinitions;
import com.rs.lib.file.JsonFileManager;
import com.rs.lib.util.Logger;

public class NPCCombatDefMerger {

	private final static String ORIGINAL_PATH = "data/npcs/combatdefs/";
	public static Map<String, NPCCombatDefinitions> ORIGINAL = new HashMap<>();

	private final static String DUMPED_PATH = "dumps/statdump/";
	public static Map<String, NPCCombatDefinitions> DUMPED = new HashMap<>();

	public static void main(String[] args) throws IOException {
		//Cache.init();

		loadPackedCombatDefinitions();

		for (String key : ORIGINAL.keySet()) {
			NPCCombatDefinitions orig = ORIGINAL.get(key);
			NPCCombatDefinitions dump = DUMPED.get(key);
			boolean found = false;
			if (dump != null) {
				orig.setLevels(dump.getLevels());
				orig.setAttackBonus(dump.getAttackBonus());
				found = true;
			} else if (orig.getIds() != null)
				for (int id : orig.getIds()) {
					NPCDefinitions npc = NPCDefinitions.getDefs(id);
					dump = DUMPED.get(npc.getName().toLowerCase().replace(" ", "_"));
					if (dump != null) {
						orig.setLevels(dump.getLevels());
						orig.setAttackBonus(dump.getAttackBonus());
						found = true;
						break;
					}
					dump = DUMPED.get(npc.getName().toLowerCase().replace(" ", "_") + "("+npc.combatLevel+")");
					if (dump != null) {
						orig.setLevels(dump.getLevels());
						orig.setAttackBonus(dump.getAttackBonus());
						found = true;
						break;
					}
				}
			if (!found)
				System.out.println("Nothing for: " + key);
			else {
				//System.out.println("Found: " + key);
			}
		}
	}

	private static void loadPackedCombatDefinitions() {
		try {
			File[] files = new File(ORIGINAL_PATH).listFiles();
			for (File f : files)
				loadFile(f, ORIGINAL);

			files = new File(DUMPED_PATH).listFiles();
			for (File f : files)
				loadFile(f, DUMPED);
		} catch (Throwable e) {
			Logger.handle(e);
		}
	}

	private static void loadFile(File f, Map<String, NPCCombatDefinitions> map) throws JsonIOException, IOException {
		if (f.isDirectory()) {
			for (File dir : f.listFiles())
				loadFile(dir, map);
			return;
		}
		NPCCombatDefinitions defs = (NPCCombatDefinitions) JsonFileManager.loadJsonFile(f, NPCCombatDefinitions.class);
		if (defs != null)
			map.put(f.getName(), defs);
	}

}
