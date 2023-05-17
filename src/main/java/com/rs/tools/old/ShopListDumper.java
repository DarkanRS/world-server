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

import com.rs.cache.ArchiveType;
import com.rs.cache.Cache;
import com.rs.cache.IndexType;
import com.rs.cache.loaders.InventoryDefinitions;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ShopListDumper {

	public static void main(String[] args) {
		try {
			//Cache.init();
			File file = new File("cacheShops.txt");
			if (file.exists())
				file.delete();
			else
				file.createNewFile();
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			writer.append("//Version = 727\n");
			writer.flush();
			int id = 300;
			for (int i = 0; i < Cache.STORE.getIndex(IndexType.CONFIG).getValidFilesCount(ArchiveType.INVENTORIES.getId()); i++) {
				InventoryDefinitions def = InventoryDefinitions.getContainer(i);
				if (def.ids == null || def.ids.length <= 0)
					continue;
				System.out.println("Container: " + id);
				StringBuilder sb = new StringBuilder(id+" 995 false - UNNAMED" + id + " - ");
				for (int x = 0;x < def.ids.length;x++)
					sb.append(""+def.ids[x] + " " + def.amounts[x] + " ");

				writer.append(sb.toString());
				writer.newLine();
				writer.flush();
				id++;
			}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
