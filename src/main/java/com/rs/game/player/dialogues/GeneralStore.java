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

import com.rs.utils.shop.ShopsHandler;

public class GeneralStore extends Dialogue {

	private int npcId;
	private String shopId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		shopId = (String) parameters[1];
		sendNPCDialogue(npcId, 9827, "Can I help you at all?");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			stage = 0;
			sendOptionsDialogue(SEND_DEFAULT_OPTIONS_TITLE, "Yes, please. What are you selling?", "How should I use your shop?", "No, thanks.");
			break;
		case 0:
			if (componentId == OPTION_1) {
				ShopsHandler.openShop(player, shopId);
				end();
			} else if (componentId == OPTION_2) {
				stage = 1;
				sendNPCDialogue(npcId, 9827, "I'm glad you ask! You can buy as many of the items", "stocked as you wish. The price of these items changes", "based on the amount in stock.");
			} else if (componentId == OPTION_3)
				end();
			break;
		case 1:
			stage = -2;
			sendNPCDialogue(npcId, 9827, "You can also sell most items to the shop and the price", "given will be based on the amount in stock.");
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
