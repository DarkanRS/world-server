package com.rs.game.player.content.skills.dungeoneering.rooms;

import com.rs.game.player.content.skills.dungeoneering.DungeonConstants;
import com.rs.game.player.content.skills.dungeoneering.DungeonConstants.Puzzle;
import com.rs.game.player.content.skills.dungeoneering.VisibleRoom;

public class HandledPuzzleRoom extends HandledRoom {

	private Puzzle puzzle;

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
