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

public final class StartRoom extends HandledRoom {

	public StartRoom(int chunkX, int chunkY, int... doorsDirections) {
		super(chunkX, chunkY, (RoomEvent) (dungeon, reference) -> {
			dungeon.telePartyToRoom(reference);
			dungeon.spawnNPC(reference, DungeonConstants.SMUGGLER, 8, 8); // smoother
			dungeon.setTableItems(reference);
			dungeon.linkPartyToDungeon();

		}, new int[] {7, 7}, doorsDirections);

	}

	@Override
	public boolean allowResources() {
		return false;
	}

}
