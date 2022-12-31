package com.rs.game.content.world.npcs.alKharid;

import com.rs.game.engine.dialogue.Conversation;
import com.rs.game.engine.dialogue.Dialogue;
import com.rs.game.engine.dialogue.HeadE;
import com.rs.game.engine.dialogue.Options;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Item;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.handlers.NPCClickHandler;


@PluginEventHandler
public class SilkTrader extends Conversation {

    //Identify NPC by ID
    private static int npcId = 539;

    public static NPCClickHandler SilkTrader = new NPCClickHandler(new Object[]{npcId}) {
        @Override
        //Handle Right-Click
        public void handle(NPCClickEvent e) {
            switch (e.getOption()) {
                //Start Conversation
                case "Talk-to" -> e.getPlayer().startConversation(new SilkTrader(e.getPlayer()));
            }
        }
    };

    public SilkTrader(Player player) {
        super(player);
        if (!player.getInventory().containsItems(new Item(950, 1))) {
            addNPC(npcId, HeadE.HAPPY_TALKING, "I buy silk.");
            addNPC(npcId, HeadE.HAPPY_TALKING, "If you get any silk to sell bring it here.");
            create();
        } else {
            addPlayer(HeadE.HAPPY_TALKING, "Hello I have some fine silk from Al Kharid to sell to you.");
            addNPC(npcId, HeadE.AMAZED, "Ah I may be interested in that!");
            addNPC(npcId, HeadE.CALM_TALK, "What sort of price were you looking at per piece of silk?");
            addOptions(new Options() {
                @Override
                public void create() {

                    option("20 Coins", new Dialogue()
                            .addPlayer(HeadE.HAPPY_TALKING, "20 Coins.")
                            .addNPC(npcId, HeadE.HAPPY_TALKING, "Ok that suits me.")
                            .addNext(() -> {
                                player.getInventory().deleteItem(950, 1);
                                player.getInventory().addItem(995, 20);
                            }));
                    option("80 Coins", new Dialogue()
                            .addPlayer(HeadE.HAPPY_TALKING, "80 Coins")
                            .addNPC(npcId, HeadE.SCARED, "80 coins! That's a bit steep.")
                            .addNPC(npcId, HeadE.HAPPY_TALKING, "How about 40 coins?.")
                            .addOptions(new Options() {
                                @java.lang.Override
                                public void create() {
                                    option("Okay 40 coins sounds good.", new Dialogue()
                                            .addPlayer(HeadE.HAPPY_TALKING, "Okay 40 coins sounds good.")
                                            .addNext(() -> {
                                                player.getInventory().deleteItem(950, 1);
                                                player.getInventory().addItem(995, 40);
                                            })
                                    );
                                    option("50 and that's my final price", new Dialogue()
                                            .addPlayer(HeadE.HAPPY_TALKING, "50 and that's my final price.")
                                            .addNPC(npcId, HeadE.CALM_TALK, "Done.")
                                            .addNext(() -> {
                                                player.getInventory().deleteItem(950, 1);
                                                player.getInventory().addItem(995, 50);
                                            })
                                    );
                                    option("No, that's not enough", new Dialogue()
                                            .addPlayer(HeadE.SHAKING_HEAD, "No, that's not enough.")
                                    );

                                }
                            }));
                    option("120 Coins", new Dialogue()
                            .addPlayer(HeadE.CALM_TALK, "120 Coins")
                            .addNPC(npcId, HeadE.SHAKING_HEAD, "120 coins! You'll never get that much for it.")
                            .addNPC(npcId, HeadE.HAPPY_TALKING, "I'll be generous and give you 50 for it.")
                            .addOptions(new Options() {
                                @java.lang.Override
                                public void create() {
                                    option("Ok I guess 50 will do.", new Dialogue()
                                            .addPlayer(HeadE.CALM_TALK, "Ok I guess 50 will do.")
                                            .addNext(() -> {
                                                player.getInventory().deleteItem(950, 1);
                                                player.getInventory().addItem(995, 50);
                                            })
                                    );
                                    option("I'll give it to you for 60", new Dialogue()
                                            .addPlayer(HeadE.HAPPY_TALKING, "I'll give it to you for 60")
                                            .addNPC(npcId, HeadE.SKEPTICAL, "You drive a hard bargain.")
                                            .addNPC(npcId, HeadE.CALM_TALK, "But I guess that will have to do.")
                                            .addNext(() -> {
                                                player.getInventory().deleteItem(950, 1);
                                                player.getInventory().addItem(995, 60);
                                            })
                                    );
                                    option("No, that's not enough", new Dialogue()
                                            .addPlayer(HeadE.SHAKING_HEAD, "No, that's not enough.")
                                    );
                                }
                            })

                    );
                    option("200 Coins", new Dialogue()
                            .addPlayer(HeadE.HAPPY_TALKING, "200 Coins.")
                            .addNPC(npcId, HeadE.ANGRY, "Don't be ridiculous that is far to much.")
                            .addNPC(npcId, HeadE.ANGRY, "You insult me with that price.")
                    );

                }


            });
        }
    }
}