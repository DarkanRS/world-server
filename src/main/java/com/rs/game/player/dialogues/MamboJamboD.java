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

import com.rs.lib.game.Item;
import com.rs.utils.shop.ShopsHandler;

public class MamboJamboD extends Dialogue {

	@Override
	public void start() {
		sendNPCDialogue(3122, 9827, "Hello..");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			stage = 0;
			sendOptionsDialogue(SEND_DEFAULT_OPTIONS_TITLE, "Show me what you are selling.", "Tell me about the herblore habitat a little bit.", "Could you tell me how to get Witchdoctor equipment?");
			break;
		case 0:
			if (componentId == OPTION_1) {
				ShopsHandler.openShop(player, "papa_mambos_shop");
				end();
			} else if (componentId == OPTION_2) {
				stage = 1;
				sendNPCDialogue(3122, 9827, "You want it from the master eh? Well here goes.. You can", "obtain seeds hunting jadinkos around this area which", "you can then use farming to plant at");
			} else if (componentId == OPTION_3) {
				stage = 20;
				sendNPCDialogue(3122, 9827, "Bring me vines from each of the god Jadinkos", "and I will trade you some of my clothes", "for them.");
			}
			break;
		case 20:
			stage = 21;
			sendOptionsDialogue(SEND_DEFAULT_OPTIONS_TITLE, "Witchdoctor robe top - 60 Zamorak vines", "Witchdoctor robe legs - 60 Saradomin vines", "Witchdoctor mask - 60 Guthix vines");
			break;
		case 21:
			stage = -2;
			if (!player.getInventory().hasFreeSlots()) {
				player.sendMessage("You don't have enough inventory space.");
				end();
				break;
			}
			if (componentId == OPTION_1) {
				if (player.getInventory().containsItem(19983, 60)) {
					player.getInventory().deleteItem(19983, 60);
					player.getInventory().addItem(new Item(20044, 1));
				} else {
					player.sendMessage("You don't have enough Zamorak vines.");
				}
			} else if (componentId == OPTION_2) {
				if (player.getInventory().containsItem(19981, 60)) {
					player.getInventory().deleteItem(19981, 60);
					player.getInventory().addItem(new Item(20045, 1));
				} else {
					player.sendMessage("You don't have enough Saradomin vines.");
				}
			} else if (componentId == OPTION_3) {
				if (player.getInventory().containsItem(19982, 60)) {
					player.getInventory().deleteItem(19982, 60);
					player.getInventory().addItem(new Item(20046, 1));
				} else {
					player.sendMessage("You don't have enough Guthix vines.");
				}
			}
			end();
			break;
		case 1:
			stage = -2;
			sendNPCDialogue(3122, 9827, "one of the two vine herb patches in this area. Once", "you have farmed the herbs, you can hunt jadinkos with", "maramasaw plants and create juju potions out of the vines", "they give you.");
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