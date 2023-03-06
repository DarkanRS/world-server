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
package com.rs.game.content.bosses.glacor;

import com.rs.game.World;
import com.rs.game.model.entity.Hit;
import com.rs.game.model.entity.npc.NPC;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Utils;

public class EnduringMinion extends NPC {

	public Glacor parent;
	public boolean defeated = false;

	public EnduringMinion(int id, Tile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea, boolean spawned, Glacor parent) {
		super(id, tile, spawned);
		this.parent = parent;
		setForceMultiAttacked(true);
	}

	@Override
	public void processEntity() {
		super.processEntity();
		if (getHitpoints() <= 0 || isDead()) {
			if (!defeated)
				World.sendProjectile(this, parent, 634, 34, 16, 30, 35, 16, 15);
			defeated = true;
		}
	}

	@Override
	public void handlePreHit(Hit hit) {
		int distance = (int) Utils.getDistance(parent.getX(), parent.getY(), getX(), getY());
		double damageReduction = distance * 0.1;
		hit.setDamage((int) (hit.getDamage() * damageReduction));

		super.handlePreHit(hit);
	}

}
