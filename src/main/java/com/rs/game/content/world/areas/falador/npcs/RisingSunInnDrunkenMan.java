package com.rs.game.content.world.areas.falador.npcs;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.HeadE;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class RisingSunInnDrunkenMan extends Conversation {
    public static NPCClickHandler RisingSunInnDrunkenMan = new NPCClickHandler(new Object[] { 3222 }, e -> {
        if (e.getOption().equalsIgnoreCase("talk-to"))
            e.getPlayer().startConversation(new RisingSunInnDrunkenMan(e.getPlayer(), e.getNPC().getId()));
    });

    public RisingSunInnDrunkenMan(Player player, int npc) {
        super(player);
        addPlayer(HeadE.HAPPY_TALKING, "Hello");
        addNPC(npc, HeadE.DRUNK, "... whassup?");
        addPlayer(HeadE.CONFUSED, "Are you alright?");
        addNPC(npc, HeadE.DRUNK, "... see ... two of you ... why there two of you?");
        addPlayer(HeadE.CONFUSED, "There's only one of me, friend.");
        addNPC(npc, HeadE.DRUNK, "... no, two of you... you can't count...");
        addNPC(npc, HeadE.DRUNK, "... maybe you drunk too much...");
        addPlayer(HeadE.SHAKING_HEAD, "Whatever you say, friend.");
        addNPC(npc, HeadE.DRUNK_ANGRY, "... giant hairy cabbages...");
    }
}
