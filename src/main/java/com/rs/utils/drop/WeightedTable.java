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

public class WeightedTable extends DropTable {

	private final transient int weight;

	public WeightedTable(int weight, Drop... drops) {
		this.weight = weight;
		this.drops = drops;
		dropOne = true;
	}

	public WeightedTable(int weight, String rollTable) {
		this.weight = weight;
		this.rollTable = rollTable;
	}

	public WeightedTable(Drop... drops) {
		this(1, drops);
	}

	public WeightedTable(String rollTable) {
		this(1, rollTable);
	}

	public int getWeight() {
		return weight;
	}

}
