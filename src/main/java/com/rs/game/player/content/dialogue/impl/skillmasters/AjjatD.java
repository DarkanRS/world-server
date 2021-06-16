package com.rs.game.player.content.dialogue.impl.skillmasters;

import com.rs.game.player.Player;
import com.rs.game.player.content.Skillcapes;
import com.rs.game.player.content.dialogue.Conversation;
import com.rs.game.player.content.dialogue.Dialogue;
import com.rs.game.player.content.dialogue.HeadE;

public class AjjatD extends Conversation {
	
	private static int AJJAT = 4288;

	public AjjatD(Player player) {
		super(player);
		
		addNPC(AJJAT, HeadE.NO_EXPRESSION, "Greetings, fellow warrior. I am Ajjat, former Black Knight and now training officer here in the Warriors' Guild.");
		Dialogue options = addOption("Select an option", "What is that cape you are wearing?", "Black Knight? Why are you here?", "What's the dummy room all about?");
		
		options.addNext(Skillcapes.Attack.getOffer99CapeDialogue(player, AJJAT));
		
		options.addPlayer(HeadE.CONFUSED, "Black Knight? Why are you here?")
		.addNPC(AJJAT, HeadE.NO_EXPRESSION, "Indeed I was, however, their...methods did not match with my ideals, so I left. Harrallak, recognizing my talent as a warrior, took me in and offered me a job here.")
		.addPlayer(HeadE.NO_EXPRESSION, "Hmm...well, if Harrallak trusts you, I guess I can.");
		
		options.addPlayer(HeadE.CONFUSED, "What's the Dummy Room all about?")
		.addNPC(AJJAT, HeadE.CHEERFUL, "Ahh yes, the dummies. Another ingenious invention of the noble dwarf, Gamfred. They're mechanical, you see, and pop up out of the floor. You have to hit them with the correct attack mode before they disappear again.")
		.addPlayer(HeadE.CONFUSED, "So, how do I tell which one is which?")
		.addNPC(AJJAT, HeadE.CHEERFUL, "There are two different ways. One indication is their colour, the other is the pose and weapons they are holding, for instance, the one holding daggers you will need to hit with a piercing attack.")
		.addNPC(AJJAT, HeadE.CHEERFUL, "In the room, you will find a poster on the wall to help you recognize each different dummy.")
		.addPlayer(HeadE.CHEERFUL, "That sounds ingenious!")
		.addNPC(AJJAT, HeadE.CHEERFUL, "Indeed, you may find that you need several weapons to be successful all of the time, but keep trying. The weapons shop upstairs may help you there.");
		
		create();
	}

}
