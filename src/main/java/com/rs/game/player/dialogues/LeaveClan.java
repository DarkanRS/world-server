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
package com.rs.game.player.dialogues;

import com.rs.net.LobbyCommunicator;

public class LeaveClan extends Dialogue {

	@Override
	public void start() {
		sendDialogue("If you leave the clan, you will need to be invited before you  can join again, and must wait a week before you contribute to clan resources.");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			stage = 0;
			sendOptionsDialogue("Really leave the clan?", "Yes, leave the clan.", "No, I will remain in the clan.");
			break;
		case 0:
			if (componentId == OPTION_1)
				LobbyCommunicator.leaveClanCompletely(player);
			end();
			break;
		default:
			end();
			break;
		}

	}

	@Override
	public void finish() {

	}

}
