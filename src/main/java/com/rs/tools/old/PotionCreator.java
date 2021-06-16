package com.rs.tools.old;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.lib.util.Utils;

public class PotionCreator {

	public static void main(String[] args) {
		//Cache.init();
		int amt = 1;
		String lastName = null;
		@SuppressWarnings("unused")
		String modified = null;
		for (int i = 23100; i < Utils.getItemDefinitionsSize(); i++) {
			ItemDefinitions def = ItemDefinitions.getDefs(i);
			String name = def.getName();
			lastName = name;
			modified = lastName.replace(" ", "_").replace("(6)", "").replace("(5)", "").replace("(4)", "").replace("(3)", "").replace("(2)", "").replace("(1)", "").toUpperCase();
			if (name.contains("flask") && !name.matches(lastName) && !def.isNoted()) {
				System.out.print(amt == 6 ? i + "\n\n" : i + ", ");
				if (amt == 6) {
					amt = 0;
				}
				amt++;
			}

		}
	}
}
