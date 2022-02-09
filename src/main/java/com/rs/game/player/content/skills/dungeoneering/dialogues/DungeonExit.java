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
package com.rs.game.player.content.skills.dungeoneering.dialogues;

import com.rs.game.player.controllers.DungeonController;
import com.rs.game.player.dialogues.Dialogue;

public class DungeonExit extends Dialogue {

	private DungeonController dungeon;

	@Override
	public void start() {
		dungeon = (DungeonController) parameters[0];
		sendDialogue("This ladder leads back to the surface. You will not be able", "to come back to this dungeon if you leave.");

	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (stage == -1) {
			sendOptionsDialogue("Leave the dungeon and return to the surface?", "Yes.", "No.");
			stage = 0;
		} else if (stage == 0) {
			if (componentId == OPTION_1)
				dungeon.leaveDungeon();
			end();
		}
	}

	@Override
	public void finish() {

	}

}
