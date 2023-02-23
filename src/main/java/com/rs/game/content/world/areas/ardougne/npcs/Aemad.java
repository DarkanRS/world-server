package com.rs.game.content.world.areas.ardougne.npcs;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.utils.shop.ShopsHandler;

@PluginEventHandler
public class Aemad extends Conversation {

    //Identify NPC by ID
    private static final int npcId = 590;

    public static NPCClickHandler Aemad = new NPCClickHandler(new Object[]{npcId}, e -> {
    	switch (e.getOption()) {
        //Start Conversation
        case "Talk-to" -> e.getPlayer().startConversation(new Aemad(e.getPlayer()));
        case "Trade-General-Store" -> ShopsHandler.openShop(e.getPlayer(), "aemads_adventuring_supplies");
    	}
    });

    public Aemad(Player player) {
        super(player);
        addNPC(npcId, HeadE.SECRETIVE, "Hello there. You've come to the right place if you're looking for adventurer's equipment.");
        addOptions(new Options() {
            @Override
            public void create() {

                option("Oh that sounds interesting..", new Dialogue()
                        .addNext(() -> {
                            ShopsHandler.openShop(player, "aemads_adventuring_supplies");
                        }));

                option("No, I've come to the wrong place.", new Dialogue()
                        .addPlayer(HeadE.CONFUSED, "No, I've come to the wrong place.")
                        .addNPC(npcId, HeadE.FRUSTRATED, "Hmph. Well, perhaps next time you'll need something from me?")
                );
            }


        });
    };

}
/*
This fixes Hank and restores default store conversation
 */