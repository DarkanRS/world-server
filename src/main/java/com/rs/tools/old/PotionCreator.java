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
package com.rs.tools.old;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.lib.util.Utils;

import java.lang.SuppressWarnings;

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
				if (amt == 6)
					amt = 0;
				amt++;
			}

		}
	}
}
