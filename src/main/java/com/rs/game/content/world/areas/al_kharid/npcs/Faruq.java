package com.rs.game.content.world.areas.al_kharid.npcs;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.utils.shop.ShopsHandler;

@PluginEventHandler
public class Faruq extends Conversation {

    //Identify NPC by ID
    private static final int npcId = 9159;

    public static NPCClickHandler Faruq = new NPCClickHandler(new Object[]{npcId}, e -> {
    	 switch (e.getOption()) {
         
         case "Talk-to" -> e.getPlayer().startConversation(new Faruq(e.getPlayer()));
         case "Trade" -> ShopsHandler.openShop(e.getPlayer(), "faruqs_tools_for_games");
    	 }
    });

    public Faruq(Player player) {
        super(player);
        addNPC(npcId, HeadE.AMAZED, "Hello! Have you come to sample my marvellous wares?");
        addOptions(new Options() {
            @Override
            public void create() {

                option("Yes, I'd like to see what you have.", new Dialogue()
                        .addNext(() -> {
                            ShopsHandler.openShop(player, "faruqs_tools_for_games");
                        }));
                option("Perhaps. Your stall has some odd-looking stuff; what are they for?", new Dialogue()
                        .addPlayer(HeadE.CALM_TALK, "Perhaps. Your stall has some odd-looking stuff; what are they for?")
                        .addNPC(npcId, HeadE.HAPPY_TALKING, "I sell them the tools to keep track of time, mark out places and routes, decide things randomly, even to hold great ballots of their group.")
                        .addOptions(new Options() {
                            @java.lang.Override
                            public void create() {
                                option("Let me see, then.", new Dialogue()
                                        .addNext(() -> {
                                            ShopsHandler.openShop(player, "faruqs_tools_for_games");
                                        }));
                                option("These tools, are they complicated?", new Dialogue()
                                        .addPlayer(HeadE.CALM_TALK, "These tools, are they complicated?")
                                        .addNPC(npcId, HeadE.CALM_TALK, "No, " + player.getDisplayName() +", they are not complicated.")
                                        .addNPC(npcId, HeadE.CALM_TALK, "I have a book that explains them, should you need.")
                                        .addNext(() -> {
                                            ShopsHandler.openShop(player, "faruqs_tools_for_games");
                                        }));
                                option("I don't think this is for me.", new Dialogue()
                                        .addPlayer(HeadE.CALM_TALK, "I don't think this is for me.")
                                        .addNPC(npcId, HeadE.CALM_TALK, "That is a shame. I shall be here if you change your mind.")
                                );

                            }
                        }));
                option("No, thanks.", new Dialogue()
                        .addPlayer(HeadE.CALM_TALK, "No, thanks.")
                );

            }


        });
    }
}