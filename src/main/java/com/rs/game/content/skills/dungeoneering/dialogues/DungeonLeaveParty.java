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
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.Options;
import com.rs.game.model.entity.player.Player;

public class DungeonLeaveParty extends Conversation {
	public DungeonLeaveParty(Player player) {
		super(player);
		addSimple("Warning: If you leave the dungeon, you will not be able to return to it!");
		addOptions("Leave the dungeon for good?", new Options() {
			@Override
			public void create() {
				option("Yes.", new Dialogue()
						.addNext(()->{player.getDungManager().leaveParty();})
				);
				option("No.", new Dialogue());
			}
		});
		create();
	}
}
