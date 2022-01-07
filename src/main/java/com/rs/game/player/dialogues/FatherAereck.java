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

import com.rs.game.player.content.world.GraveStoneSelection;
import com.rs.game.player.quests.Quest;
import com.rs.lib.util.Utils;

public class FatherAereck extends Dialogue {

	private int npcId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendNPCDialogue(npcId, 9827, "Hello there brother " + Utils.formatPlayerNameForDisplay(player.getDisplayName()) + ". How may I help you today?");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (stage == -1) {
			stage = 0;
			sendOptionsDialogue("What would you like to say?", "Can I have a different gravestone?", "Can you restore my prayer?", "I'm looking for a quest.");
		} else if (stage == 0) {
			if (componentId == OPTION_1) {
				stage = 1;
				sendPlayerDialogue(9827, "Can I have a different gravestone?");
			} else if (componentId == OPTION_2) {
				stage = 2;
				sendPlayerDialogue(9827, "Can you restore my prayer?");
			} else {
				stage = 20;
				sendPlayerDialogue(9827, "I'm looking for a quest.");
			}
		} else if (stage == 20) {
			stage = 3;
			if (player.getQuestManager().getStage(Quest.RESTLESS_GHOST) == 0) {
				sendNPCDialogue(npcId, 9827, "Well that's convenient. I seem to be having a bit of a<br>ghost problem. Could you go speak to speak to<br>Father Urhney down in the swamp about how to<br>exorcise the spirit?");
				player.getQuestManager().setStage(Quest.RESTLESS_GHOST, 1, true);
			} else
				sendNPCDialogue(npcId, 9827, "I don't have anything else for you to do right now.");
		} else if (stage == 1) {
			stage = 4;
			sendNPCDialogue(npcId, 9827, "Of course you can. Have a look at this selection of gravestones.");
		} else if (stage == 2) {
			stage = 3;
			sendNPCDialogue(npcId, 9827, "I think the Gods prefer it if you pray<br>to them at an altar dedicated to their name.");
		} else if (stage == 4) {
			end();
			GraveStoneSelection.openSelectionInterface(player);
		} else if (stage == 3)
			end();
	}

	@Override
	public void finish() {

	}
}