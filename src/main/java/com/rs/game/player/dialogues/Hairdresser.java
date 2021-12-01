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

import com.rs.game.npc.NPC;
import com.rs.game.player.content.PlayerLook;

public class Hairdresser extends Dialogue {

	NPC npc;

	@Override
	public void start() {
		npc = (NPC) parameters[0];
		sendNPCDialogue(npc.getId(), 9827, "Good afternoon, " + (player.getAppearance().isMale() ? "sir" : "lady") + ". In need of a haircut or shave, are we?");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			stage = 0;
			sendOptionsDialogue(SEND_DEFAULT_OPTIONS_TITLE, "Yes, please.", "No, thank you.");
			break;
		case 0:
			if (componentId == OPTION_2) {
				stage = 1;
				sendPlayerDialogue(9827, "No, thank you.");
			} else {
				stage = 2;
				sendPlayerDialogue(9827, "Yes, please.");
			}
			break;
		case 1:
			stage = -2;
			sendNPCDialogue(npc.getId(), 9827, "Very well. Come back if you change your mind.");
			break;
		case 2:
			if (player.getEquipment().getHatId() != -1) {
				stage = -2;
				sendNPCDialogue(npc.getId(), 9827, "Of course; but I can't see your head at the moment. Please remove your headgear first.");
			} else if (player.getEquipment().getWeaponId() != -1 || player.getEquipment().getShieldId() != -1) {
				stage = -2;
				sendNPCDialogue(npc.getId(), 9827, "I don't feel comfortable cutting hair when you are wielding something. Please remove what you are holding first.");
			} else {
				stage = 3;
				sendNPCDialogue(npc.getId(), 9827, "Certainly, sir. We have a special offer at the moment: all shaves and chaircuts are free!");
			}
			break;
		case 3:
			stage = 4;
			sendNPCDialogue(npc.getId(), 9827, "Please select the hairstyle, beard and colour you would like from this brochure.");
			break;
		case 4:
			PlayerLook.openHairdresserSalon(player);
			end();
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
