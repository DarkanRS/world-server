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

import java.io.File;
import java.io.IOException;

public class ItemCheck {

	public static final void main(String[] args) throws IOException {
		//Cache.init();
		int total = 0;
		for (int itemId = 0; itemId < Utils.getItemDefinitionsSize(); itemId++)
			if (ItemDefinitions.getDefs(itemId).isWearItem(true) && !ItemDefinitions.getDefs(itemId).isNoted()) {
				File file = new File("bonuses/" + itemId + ".txt");
				if (!file.exists()) {
					System.out.println(file.getName());
					total++;
				}
			}
		System.out.println("Total " + total);
	}
}
