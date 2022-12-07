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
package com.rs.game.model.entity;

import com.rs.game.model.entity.pathing.Direction;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;
import com.rs.utils.WorldUtil;

public class ForceMovement {

	private WorldTile toFirstTile;
	private WorldTile toSecondTile;
	private int firstTileTicketDelay;
	private int secondTileTicketDelay;
	protected int direction;

	public ForceMovement(WorldTile toFirstTile, int firstTileTicketDelay, Direction direction) {
		this(toFirstTile, firstTileTicketDelay, null, 0, WorldUtil.getAngleTo(direction));
	}

	public ForceMovement(WorldTile toFirstTile, int firstTileTicketDelay, WorldTile toSecondTile, int secondTileTicketDelay) {
		this(toFirstTile, firstTileTicketDelay, toSecondTile, secondTileTicketDelay, Utils.getAngleTo(toFirstTile, toSecondTile));
	}

	public ForceMovement(WorldTile toFirstTile, int firstTileTicketDelay, WorldTile toSecondTile, int secondTileTicketDelay, Direction direction) {
		this.toFirstTile = WorldTile.of(toFirstTile);
		this.firstTileTicketDelay = firstTileTicketDelay;
		if (toSecondTile != null)
			this.toSecondTile = WorldTile.of(toSecondTile);
		this.secondTileTicketDelay = secondTileTicketDelay;
		this.direction = WorldUtil.getAngleTo(direction);
	}

	public ForceMovement(WorldTile toFirstTile, int firstTileTicketDelay, WorldTile toSecondTile, int secondTileTicketDelay, int direction) {
		this.toFirstTile = WorldTile.of(toFirstTile);
		this.firstTileTicketDelay = firstTileTicketDelay;
		if (toSecondTile != null)
			this.toSecondTile = WorldTile.of(toSecondTile);
		this.secondTileTicketDelay = secondTileTicketDelay;
		this.direction = direction;
	}

	public int getDirection() {
		return direction;
	}

	public WorldTile getToFirstTile() {
		return toFirstTile;
	}

	public WorldTile getToSecondTile() {
		return toSecondTile;
	}

	public int getFirstTileTicketDelay() {
		return firstTileTicketDelay;
	}

	public int getSecondTileTicketDelay() {
		return secondTileTicketDelay;
	}
}
