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

import com.rs.cache.loaders.NPCDefinitions;
import com.rs.game.npc.NPC;
import com.rs.utils.shop.ShopsHandler;

public class Nurmof extends Dialogue {

	NPC npc;

	@Override
	public void start() {
		npc = (NPC) parameters[0];
		sendEntityDialogue(SEND_2_TEXT_CHAT, new String[] { NPCDefinitions.getDefs(npc.getId()).getName(), "Greetings and welcome to my pickaxe shop. Do you want", "to buy my premium quality pickaxes?" }, IS_NPC, npc.getId(), 9827);

	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (stage == -1) {
			stage = 0;
			sendOptionsDialogue(SEND_DEFAULT_OPTIONS_TITLE, "Yes, please.", "No, thank you.", "Are your pickaxes better than other pickaxes, then?");
		} else if (stage == 0) {
			if (componentId == OPTION_1) {
				ShopsHandler.openShop(player, "nurmofs_pickaxe_shop");
				end();
			} else if (componentId == OPTION_2) {
				stage = -2;
				sendEntityDialogue(SEND_1_TEXT_CHAT, new String[] { player.getDisplayName(), "No, thank you." }, IS_PLAYER, player.getIndex(), 9827);
			} else {
				stage = 1;
				sendEntityDialogue(SEND_1_TEXT_CHAT, new String[] { player.getDisplayName(), "Are your pickaxes better than other pickaxes, then?" }, IS_PLAYER, player.getIndex(), 9827);
			}
		} else if (stage == 1) {
			stage = -2;
			sendEntityDialogue(SEND_3_TEXT_CHAT, new String[] { NPCDefinitions.getDefs(npc.getId()).getName(), "Of course they are! My pickaxes are made of higher grade", "metal than your ordinary bronze pickaxes, allowing you to",
			"mine ore just that little bit faster." }, IS_NPC, npc.getId(), 9827);
		} else
			end();

	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub

	}

}
