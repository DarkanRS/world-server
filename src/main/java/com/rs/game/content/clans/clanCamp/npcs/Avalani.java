package com.rs.game.content.clans.clanCamp.npcs;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.annotations.ServerStartupEvent;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class Avalani extends Conversation {

    private static final int npcId = 13827;

    @ServerStartupEvent
    public static void addLoSOverrides() {
        Entity.addLOSOverrides(npcId);
    }

    public static NPCClickHandler Avalani = new NPCClickHandler(new Object[] { npcId }, e -> {
        if (e.getOption().equalsIgnoreCase("chat")) {
            e.getPlayer().startConversation(new Avalani(e.getPlayer(), e.getNPC()));
        }
    });


    public Avalani(Player player, NPC npc) {
        super(player);
        player.startConversation(new Dialogue()
                .addPlayer(HeadE.SCARED,"What are you doing with those chickens!")
                .addNPC(npcId, HeadE.CALM_TALK, "It's a ritual dedicated to Armadyl, to bring the portal to bear on the floating islands I have been locating.")
                .addNPC(npcId, HeadE.EVIL_LAUGH, " The skies will soon be ours at last! And I'll show those fools at the institute!")
        );
    }
}

