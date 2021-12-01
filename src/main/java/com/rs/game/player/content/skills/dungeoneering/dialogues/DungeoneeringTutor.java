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
package com.rs.game.player.content.skills.dungeoneering.dialogues;

import com.rs.game.player.dialogues.Dialogue;
import com.rs.lib.game.Item;

public class DungeoneeringTutor extends Dialogue {

	private static final int DUNGEON_TUTOR = 9712;

	@Override
	public void start() {
		sendNPCDialogue(DUNGEON_TUTOR, NORMAL, "Greetings, adventurer!");
		if (!player.containsItem(15707))
			stage = -1;
		else
			stage = 2;
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (stage == -1) {
			sendNPCDialogue(DUNGEON_TUTOR, NORMAL, "Before we carry on, let me give you this.");
			if (player.getInventory().hasFreeSlots())
				stage = 0;
			else
				stage = 1;
		} else if (stage == 0) {
			sendDialogue("He hands you a ring.");
			player.getInventory().addItem(new Item(15707, 1));
			stage = 2;
		} else if (stage == 1) {
			sendDialogue("Your inventory is currently full!");
			stage = 2;
		} else if (stage == 2) {
			sendOptionsDialogue("Select an Option.", "What is this place?", "What can I do here?", "What does this ring do?");
			stage = 3;
		} else if (stage == 3) {
			if (componentId == OPTION_1) {
				sendPlayerDialogue(NORMAL, "What is this place?");
				stage = 4;
			} else if (componentId == OPTION_2) {
				sendPlayerDialogue(NORMAL, "What can I do here?");
				stage = 8;
			} else if (componentId == OPTION_3) {
				sendPlayerDialogue(NORMAL, "What does this ring do?");
				stage = 10;
			}
		} else if (stage == 4) {
			sendNPCDialogue(DUNGEON_TUTOR, NORMAL, "This is a place of treasures, fierce battles and bitter defeats.");
			stage = 5;
		} else if (stage == 5) {
			sendNPCDialogue(DUNGEON_TUTOR, NORMAL, "We fought our way into the dungeons beneath this place.");
			stage = 6;
		} else if (stage == 6) {
			sendNPCDialogue(DUNGEON_TUTOR, NORMAL, "Those of us who made it out alive...");
			stage = 7;
		} else if (stage == 7) {
			sendNPCDialogue(DUNGEON_TUTOR, NORMAL, "...called this place Daemonhiem.");
			stage = 100;
		} else if (stage == 8) {
			sendNPCDialogue(DUNGEON_TUTOR, NORMAL, "Beneath these ruins you will find a multitude of dungeons, filled with strange creatures and resources.");
			stage = 9;
		} else if (stage == 9) {
			sendNPCDialogue(DUNGEON_TUTOR, NORMAL, "Unfortunately, due to the taint that permiates this place, we cannot risk you taking items in or out of Daemonhiem.");
			stage = 100;
		} else if (stage == 10) {
			sendNPCDialogue(DUNGEON_TUTOR, NORMAL, "Raiding these foresaken dungeons can be alot more rewarding if you're fighting alongside friends and allies. It should be more fun and you gain experience faster.");
			stage = 11;
		} else if (stage == 11) {
			sendNPCDialogue(DUNGEON_TUTOR, NORMAL, "The ring shows others you are interested in raiding a dungeon. It allowes you to form, join, and manage a raiding party.");
			stage = 12;
		} else if (stage == 12) {
			sendNPCDialogue(DUNGEON_TUTOR, NORMAL, "We've also setup rooms with the specific purpose of finding a party for you.");
			stage = 100;
		} else if (stage == 100) {
			end();
		}
	}

	@Override
	public void finish() {

	}

}
