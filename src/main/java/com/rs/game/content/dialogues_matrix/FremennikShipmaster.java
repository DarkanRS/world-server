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

import com.rs.cache.loaders.NPCDefinitions;
import com.rs.game.content.controllers.DamonheimController;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.WorldTile;

public class FremennikShipmaster extends MatrixDialogue {

	int npcId;
	boolean backing;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		backing = (Boolean) parameters[1];
		if (backing)
			sendEntityDialogue(SEND_1_TEXT_CHAT, new String[] { NPCDefinitions.getDefs(npcId).getName(), "Do you want a lift back to the south?" }, IS_NPC, npcId, 9827);
		else
			sendEntityDialogue(SEND_1_TEXT_CHAT, new String[] { NPCDefinitions.getDefs(npcId).getName(), "You want passage to Daemonheim?" }, IS_NPC, npcId, 9827);

	}

	@Override
	public void run(int interfaceId, int componentId) {
		// TODO Auto-generated method stub
		if (backing) {
			if (stage == -1) {
				stage = 0;
				sendOptionsDialogue(SEND_DEFAULT_OPTIONS_TITLE, "Yes, please.", "Not right now, thanks.", "You look happy.");
			} else if (stage == 0) {
				if (componentId == OPTION_1) {
					stage = 1;
					sendPlayerDialogue(9827, "Yes, please.");
				} else
					// not coded options
					end();
			} else if (stage == 1) {
				stage = 2;
				sendEntityDialogue(SEND_1_TEXT_CHAT, new String[] { NPCDefinitions.getDefs(npcId).getName(), "All aboard, then." }, IS_NPC, npcId, 9827);
			} else if (stage == 2) {
				sail(player, backing);
				end();
			}
		} else if (stage == -1) {
			stage = 0;
			sendOptionsDialogue(SEND_DEFAULT_OPTIONS_TITLE, "Yes, please.", "Not right now, thanks.", "Daemonheim?", "Why are you so grumpy?");
		} else if (stage == 0) {
			if (componentId == OPTION_1) {
				stage = 1;
				sendPlayerDialogue(9827, "Yes, please.");
			} else
				// not coded options
				end();
		} else if (stage == 1) {
			stage = 2;
			sendEntityDialogue(SEND_1_TEXT_CHAT, new String[] { NPCDefinitions.getDefs(npcId).getName(), "Well, don't stand arround. Get on board." }, IS_NPC, npcId, 9827);
		} else if (stage == 2) {
			sail(player, backing);
			end();
		}

	}

	public static void sail(Player player, boolean backing) {
		player.useStairs(-1, backing ? new WorldTile(3254, 3171, 0) : new WorldTile(3511, 3692, 0), 2, 3);
		if (backing)
			player.getControllerManager().forceStop();
		else
			player.getControllerManager().startController(new DamonheimController());
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub

	}

}
