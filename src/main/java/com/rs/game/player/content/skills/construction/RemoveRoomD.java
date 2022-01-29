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
package com.rs.game.player.content.skills.construction;

import com.rs.game.player.content.skills.construction.House.RoomReference;
import com.rs.game.player.dialogues.Dialogue;

public class RemoveRoomD extends Dialogue {

	private RoomReference room;

	@Override
	public void start() {
		room = (RoomReference) parameters[0];
		sendOptionsDialogue("Do you really want to remove the room?", "Yes", "No");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (componentId == OPTION_1 && stage != 1) {
			stage = 1;
			sendOptionsDialogue("You can't get anything back? Remove room?", "Yes! Get rid of my money already!", "No");
		} else if (componentId == OPTION_1 && stage == 1) {
			player.getHouse().removeRoom(room);
			end();
		} else
			end();
	}

	@Override
	public void finish() {

	}
}
