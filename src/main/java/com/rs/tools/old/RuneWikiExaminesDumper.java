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
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;

public class RuneWikiExaminesDumper {

	public static final void main(String[] args) throws IOException {
		System.out.println("Starting..");
		//Cache.init();
		for (int itemId = 0; itemId < Utils.getItemDefinitionsSize(); itemId++)
			if (!ItemDefinitions.getDefs(itemId).isNoted())
				if (dumpItem(itemId))
					System.out.println("DUMPED ITEM : " + itemId);
				else
					System.out.println("FAILED ITEM: " + itemId + ", " + ItemDefinitions.getDefs(itemId).getName());
	}

	public static boolean dumpItem(int itemId) {
		String pageName = ItemDefinitions.getDefs(itemId).getName();
		if (pageName == null || pageName.equals("null"))
			return false;
		pageName = pageName.replace("(p)", "");
		pageName = pageName.replace("(p+)", "");
		pageName = pageName.replace("(p++)", "");
		pageName = pageName.replaceAll(" ", "_");
		try {
			WebPage page = new WebPage("http://runescape.wikia.com/wiki/" + pageName);
			try {
				page.load();
			} catch (Exception e) {
				System.out.println("Invalid page: " + itemId + ", " + pageName);
				return false;
			}
			boolean isNextLine = false;
			for (String line : page.getLines()) {
				if (isNextLine) {
					String examine = line.replace("</th><td> ", "");
					examine = examine.replace("</th><td>", "");
					examine = examine.replace("<i> ", "");
					examine = examine.replace("</i> ", "");
					examine = examine.replace("&lt;colour&gt; ", "");
					examine = examine.replace("(bright/thick/warm)", "bright");
					examine = examine.replace("(Temple of Ikov) ", "");
					examine = examine.replace("(Fight Arena) ", "");
					try {
						BufferedWriter writer = new BufferedWriter(new FileWriter("itemExamines.txt", true));
						writer.write(itemId + " - " + examine);
						writer.newLine();
						writer.flush();
						writer.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					return true;
				}
				if (line.equals("<th nowrap=\"nowrap\"><a href=\"/wiki/Examine\" title=\"Examine\">Examine</a>"))
					isNextLine = true;
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
			return dumpItem(itemId);
		}
		return false;
	}

}
