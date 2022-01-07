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

public class SeerDialogue extends Dialogue {

	@Override
	public void start() {
		sendNPCDialogue(388, 9827, "Hello, welcome to Darkan! Could you use", "any help with anything?");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			stage = 0;
			sendOptionsDialogue(SEND_DEFAULT_OPTIONS_TITLE, "I'd like to know about commands.", "Tell me a bit about getting around.", "What are those statues next to you?", "No, I think I can manage myself.");
			break;
		case 0:
			if (componentId == OPTION_1) {
				stage = -2;
				sendNPCDialogue(388, 9827, "Well, all players have access to ::checkbank so you can<br>" + "see how your fellow players are doing. Also there is a way<br>" + "to check NPC drops with ::searchnpc (name) and ::drops.<br>"
						+ "There is also a loadout system but info about that<br>" + "is on the help horn you started with (::help).");
			} else if (componentId == OPTION_2) {
				stage = 1;
				sendNPCDialogue(388, 9827, "Teleporting around is fairly easy. 3 NPCs handle most of that.", "The tool leprechaun teleports you to farming patches around Darkan.", "The two standing next to the leprechaun handle the rest. Korasi");
			} else if (componentId == OPTION_3) {
				stage = -2;
				sendNPCDialogue(388, 9827, "Oh, you can get frozen key peices from godwars to get<br>" + "to Nex. Any extra peices of that frozen key you find,<br>" + "you can use to skip the killcount of the respective<br>"
						+ "godwars boss and go straight to the room and fight.<br>");
			} else
				end();
			break;
		case 1:
			stage = -2;
			sendNPCDialogue(388, 9827, "has more nooby teleports while the ethereal mimic has skilling & boss.", "Char can teleport you to her boss which is quite difficult.", "Doing ::(any skill name) such as ::farming or ::mining will teleport",
					"you to a place where you can start training that skill quickly.");
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
