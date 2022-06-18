package com.rs.game.content.quests.handlers.fightarena.dialogue;

import com.rs.game.content.dialogue.Conversation;
import com.rs.game.content.dialogue.Dialogue;
import com.rs.game.content.dialogue.HeadE;
import com.rs.game.content.dialogue.Options;
import com.rs.game.content.quests.Quest;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.handlers.NPCClickHandler;

import static com.rs.game.content.quests.handlers.fightarena.FightArena.*;

@PluginEventHandler
public class JustinServilFightArenaD extends Conversation {
	private static final int NPC = 7541;
	public JustinServilFightArenaD(Player p) {
		super(p);
		addNPC(NPC, HeadE.HAPPY_TALKING, "You are one tough fighter, thank you " + player.getDisplayName() + "!");
	}


    public static NPCClickHandler handleDialogue = new NPCClickHandler(new Object[]{NPC}) {
        @Override
        public void handle(NPCClickEvent e) {
            e.getPlayer().startConversation(new JustinServilFightArenaD(e.getPlayer()).getStart());
        }
    };
}
