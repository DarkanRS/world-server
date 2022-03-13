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

import com.rs.game.World;

public class Max extends MatrixDialogue {

	int npcId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		if (!player.getSkills().isMaxed(false)) {
			sendNPCDialogue(npcId, 9827, "You are not yet eligible for this cape, " + player.getDisplayName() + ".");
			stage = -2;
		} else
			sendNPCDialogue(npcId, 9827, "How can I help you?");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			sendPlayerDialogue(9827, "Who are you?");
			stage = 1;
			break;
		case 1:
			sendNPCDialogue(npcId, 9827, "A good question. My name is Max.");
			stage = 2;
			break;
		case 2:
			sendNPCDialogue(npcId, 9827, "I reward those who are maxed with this cape I am wearing.");
			stage = 3;
			break;
		case 3:
			sendPlayerDialogue(9827, "Nice to meet you, Max. I was actually wondering what that cape is for?");
			stage = 4;
			break;
		case 4:
			sendNPCDialogue(npcId, 9827, "This cape is a symbol that I have trained all of my skills to level 99.");
			stage = 5;
			break;
		case 5:
			sendPlayerDialogue(9827, "So have I!");
			stage = 6;
			break;
		case 6:
			sendNPCDialogue(npcId, 9827, "Indeed you have, " + player.getDisplayName() + ". Would you like to buy this cape? 2,475,000 coins - 99,000 coins for each skill!");
			stage = 7;
			break;
		case 7:
			sendOptionsDialogue(player, "Choose an option.", "Yes", "No");
			stage = 8;
			break;
		case 8:
			if (componentId == OPTION_1) {
				sendPlayerDialogue(9827, "I'll take one!");
				stage = 9;
			} else {
				sendPlayerDialogue(9827, "No thanks, that is a bit too much for me right now.");
				stage = -2;
			}
			break;
		case 9:
			int value = 2475000;
			if (player.getInventory().getFreeSlots() < 2) {
				sendNPCDialogue(npcId, 9827, "Sorry, but your inventory seems to be full. Please come back with more space.");
				stage = -2;
			} else {
				if (player.getInventory().containsItem(995, value)) {
					player.getInventory().deleteItem(995, value);
					player.getInventory().addItem(20768, 1);
					player.getInventory().addItem(20767, 1);
					sendNPCDialogue(npcId, 9827, "Enjoy your new cape!");
					if (player.getSkills().isMaxed(false) && !player.isMaxed) {
						player.isMaxed = true;
						World.sendWorldMessage("<col=ff8c38><img=7>News: " + player.getDisplayName() + " has just been awarded the Max cape!" + "</col> ", false);
					}
				} else
					sendNPCDialogue(npcId, 9827, "You don't have enough coins on you.");
				stage = -2;
			}
			break;
		case -2:
			end();
			break;
		}
	}

	@Override
	public void finish() {

	}

}