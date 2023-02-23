package com.rs.game.content.world.areas.lumbridge.npcs;

import com.rs.game.content.world.unorganized_dialogue.GrilleGoatsDialogue;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class GillieGroats {

    //Identify NPC by ID
    private static final int npcId = 3807;
    public static NPCClickHandler GillieGroats = new NPCClickHandler(new Object[]{npcId}, e -> {
    	switch (e.getOption()) {
        //Start Conversation
        case "Talk-to" -> e.getPlayer().startConversation(new GrilleGoatsDialogue(e.getPlayer()));
    	}
    });
};

