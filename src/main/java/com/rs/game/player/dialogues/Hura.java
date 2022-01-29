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

import com.rs.game.npc.NPC;

public class Hura extends Dialogue {

	NPC npc;

	@Override
	public void start() {
		npc = (NPC) parameters[0];
		sendNPCDialogue(npc.getId(), 9827, "'Ello, " + player.getDisplayName() + ".");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			stage = 0;
			sendPlayerDialogue(9827, "Hello, what's that you've got there?");
			break;
		case 0:
			stage = 1;
			sendNPCDialogue(npc.getId(), 9827, "A crossbow, are you interested?");
			break;
		case 1:
			stage = 2;
			sendPlayerDialogue(9827, "Maybe, are they any good?");
			break;
		case 2:
			stage = 3;
			sendNPCDialogue(npc.getId(), 9827, "Are they any good?! They're dwarven engineering at its", "best!");
			break;
		case 3:
			stage = 4;
			sendOptionsDialogue(SEND_DEFAULT_OPTIONS_TITLE, "How do I make one for myself?", "What about ammo?", "Thanks for telling me. Bye!");
			break;
		case 4:
			switch (componentId) {
			case 2:
				stage = 5;
				sendPlayerDialogue(9827, "How do I make one for myself?");
				break;
			case 3:
				stage = 19;
				sendPlayerDialogue(9827, "What about ammo?");
				break;
			case 4:
			default:
				stage = 18;
				sendPlayerDialogue(9827, "Thanks for telling me. Bye!");
				break;
			}
			break;
		case 5:
			stage = 6;
			sendNPCDialogue(npc.getId(), 9827, "Well, firstly you'll need to chop yourself some wood, then", "use a knife on the wood to whittle out a nice crossbow", "stock like these here.");
			break;
		case 6:
			stage = 7;
			sendPlayerDialogue(9827, "Wood fletched into stock... check.");
			break;
		case 7:
			stage = 8;
			sendNPCDialogue(npc.getId(), 9827, "Then you get yourself some metal and a hammer and smith", "yourself some limbs for the bow, mind that you use the", "right metal and woods though as some wood is too light",
					"to use with some metal and vice versa.");
			break;
		case 8:
			stage = 9;
			sendPlayerDialogue(9827, "Which goes with which?");
			break;
		case 9:
			stage = 10;
			sendNPCDialogue(npc.getId(), 9827, "Wood and Bronze as they're basic materials, Oak and", "Blurite, Willow and Iron, Steel and Teak, Mithril and Maple,", "Adamantite and Mahogany and finlly Runite and Yew.");
			break;
		case 10:
			stage = 11;
			sendPlayerDialogue(9827, "Ok, so I have my stock and a pair of limbs... what now?");
			break;
		case 11:
			stage = 12;
			sendNPCDialogue(npc.getId(), 9827, "Simply take a hammer and smack the limbs firmly onto", "the stock. You'll then need a string, only they're not the", "same as normal bows. You'll need to dry some large",
					"animal's meat to get sinew, then spin that on a  spinning");
			break;
		case 12:
			stage = 13;
			sendNPCDialogue(npc.getId(), 9827, "wheel, it's the only thing we've found to be strong enough", "for a crossbow.");
			break;
		case 13:
			stage = 14;
			sendOptionsDialogue(SEND_DEFAULT_OPTIONS_TITLE, "What about magic logs?", "Thanks for telling me. Bye!");
			break;
		case 14:
			switch (componentId) {
			case 1:
				stage = 15;
				sendPlayerDialogue(9827, "What about magic logs?");
				break;
			case 2:
			default:
				stage = 18;
				sendPlayerDialogue(9827, "Thanks for telling me. Bye!");
				break;
			}
			break;
		case 15:
			stage = 16;
			sendNPCDialogue(npc.getId(), 9827, "Well.. I don't rightly know... us dwarves don't work with", "magic, we prefer gold and rock. Much more stable. I guess", "you could ask the humans at the rangers guild to see if",
					"they can do something but I don't want anything to do");
			break;
		case 16:
			stage = 17;
			sendNPCDialogue(npc.getId(), 9827, "with it!");
		case 17:
			stage = 18;
			sendPlayerDialogue(9827, "Thanks for telling me. Bye!");
			break;
		case 18:
			stage = -2;
			sendNPCDialogue(npc.getId(), 9827, "Take care, straight shooting.");
			break;
		case 19:
			stage = 20;
			sendNPCDialogue(npc.getId(), 9827, "You can smith yourself lots of different bolts, don't", "forget to flight them with feathers like you do arrows", "though. You can poison any untipped bolt but there's also",
					"the option of tipping them with gems then echanting");
			break;
		case 20:
			stage = 21;
			sendNPCDialogue(npc.getId(), 9827, "them with runes. This can have some pretty powerfull", "effects.");
			break;
		case 21:
			stage = 22;
			sendPlayerDialogue(9827, "Oh my poor bank, how will I store all those?!");
			break;
		case 22:
			stage = 23;
			sendNPCDialogue(npc.getId(), 9827, "Find Hirko in Keldagrim, he also sells crossbow parts and", " I'm sure he has something you can use to store bolts in.");
			break;
		case 23:
			stage = -2;
			sendPlayerDialogue(9827, "Thanks for the info.");
			break;
		default:
			end();
			break;
		}

	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub

	}

}
