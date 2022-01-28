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

import com.rs.game.player.dialogues.Dialogue;

public class PrestigeReset extends Dialogue {

	@Override
	public void start() {
		sendDialogue("Are you sure you want to reset your dungeon progress? Your previous progress will be set to the number of floors you have completed and all floors will be marked as incomplete. This cannot be undone.");
	}

	@Override
	public void run(int interfaceId, int componentId) {

		if (stage == -1) {
			sendOptionsDialogue("Are you sure?", "Yes, reset my progress.", "No, don't reset my progress.");
			stage = 0;
		} else if (stage == 0) {
			if (componentId == OPTION_1)
				player.getDungManager().resetProgress();
			end();
		}
	}

	@Override
	public void finish() {

	}

}
