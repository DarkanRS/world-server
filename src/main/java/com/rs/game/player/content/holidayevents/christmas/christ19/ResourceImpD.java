package com.rs.game.player.content.holidayevents.christmas.christ19;

import com.rs.game.player.Player;
import com.rs.game.player.content.dialogue.Conversation;
import com.rs.game.player.content.dialogue.HeadE;
import com.rs.plugin.PluginManager;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.EnterChunkEvent;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class ResourceImpD extends Conversation {
	
	private static final int IMP_HEAD = 9364;
	
	public static NPCClickHandler handleSnowImpTalk = new NPCClickHandler(9372, 9373, 9374, 9375) {
		@Override
		public void handle(NPCClickEvent e) {
			String noun = "";
			int stage = 0;
			switch(e.getNPC().getId()) {
			case 9372:
				noun = "wine";
				stage = 5;
				break;
			case 9373:
				noun = "yule logs";
				stage = 9;
				break;
			case 9374:
				noun = "turkeys";
				stage = 7;
				break;
			case 9375:
				noun = "taters";
				stage = 3;
				break;
			}
			e.getPlayer().startConversation(new ResourceImpD(e.getPlayer(), noun, stage));
		}
	};

	public ResourceImpD(Player player, String noun, int stage) {
		super(player);

		switch(player.getChrist19Stage()) {
		case 2:
		case 4:
		case 6:
		case 8:
			addPlayer(HeadE.ANGRY, "Hey! Give those "+noun+" back!");
			addNPC(IMP_HEAD, HeadE.EVIL_LAUGH, "Tee hee hee! Why should I listen to you?");
			addNPC(IMP_HEAD, HeadE.ANGRY, "Because I'm wif him. Give him da "+noun+" back, Snowie's orders!");
			addNPC(IMP_HEAD, HeadE.SCARED, "Oh.. Rasmus.. Sorry, guv. I'll bring da "+noun+" back to da feast den.");
			addNPC(IMP_HEAD, HeadE.ANGRY, "You betta! Get moving dis instant!");
			addPlayer(HeadE.CHEERFUL, "Alright, we'll see you there.", () -> {
				player.setChrist19Stage(stage);
				player.setChrist19Loc(null);
				PluginManager.handle(new EnterChunkEvent(player, player.getChunkId()));
			});
			break;
		default:
			addSimple("You've harassed the imp enough. Talk to Rasmus for the next location.");
			break;
		}
		
		create();
	}

}
