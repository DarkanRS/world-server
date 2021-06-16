package com.rs.tools;

import java.io.IOException;

import com.rs.cache.loaders.EnumDefinitions;
import com.rs.cache.loaders.StructDefinitions;

public class EmoteParser {
	
	EnumDefinitions emoteEnum;
	
	public static void main(String[] args) throws IOException {
		//Cache.init();
		
		EnumDefinitions emoteEnum = EnumDefinitions.getEnum(3874);
		for (int i = 0;i < 500;i++) {
			Integer key = emoteEnum.getIntValue(i);
			if (key == null || (i > 2 && key == 1783))
				continue;
			StructDefinitions emoteStruct = StructDefinitions.getStruct(key);
			System.out.println("E_"+String.format("%03d", i) + "(" + i + ", "+key+", \""+ emoteStruct.getStringValue(1419) + "\"),");
		}
	}

}
