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

import java.util.Arrays;
import java.util.List;

public class DropSet {
	
	protected int[] ids;
	protected String[] names;
	protected DropTable[] tables;
	protected transient DropList dropList;
	private transient boolean overflowed;
	
	public DropSet(int[] ids, String[] names, DropTable[] tables) {
		this.ids = ids;
		this.names = names;
		this.tables = tables;
	}
	
	public DropSet(DropTable... tables) {
		this.tables = tables;
	}
	
	public DropSet(List<DropTable> tables) {
		this.tables = new DropTable[tables.size()];
		this.tables = tables.toArray(this.tables);
	}

	public int[] getIds() {
		return ids;
	}
	
	public String[] getNames() {
		return names;
	}
	
	public DropTable[] getTables() {
		return tables;
	}
	
	public void setNames(String[] names) {
		this.names = names;
	}
	
	public DropList createDropList() {
		dropList = new DropList(tables);
		overflowed = dropList.isOverflowed();
		return dropList;
	}
	
	public DropList getDropList() {
		if (dropList == null) 
			return createDropList();
		return dropList;
	}
	
	public boolean isOverflowed() {
		return overflowed;
	}
	
	@Override
	public String toString() {
		String s = "";
		if (ids != null && ids.length > 0)
			s += Arrays.toString(ids) + "\n";
		if (names != null && names.length > 0)
			s += Arrays.toString(names) + "\n";
		for (DropTable table : tables) {
			s += table.toString() + "\n";
		}
		s  += "[ ("+getDropList().getNothingFracString()+") - (-1(Nothing)\n";
		return s;
	}

	public boolean isEmpty() {
		return tables == null || tables.length == 0;
	}
}
