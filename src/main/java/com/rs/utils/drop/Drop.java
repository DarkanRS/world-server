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
//  Copyright © 2021 Trenton Kress
//  This file is part of project: Darkan
//
package com.rs.utils.drop;

import com.rs.lib.game.Item;
import com.rs.lib.util.Utils;
import com.rs.utils.DropSets;

public class Drop {

	private int id = -1;
	private int min;
	private int max;
	private String rollTable;

	public Drop(String rollTable) {
		this.rollTable = rollTable;
	}

	public Drop(int itemId, int min, int max) {
		id = itemId;
		this.min = min;
		this.max = max;
	}

	public Drop(int itemId, int amount) {
		id = itemId;
		min = amount;
		max = amount;
	}

	public Drop(int itemId) {
		id = itemId;
		min = 1;
		max = 1;
	}

	public Item toItem() {
		return new Item(id, getAmount());
	}

	public int getId() {
		return id;
	}

	public int getAmount() {
		int amt = Utils.random(min, max+1);
		if (max < 2)
			amt = 1;
		return amt;
	}

	public int getMin() {
		return min;
	}

	public int getMax() {
		return max;
	}

	public DropSet getRollTable() {
		return rollTable == null ? null : DropSets.getDropSet(rollTable);
	}

	@Override
	public String toString() {
		return "[" + id + ", " + min + ", " + max + "]";
	}
}
