package com.rs.game.player.content.skills.dungeoneering.rooms;

import com.rs.game.player.content.skills.dungeoneering.DungeonConstants;
import com.rs.game.player.content.skills.dungeoneering.DungeonManager;
import com.rs.game.player.content.skills.dungeoneering.RoomReference;

public class HandledRoom {

	private int chunkX;
	private int chunkY;
	private int[] doorsDirections;
	private RoomEvent event;
	private int[] keySpot;
	
	public HandledRoom(int chunkX, int chunkY, RoomEvent event, int[] keySpot, int... doorDirections) {
		this.chunkX = chunkX;
		this.chunkY = chunkY;
		this.event = event;
		this.doorsDirections = doorDirections;
		this.keySpot = keySpot;
	}
	
	public HandledRoom(int chunkX, int chunkY, RoomEvent event, int... doorsDirections) {
		this(chunkX, chunkY, event, null, doorsDirections);
	}

	public HandledRoom(int chunkX, int chunkY, int... doorsDirections) {
		this(chunkX, chunkY, null, null, doorsDirections);
	}
	public int getChunkX() {
		return chunkX;
	}

	public int getChunkY() {
		return chunkY;
	}

	public int[] getDoorDirections() {
		return doorsDirections;
	}

	public RoomEvent getRoomEvent() {
		return event;
	}

	public boolean hasSouthDoor(int rotation) {
		return hasDoor(rotation == 0 ? DungeonConstants.SOUTH_DOOR : rotation == 1 ? DungeonConstants.EAST_DOOR : rotation == 2 ? DungeonConstants.NORTH_DOOR : DungeonConstants.WEST_DOOR);
	}

	public boolean hasNorthDoor(int rotation) {
		return hasDoor(rotation == 0 ? DungeonConstants.NORTH_DOOR : rotation == 1 ? DungeonConstants.WEST_DOOR : rotation == 2 ? DungeonConstants.SOUTH_DOOR : DungeonConstants.EAST_DOOR);
	}

	public boolean hasWestDoor(int rotation) {
		return hasDoor(rotation == 0 ? DungeonConstants.WEST_DOOR : rotation == 1 ? DungeonConstants.SOUTH_DOOR : rotation == 2 ? DungeonConstants.EAST_DOOR : DungeonConstants.NORTH_DOOR);
	}

	public boolean hasEastDoor(int rotation) {
		return hasDoor(rotation == 0 ? DungeonConstants.EAST_DOOR : rotation == 1 ? DungeonConstants.NORTH_DOOR : rotation == 2 ? DungeonConstants.WEST_DOOR : DungeonConstants.SOUTH_DOOR);
	}

	public boolean hasDoor(int direction) {
		for (int dir : doorsDirections)
			if (dir == direction)
				return true;
		return false;
	}

	public boolean isComplexity(int complexity) {
		return true;
	}

	public void openRoom(DungeonManager dungeon, RoomReference reference) {
		if (event == null)
			return;
		event.openRoom(dungeon, reference);
	}

//	public boolean allowSpecialDoors() {
//		return true;
//	}

	public boolean allowResources() {
		return true;
	}

	public boolean isAvailableOnFloorType(int floorType) {
		return true;
	}
	
	
	public int[] getKeySpot() {
		return keySpot;
	}
	

}
