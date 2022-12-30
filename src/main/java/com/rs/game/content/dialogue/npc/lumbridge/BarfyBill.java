package com.rs.game.content.dialogue.npc.lumbridge;

// Basic dialogue handler for linear text conversations with no choices.

import com.rs.game.content.dialogue.Conversation;
import com.rs.game.content.dialogue.HeadE;
import com.rs.game.content.dialogue.Options;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class BarfyBill extends Conversation {

    //Identify NPC by ID
    private static int npcId = 3331;
    public static NPCClickHandler BarfyBill = new NPCClickHandler(new Object[]{npcId}) {
        @Override
        //Handle Right-Click
        public void handle(NPCClickEvent e) {
            switch (e.getOption()) {
                //Start Conversation
                case "Talk-to" -> e.getPlayer().startConversation(new BarfyBill(e.getPlayer()));
            }
        }
    };


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
