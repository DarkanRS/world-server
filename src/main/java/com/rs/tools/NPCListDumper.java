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
import com.rs.lib.util.Utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class NPCListDumper {

	public static void main(String[] args) throws IOException {
		//Cache.init();

		File file = new File("npcsThatTransform.txt");
		if (file.exists())
			file.delete();
		else
			file.createNewFile();

		BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		writer.append("//Version = 727\n");
		writer.flush();

		for (int id = 0; id < Utils.getNPCDefinitionsSize(); id++) {
			NPCDefinitions def = NPCDefinitions.getDefs(id);
			if (def.varp == -1 && def.varpBit == -1)
				continue;
			writer.append(id + " - " + def.getName() + " transforms to ");
			for (int tf : def.transformTo)
				writer.append(tf + " (" +NPCDefinitions.getDefs(tf).getName()+ "), ");
			writer.newLine();
			writer.flush();
		}

		writer.close();
	}
}
