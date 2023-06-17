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
import com.rs.cache.loaders.cs2.CS2ParamDefs;
import com.rs.lib.util.Utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ItemListDumper {

	public static void main(String[] args) {
		try {
			//Cache.init();
			//			for (int i = 0; i < Utils.getItemDefinitionsSize(); i++) {
			//				ItemDefinitions def = ItemDefinitions.getItemDefinitions(i);
			//				if (def.getClientScriptData() == null || def.getClientScriptData().isEmpty() || !def.getName().contains("potion"))
			//					continue;
			//				System.out.println(def.getName() + " - " + def.getClientScriptData() + " -");
			//			}
			new ItemListDumper();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public ItemListDumper() throws IOException {
		File file = new File("itemList.txt"); // = new
		// File("information/itemlist.txt");
		if (file.exists())
			file.delete();
		else
			file.createNewFile();
		BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		writer.append("//Version = 727\n");
		writer.flush();
		for (int id = 0; id < Utils.getItemDefinitionsSize(); id++) {
			ItemDefinitions def = ItemDefinitions.getDefs(id);
			String values = "";
			if (def.getClientScriptData() != null)
				for (Integer key : def.getClientScriptData().keySet()) {
					CS2ParamDefs param = CS2ParamDefs.getParams(key.intValue());
					values += "[" + key + " (" + param.type + "): " + Utils.CS2ValTranslate(param.type, def.getClientScriptData().get(key)) + "],";
				}
			writer.append(id + " - " + def.getName() + " - [" + values + "]");
			writer.newLine();
			writer.flush();
		}
		writer.close();
	}

	public static int convertInt(String str) {
		try {
			int i = Integer.parseInt(str);
			return i;
		} catch (NumberFormatException e) {
		}
		return 0;
	}

}
