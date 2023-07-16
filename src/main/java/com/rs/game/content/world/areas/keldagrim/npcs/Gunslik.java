package com.rs.game.content.world.areas.keldagrim.npcs;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.utils.shop.ShopsHandler;

@PluginEventHandler
public class Gunslik extends Conversation {

    //Identify NPC by ID
    private static final int npcId = 2154;

    public static NPCClickHandler Gunslik = new NPCClickHandler(new Object[]{npcId}, e -> {
    	switch (e.getOption()) {
        
        case "Talk-to" -> e.getPlayer().startConversation(new Gunslik(e.getPlayer()));
        case "Trade" -> ShopsHandler.openShop(e.getPlayer(), "gunsliks_assorted_items");
    	}
    });

    public Gunslik(Player player) {
        super(player);
        addNPC(npcId, HeadE.SECRETIVE, "Hello there. You've come to the right place if you're looking for adventurer's equipment.");
        addOptions(new Options() {
            @Override
            public void create() {

                option("Oh good!", new Dialogue()
                        .addNext(() -> {
                            ShopsHandler.openShop(player, "gunsliks_assorted_items");
                        }));

                option("Nothing, thanks.", new Dialogue()
                        .addPlayer(HeadE.CONFUSED, "Nothing, thanks.")
                );
            }


        });
    };

}
/*
This fixes Hank and restores default store conversation
 */