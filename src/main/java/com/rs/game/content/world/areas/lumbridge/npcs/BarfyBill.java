package com.rs.game.content.world.areas.lumbridge.npcs;

import com.rs.game.engine.dialogue.Conversation;
import com.rs.game.engine.dialogue.HeadE;
import com.rs.game.engine.dialogue.Options;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class BarfyBill extends Conversation {

    //Identify NPC by ID
    private static int npcId = 3331;
    public static NPCClickHandler BarfyBill = new NPCClickHandler(new Object[]{npcId}, e -> {
    	switch (e.getOption()) {
        //Start Conversation
        case "Talk-To" -> e.getPlayer().startConversation(new BarfyBill(e.getPlayer()));
    	}
    });

    public BarfyBill(Player player) {
        super(player);
        //Identify NPC by ID
        addOptions(new Options() {
            @Override
            public void create() {
                //Player initiates
                addPlayer(HeadE.CALM_TALK, "Test");
                //TODO Bill isn't responding as expected?
            }
        });
    }
}
