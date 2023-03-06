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
package com.rs.game.content.minigames.castlewars;

import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.npc.NPC;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Utils;

public class CastleWarBarricade extends NPC {

	private int team;

	public CastleWarBarricade(int team, Tile tile) {
		super(1532, tile, true);
		setCantFollowUnderCombat(true);
		this.team = team;
	}

	@Override
	public void processNPC() {
		if (isDead())
			return;
		cancelFaceEntityNoCheck();
		if (getId() == 1533 && Utils.getRandomInclusive(20) == 0)
			sendDeath(this);
	}

	public void litFire() {
		transformIntoNPC(1533);
		sendDeath(this);
	}

	public void explode() {
		// TODO gfx
		sendDeath(this);
	}

	@Override
	public void sendDeath(Entity killer) {
		resetWalkSteps();
		getCombat().removeTarget();
		if (getId() != 1533) {
			setNextAnimation(null);
			reset();
			setLocation(getRespawnTile());
			finish();
		} else
			super.sendDeath(killer);
		CastleWars.removeBarricade(team, this);
	}

}
