package com.rs.tools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

import com.rs.cache.loaders.ObjectDefinitions;
import com.rs.lib.util.Utils;

public class ObjectListDumper {

	public static void main(String[] args) throws IOException {
		//Cache.init();

		File file = new File("objectlist.txt");
		if (file.exists())
			file.delete();
		else
			file.createNewFile();
		
		BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		writer.append("//Version = 727\n");
		writer.flush();
		
		for (int id = 0; id < Utils.getObjectDefinitionsSize(); id++) {
			ObjectDefinitions def = ObjectDefinitions.getDefs(id);
//			if (def.getConfigInfoString().isEmpty())
//				continue;
//			writer.append(id + " - " + def.getName() + def.getConfigInfoString());
			
			writer.append(id + " - " + def.getName() + " " + Arrays.toString(def.types) + " " + Arrays.toString(def.options));
			
			writer.newLine();
			writer.flush();
		}
		
		writer.close();
	}
}
