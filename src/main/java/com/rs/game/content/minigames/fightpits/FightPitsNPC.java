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
package com.rs.game.content.minigames.fightpits;

import java.util.ArrayList;
import java.util.List;

import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.WorldTile;

public class FightPitsNPC extends NPC {

	public FightPitsNPC(int id, WorldTile tile) {
		super(id, tile, true);
		setForceMultiArea(true);
		setNoDistanceCheck(true);
	}

	@Override
	public void sendDeath(Entity source) {
		setNextSpotAnim(new SpotAnim(2924 + getSize()));
		super.sendDeath(source);
	}

	@Override
	public List<Entity> getPossibleTargets() {
		ArrayList<Entity> possibleTarget = new ArrayList<>();
		for (Player player : FightPits.arena)
			possibleTarget.add(player);
		return possibleTarget;
	}

}
