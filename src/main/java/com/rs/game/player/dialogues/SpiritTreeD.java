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

import com.rs.game.player.content.transportation.SpiritTree;

public class SpiritTreeD extends Dialogue {

	private int npcId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendNPCDialogue(npcId, 9827, "If you are a friend of the gnome people, you are a friend of mine.", " Do you wish to travel?");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (stage == -1) {
			stage = 0;
			sendOptionsDialogue(SEND_DEFAULT_OPTIONS_TITLE, "Yes please.", "No thanks.");
		} else if (stage == 0) {
			if (componentId == OPTION_1) {
				stage = 1;
				sendPlayerDialogue(9827, "Yes please.");
			} else {
				stage = 2;
				sendPlayerDialogue(9827, "No thanks.");
			}
		} else if (stage == 1) {
			SpiritTree.openInterface(player, npcId == 3636);
			end();
		} else if (stage == 2)
			end();
	}

	@Override
	public void finish() {

	}
}
