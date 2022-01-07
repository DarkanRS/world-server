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

import com.rs.game.player.content.skills.dungeoneering.DungeonConstants;
import com.rs.game.player.content.skills.dungeoneering.DungeonResourceShop;

public class SmugglerD extends Dialogue {

	@Override
	public void start() {
		sendNPCDialogue(DungeonConstants.SMUGGLER, NORMAL, "Hail, " + player.getDisplayName() + ". Need something?");
		stage = -1;
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (stage == -1) {
			sendOptionsDialogue("Select an Option", "What can you tell me about this place?", "Who are you?", "Do I have any rewards to claim?", "I'm here to trade.");
			stage = 0;
		} else if (stage == 0) {
			if (componentId == OPTION_1) {
				sendPlayerDialogue(NORMAL, "What can you tell me about this place?");
				stage = 1;
			} else if (componentId == OPTION_2) {
				sendPlayerDialogue(NORMAL, "Who are you?");
				stage = 2;
			} else if (componentId == OPTION_3) {
				sendNPCDialogue(DungeonConstants.SMUGGLER, NORMAL, "I have no rewards for you at the moment.");
				stage = 100;
			} else if (componentId == OPTION_4) {
				sendPlayerDialogue(NORMAL, "I'm here to trade.");
				stage = 23;
			}
		} else if (stage == 1) {
			sendNPCDialogue(DungeonConstants.SMUGGLER, NORMAL, "You know all that I can teach you already, friend, having conquered many floors yourself.");
			stage = 100;
		} else if (stage == 2) {
			sendNPCDialogue(DungeonConstants.SMUGGLER, NORMAL, "A friend.");
			stage = 3;
		} else if (stage == 3) {
			sendPlayerDialogue(NORMAL, "Okay, what are you doing here, friend?");
			stage = 4;
		} else if (stage == 4) {
			sendNPCDialogue(DungeonConstants.SMUGGLER, NORMAL, "I'm here to help out.");
			stage = 5;
		} else if (stage == 5) {
			sendPlayerDialogue(NORMAL, "With what?");
			stage = 6;
		} else if (stage == 6) {
			sendNPCDialogue(DungeonConstants.SMUGGLER, NORMAL, "Well, let's say you find yourself in need of an adventuring kit, and you've a heavy pile of rusty coins weighing you down. I can help you with both those problems. Savvy?");
			stage = 7;
		} else if (stage == 7) {
			sendPlayerDialogue(NORMAL, "Ah, so your a trader?");
			stage = 8;
		} else if (stage == 8) {
			sendNPCDialogue(DungeonConstants.SMUGGLER, NORMAL, "Keep it down, you fool!");
			stage = 9;
		} else if (stage == 9) {
			sendNPCDialogue(DungeonConstants.SMUGGLER, NORMAL, "Yes, I'm a trader. But I'm not supposed to be trading here.");
			stage = 10;
		} else if (stage == 10) {
			sendNPCDialogue(DungeonConstants.SMUGGLER, NORMAL, "If you want my goods, you'll learn not to talk about me.");
			stage = 11;
		} else if (stage == 11) {
			sendPlayerDialogue(NORMAL, "Right, got you.");
			stage = 12;
		} else if (stage == 12) {
			sendPlayerDialogue(NORMAL, "Is there anything else you can do for me?");
			stage = 13;
		} else if (stage == 13) {
			sendNPCDialogue(DungeonConstants.SMUGGLER, NORMAL, "Well, there's the job I'm supposed to be doing down here.");
			stage = 14;
		} else if (stage == 14) {
			sendPlayerDialogue(NORMAL, "Which is?");
			stage = 15;
		} else if (stage == 15) {
			sendNPCDialogue(DungeonConstants.SMUGGLER, NORMAL, "Say you chance upon an object that you know little about. Show it to me, and I'll tell you what it's used for.");
			stage = 16;
		} else if (stage == 16) {
			sendPlayerDialogue(NORMAL, "That's good to know.");
			stage = 17;
		} else if (stage == 17) {
			sendNPCDialogue(DungeonConstants.SMUGGLER, NORMAL, "I can also offer you knowledge about the behaviour of powerful opponents you might meet in the area. I've spent a long time down here, observing them.");
			stage = 18;
		} else if (stage == 18) {
			sendPlayerDialogue(NORMAL, "I'll be sure to come back if I find a particularly strong opponent, then.");
			stage = 19;
		} else if (stage == 19) {
			sendNPCDialogue(DungeonConstants.SMUGGLER, NORMAL, "You'd be wise to " + player.getDisplayName() + ".");
			stage = 20;
		} else if (stage == 20) {
			sendPlayerDialogue(NORMAL, "How do you know my name?");
			stage = 21;
		} else if (stage == 21) {
			sendNPCDialogue(DungeonConstants.SMUGGLER, NORMAL, "Nothing gets in or out of Daemonhiem wihout me knowing about it.");
			stage = 22;
		} else if (stage == 22) {
			sendPlayerDialogue(NORMAL, "Fair enough.");
			stage = 100;
		} else if (stage == 23) {
			DungeonResourceShop.openResourceShop(player, (int) parameters[0]);
			end();
		} else if (stage == 100)
			end();
	}

	@Override
	public void finish() {

	}
}
