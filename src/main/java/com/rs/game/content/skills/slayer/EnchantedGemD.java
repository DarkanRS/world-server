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
package com.rs.game.content.skills.slayer;

import com.rs.game.content.dialogues_matrix.MatrixDialogue;
import com.rs.lib.util.Utils;

public class EnchantedGemD extends MatrixDialogue {

	private Master npc;

	@Override
	public void start() {
		npc = (Master) parameters[0];
		sendNPCDialogue(npc.npcId, 9827, "Hello there, "+player.getDisplayName()+", what can I help you with?");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			stage = 0;
			sendOptionsDialogue(SEND_DEFAULT_OPTIONS_TITLE, "How am I doing so far?", "Who are you?", "Where are you?");
			break;
		case 0:
			switch (componentId) {
			case OPTION_1:
				stage = 3;
				sendPlayerDialogue(9827, "How am I doing so far?");
				break;
			case OPTION_2:
				stage = 1;
				sendPlayerDialogue(9827, "Who are you?");
				break;
			case OPTION_3:
				stage = 2;
				sendPlayerDialogue(9827, "Where are you?");
				break;
			default:
				end();
				break;
			}
			break;
		case 1:
			stage = -2;
			sendNPCDialogue(npc.npcId, 9827, "My name is "+Utils.formatPlayerNameForDisplay(Master.getMasterForId(npc.npcId).name().toLowerCase())+"; I'm a Slayer Master.");
			break;
		case 2:
			stage = -2;
			sendNPCDialogue(npc.npcId, 9827, "I'm in a meme.");
			break;
		case 3:
			stage = -2;
			sendNPCDialogue(npc.npcId, 9827, player.getSlayer().getTaskString());
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
