package com.rs.game.player.dialogues;

import com.rs.game.player.Player;
import com.rs.game.player.content.dialogue.Conversation;
import com.rs.game.player.content.dialogue.Dialogue;
import com.rs.game.player.content.dialogue.HeadE;

public class Shanomi extends Conversation {

	public Shanomi(Player player) {
		super(player);
		
		addNPC(4290, HeadE.HAPPY_TALKING, "Greetings " + player.getDisplayName() + " Welcome you are in the test of combat.");
		
		Dialogue option = addOption("What would you like to say?", "What do I do here?", "Where do the machines come from?", "Bye!");
		
		option.addPlayer(HeadE.CONFUSED, "What do I do here?")
		.addNPC(4290, HeadE.HAPPY_TALKING, "A spare suit of armor need you will. Full helm, plate leggings and platebody yes? Placing it in the centre of the magical machines you will be doing. KA-POOF! The armor, it attaks its most furiously as if alive! Kill it you must, yes.")
		.addPlayer(HeadE.CONFUSED, "So I use a full set of plate armor on the centre plate of the machines and it will animate it? Then I have to kill my own armor... how bizzare!")
		.addNPC(4290, HeadE.HAPPY_TALKING, "Yes. It is as you are saying. For this earn tokens you will. Also gain experience in combat you will. Trained long and hard here have I.")
		.addPlayer(HeadE.CHUCKLE, "Your not from around here are you?")
		.addNPC(4290, HeadE.HAPPY_TALKING, "Yes. It is as you say.")
		.addPlayer(HeadE.CONFUSED, "So can my armor get damaged?")
		.addNPC(4290, HeadE.HAPPY_TALKING, "Lose armor you will if damaged too much it becomes. Rare this is, but still possible. If kill you the armor does, also lose armor you will.")
		.addPlayer(HeadE.CONFUSED, "So, occasionally I might lose a bit because it's being bashed about and I'll obviously lose it if I die... that it?")
		.addNPC(4290, HeadE.HAPPY_TALKING, "It is as you say.")
		.addNext(option);
		
		option.addPlayer(HeadE.CONFUSED, "Where do the machines come from?")
		.addNPC(4290, HeadE.HAPPY_TALKING, "Make them I did, with magics.")
		.addPlayer(HeadE.CONFUSED, "Magic, in the Warrior's Guild?")
		.addNPC(4290, HeadE.HAPPY_TALKING, "A skilled warrior also am I. Harallak mistakes does not make. Potential in my invention he sees and opprotunity grasps.")
		.addPlayer(HeadE.CHEERFUL, "I see, so you made the magical machines and Harrallak saw how they could be used in the guild to train warrior's combat... interesting. Harrallak certainly is an intellegent guy.")
		.addNext(option);
		
		option.addNPC(4290, HeadE.HAPPY_TALKING, "Health be with you travelling.");
	}
}
