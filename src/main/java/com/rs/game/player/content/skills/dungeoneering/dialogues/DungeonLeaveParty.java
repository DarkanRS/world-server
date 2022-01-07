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
//  Copyright © 2021 Trenton Kress
//  This file is part of project: Darkan
//
package com.rs.game.player.content.skills.dungeoneering.dialogues;

import com.rs.game.player.dialogues.Dialogue;

public class DungeonLeaveParty extends Dialogue {

	@Override
	public void start() {
		sendDialogue("Warning: If you leave the dungeon, you will not be able to return to it!");

	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (stage == -1) {
			sendOptionsDialogue("Leave the dungeon for good?", "Yes.", "No.");
			stage = 0;
		} else if (stage == 0) {
			if (componentId == OPTION_1)
				player.getDungManager().leaveParty();
			end();
		}
	}

	@Override
	public void finish() {

	}

}
