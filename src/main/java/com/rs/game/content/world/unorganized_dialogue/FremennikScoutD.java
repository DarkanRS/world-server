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
package com.rs.game.content.world.unorganized_dialogue;

import com.rs.game.content.skills.dungeoneering.rooms.PuzzleRoom;
import com.rs.game.content.skills.dungeoneering.rooms.puzzles.FremennikCampRoom;
import com.rs.game.content.skills.fletching.Fletching;
import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.HeadE;
import com.rs.game.model.entity.player.Player;

public class FremennikScoutD extends Conversation {

	public FremennikScoutD(Player player, PuzzleRoom room) {
		super(player);
		if (room.isComplete()) {
			addNPC(FremennikCampRoom.FREMENNIK_SCOUT, HeadE.CHEERFUL_EXPOSITION, "Wonderful! That was the last of them. As promised, I'll unlock the door for you.");
			create();
			return;
		}
		addNPC(FremennikCampRoom.FREMENNIK_SCOUT, HeadE.CONFUSED, "Need some tools?");
		addItem(Fletching.DUNGEONEERING_KNIFE, "The scout hands you a knife.", () -> player.getInventory().addItem(Fletching.DUNGEONEERING_KNIFE, 1));
		create();
	}
}
