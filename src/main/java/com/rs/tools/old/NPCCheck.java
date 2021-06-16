package com.rs.tools.old;

import java.io.IOException;
import java.util.Arrays;

import com.rs.cache.loaders.ItemDefinitions;

public class NPCCheck {

	public static void main(String[] args) throws IOException {
		//Cache.init();
		ItemDefinitions defs = ItemDefinitions.getDefs(19760);
		ItemDefinitions defs2 = ItemDefinitions.getDefs(20771);
		System.out.println("defs" + defs.inventoryOptions[50]);
		System.out.println("defs" + Arrays.toString(defs2.inventoryOptions));
	}

}
