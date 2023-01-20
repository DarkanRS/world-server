package com.rs.game.content.world.areas.lumbridge.npcs;

import com.rs.game.engine.dialogue.Conversation;
import com.rs.game.engine.dialogue.HeadE;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class GuardsmanBrawn extends Conversation {

    //Identify NPC by ID
    private static int npcId = 7887;

    public static NPCClickHandler GuardsmanBrawn = new NPCClickHandler(new Object[]{npcId}, e -> {
    	switch (e.getOption()) {
        //Start Conversation
        case "Talk-to" -> e.getPlayer().startConversation(new GuardsmanBrawn(e.getPlayer()));
    }
    });

    public GuardsmanBrawn(Player player) {
        super(player);
        //TODO replace placeholder conversation
        addNPC(npcId, HeadE.FRUSTRATED, "Not right now, I'm Busy!");
        create();
    }
}
