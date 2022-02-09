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

		for (Object itemId : dung.getValues().values())
			System.out.println(ItemDefinitions.getDefs((int) itemId).name.toUpperCase().replace(" ", "_") + "(0000, " + itemId + "),");
	}

}
