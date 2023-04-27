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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class DumpItemRenderIds {

	public static void main(String[] args) throws IOException {
		//Cache.init();
		dumpRenders();
	}

	public static void dumpRenders() throws IOException {
		File file = new File("./renderids.txt");
		if (file.exists())
			file.delete();
		BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		for (int i = 0; i < 13247; i++) {
			ItemDefinitions def = ItemDefinitions.getDefs(i);
			if (def == null)
				continue;
			writer.write(i + "=" + def.getRenderAnimId());
			writer.newLine();
		}
		writer.flush();
		writer.close();
		System.out.println("Dumped all render ids.");
	}
}