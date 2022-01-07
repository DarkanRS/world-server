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

import com.rs.game.player.content.skills.dungeoneering.DungeonConstants;
import com.rs.game.player.content.skills.dungeoneering.DungeonPartyManager;
import com.rs.game.player.dialogues.Dialogue;

public class DungeonSize extends Dialogue {

	@Override
	public void start() {
		sendDialogue("What size of dungeon would you like?");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		DungeonPartyManager party = player.getDungManager().getParty();
		if (stage == -1) {
			if (party == null || party.getTeam().size() < 3)
				sendOptionsDialogue("Would you like to start a dungeon?", "Small.", "Medium.");
			else
				sendOptionsDialogue("Would you like to start a dungeon?", "Small.", "Medium.", "Large.");
			stage = 0;
		} else if (stage == 0) {
			if (party != null) {
				if (componentId == OPTION_1)
					player.getDungManager().setSize(DungeonConstants.SMALL_DUNGEON);
				else if (componentId == OPTION_2)
					player.getDungManager().setSize(DungeonConstants.MEDIUM_DUNGEON);
				else if (componentId == OPTION_3 && party.getTeam().size() >= 3)
					player.getDungManager().setSize(DungeonConstants.LARGE_DUNGEON);
				player.getDungManager().enterDungeon(false);
			}
			end();
		}
	}

	@Override
	public void finish() {

	}

}
