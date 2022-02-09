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
package com.rs.game.player.dialogues;

import com.rs.game.player.controllers.QueenBlackDragonController;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.WorldTile;

public class GrotwormLairD extends Dialogue {

	@Override
	public void start() {
		if ((boolean) parameters[0]) {
			stage = -1;
			sendDialogue("The shortcut leads to the deepest level of the dungeon. The worms in that area are significantly more dangerous.");
		} else {
			stage = 2;
			sendDialogue("You will be sent to the heart of this cave complex - alone. There is no way out other than victory, teleportation, or death. Only those who can endure dangerous counters (level 110 or more) should proceed.");
		}
	}

	@Override
	public void run(int interfaceId, int componentId) {
		stage++;
		if (stage == 0)
			sendOptionsDialogue("Slide down the worm burrow?", "Yes.", "No.");
		else if (stage == 1) {
			if (componentId == OPTION_1)
				player.useStairs(-1, new WorldTile(1206, 6506, 0), 1, 2);
			end();
		} else if (stage == 3) {
			sendOptionsDialogue(SEND_DEFAULT_OPTIONS_TITLE, "Proceed.", "Step away from the portal.");
			stage = 3;
		} else if (stage == 4) {
			if (componentId == OPTION_1) {
				if (player.getSkills().getLevelForXp(Constants.SUMMONING) < 60) {
					player.sendMessage("You need a summoning level of 60 to go through this portal.");
					return;
				}
				player.lock();
				player.getControllerManager().startController(new QueenBlackDragonController());
				player.setNextAnimation(new Animation(16752));
			}
			end();
		}
	}

	@Override
	public void finish() {

	}
}
