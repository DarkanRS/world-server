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

public enum Track {
	PISC_1(new Burrow(19439, Tile.of(2353, 3595, 0),
			new Trail(19375, Tile.of(2347, 3607, 0), 2976, 4,
					new Trail(19428, Tile.of(2355, 3601, 0), 2978, 3),
					new Trail(19428, Tile.of(2354, 3609, 0), 2983, 3),
					new Trail(19376, Tile.of(2348, 3612, 0), 2988, 3,
							new Trail(19428, Tile.of(2354, 3609, 0), 2989, 3),
							new Trail(29428, Tile.of(2351, 3619, 0), 2990, 3))),
			new Trail(19375, Tile.of(2347, 3607, 0), 2976, 4)
			));

	private Burrow burrow;

	private Track(Burrow burrow) {
		this.burrow = burrow;
	}
}
