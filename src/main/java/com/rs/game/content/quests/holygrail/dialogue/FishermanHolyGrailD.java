package com.rs.game.content.quests.holygrail.dialogue;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.HeadE;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class FishermanHolyGrailD extends Conversation {
	private static final int NPC = 219;
	public FishermanHolyGrailD(Player player) {
		super(player);
		addPlayer(HeadE.HAPPY_TALKING, "Any idea how to get into the castle?");
		addNPC(NPC, HeadE.CALM_TALK, "Why, that's easy! Just ring one of the bells outside.");
		addPlayer(HeadE.HAPPY_TALKING, "...I didn't see any bells.");
		addNPC(NPC, HeadE.CALM_TALK, "You must be blind then. There's ALWAYS bells there when I go to the castle.");
	}


    public static NPCClickHandler handleDialogue = new NPCClickHandler(new Object[]{NPC}, e -> e.getPlayer().startConversation(new FishermanHolyGrailD(e.getPlayer()).getStart()));
}
