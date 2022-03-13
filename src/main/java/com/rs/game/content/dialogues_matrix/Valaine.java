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

public class Valaine extends MatrixDialogue {

	private NPC npc;

	@Override
	public void start() {
		npc = (NPC) parameters[0];
		sendNPCDialogue(npc.getId(), 9827, "Hello there. Want to have a look at what we're selling", "today?");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			stage = 0;
			sendOptionsDialogue(SEND_DEFAULT_OPTIONS_TITLE, "Yes, please.", "How should I use your shop?", "No, thank you.");
			break;
		case 0:
			switch (componentId) {
			case OPTION_1:
				ShopsHandler.openShop(player, "valaines_shop_of_champions");
				end();
				break;
			case OPTION_2:
				stage = 1;
				sendNPCDialogue(npc.getId(), 9827, "I'm glad you ask! You can buy as many of the items", "stocked as you wish. The price of these items changes", "based on the amount in stock.");
				break;
			case OPTION_3:
			default:
				end();
				break;
			}
			break;
		case 1:
			stage = -2;
			sendNPCDialogue(npc.getId(), 9827, "You can also sell most items to the shop and the price given will be based on the amount in stock.");
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
