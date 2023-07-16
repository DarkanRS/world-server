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

import com.rs.cache.loaders.NPCDefinitions;
import com.rs.game.model.entity.npc.combat.NPCCombatDefinitions;
import com.rs.lib.util.Utils;
import com.rs.tools.old.WebPage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.*;

public class NPCCharmDumper {

	private static Set<String> DUMPED = new HashSet<>();
	private static Map<String, String> MAP = new HashMap<>();

	public static void main(String[] args) {
		try {
			//Cache.init();
			NPCCombatDefinitions.init();

			File file = new File("charmDrops.txt");
			if (file.exists())
				file.delete();
			else
				file.createNewFile();

			BufferedWriter writer = new BufferedWriter(new FileWriter(file));

			for (int npcId = 0;npcId < Utils.getNPCDefinitionsSize();npcId++) {
				NPCDefinitions defs = NPCDefinitions.getDefs(npcId);
				if (defs.hasAttackOption() && !defs.isDungNPC()) {
					if (DUMPED.contains(defs.getName()))
						continue;

					if (dumpNPC(defs.getName()))
						System.out.println("Successfully dumped " + defs.getName());
					DUMPED.add(defs.getName());
				}
			}

			List<String> sorted = new ArrayList<>(MAP.keySet());
			Collections.sort(sorted);

			for (String key : sorted) {
				writer.append(MAP.get(key));
				writer.newLine();
				writer.flush();
			}

			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static boolean dumpNPC(String pageName) {
		try {
			WebPage page = new WebPage("https://runescape.wiki/w/Charm:"+pageName+"?action=raw");
			try {
				page.load();
			} catch (SocketTimeoutException e) {
				return dumpNPC(pageName);
			} catch (Exception e) {
				return false;
			}

			int kills = -1, gold = -1, green = -1, crimson = -1, blue = -1, charms = 1;

			for (String line : page.getLines()) {
				line = line.replace(" ", "").replace("|", "");
				String[] split = line.split("=");

				if (split[0].startsWith("kills")) {
					split[0] = split[0].replace("kills", "");
					if ((split[0].length() > 2) || (split.length < 2))
						continue;
					try {
						kills = Integer.valueOf(split[1]);
					} catch (Exception e) { }
				} else if (split[0].startsWith("charms")) {
					split[0] = split[0].replace("charms", "");
					if ((split[0].length() > 2) || (split.length < 2))
						continue;
					try {
						charms = Integer.valueOf(split[1]);
					} catch (Exception e) { }
				} else if (split[0].startsWith("gold")) {
					split[0] = split[0].replace("gold", "");
					if ((split[0].length() > 2) || (split.length < 2))
						continue;
					try {
						gold = Integer.valueOf(split[1]);
					} catch (Exception e) { }
				} else if (split[0].startsWith("green")) {
					split[0] = split[0].replace("green", "");
					if ((split[0].length() > 2) || (split.length < 2))
						continue;
					try {
						green = Integer.valueOf(split[1]);
					} catch (Exception e) { }
				} else if (split[0].startsWith("crimson")) {
					split[0] = split[0].replace("crimson", "");
					if ((split[0].length() > 2) || (split.length < 2))
						continue;
					try {
						crimson = Integer.valueOf(split[1]);
					} catch (Exception e) { }
				} else if (split[0].startsWith("blue")) {
					split[0] = split[0].replace("blue", "");
					if ((split[0].length() > 2) || (split.length < 2))
						continue;
					try {
						blue = Integer.valueOf(split[1]);
					} catch (Exception e) { }
				}
			}

			if (kills > 500) {
				System.out.println(pageName + " - " + kills + ", " + gold + ", " + green + ", " + crimson + ", " + blue);


				int goldRate = (int) Math.round((((double) gold / (double) charms) / kills * 100.0));
				int greenRate = (int) Math.round((((double) green / (double) charms) / kills * 100.0));
				int crimsonRate = (int) Math.round((((double) crimson / (double) charms) / kills * 100.0));
				int blueRate = (int) Math.round((((double) blue / (double) charms) / kills * 100.0));

				MAP.put(pageName, pageName.toLowerCase().replace(" ", "_") + ":" + goldRate + "-" + greenRate + "-" + crimsonRate + "-" + blueRate + ":");
			} else
				System.err.println(pageName + " - " + kills + " too small sample size.");
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
