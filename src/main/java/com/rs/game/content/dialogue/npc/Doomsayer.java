package com.rs.game.content.dialogue.npc;

// Basic dialogue handler for linear text conversations with no choices.

import com.rs.game.content.dialogue.Conversation;
import com.rs.game.content.dialogue.HeadE;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class Doomsayer extends Conversation {

	//Identify NPC by ID Object[]{XXXX})
	public static NPCClickHandler Doomsayer = new NPCClickHandler(new Object[] { 3777 }) {
		@Override
		//Handle Right-Click
		public void handle(NPCClickEvent e) {
			switch(e.getOption()) {
				//Start Conversation
				case "Talk-to" -> e.getPlayer().startConversation(new Doomsayer(e.getPlayer()));
			}
		}
	};

	public Doomsayer(Player player) {
		super(player);
		//Identify NPC by ID (3777 - Doomsayer)
		int npc = 3777;
		//Add NPC conversation line
		addNPC(npc, HeadE.SCARED, "Dooooom!")
		//Add PLAYER conversation line
		.addPlayer( HeadE.WORRIED, "Do you mean the Battle of Lumbridge? Are you telling me I should go and help out by going to join in?")
		.addNPC(npc, HeadE.CONFUSED,"No, why should I be doing that? I'm talking about doooooom here, not some battlefield.")
		.addPlayer(HeadE.CONFUSED, "Well, everyone else seems to be... um... anyway, you mentioned doom. Where is this doom?")
		.addNPC(npc, HeadE.SCARED, "All around us! I can feel it in the air, hear it on the wind, smell it...also in the air!")
		.addPlayer(HeadE.CONFUSED, "Is there anything we can do about this doom?")
		.addNPC(npc, HeadE.HAPPY_TALKING, "There is nothing you need to do my friend! I am the Doomsayer, although my real title could be something like the Danger Tutor.")
		.addPlayer(HeadE.CONFUSED, "Danger Tutor?")
		.addNPC(npc, HeadE.CHEERFUL, "Yes! I roam the world sensing danger.")
		.addNPC(npc, HeadE.CHEERFUL, " If I find a dangerous area, then I put up warning signs that will tell you what is so dangerous about that area.");
		//Finish Script
		create();
	}
}

//TODO add toggle warnings handler

//Correct Behaviour
//Doomsayer: If you see the signs often enough, then you can turn them of; by that time you likely known what the area has in store for you.
//Player: But what If I want to see the warnings again?
//Doomsayer: That's why I'm waiting here!
//Doomsayer: If you want to see the warning messages again, I can turn them back on for you.
//Doomsayer: Do you need to turn on any warnings right now?
//Player: Yes, I do.
//Screen opens
//Player: Not right now.
//Doomsayer: Ok, keep an eye out for the messages though!
//Player: I will.