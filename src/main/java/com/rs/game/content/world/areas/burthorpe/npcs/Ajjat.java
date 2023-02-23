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
package com.rs.game.content.world.areas.burthorpe.npcs;

import com.rs.game.content.Skillcapes;
import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class Ajjat extends Conversation {

	private static final int npcId = 4288;

	public static NPCClickHandler Ajjat = new NPCClickHandler(new Object[]{npcId}, e -> {
		switch (e.getOption()) {
		//Start Conversation
		case "Talk-to" -> e.getPlayer().startConversation(new Ajjat(e.getPlayer()));
		}
	});

	public Ajjat(Player player) {
		super(player);

		addNPC(npcId, HeadE.NO_EXPRESSION, "Greetings, fellow warrior. I am Ajjat, former Black Knight and now training officer here in the Warriors' Guild.");
		Dialogue options = addOption("Select an option", "What is that cape you are wearing?", "Black Knight? Why are you here?", "What's the dummy room all about?");

		options.addNext(Skillcapes.Attack.getOffer99CapeDialogue(player, npcId));

		options.addPlayer(HeadE.CONFUSED, "Black Knight? Why are you here?")
		.addNPC(npcId, HeadE.NO_EXPRESSION, "Indeed I was, however, their...methods did not match with my ideals, so I left. Harrallak, recognizing my talent as a warrior, took me in and offered me a job here.")
		.addPlayer(HeadE.NO_EXPRESSION, "Hmm...well, if Harrallak trusts you, I guess I can.");

		options.addPlayer(HeadE.CONFUSED, "What's the Dummy Room all about?")
		.addNPC(npcId, HeadE.CHEERFUL, "Ahh yes, the dummies. Another ingenious invention of the noble dwarf, Gamfred. They're mechanical, you see, and pop up out of the floor. You have to hit them with the correct attack mode before they disappear again.")
		.addPlayer(HeadE.CONFUSED, "So, how do I tell which one is which?")
		.addNPC(npcId, HeadE.CHEERFUL, "There are two different ways. One indication is their colour, the other is the pose and weapons they are holding, for instance, the one holding daggers you will need to hit with a piercing attack.")
		.addNPC(npcId, HeadE.CHEERFUL, "In the room, you will find a poster on the wall to help you recognize each different dummy.")
		.addPlayer(HeadE.CHEERFUL, "That sounds ingenious!")
		.addNPC(npcId, HeadE.CHEERFUL, "Indeed, you may find that you need several weapons to be successful all of the time, but keep trying. The weapons shop upstairs may help you there.");

		create();
	}

}
