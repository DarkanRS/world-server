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
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonIOException;
import com.rs.Settings;
import com.rs.cache.Cache;
import com.rs.cache.loaders.Bonus;
import com.rs.cache.loaders.NPCDefinitions;
import com.rs.game.npc.combat.NPCCombatDefinitions;
import com.rs.lib.file.JsonFileManager;
import com.rs.lib.util.Utils;
import com.rs.tools.old.WebPage;

public class NPCStatDumper {

	private static Map<String, Map<Integer, NPCCombatDefinitions>> DEF_BY_CB_LVL = new HashMap<>();

	public static void main(String[] args) throws IOException {
		Cache.init(Settings.getConfig().getCachePath());
		NPCCombatDefinitions.init();
		for (int npcId = 0;npcId < Utils.getNPCDefinitionsSize();npcId++) {
			NPCDefinitions defs = NPCDefinitions.getDefs(npcId);
			if (defs.hasAttackOption()) {
				String name = defs.getName()
						.replace(" (second form)", "")
						.replace(" (third form)", "")
						.replace(" (fourth form)", "");
				Map<Integer, NPCCombatDefinitions> cbDefs = DEF_BY_CB_LVL.get(name);
				if (cbDefs == null)
					cbDefs = new HashMap<>();
				NPCCombatDefinitions cbDef = cbDefs.get(defs.combatLevel);
				if (cbDef == null)
					cbDef = new NPCCombatDefinitions(NPCCombatDefinitions.getDefs(npcId));
				cbDef.addId(npcId);
				cbDefs.put(defs.combatLevel, cbDef);
				DEF_BY_CB_LVL.put(name, cbDefs);
			}
		}

		for (String name : DEF_BY_CB_LVL.keySet()) {
			Map<Integer, NPCCombatDefinitions> cbMap = DEF_BY_CB_LVL.get(name);
			if (cbMap.size() == 1)
				for (int combatLevel : cbMap.keySet()) {
					cbMap.get(combatLevel).addName(name);
					if (dumpNPC(name, cbMap.get(combatLevel), combatLevel, false))
						System.out.println("Successfully dumped " + name);
					else
						System.out.println("Failed to dump " + name);
				}
			else
				for (int combatLevel : cbMap.keySet())
					if (dumpNPC(name, cbMap.get(combatLevel), combatLevel, true))
						System.out.println("Successfully dumped " + name);
					else
						System.out.println("Failed to dump " + name);
		}
	}

	@SuppressWarnings("unused")
	public static boolean dumpNPC(String pageName, NPCCombatDefinitions originalDef, int combatLevel, boolean folderItUp) {
		try {
			WebPage page = new WebPage("https://oldschool.runescape.wiki/w/"+pageName+"?action=raw");
			try {
				page.load();
			} catch (SocketTimeoutException e) {
				return dumpNPC(pageName, originalDef, combatLevel, folderItUp);
			} catch (Exception e) {
				saveDef(pageName + (folderItUp ? (" (" + combatLevel + ")") : ""), originalDef, folderItUp ? pageName.toLowerCase().replace(" ", "_") : null);
				return false;
			}

			boolean started = false;
			boolean multi = false;

			Bonus attackStyle = null;
			int combat = 0, atk = 0, str = 0, def = 0, range = 0, mage = 0, hitpoints = 0, maxhit = 0;

			Bonus[] attackStyles = new Bonus[20];
			int[] combats = new int[20];
			int[] atks = new int[20];
			int[] strs = new int[20];
			int[] defs = new int[20];
			int[] ranges = new int[20];
			int[] mages = new int[20];
			int[] hitpointss = new int[20];
			int[] maxhits = new int[20];

			for (String line : page.getLines()) {
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
					if (split[0].length() > 2)
						continue;
					if (split.length < 2)
						continue;
					try {
						if (split[0].isEmpty())
							attackStyle = getBonus(split[1]);
						else
							attackStyles[Integer.valueOf(split[0])-1] = getBonus(split[1]);
					} catch (Exception e) { }
				} else if (split[0].startsWith("combat")) {
					split[0] = split[0].replace("combat", "");
					if (split[0].length() > 2)
						continue;
					if (split.length < 2)
						continue;
					try {
						if (split[0].isEmpty())
							combat = Integer.valueOf(split[1]);
						else
							combats[Integer.valueOf(split[0])-1] = Integer.valueOf(split[1]);
					} catch (Exception e) { }
				} else if (split[0].startsWith("att")) {
					split[0] = split[0].replace("att", "");
					if (split[0].length() > 2)
						continue;
					if (split.length < 2)
						continue;
					try {
						if (split[0].isEmpty())
							atk = Integer.valueOf(split[1]);
						else
							atks[Integer.valueOf(split[0])-1] = Integer.valueOf(split[1]);
					} catch (Exception e) { }
				} else if (split[0].startsWith("str")) {
					split[0] = split[0].replace("str", "");
					if (split[0].length() > 2)
						continue;
					if (split.length < 2)
						continue;
					try {
						if (split[0].isEmpty())
							str = Integer.valueOf(split[1]);
						else
							strs[Integer.valueOf(split[0])-1] = Integer.valueOf(split[1]);
					} catch (Exception e) { }
				} else if (split[0].startsWith("def")) {
					split[0] = split[0].replace("def", "");
					if (split[0].length() > 2)
						continue;
					if (split.length < 2)
						continue;
					try {
						if (split[0].isEmpty())
							def = Integer.valueOf(split[1]);
						else
							defs[Integer.valueOf(split[0])-1] = Integer.valueOf(split[1]);
					} catch (Exception e) { }
				} else if (split[0].startsWith("range")) {
					split[0] = split[0].replace("range", "");
					if (split[0].length() > 2)
						continue;
					if (split.length < 2)
						continue;
					try {
						if (split[0].isEmpty())
							range = Integer.valueOf(split[1]);
						else
							ranges[Integer.valueOf(split[0])-1] = Integer.valueOf(split[1]);
					} catch (Exception e) { }
				} else if (split[0].startsWith("mage")) {
					split[0] = split[0].replace("mage", "");
					if (split[0].length() > 2)
						continue;
					if (split.length < 2)
						continue;
					try {
						if (split[0].isEmpty())
							mage = Integer.valueOf(split[1]);
						else
							mages[Integer.valueOf(split[0])-1] = Integer.valueOf(split[1]);
					} catch (Exception e) { }
				} else if (split[0].startsWith("hitpoints")) {
					split[0] = split[0].replace("hitpoints", "");
					if (split[0].length() > 2)
						continue;
					if (split.length < 2)
						continue;
					try {
						if (split[0].isEmpty())
							hitpoints = Integer.valueOf(split[1]);
						else
							hitpointss[Integer.valueOf(split[0])-1] = Integer.valueOf(split[1]);
					} catch (Exception e) { }
				} else if (split[0].startsWith("maxhit")) {
					split[0] = split[0].replace("maxhit", "");
					if (split[0].length() > 2)
						continue;
					if (split.length < 2)
						continue;
					try {
						if (split[0].isEmpty())
							maxhit = Integer.valueOf(split[1]);
						else
							maxhits[Integer.valueOf(split[0])-1] = Integer.valueOf(split[1]);
					} catch (Exception e) { }
				}
			}
			if (multi) {
				int closestIdx = Utils.findClosestIdx(combats, combatLevel);
				NPCCombatDefinitions cbDef;
				if (originalDef != null)
					cbDef = originalDef;
				else {
					cbDef = new NPCCombatDefinitions();
					cbDef.setHitpoints(higher(hitpointss[closestIdx]*10, hitpoints*10));
					cbDef.setMaxHit(higher(maxhits[closestIdx]*10, maxhit*10));
				}
				cbDef.setAttackBonus(nonNull(attackStyles[closestIdx], attackStyle));
				cbDef.setLevels(new int[] { higher(atks[closestIdx], atk), higher(defs[closestIdx], str), higher(strs[closestIdx], def), higher(ranges[closestIdx], range), higher(mages[closestIdx], mage) });
				saveDef(pageName + (folderItUp ? (" (" + combatLevel + ")") : ""), cbDef, folderItUp ? pageName.toLowerCase().replace(" ", "_") : null);
			} else {
				NPCCombatDefinitions cbDef;
				if (originalDef != null)
					cbDef = originalDef;
				else {
					cbDef = new NPCCombatDefinitions(pageName);
					cbDef.setHitpoints(hitpoints*10);
					cbDef.setMaxHit(maxhit*10);
				}
				cbDef.setAttackBonus(attackStyle);
				cbDef.setLevels(new int[] { atk, def, str, range, mage });
				saveDef(pageName + (folderItUp ? (" (" + combatLevel + ")") : ""), cbDef, folderItUp ? pageName.toLowerCase().replace(" ", "_") : null);
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		saveDef(pageName + (folderItUp ? (" (" + combatLevel + ")") : ""), originalDef, folderItUp ? pageName.toLowerCase().replace(" ", "_") : null);
		return false;
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

	public static void saveDef(String name, NPCCombatDefinitions def, String folder) {
		try {
			String additions = "";
			if (def.getLevels()[0] == 0 && def.getLevels()[1] == 0 && def.getLevels()[2] == 0 && def.getLevels()[3] == 0 && def.getLevels()[4] == 0 && def.getAttackBonus() == null)
				additions += " (nostats)";
			if (def.getAttackEmote() == -1 || def.getDeathEmote() == -1)
				additions += " (noanims)";
			System.out.println(name + ": " + Arrays.toString(def.getLevels()) + ", " + def.getAttackBonus());
			JsonFileManager.saveJsonFile(def, new File("./dumps/"+(folder != null ? (folder+"/") : "")+name.toLowerCase().replace(" ", "_")+additions+".json"));
		} catch (JsonIOException | IOException e) {
			e.printStackTrace();
		}
	}
}
