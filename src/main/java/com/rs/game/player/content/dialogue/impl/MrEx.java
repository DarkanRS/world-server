package com.rs.game.player.content.dialogue.impl;

import com.rs.game.player.Player;
import com.rs.game.player.content.dialogue.Conversation;
import com.rs.game.player.content.dialogue.Dialogue;
import com.rs.game.player.content.dialogue.HeadE;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class MrEx extends Conversation {
	
	private static int MREX = 3709;

	public MrEx(Player player) {
		super(player);
		
		Dialogue options = player.isIronMan() ? addOption("Select an option", "Wilderness hats", "Ironman mode", "Skull me") : addOption("Select an option", "Wilderness hats", "Skull me");
		
		options.addPlayer(HeadE.CONFUSED, "I've seen wilderness hats around, what are those?")
		.addNPC(MREX, HeadE.NO_EXPRESSION, "Oh, I'm not giving those out quite yet. Sorry.")
		.addPlayer(HeadE.NO_EXPRESSION, "Unlucky..");
		
		if (player.isIronMan()) {
			options.addNPC(MREX, HeadE.CONFUSED, "Hey, I don't mean any offence by this, but would you like me to teach you to interact with other players?")
			.addNPC(MREX, HeadE.CHEERFUL, "Essentially, I am asking if you would be interested in de-activating your ironman mode by teaching you this.")
			.addOption("De-activate Ironman Mode?", "Yes, deactivate my ironman mode please.", "No, I'm quite happy being an Ironman.")
			.addNPC(MREX, HeadE.CHEERFUL, "Are you sure? You don't sound very sure.")
			.addOption("De-activate Ironman Mode?", "Yes, I am completely sure. De-activate my ironman status.", "No, I've changed my mind.")
			.addNPC(MREX, HeadE.CHEERFUL, "Alright, here's the secret to interacting with other players then")
			.addSimple("Mr. Ex whispers in your ears and teaches you how to trade.", () -> {
				player.setIronMan(false);
				player.clearTitle();
			})
			.addPlayer(HeadE.CHEERFUL, "Thank you!")
			.addNPC(MREX, HeadE.CHEERFUL, "No problem.");
		}
		
		options.addNPC(MREX, HeadE.CONFUSED, "Skull you?")
		.addOption("Skull?", "Yes, skull me.", "No, nevermind.")
		.addNPC(MREX, HeadE.CHEERFUL, "Are you sure?")
		.addOption("Skull?", "Yes, skull me.", "No, I've changed my mind.")
		.addSimple("You have been skulled.", () -> {
			player.setWildernessSkull();
		})
		.addPlayer(HeadE.CHEERFUL, "Thank you!")
		.addNPC(MREX, HeadE.CHEERFUL, "No problem.");
		
		create();
	}
	
	public static NPCClickHandler handleTalk = new NPCClickHandler(MREX) {
		@Override
		public void handle(NPCClickEvent e) {
			e.getPlayer().startConversation(new MrEx(e.getPlayer()));
		}
	};

}
