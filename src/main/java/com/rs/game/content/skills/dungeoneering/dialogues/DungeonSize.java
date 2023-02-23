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

import com.rs.game.content.skills.dungeoneering.DungeonConstants;
import com.rs.game.content.skills.dungeoneering.DungeonPartyManager;
import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.Options;
import com.rs.game.model.entity.player.Player;

public class DungeonSize extends Conversation {
	public DungeonSize(Player player) {
		super(player);
		DungeonPartyManager party = player.getDungManager().getParty();
		addSimple("What size of dungeon would you like?");
		addOptions("Would you like to start a dungeon?", new Options() {
			@Override
			public void create() {
				option("Small.", new Dialogue()
						.addNext(()->{
							if(party != null) {
								player.getDungManager().setSize(DungeonConstants.SMALL_DUNGEON);
								player.getDungManager().enterDungeon(false);
							}
						})
				);
				option("Medium.", new Dialogue()
						.addNext(()->{
							if(party != null) {
								player.getDungManager().setSize(DungeonConstants.MEDIUM_DUNGEON);
								player.getDungManager().enterDungeon(false);
							}
						})
				);
				if(party != null && party.getTeam().size() >= 3)
					option("Large.", new Dialogue()
							.addNext(()->{
								player.getDungManager().setSize(DungeonConstants.LARGE_DUNGEON);
								player.getDungManager().enterDungeon(false);
							})
					);
			}
		});
		create();
	}
}
