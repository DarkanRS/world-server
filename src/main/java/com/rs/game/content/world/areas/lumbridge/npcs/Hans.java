package com.rs.game.content.world.areas.lumbridge.npcs;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.game.content.NpcID;
import com.rs.game.model.entity.ForceTalk;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class Hans extends Conversation {

    //Identify NPC by ID
    private static final int npcId = NpcID.NPCS.valueOf("Hans").getId();
    public static NPCClickHandler Hans = new NPCClickHandler(new Object[]{npcId}, e -> {
    	switch (e.getOption()) {
        //Start Conversation
        case "Talk-to" -> e.getPlayer().startConversation(new Hans(e.getPlayer(), e.getNPC()));
    }
    });


    public Hans(Player player, NPC npc) {
        super(player);
        //Identify NPC by ID
        addOptions(new Options() {
            @Override
            public void create() {
                //Player initiates
                addNPC(npcId, HeadE.CALM_TALK, "Hello. What are you doing here?");
                addOptions(new Options() {
                               @Override
                               public void create() {
                                   option("I'm looking for whoever is in charge of this place.", new Dialogue()
                                           .addPlayer(HeadE.CALM_TALK, "I'm looking for whoever is in charge of this place.")
                                           .addNPC(npcId, HeadE.CALM_TALK, "Who, the Duke? He's in his study, on the first floor.")
                                   );
                                   option("I have come to kill everyone in this castle!", new Dialogue()
                                                   .addPlayer(HeadE.EVIL_LAUGH, "I have come to kill everyone in this castle!")
                                                   .addNPC(npcId, HeadE.TERRIFIED, "Help!")
                                                   .addNext(() -> npc.setNextForceTalk(new ForceTalk("Help! Help! Help!")))
                                           //TODO Make run-away
                                   );
                                   option("Can you tell me how long I've been here?", new Dialogue()
                                           .addPlayer(HeadE.CALM_TALK, "Can you tell me how long I've been here?")
                                           .addNPC(npcId, HeadE.CALM_TALK, "As long as you have been wasting my time!")
                                   );
                                   option("Nothing.", new Dialogue()
                                   );

                               }

                           }
                );
            }
        });
    }
}
