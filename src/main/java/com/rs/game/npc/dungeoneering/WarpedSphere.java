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
package com.rs.game.npc.dungeoneering;

import com.rs.game.Hit;
import com.rs.game.Hit.HitLook;
import com.rs.game.player.Player;
import com.rs.game.player.content.skills.dungeoneering.DungeonManager;
import com.rs.game.player.content.skills.dungeoneering.RoomReference;
import com.rs.game.player.content.skills.magic.Magic;
import com.rs.lib.game.WorldTile;

public class WarpedSphere extends DungeonNPC {

	private static final int[][] ORB_TELEPORT_LOCATIONS =
		{{ 0, 0 },
				{ 0, 0 },
				{ 13, 4 },
				{ 11, 12 },
				{ 3, 4 },
				{ 3, 12 } },

		PLAYER_TELEPORT_LOCATIONS =
	{{ 0, 0 },
			{ 0, 0 },
			{ 6, 11 },
			{ 10, 4 },
			{ 10, 11 },
			{ 10, 4 } };

	private final RoomReference reference;
	private int stage;

	public WarpedSphere(RoomReference reference, int id, WorldTile tile, DungeonManager manager) {
		super(id, tile, manager);
		this.reference = reference;
		stage = -1;//Gotta follow warmonger
	}

	@Override
	public void processNPC() {
		super.processNPC();
		if (stage > 0)
			if (hasWalkSteps()) {
				WorldTile previousTile = getLastWorldTile();
				if (previousTile == null)
					return;
				final int[] TELEPORT_TILE = PLAYER_TELEPORT_LOCATIONS[stage];
				for (Player player : getManager().getParty().getTeam())
					if (!player.isLocked() && player.matches(previousTile)) {
						player.applyHit(new Hit(this, 100, HitLook.REFLECTED_DAMAGE));
						Magic.sendTeleportSpell(player, 13493, 13494, 2437, 2435, 0, 0, getManager().getTile(reference, TELEPORT_TILE[0], TELEPORT_TILE[1]), 6, false, 0);
					}
			}

		if (!hasWalkSteps()) {
			boolean can = false;
			if (Math.random() > 0.59564)
				can = true;

			if (can) {
				int moveX = (int) Math.round(Math.random() * 10.0 - 5.0);
				int moveY = (int) Math.round(Math.random() * 10.0 - 5.0);
				addWalkSteps(getRespawnTile().getX() + moveX, getRespawnTile().getY() + moveY, 5, true);
			}
		}
	}

	public void nextStage() {
		stage++;
		final int[] TELEPORT_TILE = ORB_TELEPORT_LOCATIONS[stage];
		if (TELEPORT_TILE[0] == 0)
			return;
		WorldTile nextTile = getManager().getTile(reference, TELEPORT_TILE[0], TELEPORT_TILE[1]);
		setRespawnTile(nextTile);
		setNextWorldTile(nextTile);
	}
}
