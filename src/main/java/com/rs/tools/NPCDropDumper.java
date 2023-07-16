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

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.lib.file.JsonFileManager;
import com.rs.lib.util.Utils;
import com.rs.tools.old.WebPage;
import com.rs.utils.drop.DropSet;
import com.rs.utils.drop.DropTable;

import java.io.File;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NPCDropDumper {

	//	private static Set<String> DUMPED = new HashSet<>();

	public static void main(String[] args) {
		//Cache.init();
		dumpNPC("Chaos druid");
		//			for (int npcId = 0;npcId < Utils.getNPCDefinitionsSize();npcId++) {
		//				NPCDefinitions defs = NPCDefinitions.getDefs(npcId);
		//				if (!DUMPED.contains(defs.getName()) && defs.hasAttackOption()) {
		//					DUMPED.add(defs.getName());
		//					if (dumpNPC(defs.getName().replace(" ", " ")))
		//						System.out.println("Successfully dumped " + defs.getName());
		////					else
		////						System.out.println("Failed to dump " + defs.getName());
		//				}
		//			}
	}

	private static Map<String, Integer> CUSTOM_NAME_MAP = new HashMap<>();

	static {
		CUSTOM_NAME_MAP.put("Coins", 995);
		CUSTOM_NAME_MAP.put("Tinderbox", 590);
		CUSTOM_NAME_MAP.put("Mystic hat (dark)", 4099);
		CUSTOM_NAME_MAP.put("Blue wizard robe", 577);
		CUSTOM_NAME_MAP.put("Blue wizard hat", 579);
		CUSTOM_NAME_MAP.put("Mystic robe top (dark)", 4101);
		CUSTOM_NAME_MAP.put("Mystic robe bottom (dark)", 4103);
		CUSTOM_NAME_MAP.put("Mystic gloves (dark)", 4105);
		CUSTOM_NAME_MAP.put("Mystic boots (dark)", 4107);
		CUSTOM_NAME_MAP.put("Mystic hat (light)", 4109);
		CUSTOM_NAME_MAP.put("Mystic robe top (light)", 4111);
		CUSTOM_NAME_MAP.put("Mystic robe bottom (light)", 4113);
		CUSTOM_NAME_MAP.put("Mystic gloves (light)", 4115);
		CUSTOM_NAME_MAP.put("Mystic boots (light)", 4117);
		CUSTOM_NAME_MAP.put("Adamantite bar", 2361);
		CUSTOM_NAME_MAP.put("Runite bar", 2363);

		CUSTOM_NAME_MAP.put("Grimy ranarr weed", 207);
		CUSTOM_NAME_MAP.put("Seers ring", 6731);
		CUSTOM_NAME_MAP.put("Archers ring", 6733);

		CUSTOM_NAME_MAP.put("Black cape", 1019);
		CUSTOM_NAME_MAP.put("Black robe", 581);
		CUSTOM_NAME_MAP.put("Zamorak monk bottom", 1033);
		CUSTOM_NAME_MAP.put("Zamorak monk top", 1035);
		CUSTOM_NAME_MAP.put("Pot", 1931);
		CUSTOM_NAME_MAP.put("Raw beef (undead)", 4287);
		CUSTOM_NAME_MAP.put("Raw chicken (undead)", 4289);
		CUSTOM_NAME_MAP.put("Right eye patch", 1025);
		CUSTOM_NAME_MAP.put("Gold amulet (u)", 1673);
		CUSTOM_NAME_MAP.put("Sapphire amulet (u)", 1675);
		CUSTOM_NAME_MAP.put("Emerald amulet (u)", 1677);
		CUSTOM_NAME_MAP.put("Ruby amulet (u)", 1679);
		CUSTOM_NAME_MAP.put("Diamond amulet (u)", 1681);

		CUSTOM_NAME_MAP.put("Gold amulet", 1692);
		CUSTOM_NAME_MAP.put("Sapphire amulet", 1694);
		CUSTOM_NAME_MAP.put("Emerald amulet", 1696);
		CUSTOM_NAME_MAP.put("Ruby amulet", 1698);
		CUSTOM_NAME_MAP.put("Diamond amulet", 1700);
		CUSTOM_NAME_MAP.put("Dragon chainbody", 3140);
		CUSTOM_NAME_MAP.put("Purple gloves", 2942);
		CUSTOM_NAME_MAP.put("Yellow gloves", 2922);
		CUSTOM_NAME_MAP.put("Red gloves", 2912);
		CUSTOM_NAME_MAP.put("Teal gloves", 2932);
		CUSTOM_NAME_MAP.put("Grain", 1947);
		CUSTOM_NAME_MAP.put("Red cape", 1007);
		CUSTOM_NAME_MAP.put("Blue cape", 1021);
		CUSTOM_NAME_MAP.put("", 00000);
		CUSTOM_NAME_MAP.put("", 00000);
		CUSTOM_NAME_MAP.put("", 00000);
		CUSTOM_NAME_MAP.put("", 00000);
		CUSTOM_NAME_MAP.put("", 00000);
		CUSTOM_NAME_MAP.put("", 00000);
		CUSTOM_NAME_MAP.put("", 00000);
		CUSTOM_NAME_MAP.put("", 00000);
		CUSTOM_NAME_MAP.put("", 00000);
		CUSTOM_NAME_MAP.put("", 00000);
		CUSTOM_NAME_MAP.put("", 00000);

		CUSTOM_NAME_MAP.put("Dragon pickaxe", -2);
		CUSTOM_NAME_MAP.put("Dragon arrowtips", -2);
		CUSTOM_NAME_MAP.put("Dragon javelin heads", -2);
		CUSTOM_NAME_MAP.put("Dark fishing bait", -2);
		CUSTOM_NAME_MAP.put("Smouldering stone", -2);
		CUSTOM_NAME_MAP.put("Giant key", -2);
		CUSTOM_NAME_MAP.put("Looting bag", -2);
		CUSTOM_NAME_MAP.put("Ancient shard", -2);
		CUSTOM_NAME_MAP.put("Brimstone key", -2);
		CUSTOM_NAME_MAP.put("Dark totem base", -2);
		CUSTOM_NAME_MAP.put("Dark totem middle", -2);
		CUSTOM_NAME_MAP.put("Dark totem top", -2);
		CUSTOM_NAME_MAP.put("", -2);
		CUSTOM_NAME_MAP.put("", -2);
		CUSTOM_NAME_MAP.put("", -2);
		CUSTOM_NAME_MAP.put("", -2);
		CUSTOM_NAME_MAP.put("", -2);
		CUSTOM_NAME_MAP.put("", -2);
		CUSTOM_NAME_MAP.put("", -2);
		CUSTOM_NAME_MAP.put("", -2);
		CUSTOM_NAME_MAP.put("", -2);
	}

	public static boolean dumpNPC(String pageName) {
		try {
			WebPage page = new WebPage("https://oldschool.runescape.wiki/w/"+pageName+"?action=raw");
			try {
				page.load();
			} catch (SocketTimeoutException e) {
				return dumpNPC(pageName);
			} catch (Exception e) {
				return false;
			}
			boolean accurate = false;
			String lastDesc = "";
			String subName = "";
			for (String line : page.getLines())
				if (line.contains("DropLogProject"))
					accurate = true;
			if (!accurate)
				return false;
			boolean skipping = false;
			int tableNum = 1;
			ArrayList<DropTable> drops = new ArrayList<>();
			for (String line : page.getLines()) {
				line = line.replace("{", "").replace("}", "");
				if (line.contains("=="))
					lastDesc = line.toLowerCase().replace("=", "").replace(" ", "_").replace("drops ", "");
				if (line.contains("DropLogProject|jagex=yes")) {
					if (drops.isEmpty())
						subName = lastDesc;
					else
						finalizeDrops(pageName, subName.isEmpty() ? (pageName+"_sub"+tableNum) : (pageName+"_"+subName), drops);
					subName = lastDesc;

				}
				if (line.contains("Deadman mode") || line.contains("Pickpocketing"))
					skipping = true;
				if (line.contains("DropsTableBottom"))
					skipping = false;
				if (skipping)
					continue;
				int itemId = -1;
				int min = -1;
				int max = -1;
				double num = -1;
				double den = -1;
				String name = "";
				if (line.contains("RareDropTable/Sandbox|")) {
					String[] rdt = line.split("\\|");
					if (rdt[1].toLowerCase().contains("/")) {
						String[] frac = rdt[1].replace("~", "").replace(" ", "").split("/");
						num = Double.valueOf(frac[0].trim());
						den = Double.valueOf(frac[1].trim());
					}
					if (num != -1 && den != -1)
						drops.add(new DropTable(num, den, "rdt_standard"));
					else
						System.out.println("Failed parsing drop: RareDropTable/Sandbox: " + itemId + ", " + min + ", " + max + ", " + num + "/" + den);
					if (rdt[2].toLowerCase().contains("/")) {
						String[] frac = rdt[2].replace("~", "").replace(" ", "").split("/");
						num = Double.valueOf(frac[0].trim());
						den = Double.valueOf(frac[1].trim());
					}
					if (num != -1 && den != -1)
						drops.add(new DropTable(num, den, "rdt_gem"));
					else
						System.out.println("Failed parsing drop: " + name + ", " + itemId + ", " + min + ", " + max + ", " + num + "/" + den);
				} else if (line.equals("GWDRDT") || line.contains("VariableAllotmentSeedDropTable|") || line.contains("FixedAllotmentSeedDropTable|") || line.contains("HerbDropTable|") || line.contains("GemDropTable|") || line.contains("ManySeedDropTable|") || line.contains("RareSeedDropTable|"))
					addDropTable(line, drops);
				else if (line.contains("Ensouled") || line.contains("Curved bone") || line.contains("Long bone") || line.contains("Rag and Bone") || line.contains("Clue scroll") || line.contains("Only dropped") || line.contains("f2p=yes") || line.contains("name=\"f2"))
					continue;
				else if (line.contains("DropsLine|") && (line.contains("/") || line.toLowerCase().contains("always"))) {
					String[] memes = line.split("\\|");
					for (String meme : memes) {
						String[] key = meme.split("=");
						if (key[0].equals("Name")) {
							name = key[1].replace("(", " (").replace("  (", " (");
							name = name.replace(" axe", " hatchet");
							name = name.replace("med helm", "helm");
							name = name.replace("dueling", "duelling");
							name = name.replace(" leaf", "");
							name = name.replace("d'hide vamb", "d'hide vambraces");
							name = name.replace("crossbow (u)", "c'bow (u)");
							if (name.contains("mix ("))
								name = name.replace("str.", "strength").replace("Superattack", "Super attack").replace("def.", "defence");
							for (int i = 0;i < Utils.getItemDefinitionsSize();i++)
								if (ItemDefinitions.getDefs(i).getName().equalsIgnoreCase(name)) {
									itemId = i;
									break;
								}
							if (CUSTOM_NAME_MAP.get(name) != null)
								itemId = CUSTOM_NAME_MAP.get(name);
						} else if (key[0].equals("Quantity")) {
							if (key[1].toLowerCase().contains("(noted)")) {
								key[1] = key[1].toLowerCase().replace(" (noted)", "").replace("(noted)", "");
								itemId = ItemDefinitions.getDefs(itemId).getCertId();
							}
							if (key[1].contains(",")) {
								key[1] = key[1].replace(" ", "");
								String[] minMax = key[1].split(",");
								min = Integer.valueOf(minMax[0].trim());
								max = Integer.valueOf(minMax[1].trim());
							} else if (key[1].contains("-")) {
								String[] minMax = key[1].split("-");
								min = Integer.valueOf(minMax[0].trim());
								max = Integer.valueOf(minMax[1].trim());
							} else {
								min = Integer.valueOf(key[1].replace(" ", "").trim());
								max = min;
							}
						} else if (key[0].equals("Rarity")) {
							if (key[1].toLowerCase().contains("always")) {
								num = 0;
								den = 0;
							}
							if (key[1].toLowerCase().contains("/")) {
								String[] frac = key[1].replace("~", "").replace(" ", "").split("/");
								num = Double.valueOf(frac[0].trim());
								den = Double.valueOf(frac[1].trim());
							}
						} else if (key[0].equals("AltRarity")) {
							if (key[1].toLowerCase().contains("always")) {
								num = 0;
								den = 0;
							}
							if (key[1].toLowerCase().contains("/")) {
								String[] frac = key[1].replace("~", "").replace(" ", "").split("/");
								num = Double.valueOf(frac[0].trim());
								den = Double.valueOf(frac[1].trim());
							}
						}
					}
					if (itemId >= 0 && min != -1 && max != -1 && num != -1 && den != -1)
						drops.add(new DropTable(num, den, itemId, min, max));
					else {
						if (itemId == -1)
							System.out.println(pageName + " Error -> Unknown item: " + name);
						if (min == -1 || max == -1)
							System.out.println(pageName + " Error -> Failed to parse min/max of "+name+": " + min + ", " + max);
						if (num == -1 || den == -1)
							System.out.println(pageName + " Error -> Failed to parse rate of "+name+": " + num + ", " + den);
					}
				}
			}
			finalizeDrops(pageName, subName.isEmpty() ? pageName : (pageName + "_" + subName), drops);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	private static void addDropTable(String line, List<DropTable> drops) {
		try {
			String[] memes = line.split("\\|");
			double num = -1, den = -1;
			String tableName = "";

			if (line.contains("ManySeedDropTable|"))
				tableName = "common_seeds";
			else if (line.contains("FixedAllotmentSeedDropTable|"))
				tableName = "fixed_allotment";
			else if (line.contains("RareSeedDropTable|"))
				tableName = "rare_seeds";
			else if (line.contains("VariableAllotmentSeedDropTable|"))
				tableName = "variable_allotment";
			else if (line.contains("HerbDropTable|"))
				tableName = "herb";
			else if (line.contains("GemDropTable|"))
				tableName = "rdt_gem";
			else if (line.contains("GWDRDT")) {
				drops.add(new DropTable(8, 127, "rdt_standard_gwd"));
				drops.add(new DropTable(2, 127, "rdt_gem_gwd"));
				return;
			}

			if (memes[1].toLowerCase().contains("/")) {
				String[] frac = memes[1].replace("~", "").replace(" ", "").split("/");
				num = Double.valueOf(frac[0].trim());
				den = Double.valueOf(frac[1].trim());
			}
			if (num != -1 && den != -1 && !tableName.isEmpty())
				drops.add(new DropTable(num, den, tableName));
			else
				System.out.println("Failed parsing drop: table:" + tableName + ", " + num + "/" + den);
		} catch(Exception e) {
			System.err.println("Failed to parse drop: " + line);
		}
	}

	public static void finalizeDrops(String pageName, String name, List<DropTable> drops) {
		System.out.println("Finalizing drop: " + name + " " + drops.size());
		drops.sort((o1, o2) -> Double.compare(o2.getRate() == 0.0 ? 100.0 : o2.getRate(), o1.getRate() == 0.0 ? 100.0 : o1.getRate()));

		try {
			DropTable[] dropArr = new DropTable[drops.size()];
			for (int i = 0;i < drops.size();i++)
				dropArr[i] = drops.get(i);
			DropSet table = new DropSet(null, new String[] { pageName.replace("_", " ") }, dropArr);
			table.createDropList();
			if (table.getDropList() == null) {
				System.out.println("Empty drop list for " + name);
				return;
			}
			if (table.isOverflowed())
				System.out.println("Overflowed: " + name.toLowerCase());
			if (name.toLowerCase().contains("revenant") || name.toLowerCase().contains("kourend"))
				return;
			JsonFileManager.saveJsonFile(table, new File("./dumps/"+(table.isOverflowed() ? "overflowed" : "success")+"/"+name.toLowerCase().replace(" ", "_").replace("_drops", "")+".json"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		drops.clear();
	}
}
