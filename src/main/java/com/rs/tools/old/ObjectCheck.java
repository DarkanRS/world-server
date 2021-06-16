package com.rs.tools.old;

import java.io.IOException;

import com.rs.cache.loaders.ObjectDefinitions;
import com.rs.lib.util.Utils;

public class ObjectCheck {

	public static void main(String[] args) throws IOException {
		//Cache.init();
		for (int i = 0; i < Utils.getObjectDefinitionsSize(); i++) {
			ObjectDefinitions def = ObjectDefinitions.getDefs(i);
			if (def.containsOption("Steal-from")) {
				System.out.println(def.id + " - " + def.getName());
			}
		}
	}

}
