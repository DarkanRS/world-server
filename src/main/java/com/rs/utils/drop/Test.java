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
package com.rs.utils.drop;

import com.rs.game.content.minigames.barrows.BarrowsController;
import com.rs.game.model.item.ItemsContainer;
import com.rs.lib.game.Item;
import com.rs.lib.util.Utils;
import com.rs.utils.DropSets;

import java.io.IOException;

public class Test {

	public static void main(String[] args) throws IOException {
		//Cache.init();
		DropSets.init();

		ItemsContainer<Item> drops = new ItemsContainer<>(200, true);

		long start = System.currentTimeMillis();
		int sample = 100000;
		for (int i = 0;i < sample;i++)
			drops.addAll(BarrowsController.getSimulatedDrop(7, 1012));
		long time = System.currentTimeMillis() - start;

		drops.sortByItemId();
		for (Item item : drops.toArray()) {
			if (item == null)
				continue;
			System.out.println(item.getDefinitions().getName()+": " + Utils.getFormattedNumber(item.getAmount()) + " (1/" + Math.round((double) sample / (double) item.getAmount()) + ")");
		}
		System.out.println("Generated " + sample + " drops in " + time + "ms");
		int barrowsItems = 0;
		for (Item i : drops.toArray())
			if (i != null && i.getName().contains("'"))
				barrowsItems += i.getAmount();
		System.out.println("Barrows item approximately 1/"+(sample/barrowsItems));
	}

}
