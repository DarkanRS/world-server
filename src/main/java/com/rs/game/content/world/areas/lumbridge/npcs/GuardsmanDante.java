package com.rs.game.content.world.areas.lumbridge.npcs;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.HeadE;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class GuardsmanDante extends Conversation {

    //Identify NPC by ID
    private static final int npcId = 7885;

    public static NPCClickHandler GuardsmanDante = new NPCClickHandler(new Object[]{npcId}, e -> {
    	 switch (e.getOption()) {
         
         case "Talk-to" -> e.getPlayer().startConversation(new GuardsmanDante(e.getPlayer()));
     }
    });

    public GuardsmanDante(Player player) {
        super(player);
        //TODO replace placeholder conversation
        addNPC(npcId, HeadE.FRUSTRATED, "Not right now, I'm Busy!");
        create();
    }
}
