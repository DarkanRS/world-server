package com.rs.game.content.quests.fightarena.dialogue;

import com.rs.game.engine.dialogue.Conversation;
import com.rs.game.engine.dialogue.HeadE;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class JustinServilFightArenaD extends Conversation {
	private static final int NPC = 7541;
	public JustinServilFightArenaD(Player p) {
		super(p);
		addNPC(NPC, HeadE.HAPPY_TALKING, "You are one tough fighter, thank you " + player.getDisplayName() + "!");
	}


    public static NPCClickHandler handleDialogue = new NPCClickHandler(new Object[]{NPC}, e -> e.getPlayer().startConversation(new JustinServilFightArenaD(e.getPlayer()).getStart()));
}
