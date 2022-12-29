package com.rs.game.content.dialogue.npc;

import com.rs.game.content.dialogue.Conversation;
import com.rs.game.content.dialogue.Dialogue;
import com.rs.game.content.dialogue.HeadE;
import com.rs.game.model.entity.player.Player;

public class Doomsayer extends Conversation {

	public Doomsayer(Player player) {
		super(player);
        //Using edited transcript from https://runescape.fandom.com/wiki/Transcript:Doomsayer
        addNPC(3777, HeadE.SCARED, "Dooooom!")
        .addPlayer( HeadE.WORRIED, "Do you mean the Battle of Lumbridge? Are you telling me I should go and help out by going to join in?")
        .addNPC(3777, HeadE.CONFUSED,"No, why should I be doing that? I'm talking about doooooom here, not some battlefield.")
        .addPlayer(HeadE.CONFUSED, "Well, everyone else seems to be... um... anyway, you mentioned doom. Where is this doom?")
        .addNPC(3777, HeadE.SCARED, "All around us! I can feel it in the air, hear it on the wind, smell it...also in the air!")
        .addPlayer(HeadE.CONFUSED, "Is there anything we can do about this doom?")
        .addNPC(3777, HeadE.HAPPY_TALKING, "There is nothing you need to do my friend! I am the Doomsayer, although my real title could be something like the Danger Tutor.")
        .addPlayer(HeadE.CONFUSED, "Danger Tutor?")
        .addNPC(3777, HeadE.CHEERFUL, "Yes! I roam the world sensing danger.")
        .addNPC(3777, HeadE.CHEERFUL, " If I find a dangerous area, then I put up warning signs that will tell you what is so dangerous about that area.");
	}
}
