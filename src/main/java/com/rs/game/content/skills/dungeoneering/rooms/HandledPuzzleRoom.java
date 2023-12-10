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
package com.rs.game.content.skills.dungeoneering.rooms;

import com.rs.game.content.skills.dungeoneering.DungeonConstants;
import com.rs.game.content.skills.dungeoneering.DungeonConstants.Puzzle;
import com.rs.game.content.skills.dungeoneering.VisibleRoom;

public class HandledPuzzleRoom extends HandledRoom {

	private final Puzzle puzzle;

	public HandledPuzzleRoom(int i, Puzzle puzzle) {
		this(i, puzzle, null);
	}

	public HandledPuzzleRoom(int i, Puzzle puzzle, RoomEvent event) {
		super(puzzle.getChunkX(), 528 + (i * 2), event, puzzle.getKeySpot(i), DungeonConstants.PUZZLE_DOOR_ORDER[i]);
		this.puzzle = puzzle;
	}

	//	@Override
	//	public final boolean allowSpecialDoors() {
	//		return false;
	//	}

	@Override
	public boolean isComplexity(int complexity) {
		return complexity == 6;
	}

	public VisibleRoom createVisibleRoom() {
		return puzzle.newInstance();
	}

	@Override
	public boolean isAvailableOnFloorType(int floorType) {
		return puzzle.isAvailableOnFloorType(floorType);
	}

	@Override
	public boolean allowResources() {
		return puzzle.allowResources();
	}

}
