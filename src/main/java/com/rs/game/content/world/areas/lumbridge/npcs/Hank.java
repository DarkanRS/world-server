package com.rs.game.content.world.areas.lumbridge.npcs;

import com.rs.engine.dialogue.Conversation;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.utils.shop.ShopsHandler;

@PluginEventHandler
public class Hank extends Conversation {

    //Identify NPC by ID
    private static final int npcId = 8864;

    public static NPCClickHandler Hank = new NPCClickHandler(new Object[]{npcId}, e -> {
    	 switch (e.getOption()) {
         //Start Conversation
         case "Talk-to" -> e.getPlayer().startConversation(new Hank(e.getPlayer()));
         case "Trade" -> ShopsHandler.openShop(e.getPlayer(), "lumbridge_fishing_supplies");
     }
    });

    public Hank(Player player) {
        super(player);
    };
}
/*
This fixes Hank and restores default store conversation
 */