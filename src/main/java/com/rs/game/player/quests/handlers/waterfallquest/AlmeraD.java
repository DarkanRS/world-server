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
package com.rs.game.player.quests.handlers.waterfallquest;

import com.rs.game.npc.NPC;
import com.rs.game.player.dialogues.Dialogue;
import com.rs.game.player.quests.Quest;

public class AlmeraD extends Dialogue {

	// sendOptionsDialogue(SEND_DEFAULT_OPTIONS_TITLE,
	// "How is life on the waterfall?", "I am looking for a quest.");
	// sendPlayerDialogue(CALM_TALK, "");
	// sendEntityDialogue(SEND_1_TEXT_CHAT, new String[] {
	// npc.getDefinitions().name, "" }, IS_NPC, npc.getId(), CALM_TALK);

	private NPC npc;

	@Override
	public void start() {
		npc = (NPC) parameters[0];
		sendOptionsDialogue(SEND_DEFAULT_OPTIONS_TITLE, "How is life on the waterfall?", "I am looking for a quest.");
		stage = 1;
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (stage == 1) {
			if (componentId == OPTION_1) {
				sendPlayerDialogue(CALM_TALK, "How is life on the waterfall?");
				stage = 10;
			} else if (componentId == OPTION_2) {
				sendPlayerDialogue(SECRELTY_TALKING, "I am looking for a quest.");
				stage = 5;
			}
		} else if (stage == 10) {
			sendEntityDialogue(SEND_1_TEXT_CHAT, new String[] { npc.getDefinitions().getName(), "I am worried about my son,", "but other than that, everything is fine." }, IS_NPC, npc.getId(), CALM_TALK);
			stage = -1;
		} else if (stage == 5) {
			sendEntityDialogue(SEND_1_TEXT_CHAT, new String[] { npc.getDefinitions().getName(), "I might have one for you.", "My son Hudon has gone missing", "on some hunt for treasure in", "the waterfall." }, IS_NPC, npc.getId(), WORRIED);
			stage = 6;
		} else if (stage == 6) {
			sendEntityDialogue(SEND_1_TEXT_CHAT, new String[] { npc.getDefinitions().getName(), "Could you please go make sure he is alright for me?" }, IS_NPC, npc.getId(), WORRIED);
			stage = 7;
		} else if (stage == 7) {
			sendOptionsDialogue(SEND_DEFAULT_OPTIONS_TITLE, "Of course.", "No thanks, I don't have time right now.");
			stage = 8;
		} else if (stage == 8) {
			if (componentId == OPTION_1) {
				sendPlayerDialogue(TOUGH, "Of course I will!");
				stage = 20;
			} else if (componentId == OPTION_2) {
				sendPlayerDialogue(SAD, "Sorry, but I don't have time right now. Bye.");
				stage = 9;
			}
		} else if (stage == 20) {
			player.getQuestManager().setStage(Quest.WATERFALL_QUEST, 1, true);
			end();
		} else {
			end();
		}
	}

	@Override
	public void finish() {

	}
}
