package com.rs.game.content.world.areas.al_kharid.npcs;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class Karim extends Conversation {

    //Identify NPC by ID
    private static final int npcId = 543;

    public static NPCClickHandler Karim = new NPCClickHandler(new Object[]{npcId}, e -> {
    	switch (e.getOption()) {
        //Start Conversation
        case "Talk-to" -> e.getPlayer().startConversation(new Karim(e.getPlayer()));
    	}
    });

    public Karim(Player player) {
        super(player);
        addNPC(npcId, HeadE.CALM_TALK, "Would you like to buy a nice kebab? Only one gold.");
        addOptions(new Options() {
            @Override
            public void create() {

                option("I think I'll give it a miss.", new Dialogue()
                        .addPlayer(HeadE.CALM_TALK, "I think I'll give it a miss.")
                );

                option("Yes please.", new Dialogue()
                        .addNext(() -> {
                            if(player.getInventory().hasCoins(1)){
                                player.getInventory().removeCoins(1);
                                player.getInventory().addItem(1971, 1);
                            }
                            else {
                                addPlayer(HeadE.CALM_TALK, "Oops, I forgot to bring any money with me.");
                                addNPC(npcId, HeadE.SCARED, "Come back when you have some.");
                            }
                        })
                );

            }


        });
    }
}