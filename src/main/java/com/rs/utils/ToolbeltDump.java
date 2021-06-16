package com.rs.utils;

import java.io.IOException;

import com.rs.cache.loaders.EnumDefinitions;
import com.rs.cache.loaders.ItemDefinitions;

public class ToolbeltDump {
	
	@SuppressWarnings("unused")
	public static void main(String[] args) throws IOException {
		//Cache.init();
		
		EnumDefinitions general = EnumDefinitions.getEnum(5350);
		EnumDefinitions fishing = EnumDefinitions.getEnum(5353);
		EnumDefinitions crafting = EnumDefinitions.getEnum(5356);
		EnumDefinitions farming = EnumDefinitions.getEnum(5359);
		EnumDefinitions dung = EnumDefinitions.getEnum(5731);
		
		System.out.println(EnumDefinitions.getEnum(5732).getValues().values());
		
		for (Object itemId : dung.getValues().values()) {
			System.out.println(ItemDefinitions.getDefs((int) itemId).name.toUpperCase().replace(" ", "_") + "(0000, " + itemId + "),");
		}
	}

}
