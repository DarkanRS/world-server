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

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.cache.loaders.NPCDefinitions;
import com.rs.lib.util.Utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;

public class WikiNpcDropDumper {

	public static final void main(String[] args) throws IOException {
		System.out.println("Starting..");
		//Cache.init();
		for (int npcId = 0; npcId < Utils.getNPCDefinitionsSize(); npcId++)
			if (NPCDefinitions.getDefs(npcId).hasAttackOption())
				if (dumpDrops(npcId))
					System.out.println("DUMPED NPC : " + npcId);
	}

	@SuppressWarnings("resource")
	public static boolean dumpDrops(int npcId) {
		String pageName = NPCDefinitions.getDefs(npcId).getName();
		String rarity = "null";
		String itemName = "";
		int minAmount = 0;
		int maxAmount = 0;
		int itemId = 0;
		boolean noted = false;

		if (pageName == null || pageName.equals("null"))
			return false;

		try {
			WebPage page = new WebPage("http://runescape.wikia.com/wiki/" + pageName);
			try {
				page.load();
			} catch (Exception e) {
				System.out.println("Invalid page: " + npcId + ", " + pageName);
				return false;
			}

			BufferedWriter writer = new BufferedWriter(new FileWriter("npcDrops.txt", true));
			writer.write(npcId + ":");
			for (String line : page.getLines()) {
				boolean nextIsAmounts = false;

				if (line.contains("Edit Rare drop table drops section"))
					return true;

				/*
				 * Rarity
				 */
				if (line.contains("background:#98FB98") && line.contains("Common"))
					rarity = "COMMON";
				else if (line.contains("background:#F0E68C") && line.contains("Uncommon"))
					rarity = "UNCOMMON";
				else if (line.contains("background:#F4A460") && line.contains("Rare"))
					rarity = "RARE";
				else if (line.contains("background:#F08080") && line.contains("Very rare"))
					rarity = "VERYRARE";

				/*
				 * Item name
				 */
				if (line.contains("</td><td style=\"text-align: left;\"><a href=\"/wiki/")) {
					nextIsAmounts = true;
					itemName = line.substring(line.indexOf("wiki/") + 1);
					itemName = line.substring(itemName.indexOf(">") + 1).replace("</a>", "");
					itemId = getItemIdByName(itemName);
				}

				/*
				 * Amounts
				 */
				if (nextIsAmounts) {
					if (line.contains("(noted)"))
						noted = true;
					if (line.contains("1 &#8211; 3")) {
						minAmount = Integer.parseInt(line.substring(line.indexOf("<td>") + 1));
						maxAmount = Integer.parseInt(line.substring(line.indexOf("11; 3") + 1));
					}
					nextIsAmounts = false;
				}

				if (!rarity.equals("null") && itemId != 0 && minAmount != 0 && maxAmount != 0)
					try {
						if (noted && ItemDefinitions.getDefs(itemId).getCertId() != -1)
							itemId = ItemDefinitions.getDefs(itemId).getCertId();
						writer.write(itemId + "-" + rarity + "-" + minAmount + "-" + maxAmount + ":");
					} catch (IOException e) {
						e.printStackTrace();
					}
			}
			writer.newLine();
			writer.flush();
			writer.close();
			return true;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
			return dumpDrops(npcId);
		}
		return false;
	}

	public static int getItemIdByName(String name) {
		if (name.equalsIgnoreCase("tinderbox"))
			return 590;
		for (int i = 0; i < Utils.getItemDefinitionsSize(); i++) {
			ItemDefinitions itemDef = ItemDefinitions.getDefs(i);
			if (itemDef.getName().equals(name))
				return i;
		}
		System.out.println("Error finding item with name: " + name);
		return -1;
	}
}
