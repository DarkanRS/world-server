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

public class DungeonDifficulty extends Conversation {
	public DungeonDifficulty(Player player, int partySize) {
		super(player);
		addOptions("What difficulty of dungeon would you like?", new Options() {
			@Override
			public void create() {
				for(int i = 1; i <= partySize; i++) {
					final int size = i;
					option(i + (i == partySize ? " (recommended)" : ""), new Dialogue().addNext(() -> {
						player.getDungManager().setDificulty(size);
						player.getDungManager().enterDungeon(true);
					}));
				}
			}
		});
		create();
	}
}