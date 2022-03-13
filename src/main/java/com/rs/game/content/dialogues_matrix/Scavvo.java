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

import com.rs.game.model.entity.npc.NPC;
import com.rs.utils.shop.ShopsHandler;

public class Scavvo extends MatrixDialogue {

	NPC npc;

	@Override
	public void start() {
		npc = (NPC) parameters[0];
		sendNPCDialogue(npc.getId(), 9827, "'Ello, matey! D'ya wanna buy some exciting new toys?");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			stage = 0;
			sendOptionsDialogue(SEND_DEFAULT_OPTIONS_TITLE, "No - toys are for kids.", "Let's have a look, then.", "Ohh, goody-goody - toys!", "Why do you sell most rune armour but not platebodies?");
			break;
		case 0:
			switch (componentId) {
			case OPTION_1:
				stage = -2;
				sendPlayerDialogue(9827, "No - toys are for kids.");
				break;
			case OPTION_2:
				ShopsHandler.openShop(player, "scavvos_rune_shop");
				end();
				break;
			case OPTION_3:
				stage = 1;
				sendPlayerDialogue(9827, "Ohh, goody-goody - toys!");
				break;
			case OPTION_4:
				stage = 2;
				sendPlayerDialogue(9827, "Why do you sell most rune armour but not platebodies?");
				break;
			default:
				end();
				break;
			}
			break;
		case 1:
			ShopsHandler.openShop(player, "scavvos_rune_shop");
			end();
			break;
		case 2:
			stage = -2;
			sendNPCDialogue(npc.getId(), 9827, "Oh, you have to complete a special quest in order to wear", "rune platebodies. You should talk to the guild master", "downstairs about that.");
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
