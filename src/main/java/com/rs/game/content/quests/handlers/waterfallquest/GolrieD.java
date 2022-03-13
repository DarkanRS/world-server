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
package com.rs.game.content.quests.handlers.waterfallquest;

import com.rs.game.content.dialogues_matrix.MatrixDialogue;
import com.rs.game.content.quests.Quest;
import com.rs.game.model.entity.npc.NPC;

public class GolrieD extends MatrixDialogue {
	// sendOptionsDialogue(SEND_DEFAULT_OPTIONS_TITLE,
	// "How is life on the waterfall?", "I am looking for a quest.");
	// sendPlayerDialogue(CALM_TALK, "");
	// sendEntityDialogue(SEND_1_TEXT_CHAT, new String[] {
	// npc.getDefinitions().name, "" }, IS_NPC, npc.getId(), CALM_TALK);

	private NPC npc;

	@Override
	public void start() {
		npc = (NPC) parameters[0];
		if (player.getQuestManager().getStage(Quest.WATERFALL_QUEST) == 3) {
			sendPlayerDialogue(TALKING_ALOT, "Hi, I heard you may be able to help me.");
			stage = 1;
		} else if (player.getQuestManager().getStage(Quest.WATERFALL_QUEST) > 3) {
			sendEntityDialogue(SEND_1_TEXT_CHAT, new String[] { npc.getDefinitions().getName(), "Have you lost Glarial's pebble?" }, IS_NPC, npc.getId(), MEAN_FACE);
			stage = -10;
		} else {
			sendEntityDialogue(SEND_1_TEXT_CHAT, new String[] { npc.getDefinitions().getName(), "What are you doing here?" }, IS_NPC, npc.getId(), CONFUSED);
			stage = -1;
		}
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (stage == 1) {
			sendEntityDialogue(SEND_1_TEXT_CHAT, new String[] { npc.getDefinitions().getName(), "I might have something of use laying", "around in here somewhere." }, IS_NPC, npc.getId(), CALM_TALK);
			stage++;
		} else if (stage == 2) {
			sendPlayerDialogue(CONFUSED, "Mind if I take a look?");
			stage++;
		} else if (stage == 3) {
			sendEntityDialogue(SEND_1_TEXT_CHAT, new String[] { npc.getDefinitions().getName(), "No, by all means go ahead." }, IS_NPC, npc.getId(), HAPPY_TALKING);
			stage++;
		} else if (stage == 4) {
			sendPlayerDialogue(CONFUSED, "Could I have this old pebble?");
			stage++;
		} else if (stage == 5) {
			sendEntityDialogue(SEND_1_TEXT_CHAT, new String[] { npc.getDefinitions().getName(), "Sure, it's of no use to me." }, IS_NPC, npc.getId(), CALM_TALK);
			stage++;
		} else if (stage == 6) {
			sendPlayerDialogue(TALKING_ALOT, "Thank you very much for your time.");
			player.getQuestManager().setStage(Quest.WATERFALL_QUEST, 4);
			player.getInventory().addItem(294, 1);
			stage++;
		} else if (stage == 7) {
			sendEntityDialogue(SEND_1_TEXT_CHAT, new String[] { npc.getDefinitions().getName(), "And thanks for saving me from", "this disgusting hole." }, IS_NPC, npc.getId(), HAPPY_TALKING);
			stage++;
		} else if (stage == 8) {
			sendPlayerDialogue(TALKING_ALOT, "You're welcome.");
			stage++;
		} else if (stage == -10) {
			if (!player.getInventory().containsItem(294, 1)) {
				sendPlayerDialogue(SAD, "Yes I have..");
				stage++;
			} else {
				sendPlayerDialogue(TALKING_ALOT, "Nope, just passing by.");
				stage--;
			}
		} else if (stage == -9) {
			sendEntityDialogue(SEND_1_TEXT_CHAT, new String[] { npc.getDefinitions().getName(), "Here. Take this one I just found." }, IS_NPC, npc.getId(), HAPPY_TALKING);
			player.getInventory().addItem(294, 1);
			stage++;
		} else
			end();
	}

	@Override
	public void finish() {

	}
}
