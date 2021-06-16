package com.rs.game.player.content.dialogue.impl.skillmasters;

import com.rs.game.player.Player;
import com.rs.game.player.content.Skillcapes;
import com.rs.game.player.content.dialogue.Conversation;

public class GenericSkillcapeOwnerD extends Conversation {

	public GenericSkillcapeOwnerD(Player player, int npcId, Skillcapes cape) {
		super(player);
	
		addOption("Choose an option", "What is that cape you're wearing?", "Bye.")
		.addNext(cape.getOffer99CapeDialogue(player, npcId));
		
		create();
	}
}
