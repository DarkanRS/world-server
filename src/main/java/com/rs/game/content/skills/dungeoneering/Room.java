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
package com.rs.game.content.skills.dungeoneering;

import com.rs.game.content.skills.dungeoneering.rooms.HandledPuzzleRoom;
import com.rs.game.content.skills.dungeoneering.rooms.HandledRoom;
import com.rs.game.content.skills.dungeoneering.rooms.NormalRoom;
import com.rs.game.content.skills.dungeoneering.rooms.StartRoom;

import java.util.Arrays;

public class Room {

	private HandledRoom room;
	private final int rotation;
	private int dropId;
	//	private int creationIndex;

	private Door[] doors;

	private int thiefChest;

	private boolean critPath;

	public Room(HandledRoom room, int rotation) {
		this.room = room;
		this.rotation = rotation;
		reset();
		thiefChest = -1;
	}

	public void reset() {
		critPath = false;
		dropId = -1;
		doors = new Door[room.getDoorDirections().length];
	}


	public void setDoor(int index, Door door) {
		doors[index] = door;
	}

	public Door getDoor(int index) {
		return doors[index];
	}
	public int getDoorsCount() {
		return doors.length;
	}

	public Door getDoorByRotation(int rotation) {
		int index = getDoorIndexByRotation(rotation);
		return index == -1 ? null : getDoor(index);
	}

	public void removeGuardianDoors() {
		for (int i = 0; i < doors.length; i++) {
			Door door = doors[i];
			if (door != null && door.getType() == DungeonConstants.GUARDIAN_DOOR)
				doors[i] = null;
		}
	}

	public void removeChallengeDoors() {
		for (int i = 0; i < doors.length; i++) {
			Door door = doors[i];
			if (door != null && door.getType() == DungeonConstants.CHALLENGE_DOOR)
				doors[i] = null;
		}
	}

	public int getDoorIndexByRotation(int rotation) {
		for (int i = 0; i < room.getDoorDirections().length; i++)
			if ((room.getDoorDirections()[i] + this.rotation & 0x3) == rotation)
				return i;
		return -1;
	}

	public int getChunkX(int complexity) {
		return room.getChunkX() + getChunkXOffset(complexity);
	}

	public int getChunkY(int type) {
		return room.getChunkY() + getChunkYOffset(type);
	}

	public int getChunkXOffset(int complexity) {
		return complexity < 6 && room instanceof StartRoom ? (complexity <= 2 ? -6 : -(5 - complexity) * 2) : 0;
	}

	public int getChunkYOffset(int type) {
		return (room instanceof StartRoom ? 16 : room instanceof NormalRoom ? 48 : room instanceof HandledPuzzleRoom ? 16 : 0) * type;
	}

	public int getRotation() {
		return rotation;
	}

	@Override
	public String toString() {
		return "[Room][" + rotation + "]" + ", " + Arrays.toString(room.getDoorDirections()) + ", " + (getChunkX(0) << 3) + ", " + (getChunkY(0) << 3);
	}

	public boolean hasSouthDoor() {
		return room.hasSouthDoor(rotation);
	}

	public boolean hasNorthDoor() {
		return room.hasNorthDoor(rotation);
	}

	public boolean hasWestDoor() {
		return room.hasWestDoor(rotation);
	}

	public boolean hasEastDoor() {
		return room.hasEastDoor(rotation);
	}

	public boolean hasDoor(int dir) {
		return room.hasDoor((dir - rotation) & 0x3);
	}

	public boolean containsOnlySouthDoor() {
		return room.getDoorDirections().length == 1 && room.getDoorDirections()[0] == DungeonConstants.SOUTH_DOOR;
	}

	public void setRoom(HandledRoom room) {
		this.room = room;
	}

	public HandledRoom getRoom() {
		return room;
	}

	public void openRoom(DungeonManager dungeon, RoomReference reference) {
		room.openRoom(dungeon, reference);
	}

	//	public int getCreationIndex() {
	//		return creationIndex;
	//	}
	//
	//	public void setCreationIndex(int creationIndex) {
	//		this.creationIndex = creationIndex;
	//	}

	public int getDropId() {
		return dropId;
	}

	public void setDropId(int dropId) {
		this.dropId = dropId;
	}

	public int getThiefChest() {
		return thiefChest;
	}

	public void setThiefChest(int thiefChest) {
		this.thiefChest = thiefChest;
	}

	public boolean isCritPath() {
		return critPath;
	}

	public void setCritPath(boolean critPath) {
		this.critPath = critPath;
	}

}