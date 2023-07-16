package com.rs.game.content.quests.fightarena.dialogue;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.HeadE;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class JustinServilFightArenaD extends Conversation {
	private static final int NPC = 7541;
	public JustinServilFightArenaD(Player player) {
		super(player);
		addNPC(NPC, HeadE.HAPPY_TALKING, "You are one tough fighter, thank you " + this.player.getDisplayName() + "!");
	}


    public static NPCClickHandler handleDialogue = new NPCClickHandler(new Object[]{NPC}, e -> e.getPlayer().startConversation(new JustinServilFightArenaD(e.getPlayer()).getStart()));
}
