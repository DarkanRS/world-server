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
import com.rs.engine.dialogue.HeadE;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Item;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class DungeoneeringTutor extends Conversation {
	
	private static final int DUNGEON_TUTOR = 9712;
	
	public static NPCClickHandler talk = new NPCClickHandler(new Object[] { DUNGEON_TUTOR }, e -> e.getPlayer().startConversation(new DungeoneeringTutor(e.getPlayer())));

	public DungeoneeringTutor(Player player) {
		super(player);
		
		addNPC(DUNGEON_TUTOR, HeadE.CHEERFUL, "Greetings, adventurer!");
		if (!player.containsItem(15707)) {
			addNPC(DUNGEON_TUTOR, HeadE.CHEERFUL, "Before we carry on, let me give you this.");
			if (player.getInventory().hasFreeSlots())
				addItem(15707, "He hands you a ring.", () -> player.getInventory().addItem(new Item(15707, 1)));
			else {
				addSimple("Your inventory is currently full.");
				create();
				return;
			}
		}
		addOptions((ops) -> {
			ops.add("What is this place?", new Dialogue()
					.addPlayer(HeadE.CONFUSED, "What is this place?")
					.addNPC(DUNGEON_TUTOR, HeadE.CHEERFUL_EXPOSITION, "This is a place of treasures, fierce battles and bitter defeats.")
					.addNPC(DUNGEON_TUTOR, HeadE.ANGRY, "We fought our way into the dungeons beneath this place.")
					.addNPC(DUNGEON_TUTOR, HeadE.ANGRY, "Those of us who made it out alive...")
					.addNPC(DUNGEON_TUTOR, HeadE.ANGRY, "...called this place Daemonhiem."));
			ops.add("What can I do here?", new Dialogue()
					.addPlayer(HeadE.CONFUSED, "What can I do here?")
					.addNPC(DUNGEON_TUTOR, HeadE.CHEERFUL_EXPOSITION, "Beneath these ruins you will find a multitude of dungeons, filled with strange creatures and resources.")
					.addNPC(DUNGEON_TUTOR, HeadE.CHEERFUL_EXPOSITION, "Unfortunately, due to the taint that permiates this place, we cannot risk you taking items in or out of Daemonhiem."));
			ops.add("What does this ring do?", new Dialogue()
					.addPlayer(HeadE.CONFUSED, "What does this ring do?")
					.addNPC(DUNGEON_TUTOR, HeadE.CHEERFUL_EXPOSITION, "Raiding these foresaken dungeons can be alot more rewarding if you're fighting alongside friends and allies. It should be more fun and you gain experience faster.")
					.addNPC(DUNGEON_TUTOR, HeadE.CHEERFUL_EXPOSITION, "The ring shows others you are interested in raiding a dungeon. It allowes you to form, join, and manage a raiding party.")
					.addNPC(DUNGEON_TUTOR, HeadE.CHEERFUL_EXPOSITION, "We've also setup rooms with the specific purpose of finding a party for you."));
			ops.add("Nevermind.");
		});
	}
}
