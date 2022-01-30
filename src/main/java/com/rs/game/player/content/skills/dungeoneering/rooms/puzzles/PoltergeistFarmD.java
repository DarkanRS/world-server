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
package com.rs.game.player.content.skills.dungeoneering.rooms.puzzles;

import com.rs.game.object.GameObject;
import com.rs.game.player.dialogues.Dialogue;

public class PoltergeistFarmD extends Dialogue {

	@Override
	public void start() {
		sendOptionsDialogue("Select an Option", "Pick corianger.", "Pick explosemary.", "Pick parslay.", "More herbs.");

	}

	@Override
	public void run(int interfaceId, int componentId) {
		PoltergeistRoom room = (PoltergeistRoom) parameters[0];
		GameObject object = (GameObject) parameters[1];
		if (!room.canTakeHerb())
			// another player took all the herbs while dialogue was open
			end();
		else if (stage == -1) {
			if (componentId == OPTION_1) {
				room.takeHerb(player, object, 0);
				end();
			} else if (componentId == OPTION_2) {
				room.takeHerb(player, object, 1);
				end();
			} else if (componentId == OPTION_3) {
				room.takeHerb(player, object, 2);
				end();
			} else if (componentId == OPTION_4) {
				stage = 0;
				sendOptionsDialogue("Select an Option", "Pick cardamaim.", "Pick papreaper.", "Pick slaughtercress.", "More herbs.");

			}
		} else if (stage == 0)
			if (componentId == OPTION_1) {
				room.takeHerb(player, object, 3);
				end();
			} else if (componentId == OPTION_2) {
				room.takeHerb(player, object, 4);
				end();
			} else if (componentId == OPTION_3) {
				room.takeHerb(player, object, 5);
				end();
			} else if (componentId == OPTION_4) {
				stage = -1;
				sendOptionsDialogue("Select an Option", "Pick corianger.", "Pick explosemary.", "Pick parslay.", "More herbs.");
			}
	}

	@Override
	public void finish() {

	}

}
