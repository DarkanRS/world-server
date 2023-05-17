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
import com.rs.lib.util.Utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;

public class RuneWikiBonusDumper {

	public static final void main(String[] args) throws IOException {
		System.out.println("Starting..");
		//Cache.init();
		for (int itemId = 22067; itemId < Utils.getItemDefinitionsSize(); itemId++)
			if (ItemDefinitions.getDefs(itemId).isWearItem() && !ItemDefinitions.getDefs(itemId).isNoted())
				if (dumpItem(itemId))
					System.out.println("Dumped ITEM: " + itemId + ", " + ItemDefinitions.getDefs(itemId).getName());
	}

	public static boolean dumpItem(int itemId) {
		File file = new File("bonuses/" + itemId + ".txt");
		if (file.exists())
			return false;
		String pageName = ItemDefinitions.getDefs(itemId).getName().replace(" (black)", "").replace(" (white)", "").replace(" (yellow)", "").replace(" (red)", "");
		if (pageName == null || pageName.equals("null"))
			return false;
		pageName = pageName.replace(" (p)", "");
		pageName = pageName.replace(" (p+)", "");
		pageName = pageName.replace(" (p++)", "");
		pageName = pageName.replace(" Broken", "");
		pageName = pageName.replace(" 25", "");
		pageName = pageName.replace(" 50", "");
		pageName = pageName.replace(" 75", "");
		pageName = pageName.replace(" 100", "");
		pageName = pageName.replace("jav'n", "javelin");
		pageName = pageName.replaceAll(" ", "_");

		try {
			WebPage page = new WebPage("http://runescape.wikia.com/wiki/" + pageName);
			try {
				page.load();
			} catch (Exception e) {
				System.out.println("Invalid page: " + itemId + ", " + pageName);
				return false;
			}

			int bonusId = 0;
			int[] bonuses = new int[18];
			for (String line : page.getLines())
				if (bonusId == 0 || bonusId == 5) {
					String replace = "<td colspan=\"2\" width=\"30\" align=\"center\">";
					if (line.startsWith(replace)) {
						line = line.replace(replace, "").replaceAll(" ", "").replace("+", "").replace("<br/>", "");
						int bonus = Integer.valueOf(line);
						// System.out.println(bonus);
						bonuses[bonusId++] = bonus;
						continue;
					}
				} else if ((bonusId >= 1 && bonusId <= 4) || (bonusId >= 6 && bonusId <= 10)) {
					String replace = "</td><td colspan=\"2\" width=\"30\" align=\"center\">";
					if (line.startsWith(replace)) {
						line = line.replace(replace, "").replaceAll(" ", "").replace("+", "").replace("64to100", "100");
						int bonus = Integer.valueOf(line);
						// System.out.println(bonus);
						bonuses[bonusId++] = bonus;
						continue;
					}
				} else if (bonusId == 11) {
					String replace = "<td colspan=\"4\" width=\"60\" align=\"center\">";
					if (line.startsWith(replace)) {
						line = line.replace(replace, "").replaceAll(" ", "").replace("%", "").replace(".0", "");
						int bonus = Integer.valueOf(line);
						// System.out.println(bonus);
						bonuses[bonusId++] = bonus;
						continue;
					}
				} else if (bonusId == 12 || bonusId == 13) {
					String replace = "</td><td colspan=\"4\" width=\"60\" align=\"center\">";
					if (line.startsWith(replace)) {
						line = line.replace(replace, "").replaceAll(" ", "").replace("%", "").replace(".0", "");
						int bonus = Integer.valueOf(line);
						// System.out.println(bonus);
						bonuses[bonusId++] = bonus;
						continue;
					}
				} else if (bonusId == 14) {
					String replace = "<td colspan=\"3\" width=\"45\" align=\"center\">";
					if (line.startsWith(replace)) {
						line = line.replace(replace, "").replaceAll(" ", "").replace("+", "").replace(".0", "").replace(".1", "").replace(".5", "");
						int bonus = (Double.valueOf(line)).intValue();
						// System.out.println(bonus);
						bonuses[bonusId++] = bonus;
						continue;
					}
				} else if (bonusId >= 15 && bonusId <= 17) {
					String replace = "</td><td colspan=\"3\" width=\"45\" align=\"center\">";
					if (line.startsWith(replace)) {
						line = line.replace(replace, "").replaceAll(" ", "").replace("%", "").replace("+", "").replace(".0", "").replace("52to70", "70").replace("0(4trimmed)", "4").replace("15(Slayertasksonly)", "0").replace(".5", "")
								.replace("?", "0").replace("<i>Varies</i>", "0");
						int bonus = Integer.valueOf(line);
						// System.out.println(bonus);
						bonuses[bonusId++] = bonus;
						if (bonusId == 18)
							break;
						continue;
					}
				}
			if (bonusId != 18)
				return false;
			boolean letssee = false;
			for (int bonus : bonuses)
				if (bonus != 0) {
					letssee = true;
					break;
				}
			if (!letssee)
				return false;
			try {
				BufferedWriter writer = new BufferedWriter(new FileWriter(file));
				writer.write("Attack bonus");
				writer.newLine();
				writer.flush();
				for (int index = 0; index < 5; index++) {
					writer.write("" + bonuses[index]);
					writer.newLine();
					writer.flush();
				}
				writer.write("Defence bonus");
				writer.newLine();
				writer.flush();
				for (int index = 5; index < 11; index++) {
					writer.write("" + bonuses[index]);
					writer.newLine();
					writer.flush();
				}
				writer.write("Damage absorption");
				writer.newLine();
				writer.flush();
				for (int index = 11; index < 14; index++) {
					writer.write("" + bonuses[index]);
					writer.newLine();
					writer.flush();
				}
				writer.write("Other bonuses");
				writer.newLine();
				writer.flush();
				for (int index = 14; index < 18; index++) {
					writer.write("" + bonuses[index]);
					writer.newLine();
					writer.flush();
					System.out.println("Exists ? : " + file.exists());
				}
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return true;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

}
