package com.rs.game;

import com.rs.game.pathing.Direction;
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
		this.toFirstTile = new WorldTile(toFirstTile);
		this.firstTileTicketDelay = firstTileTicketDelay;
		if (toSecondTile != null)
			this.toSecondTile = new WorldTile(toSecondTile);
		this.secondTileTicketDelay = secondTileTicketDelay;
		this.direction = WorldUtil.getAngleTo(direction);
	}

	public ForceMovement(WorldTile toFirstTile, int firstTileTicketDelay, WorldTile toSecondTile, int secondTileTicketDelay, int direction) {
		this.toFirstTile = new WorldTile(toFirstTile);
		this.firstTileTicketDelay = firstTileTicketDelay;
		if (toSecondTile != null)
			this.toSecondTile = new WorldTile(toSecondTile);
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
