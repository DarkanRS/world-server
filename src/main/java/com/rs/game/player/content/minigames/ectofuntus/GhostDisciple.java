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
package com.rs.game.player.content.minigames.ectofuntus;

import com.rs.game.player.dialogues.Dialogue;

public class GhostDisciple extends Dialogue {

	private int npcId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendNPCDialogue(npcId, 9827, "Wooo wooo woooooo.");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			stage = 0;
			sendOptionsDialogue(SEND_DEFAULT_OPTIONS_TITLE, "Can I claim my ectotokens please?", "Ok. I better be going..");
			break;
		case 0:
			if (componentId == OPTION_1) {
				stage = -1;
				if (player.getInventory().hasFreeSlots() && player.unclaimedEctoTokens > 0) {
					player.getInventory().addItem(Ectofuntus.ECTO_TOKEN, player.unclaimedEctoTokens);
					player.unclaimedEctoTokens = 0;
				}
				sendNPCDialogue(npcId, 9827, "Woo wooooo woo woo woooo woo woo wooo!");
			} else if (componentId == OPTION_2)
				end();
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
