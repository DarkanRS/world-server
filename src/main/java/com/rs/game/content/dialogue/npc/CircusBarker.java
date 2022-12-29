package com.rs.game.content.dialogue.npc;

// Basic dialogue handler for linear text conversations with no choices.

import com.rs.game.content.dialogue.Conversation;
import com.rs.game.content.dialogue.HeadE;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class CircusBarker extends Conversation {

	//Identify NPC by ID (8081 - CircusBarker)
	public static NPCClickHandler CircusBarker = new NPCClickHandler(new Object[] { 8081 }) {
		@Override
		//Handle Right-Click
		public void handle(NPCClickEvent e) {
			switch(e.getOption()) {
				//Start Conversation
				case "Talk-to" -> e.getPlayer().startConversation(new CircusBarker(e.getPlayer()));
			}
		}
	};

	public CircusBarker(Player player) {
		super(player);
		//Identify NPC by ID (3777 - Doomsayer)
		int npc = 3777;
		//Add NPC conversation line
		addNPC(npc, HeadE.LOSING_IT_LAUGHING, "Come to Balthzar Beauregard's Big Top Bonanza! It's fun for all; with rewards for all. Come and have a laugh.")
		//Add PLAYER conversation line
		.addPlayer( HeadE.WORRIED, "Where is it? Can you take me there?")
		.addNPC(npc, HeadE.ANGRY,"Do I look like a carrier pigeon!")
		.addPlayer(HeadE.CONFUSED, "Erm, sorry?")
		.addNPC(npc, HeadE.ANGRY, "Have a nice walk!");
		//Finish Script
		create();
	}
}

//Ticket Vendor locate and teleport