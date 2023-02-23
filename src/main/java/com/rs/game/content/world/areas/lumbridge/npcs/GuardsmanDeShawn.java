package com.rs.game.content.world.areas.lumbridge.npcs;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.HeadE;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class GuardsmanDeShawn extends Conversation {

    //Identify NPC by ID
    private static final int npcId = 7886;

    public static NPCClickHandler GuardsmanDeShawn = new NPCClickHandler(new Object[]{npcId}, e -> {
    	 switch (e.getOption()) {
         //Start Conversation
         case "Talk-to" -> e.getPlayer().startConversation(new GuardsmanDeShawn(e.getPlayer()));
     }
    });

    public GuardsmanDeShawn(Player player) {
        super(player);
        //TODO replace placeholder conversation
        addNPC(npcId, HeadE.FRUSTRATED, "Not right now, I'm Busy!");
        create();
    }
}
