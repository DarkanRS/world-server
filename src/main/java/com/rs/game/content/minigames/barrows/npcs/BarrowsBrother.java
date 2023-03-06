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
package com.rs.game.content.minigames.barrows.npcs;

import com.rs.game.content.minigames.barrows.BarrowsController;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.Hit;
import com.rs.game.model.entity.npc.OwnedNPC;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class BarrowsBrother extends OwnedNPC {

	private BarrowsController barrows;

	public BarrowsBrother(int id, Tile tile, BarrowsController barrows) {
		super(barrows.getPlayer(), id, tile, false);
		this.barrows = barrows;
		setIntelligentRouteFinder(true);
	}

	@Override
	public void sendDeath(Entity source) {
		if (barrows != null) {
			barrows.targetDied();
			barrows = null;
		}
		super.sendDeath(source);
	}

	@Override
	public double getMeleePrayerMultiplier() {
		return getId() != 2030 ? 0 : Utils.random(3) == 0 ? 1 : 0;
	}

	@Override
	public void handlePreHitOut(Entity target, Hit hit) {
		super.handlePreHitOut(target, hit);
		if (getId() == 2027 && hit.getDamage() > 0 && Utils.random(4) == 0) {
			target.setNextSpotAnim(new SpotAnim(398));
			heal(hit.getDamage());
		}
	}

	public void disappear() {
		barrows = null;
		finish();
	}

	@Override
	public void finish() {
		if (hasFinished())
			return;
		if (barrows != null) {
			barrows.targetFinishedWithoutDie();
			barrows = null;
		}
		super.finish();
	}

	@Override
	public List<Entity> getPossibleTargets() {
		List<Entity> owners = new ArrayList<>();
		Player owner = getOwner();
		if (owner != null)
			owners.add(owner);
		return owners;
	}

}
