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
package com.rs.game.player.content.skills.hunter.tracking;

import java.util.ArrayList;
import java.util.List;

import com.rs.lib.game.WorldTile;

public class Burrow {
	
	private int burrowId;
	private WorldTile burrowTile;
	private List<Trail> next;
	
	public Burrow(int burrowId, WorldTile burrowTile, Trail... next) {
		this.burrowId = burrowId;
		this.burrowTile = burrowTile;
		this.next = new ArrayList<>();
		for (Trail t : next)
			this.next.add(t);
	}
}
