// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.
//
//  Copyright (C) 2021 Trenton Kress
//  This file is part of project: Darkan
//
package com.rs.tools;

import com.rs.cache.Cache;
import com.rs.cache.loaders.cs2.CS2Definitions;
import com.rs.cache.loaders.cs2.CS2Instruction;
import com.rs.cache.loaders.cs2.CS2Script;
import com.rs.lib.io.InputStream;

import java.io.IOException;
import java.util.Arrays;

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
		for (int i = 0;i < script.operations.length;i++)
			System.out.println("/*["+i+"]:*/ " + script.getOpString(i));

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
		for (int i = 0;i < script.operations.length;i++)
			System.out.println("/*["+i+"]:*/ " + script.getOpString(i));

		script.write(Cache.STORE);
	}

}
