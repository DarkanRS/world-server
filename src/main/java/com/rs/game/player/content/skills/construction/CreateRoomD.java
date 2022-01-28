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

public class CreateRoomD extends Dialogue {

	private RoomReference room;

	@Override
	public void start() {
		room = (RoomReference) parameters[0];
		sendPreview();
	}

	public void sendPreview() {
		sendOptionsDialogue("Select an Option", "Rotate clockwise", "Rotate anticlockwise.", "Build.", "Cancel");
		player.getHouse().previewRoom(room, false);
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (componentId == OPTION_4) {
			end();
			return;
		}
		if (componentId == OPTION_3) {
			end();
			player.getHouse().createRoom(room);
			return;
		}
		player.getHouse().previewRoom(room, true);
		room.setRotation((room.getRotation() + (componentId == OPTION_1 ? 1 : -1)) & 0x3);
		sendPreview();
	}

	@Override
	public void finish() {
		player.getHouse().previewRoom(room, true);
	}

}