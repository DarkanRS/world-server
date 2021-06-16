package com.rs.tools;

import java.io.IOException;
import java.util.Arrays;

import com.rs.cache.Cache;
import com.rs.cache.loaders.cs2.CS2Definitions;
import com.rs.cache.loaders.cs2.CS2Instruction;
import com.rs.cache.loaders.cs2.CS2Script;
import com.rs.lib.io.InputStream;

public class CS2ScriptPacker {
	
	public static void main(String[] args) throws IOException {
		//Cache.init();
		
		int baseId = 1351;
		int toId = 6566;
		
		CS2Script script = CS2Definitions.getScript(baseId);
		script.id = toId;
		System.out.println(Arrays.toString(script.arguments));
		System.out.println(Arrays.toString(script.intOpValues));
		System.out.println(script.intLocalsCount);
		System.out.println(script.stringLocalsCount);
		for (int i = 0;i < script.operations.length;i++) {
			System.out.println("/*["+i+"]:*/ " + script.getOpString(i));
		}
		
		script.intArgsCount = 2;
		script.stringArgsCount = 1;
		script.intLocalsCount = 2;
		script.stringLocalsCount = 1;
		script.setInstruction(6, CS2Instruction.LOAD_STRING, 0);
		script.setInstruction(7, CS2Instruction.CC_SETTEXT, 0);
		
		script = new CS2Script(new InputStream(script.encode()));
		script.id = toId;
		System.out.println(Arrays.toString(script.arguments));
		System.out.println(Arrays.toString(script.intOpValues));
		for (int i = 0;i < script.operations.length;i++) {
			System.out.println("/*["+i+"]:*/ " + script.getOpString(i));
		}
		
		script.write(Cache.STORE);
	}

}
