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
package com.rs.game.content.skills.hunter.tracking;

import com.rs.lib.game.Tile;

import java.util.ArrayList;
import java.util.List;

public class Trail {

	private Trail prev;
	private List<Trail> next;

	private int nextObj;
	private Tile nextObjTile;
	private int varbit;
	private int value;

	public Trail(int nextObj, Tile nextObjTile, int varbit, int value, Trail... nexts) {
		this.nextObj = nextObj;
		this.nextObjTile = nextObjTile;
		this.varbit = varbit;
		this.value = value;
		if (nexts.length <= 0)
			return;
		next = new ArrayList<>();
		for (Trail t : nexts) {
			t.prev = this;
			next.add(t);
		}
	}

}
