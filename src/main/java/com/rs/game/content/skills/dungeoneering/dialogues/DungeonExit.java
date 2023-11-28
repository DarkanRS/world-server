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
package com.rs.game.content.skills.dungeoneering.dialogues;

import com.rs.engine.dialogue.Conversation;
import com.rs.game.content.skills.dungeoneering.DungeonController;
import com.rs.game.model.entity.player.Player;

public class DungeonExit extends Conversation {

	public DungeonExit(Player player, DungeonController controller) {
		super(player);
		addSimple("This ladder leads back to the surface. You will not be able to come back to this dungeon if you leave.");
		addOptions("Leave the dungeon and return to the surface?", (ops) -> {
			ops.add("Yes.", controller::leaveDungeon);
			ops.add("No.");
		});
	}
}
