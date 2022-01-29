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

import com.rs.game.player.controllers.WarriorsGuild;

public class KamfreendaDefender extends Dialogue {

	private int npcId = 4289;

	@Override
	public void start() {
		if (WarriorsGuild.getBestDefender(player) == 8844)
			sendNPCDialogue(npcId, 9827, "It seems that you do not have a defender.");
		else
			sendNPCDialogue(npcId, 9827, "Ah, I see that you have one of the defenders already! Well done.");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		stage++;
		if (stage == 0)
			sendNPCDialogue(npcId, 9827, "I'll release some cyclopses that might drop the next defender for you. Have fun in there.");
		else if (stage == 1)
			sendNPCDialogue(npcId, 9827, "Oh, and be careful; the cyclopses will occasionally summon a cyclossus. They are rather mean and can only be hurt with a rune or dragon defender.");
		else if (stage == 2) {
			end();
			player.getInterfaceManager().sendInterface(1058);

		}
	}

	@Override
	public void finish() {

	}
}
