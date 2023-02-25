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
package com.rs.game.content.bosses.nomad;

import com.rs.game.model.entity.Hit;
import com.rs.game.model.entity.npc.NPC;
import com.rs.lib.game.Tile;

public class FakeNomad extends NPC {

	private Nomad nomad;

	public FakeNomad(Tile tile, Nomad nomad) {
		super(8529, tile, true);
		this.nomad = nomad;
		setForceMultiArea(true);
	}

	@Override
	public void handlePreHit(Hit hit) {
		nomad.destroyCopy(this);
	}

}
