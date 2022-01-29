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
import com.rs.game.player.content.skills.construction.HouseConstants.Builds;
import com.rs.game.player.content.skills.construction.HouseConstants.Room;
import com.rs.game.player.dialogues.Dialogue;

public class CreateRoomStairsD extends Dialogue {

	private RoomReference room;
	private boolean up;

	@Override
	public void start() {
		room = (RoomReference) parameters[0];
		up = (boolean) parameters[1];
		sendOptionsDialogue("These stairs do not lead anywhere. Do you want to build a room at the " + (up ? "top" : "bottom") + "?", "Yes.", "No.");

	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (stage == -1) {
			if (componentId == OPTION_1) {
				stage = 0;
				if (room.getZ() == 1 && !up)
					sendOptionsDialogue(SEND_DEFAULT_OPTIONS_TITLE, "Skill hall", "Quest hall", "Dungeon stairs room");
				else
					sendOptionsDialogue(SEND_DEFAULT_OPTIONS_TITLE, "Skill hall", "Quest hall");
				return;
			}
		} else {
			Room r = room.getZ() == 1 && !up && componentId == OPTION_3 ? Room.DUNGEON_STAIRS : componentId == OPTION_2 ? up ? Room.HALL_QUEST_DOWN : Room.HALL_QUEST : up ? Room.HALL_SKILL_DOWN : Room.HALL_SKILL;
			Builds stair = room.getZ() == 1 && !up && componentId == OPTION_3 ? Builds.STAIRCASE_2 : componentId == OPTION_2 ? up ? Builds.STAIRCASE_DOWN_1 : Builds.STAIRCASE_1 : up ? Builds.STAIRCASE_DOWN : Builds.STAIRCASE;
			RoomReference newRoom = new RoomReference(r, room.getX(), room.getY(), room.getZ() + (up ? 1 : -1), room.getRotation());
			int slot = room.getStaircaseSlot();
			if (slot != -1) {
				newRoom.addObject(stair, slot);
				player.getHouse().createRoom(newRoom);
			}
		}
		end();

	}

	@Override
	public void finish() {

	}

}