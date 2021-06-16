package com.rs.tools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.rs.cache.loaders.NPCDefinitions;
import com.rs.lib.util.Utils;

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
