package com.rs.game.content.quests.tribaltotem;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

import static com.rs.game.content.quests.tribaltotem.TribalTotem.GET_TOTEM;
import static com.rs.game.content.quests.tribaltotem.TribalTotem.REDIRECT_TELE_STONE;

@PluginEventHandler
public class RPDTEmployeeTribalTotemD extends Conversation {
	private static final int NPC = 843;
	public RPDTEmployeeTribalTotemD(Player player) {
		super(player);
		switch(player.getQuestManager().getStage(Quest.TRIBAL_TOTEM)) {
		case REDIRECT_TELE_STONE -> {
			addNPC(NPC, HeadE.CALM_TALK, "Welcome to R.P.D.T.!");
			if(player.getQuestManager().getAttribs(Quest.TRIBAL_TOTEM).getB("CHANGED_CRATE")) {
				addPlayer(HeadE.HAPPY_TALKING, "So, when are you going to deliver this crate?");
				addNPC(NPC, HeadE.CALM_TALK, "Well...I guess we could do it now...", ()->{
					player.getQuestManager().setStage(Quest.TRIBAL_TOTEM, GET_TOTEM);
					player.sendMessage("The crate is delivered to the mansion.");
				});
			}
			else
				addPlayer(HeadE.HAPPY_TALKING, "Thank you.");
		}
		default -> {
			addNPC(NPC, HeadE.CALM_TALK, "Welcome to R.P.D.T.!");
			addPlayer(HeadE.HAPPY_TALKING, "Thank you.");
		}
		}
	}



	public static NPCClickHandler handleDialogue = new NPCClickHandler(new Object[] { NPC }, e -> e.getPlayer().startConversation(new RPDTEmployeeTribalTotemD(e.getPlayer()).getStart()));
}
