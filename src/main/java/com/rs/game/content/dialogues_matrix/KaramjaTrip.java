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
package com.rs.game.content.dialogues_matrix;

import com.rs.cache.loaders.NPCDefinitions;

public class KaramjaTrip extends MatrixDialogue {

	private int npcId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendEntityDialogue(SEND_1_TEXT_CHAT, new String[] { NPCDefinitions.getDefs(npcId).getName(), "Do you want to go on a trip to Karamja?" }, IS_NPC, npcId, 9827);
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (stage == -1) {
			sendEntityDialogue(SEND_1_TEXT_CHAT, new String[] { NPCDefinitions.getDefs(npcId).getName(), "The trip will cost you 30 coins." }, IS_NPC, npcId, 9827);
			stage = 0;
		} else if (stage == 0) {
			sendOptionsDialogue("Would you like to go?", "Yes please.", "No thank you.");
			stage = 1;
		} else if (stage == 1) {
			if (componentId == OPTION_1)
				doTrip();
			else if (componentId == OPTION_2)
				sendPlayerDialogue(9827, "No thank you.");
			stage = 3;
		} else if (stage == 2) {
			sendDialogue("The ship arrived at Karamja.");
			stage = 3;
		} else if (stage == 3)
			end();
	}

	@Override
	public void finish() {

	}

	public void doTrip() {
		// Inter 299
		sendDialogue("We are having problems... Please wait");

	}

}
