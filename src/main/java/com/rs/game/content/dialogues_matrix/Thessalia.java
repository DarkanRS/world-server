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

import com.rs.game.content.PlayerLook;
import com.rs.game.model.entity.npc.NPC;
import com.rs.utils.shop.ShopsHandler;

public class Thessalia extends MatrixDialogue {

	NPC npc;

	@Override
	public void start() {
		npc = (NPC) parameters[0];
		sendNPCDialogue(npc.getId(), 9827, "Would you like to buy any fine clothes?");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			stage = 0;
			sendOptionsDialogue(SEND_DEFAULT_OPTIONS_TITLE, "What do you have?", "No, thank you.");
			break;
		case 0:
			if (componentId == OPTION_2) {
				stage = 1;
				sendPlayerDialogue(9827, "No, thank you.");
			} else {
				stage = 2;
				sendPlayerDialogue(9827, "What do you have?");
			}
			break;
		case 1:
			stage = -2;
			sendNPCDialogue(npc.getId(), 9827, "Well, please return if you change your mind.");
			break;
		case 2:
			stage = 3;
			sendNPCDialogue(npc.getId(), 9827, "Well, I have a number of fine pieces of clothing on sale or, if you prefer, I can offer you an exclusive, total clothing makeover?");
			break;
		case 3:
			stage = 4;
			sendOptionsDialogue(SEND_DEFAULT_OPTIONS_TITLE, "Tell me more about this makeover.", "I'd just like to buy some clothes.");
			break;
		case 4:
			if (componentId == OPTION_2) {
				ShopsHandler.openShop(player, "thessalias_clothing");
				end();
			} else {
				stage = 5;
				sendPlayerDialogue(9827, "Tell me more about this makeover.");
			}
			break;
		case 5:
			stage = 6;
			sendNPCDialogue(npc.getId(), 9827, "Certainly!");
			break;
		case 6:
			stage = 7;
			sendNPCDialogue(npc.getId(), 9827,
					"Here at Thessalia's Fine Clothing Boutique we offer a unique service, where we totally revamp your outfit to your choosing. Tired of always wearing the same outfit, day-in, day-out? Then this is the service for you!");
			break;
		case 7:
			stage = 8;
			sendNPCDialogue(npc.getId(), 9827, "So, what do you say? Interested?");
			break;
		case 8:
			stage = 9;
			sendOptionsDialogue(SEND_DEFAULT_OPTIONS_TITLE, "I'd like to change my outfit, please.", "I'd just like to buy some clothes.", "No, thank you.");
			break;
		case 9:
			if (componentId == OPTION_3) {
				stage = 1;
				sendPlayerDialogue(9827, "No, thank you.");
			} else if (componentId == OPTION_2) {
				ShopsHandler.openShop(player, "thessalias_clothing");
				end();
			} else {
				stage = 10;
				sendPlayerDialogue(9827, "I'd like to change my outfit, please");
			}
			break;
		case 10:
			if (player.getEquipment().wearingArmour()) {
				stage = -2;
				sendNPCDialogue(npc.getId(), 9827, "You can't try them on while wearing armour. Take it off and then speak to me again.");
			} else {
				stage = 11;
				sendNPCDialogue(npc.getId(), 9827, "Wonderful. Feel free to try on some items and see if there's anything you would like.");
			}
			break;
		case 11:
			stage = 12;
			sendPlayerDialogue(9827, "Okay, thanks.");
			break;
		case 12:
			PlayerLook.openThessaliasMakeOver(player);
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
