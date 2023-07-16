package com.rs.game.content.world.areas.burthorpe.npcs;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.HeadE;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class Eadburg extends Conversation {
    private static final int npcId = 1072;

    public static NPCClickHandler Eadburg = new NPCClickHandler(new Object[] { npcId }, e -> {
        if (e.getOption().equalsIgnoreCase("talk-to")) {
            e.getPlayer().startConversation(new Eadburg(e.getPlayer()));
        }
    });


    public Eadburg(Player player) {
        super(player);
            addPlayer(HeadE.HAPPY_TALKING, "What's cooking?");
            addNPC(npcId, HeadE.CALM_TALK, "The stew for the servant's main meal.");
            addPlayer(HeadE.CALM_TALK, "Oh... Fair enough. See you later.");
        addNPC(npcId, HeadE.CALM_TALK, "Bye.");
        create();
    }
}

