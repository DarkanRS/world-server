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

public class TzHaarMejJal extends MatrixDialogue {

	private int npcId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendNPCDialogue(npcId, 9827, "You want help JalYt-Ket-" + player.getDisplayName() + "?");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			stage = 0;
			sendOptionsDialogue(SEND_DEFAULT_OPTIONS_TITLE, "What is this place?", "What did you call me?", "No I'm fine thanks.");
			break;
		case 0:
			switch (componentId) {
			case OPTION_1:
				stage = 1;
				sendPlayerDialogue(9827, "What is this place?");
				break;
			case OPTION_2:
				stage = 11;
				sendPlayerDialogue(9827, "What did you call me?");
				break;
			case OPTION_3:
			default:
				stage = -2;
				sendPlayerDialogue(9827, "No I'm fine thanks.");
				break;
			}
			break;
		case 1:
			stage = 2;
			sendNPCDialogue(npcId, 9827, "This is the Fight Cave, ThzHaar-Xil made it for practice but many JalYt come here to fight, too. Just enter the cave and make sure you're prepared.");
			break;
		case 2:
			stage = 3;
			sendOptionsDialogue(SEND_DEFAULT_OPTIONS_TITLE, "Are there any rules?", "Ok thanks.");
			break;
		case 3:
			switch (componentId) {
			case OPTION_1:
				stage = 4;
				sendPlayerDialogue(9827, "Are there any rules?");
				break;
			case OPTION_2:
			default:
				stage = -2;
				sendPlayerDialogue(9827, "Ok thanks.");
				break;
			}
			break;
		case 4:
			stage = 5;
			sendNPCDialogue(npcId, 9827, "Rules? Survival is the only rule in there.");
			break;
		case 5:
			stage = 6;
			sendOptionsDialogue(SEND_DEFAULT_OPTIONS_TITLE, "Do I win anything?", "Sounds good.");
			break;
		case 6:
			switch (componentId) {
			case OPTION_1:
				stage = 7;
				sendPlayerDialogue(9827, "Do I win anything?");
				break;
			case OPTION_2:
			default:
				stage = -2;
				sendPlayerDialogue(9827, "Sounds good.");
				break;
			}
			break;
		case 7:
			stage = 8;
			sendNPCDialogue(npcId, 9827, "You ask a lot questions.<br>Might give you TokKul if you last long enough.");
			break;
		case 8:
			stage = 9;
			sendPlayerDialogue(9827, "...");
			break;
		case 9:
			stage = 10;
			sendNPCDialogue(npcId, 9827, "Before you ask, TokKul is like your coins.");
			break;
		case 10:
			stage = -2;
			sendNPCDialogue(npcId, 9827, "Gold is like you JalYt, soft and easily broken, we use hard rock forged in fire like TzHaar!");
			break;
		case 11:
			stage = 12;
			sendNPCDialogue(npcId, 9827, "Are you not a JalYt-Ket?");
			break;
		case 12:
			stage = 13;
			sendOptionsDialogue(SEND_DEFAULT_OPTIONS_TITLE, "What's a 'JalYt-Ket'?", "I guess so...?", "No I'm not!");
			break;
		case 13:
			switch (componentId) {
			case OPTION_1:
				stage = 14;
				sendPlayerDialogue(9827, "What's a 'JalYt-Ket'?");
				break;
			case OPTION_2:
				stage = -2;
				sendPlayerDialogue(9827, "I guess so...");
				break;
			case OPTION_3:
			default:
				stage = -2;
				sendPlayerDialogue(9827, "No I'm not!");
				break;
			}
			break;
		case 14:
			stage = 15;
			sendNPCDialogue(npcId, 9827, "That what you are...you tough and strong, no?");
			break;
		case 15:
			stage = 16;
			sendPlayerDialogue(9827, "Well, yes I suppose I am...");
			break;
		case 16:
			stage = 17;
			sendNPCDialogue(npcId, 9827, "Then you JalYt-Ket!");
			break;
		case 17:
			stage = 18;
			sendOptionsDialogue(SEND_DEFAULT_OPTIONS_TITLE, "What are you then?", "Thanks for explaining it.");
			break;
		case 18:
			switch (componentId) {
			case OPTION_1:
				stage = 19;
				sendPlayerDialogue(9827, "What are you then?");
				break;
			case OPTION_2:
			default:
				stage = -2;
				sendPlayerDialogue(9827, "Thanks for explaining it.");
				break;
			}
			break;
		case 19:
			stage = 20;
			sendNPCDialogue(npcId, 9827, "Foolish JalYt, I am TzHaar-Mej one of the mystics of this city.");
			break;
		case 20:
			stage = 21;
			sendOptionsDialogue(SEND_DEFAULT_OPTIONS_TITLE, "What other types are there?", "Ah ok then.");
			break;
		case 21:
			stage = 22;
			switch (componentId) {
			case OPTION_1:
				stage = 22;
				sendPlayerDialogue(9827, "What other types are there?");
				break;
			case OPTION_2:
			default:
				stage = -2;
				sendPlayerDialogue(9827, "Ah ok then.");
				break;
			}
			break;
		case 22:
			stage = -2;
			sendNPCDialogue(npcId, 9827, "There are the mighty TzHaar-Key who guard us, the swift TzHaar-Xil who hunt for our food, and the skilled TzHaar-Hur who creft our homes and tools.");
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
