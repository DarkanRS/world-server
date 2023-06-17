package com.rs.game.content.world.areas.catherby.npcs;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.utils.shop.ShopsHandler;

@PluginEventHandler
public class Arhein extends Conversation {

    //Identify NPC by ID
    private static final int npcId = 563;

    public static NPCClickHandler Arhein = new NPCClickHandler(new Object[]{npcId}, e -> {
        switch (e.getOption()) {
            
            case "Talk-to" -> e.getPlayer().startConversation(new Arhein(e.getPlayer()));
            case "Trade" -> ShopsHandler.openShop(e.getPlayer(), "arheins_shop");
        }
    });

    public Arhein(Player player) {
        super(player);
        player.startConversation(new Conversation(new Dialogue()
                .addNPC(npcId, HeadE.HAPPY_TALKING, "Hello! Would you like to trade?")
                .addOptions("Would you like to trade?", new Options() {
                    @Override
                    public void create() {
                        option("Yes.", new Dialogue()
                                .addNPC(npcId, HeadE.CALM_TALK, "Sure.")
                                .addNext(() -> ShopsHandler.openShop(player, "arheins_shop" )
                                ));
                        option("No thank you.", new Dialogue()
                                .addNPC(npcId, HeadE.CALM_TALK, "No thanks.")
                        );
                        option("Is that your ship?", new Dialogue()
                                .addPlayer(HeadE.CONFUSED, "Is that your ship?")
                                .addNPC(npcId, HeadE.CALM_TALK, "Yes, I use it to make deliveries to my customers up and down the coast. ")
                                .addNPC(npcId, HeadE.CALM_TALK, "These crates here are all ready for my next trip..")
                                .addOptions("What would you like to say?", new Options() {
                                    @Override
                                    public void create() {
                                        option("Where do you deliver to?", new Dialogue()
                                                .addPlayer(HeadE.CALM_TALK, "Where do you deliver to?")
                                                .addNPC(npcId, HeadE.HAPPY_TALKING, "Oh, various places up and down the coast. Mostly Karamja and Port Sarim.")
                                                .addOptions("What would you like to say?", new Options() {
                                                            @Override
                                                            public void create() {
                                                                option("I don't suppose I could get a lift anywhere?", new Dialogue()
                                                                        .addPlayer(HeadE.CALM_TALK, "I don't suppose I could get a lift anywhere?")
                                                                        .addNPC(npcId, HeadE.SHAKING_HEAD, "Sorry pal, but I'm afraid I'm not quite ready to sail yet.")
                                                                        .addNPC(npcId, HeadE.SKEPTICAL, "I'm waiting on a big delivery of candles which I need to deliver further along the coast.")
                                                                );
                                                                option("Well, good luck with your business.", new Dialogue()
                                                                        .addPlayer(HeadE.CALM_TALK, "Well, good luck with your business.")
                                                                        .addNPC(npcId, HeadE.HAPPY_TALKING, "Thanks buddy!")
                                                                );

                                                            }

                                                        }

                                                )
                                        );
                                        option("Are you rich then?", new Dialogue()
                                                .addPlayer(HeadE.CALM_TALK, "Are you rich then?")
                                                .addNPC(npcId, HeadE.HAPPY_TALKING, " Business is going reasonably well... I wouldn't say I was the richest of merchants ever, but I'm doing fairly well all things considered.")
                                        );
                                    }
                                }));


                    }})));
    }
}

