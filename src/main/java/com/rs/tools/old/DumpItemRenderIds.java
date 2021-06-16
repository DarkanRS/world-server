package com.rs.tools.old;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.rs.cache.loaders.ItemDefinitions;

public class DumpItemRenderIds {

	public static void main(String[] args) throws IOException {
		//Cache.init();
		dumpRenders();
	}

	public static void dumpRenders() throws IOException {
		File file = new File("./renderids.txt");
		if (file.exists()) {
			file.delete();
		}
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