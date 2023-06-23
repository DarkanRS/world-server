package com.rs.game.content.world.areas.lumbridge.npcs;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.HeadE;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class CircusBarker extends Conversation {

	//Identify NPC by ID
	private static final int npcId = 8081;
	public static NPCClickHandler CircusBarker = new NPCClickHandler(new Object[]{8079, 8080, 8081}, e -> {
		switch (e.getOption()) {
		//			
		case "Talk-to" -> e.getPlayer().startConversation(new CircusBarker(e.getPlayer()));
		//		}
		//	}
		}
	});

	public CircusBarker(Player player) {
		super(player);
		//Identify NPC by ID
		//TODO add support for ID 8079 8080 too, might not be needed?
		//Add NPC conversation line
		addNPC(npcId, HeadE.LOSING_IT_LAUGHING, "Come to Balthzar Beauregard's Big Top Bonanza! It's fun for all; with rewards for all. Come and have a laugh.")
				//Add PLAYER conversation line
				.addPlayer(HeadE.HAPPY_TALKING, "Where is it? Can you take me there?")
				//TODO Ticket Vendor locate and teleport
				.addNPC(npcId, HeadE.ANGRY, "Do I look like a carrier pigeon!")
				.addPlayer(HeadE.CONFUSED, "Erm, sorry?")
				.addNPC(npcId, HeadE.ANGRY, "Have a nice walk!");
		create();
	}
}

//Correct Behaviour
//Circus Barker: The ticket vendor is currently located near the <Location of ticket vendor>.
//Circus Barker: The nearest lodestone is in <location of nearest lodestone>. Would you like me to send you there?
		//Yes, Please!
			//(Teleport to lodestone starts.)
		//No, thank you.

