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

import com.rs.cache.loaders.EnumDefinitions;
import com.rs.cache.loaders.StructDefinitions;

import java.io.IOException;

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
