package com.rs.tools.old;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.rs.cache.ArchiveType;
import com.rs.cache.Cache;
import com.rs.cache.IndexType;
import com.rs.cache.loaders.InventoryDefinitions;

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
				for (int x = 0;x < def.ids.length;x++) {
					sb.append(""+def.ids[x] + " " + def.amounts[x] + " ");
				}
				
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
