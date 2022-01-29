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
import com.rs.game.player.content.minigames.partyroom.PartyRoom;

public class PartyPete extends Dialogue {

	@Override
	public void start() {
		sendEntityDialogue(SEND_3_TEXT_CHAT, new String[] { NPCDefinitions.getDefs(659).getName(), "The items in the party chest are worth " + PartyRoom.getTotalCoins() + "", "coins! Hang around until they drop and you might get",
		"something valuable!" }, IS_NPC, 659, 9843);
	}

	@Override
	public void run(int interfaceId, int componentId) {
		end();
	}

	@Override
	public void finish() {

	}

}
