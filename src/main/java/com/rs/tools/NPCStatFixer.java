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

import java.io.File;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonIOException;
import com.rs.Settings;
import com.rs.cache.Cache;
import com.rs.cache.loaders.Bonus;
import com.rs.cache.loaders.NPCDefinitions;
import com.rs.game.model.entity.npc.combat.NPCCombatDefinitions;
import com.rs.lib.file.JsonFileManager;
import com.rs.lib.util.Logger;
import com.rs.lib.util.Utils;
import com.rs.tools.old.WebPage;

public class NPCStatFixer {

	private static Map<File, NPCCombatDefinitions> DEFS = new HashMap<>();

	private static void loadDefs() {
		try {
			File[] dropFiles = new File("./combatdefs").listFiles();
			for (File f : dropFiles)
				loadFile(f);
		} catch (Throwable e) {
			Logger.handle(e);
		}
	}

	private static void loadFile(File f) {
		try {
			if (f.isDirectory()) {
				for (File dir : f.listFiles())
					loadFile(dir);
				return;
			}
			NPCCombatDefinitions defs = (NPCCombatDefinitions) JsonFileManager.loadJsonFile(f, NPCCombatDefinitions.class);
			if (defs != null)
				DEFS.put(f, defs);
		} catch(Throwable e) {
			System.err.println("Error loading file: " + f.getPath());
		}
	}

	public static void main(String[] args) throws IOException {
		Settings.loadConfig();
		Cache.init(Settings.getConfig().getCachePath());
		loadDefs();

		for (File f : DEFS.keySet()) {
			if (f.getName().toLowerCase().contains("cerberus") || f.getName().toLowerCase().contains("graardor"))
				continue;
			NPCCombatDefinitions cbDef = DEFS.get(f);
			String name = cbDef.getNames() != null && cbDef.getNames().length > 0 ? cbDef.getNames()[0] : NPCDefinitions.getDefs(cbDef.getIds()[0]).getName();
			name = name.replace(" (second form)", "").replace(" (third form)", "").replace(" (fourth form)", "");
			int combatLevel = cbDef.getNames() != null && cbDef.getNames().length > 0 ? 1 : NPCDefinitions.getDefs(cbDef.getIds()[0]).combatLevel;
			dumpNPC(name, f, cbDef, combatLevel);
		}
	}

	@SuppressWarnings("unused")
	public static boolean dumpNPC(String pageName, File file, NPCCombatDefinitions cbDef, int combatLevel) {
		try {
			WebPage page = new WebPage("https://oldschool.runescape.wiki/w/"+pageName+"?action=raw");
			try {
				page.load();
			} catch (SocketTimeoutException e) {
				return dumpNPC(pageName, file, cbDef, combatLevel);
			} catch (Exception e) {
				return false;
			}

			boolean started = false;
			boolean multi = false;

			Bonus attackStyle = null;
			int combat = 0, atk = 0, str = 0, def = 0, range = 0, mage = 0, hitpoints = 0, maxhit = 0;

			ArrayList<Bonus> attackStyles = new ArrayList<>();
			ArrayList<Integer> combats = new ArrayList<>();
			ArrayList<Integer> atks = new ArrayList<>();
			ArrayList<Integer> strs = new ArrayList<>();
			ArrayList<Integer> defs = new ArrayList<>();
			ArrayList<Integer> ranges = new ArrayList<>();
			ArrayList<Integer> mages = new ArrayList<>();
			ArrayList<Integer> hitpointss = new ArrayList<>();
			ArrayList<Integer> maxhits = new ArrayList<>();

			for (String line : page.getLines()) {
				if (line.contains("#REDIRECT [[Nonexistence]]")) {
					System.out.println("NPC is nonexistent " + pageName);
					return false;
				}
				if (line.contains("{{Infobox Monster"))
					started = true;
				if (line.contains("version1"))
					multi = true;
				if (!started || line.contains("bns"))
					continue;

				line = line.replace(" ", "").replace("|", "");
				String[] split = line.split("=");

				if (split[0].startsWith("attackstyle")) {
					split[0] = split[0].replace("attackstyle", "");
					if ((split[0].length() > 2) || (split.length < 2))
						continue;
					try {
						if (split[0].isEmpty())
							attackStyle = getBonus(split[1]);
						else
							attackStyles.add(Integer.valueOf(split[0])-1, getBonus(split[1]));
					} catch (Exception e) { }
				} else if (split[0].startsWith("combat")) {
					split[0] = split[0].replace("combat", "");
					if ((split[0].length() > 2) || (split.length < 2))
						continue;
					try {
						if (split[0].isEmpty())
							combat = Integer.valueOf(split[1]);
						else
							combats.add(Integer.valueOf(split[0])-1, Integer.valueOf(split[1]));
					} catch (Exception e) { }
				} else if (split[0].startsWith("att")) {
					split[0] = split[0].replace("att", "");
					if ((split[0].length() > 2) || (split.length < 2))
						continue;
					try {
						if (split[0].isEmpty())
							atk = Integer.valueOf(split[1]);
						else
							atks.add(Integer.valueOf(split[0])-1, Integer.valueOf(split[1]));
					} catch (Exception e) { }
				} else if (split[0].startsWith("str")) {
					split[0] = split[0].replace("str", "");
					if ((split[0].length() > 2) || (split.length < 2))
						continue;
					try {
						if (split[0].isEmpty())
							str = Integer.valueOf(split[1]);
						else
							strs.add(Integer.valueOf(split[0])-1, Integer.valueOf(split[1]));
					} catch (Exception e) { }
				} else if (split[0].startsWith("def")) {
					split[0] = split[0].replace("def", "");
					if ((split[0].length() > 2) || (split.length < 2))
						continue;
					try {
						if (split[0].isEmpty())
							def = Integer.valueOf(split[1]);
						else
							defs.add(Integer.valueOf(split[0])-1, Integer.valueOf(split[1]));
					} catch (Exception e) { }
				} else if (split[0].startsWith("range")) {
					split[0] = split[0].replace("range", "");
					if ((split[0].length() > 2) || (split.length < 2))
						continue;
					try {
						if (split[0].isEmpty())
							range = Integer.valueOf(split[1]);
						else
							ranges.add(Integer.valueOf(split[0])-1, Integer.valueOf(split[1]));
					} catch (Exception e) { }
				} else if (split[0].startsWith("mage")) {
					split[0] = split[0].replace("mage", "");
					if ((split[0].length() > 2) || (split.length < 2))
						continue;
					try {
						if (split[0].isEmpty())
							mage = Integer.valueOf(split[1]);
						else
							mages.add(Integer.valueOf(split[0])-1, Integer.valueOf(split[1]));
					} catch (Exception e) { }
				} else if (split[0].startsWith("hitpoints")) {
					split[0] = split[0].replace("hitpoints", "");
					if ((split[0].length() > 2) || (split.length < 2))
						continue;
					try {
						if (split[0].isEmpty())
							hitpoints = Integer.valueOf(split[1]);
						else
							hitpointss.add(Integer.valueOf(split[0])-1, Integer.valueOf(split[1]));
					} catch (Exception e) { }
				} else if (split[0].startsWith("maxhit")) {
					split[0] = split[0].replace("maxhit", "");
					if ((split[0].length() > 2) || (split.length < 2))
						continue;
					try {
						if (split[0].isEmpty())
							maxhit = Integer.valueOf(split[1]);
						else
							maxhits.add(Integer.valueOf(split[0])-1, Integer.valueOf(split[1]));
					} catch (Exception e) { }
				}
			}
			if (multi) {
				int closestIdx = combats.size() == 0 ? 0 : Utils.findClosestIdx(combats.stream().mapToInt(i -> i).toArray(), combatLevel);
				int[] wikiLevels = {
						higher(closestIdx < atks.size() ? atks.get(closestIdx) : 0, atk),
						higher(closestIdx < defs.size() ? defs.get(closestIdx) : 0, def),
						higher(closestIdx < strs.size() ? strs.get(closestIdx) : 0, str),
						higher(closestIdx < ranges.size() ? ranges.get(closestIdx) : 0, range),
						higher(closestIdx < mages.size() ? mages.get(closestIdx) : 0, mage)
				};
				int[] currDefLevels = cbDef.getLevels();
				if (!equal(currDefLevels, wikiLevels) && !(wikiLevels[0] == 0 && wikiLevels[1] == 0 && wikiLevels[2] == 0 && wikiLevels[3] == 0 && wikiLevels[4] == 0)) {
					System.out.println("Fixing levels for " + pageName + " " + combatLevel + " " + combats);
					System.out.println(Arrays.toString(wikiLevels));
					cbDef.setLevels(wikiLevels);
					//saveDef(file, cbDef);
				}
			} else {
				int[] wikiLevels = { atk, def, str, range, mage };
				int[] currDefLevels = cbDef.getLevels();
				if (!equal(currDefLevels, wikiLevels) && !(wikiLevels[0] == 0 && wikiLevels[1] == 0 && wikiLevels[2] == 0 && wikiLevels[3] == 0 && wikiLevels[4] == 0)) {
					System.out.println("Fixing levels for " + pageName + " " + combatLevel + " " + combats);
					System.out.println(Arrays.toString(wikiLevels));
					cbDef.setLevels(wikiLevels);
					//saveDef(file, cbDef);
				}
			}
			return true;
		} catch (Exception e) {
			System.err.println("Error parsing: " + pageName);
			e.printStackTrace();
		}
		return false;
	}

	private static boolean equal(int[] arr1, int[] arr2) {
		if (arr1.length != arr2.length)
			return false;
		for (int i = 0;i < arr1.length;i++)
			if (arr1[i] != arr2[i])
				return false;
		return true;
	}

	public static Bonus getBonus(String str) {
		if (str.toLowerCase().contains("slash"))
			return Bonus.SLASH_ATT;
		if (str.toLowerCase().contains("crush"))
			return Bonus.CRUSH_ATT;
		if (str.toLowerCase().contains("stab"))
			return Bonus.STAB_ATT;
		return null;
	}

	public static Bonus nonNull(Bonus b1, Bonus b2) {
		return b1 != null ? b1 : b2;
	}

	public static int higher(int i1, int i2) {
		return i1 > i2 ? i1 : i2;
	}

	public static void saveDef(File file, NPCCombatDefinitions def) {
		try {
			JsonFileManager.saveJsonFile(def, file);
		} catch (JsonIOException | IOException e) {
			e.printStackTrace();
		}
	}
}
