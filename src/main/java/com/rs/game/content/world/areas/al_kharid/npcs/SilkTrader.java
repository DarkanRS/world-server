package com.rs.game.content.world.areas.al_kharid.npcs;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class SilkTrader extends Conversation {

    //Identify NPC by ID
    private static final int npcId = 539;

    public static NPCClickHandler SilkTrader = new NPCClickHandler(new Object[]{npcId}, e -> {
    	switch (e.getOption()) {
        
        case "Talk-to" -> e.getPlayer().startConversation(new SilkTrader(e.getPlayer()));
    	}
    });

    public SilkTrader(Player player) {
        super(player);

        addNPC(npcId, HeadE.AMAZED, "Do you want to buy any fine silks?");
        addOptions(new Options() {
            @Override
            public void create() {

                option("How much are they?", new Dialogue()
                        .addPlayer(HeadE.HAPPY_TALKING, "How much are they?")
                        .addNPC(npcId,HeadE.HAPPY_TALKING, "3 coins.")
                        .addOptions(new Options() {
                            @java.lang.Override
                            public void create() {
                                option("3 coins sounds good.", new Dialogue()
                                        .addNext(() -> {
                                            if(player.getInventory().hasCoins(3)) {
                                                player.getInventory().removeCoins(3);
                                                player.getInventory().addItem(950, 1);
                                            }
                                            else{
                                                addPlayer(HeadE.CALM_TALK, "Oh dear. I don't have enough money.");
                                                addNPC(npcId, HeadE.SCARED, "Well, come back when you do have some money!");
                                            }
                                        })
                                );
                                option("No. That's too much for me.", new Dialogue()
                                        .addPlayer(HeadE.SHAKING_HEAD, "No. That's too much for me.")
                                        .addNPC(npcId, HeadE.SAD, "2 gp and that's as low as I'll go.")
                                        .addNPC(npcId, HeadE.SKEPTICAL, " I'm not selling it for any less. You'll probably go and sell it in Varrock for a profit, anyway.")
                                        .addOptions(new Options() {
                                            @java.lang.Override
                                            public void create() {
                                                option("2 coins sounds good.", new Dialogue()
                                                        .addNext(() -> {
                                                            if(player.getInventory().hasCoins(2)) {
                                                                player.getInventory().removeCoins(2);
                                                                player.getInventory().addItem(950, 1);
                                                            }
                                                            else{
                                                                addPlayer(HeadE.CALM_TALK, "Oh dear. I don't have enough money.");
                                                                addNPC(npcId, HeadE.SCARED, "Well, come back when you do have some money!");
                                                            }
                                                        })
                                                );
                                                option("No, really. I don't want it.", new Dialogue()
                                                        .addPlayer(HeadE.SHAKING_HEAD, "No, really. I don't want it..")
                                                        .addNPC(npcId, HeadE.SAD, "Okay, but that's the best price you're going to get.")
                                                );
                                            }
                                        }));
                            }
                        }));
                option("No. Silk doesn't suit me.", new Dialogue()
                        .addPlayer(HeadE.HAPPY_TALKING, "No. Silk doesn't suit me.")
                );

            }
        });
    }
}