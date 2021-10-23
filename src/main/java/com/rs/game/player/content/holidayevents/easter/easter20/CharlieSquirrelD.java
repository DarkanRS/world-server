package com.rs.game.player.content.holidayevents.easter.easter20;

import com.rs.game.player.Player;
import com.rs.game.player.content.dialogue.Conversation;
import com.rs.game.player.content.dialogue.HeadE;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class CharlieSquirrelD extends Conversation {
	
	private static final int CHARLIE = 9686;
	
	public static NPCClickHandler handleCharlieTalk = new NPCClickHandler(CHARLIE) {
		@Override
		public void handle(NPCClickEvent e) {
			e.getPlayer().startConversation(new CharlieSquirrelD(e.getPlayer()));
		}
	};

	public CharlieSquirrelD(Player player) {
		super(player);

		switch(player.getI(Easter2021.STAGE_KEY)) {
		case 7:
			addPlayer(HeadE.CONFUSED, "Hey, are you Charlie?");
			addNPC(CHARLIE, HeadE.CAT_CHEERFUL, "Yeah, that's me! Can I help you?");
			addPlayer(HeadE.HAPPY_TALKING, "Yes, the Easter Bunny sent me to ask you if you had some workers that can help operate his chocolate egg factory.");
			addNPC(CHARLIE, HeadE.CAT_CHEERFUL, "Oh, definitely! We'd love to help out. Is this because of his son being useless again?");
			addPlayer(HeadE.UPSET, "Yeah, it is. It's an unfortunate situation.");
			addNPC(CHARLIE, HeadE.CAT_CHEERFUL, "It's fine, we've been picking up his slack for years. We'll head over as quick as we can.");
			addPlayer(HeadE.CHEERFUL, "Thank you for your help!");
			addNext(() -> {
				player.save(Easter2021.STAGE_KEY, 8);
				player.getVars().setVarBit(6014, 85);
			});
			break;
		case 8:
			addNPC(CHARLIE, HeadE.CAT_CHEERFUL, "You head on back and let the Easter Bunny know we're coming!");
			break;
		default:
			addPlayer(HeadE.HAPPY_TALKING, "Hello!");
			addNPC(CHARLIE, HeadE.CAT_CHEERFUL, "Oh, hello! How's it going?");
			addPlayer(HeadE.HAPPY_TALKING, "It's going great, thanks!");
			break;
		}
		
		create();
	}

}
