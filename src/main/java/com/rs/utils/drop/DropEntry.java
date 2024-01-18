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

public class DropEntry {
	private final DropTable table;
	private final double min;
	private final double max;
	private boolean always;

	public DropEntry(DropTable table) {
		this(table, 0.0, 0.0);
		always = true;
	}

	public DropEntry(DropTable table, double min, double max) {
		this.table = table;
		this.min = min;
		this.max = max;
	}

	public DropTable getTable() {
		return table;
	}

	public double getMin() {
		return min;
	}

	public double getMax() {
		return max;
	}

	public boolean isAlways() {
		return always;
	}

	@Override
	public String toString() {
		return "[" + (table == null ? "Nothing" : table.toString()) + " (" + min + "-" + max + ")]";
	}
}
