package com.rs.game.content.world.areas.varrock.npcs;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.game.model.entity.pathing.RouteEvent;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Tile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class Bartender {
    public static NPCClickHandler handleBlueMoonBartender = new NPCClickHandler(new Object[] { 733 }, e -> {
        Player p = e.getPlayer();

        p.setRouteEvent(new RouteEvent(Tile.of(3224, 3397, 0), () -> {
            p.faceEntity(e.getNPC());
            if (p.getTreasureTrailsManager().useNPC(e.getNPC()))
                return;
            p.startConversation(new Conversation(p) {
                int BARTENDER = 733;
                {
                    addNPC(BARTENDER, HeadE.HAPPY_TALKING, "What can I do yer for?");
                    addOptions("Choose an option:", new Options() {
                        @Override
                        public void create() {
                            option("A glass of your finest ale please.", new Dialogue()
                                    .addPlayer(HeadE.HAPPY_TALKING, "A glass of your finest ale please.")
                                    .addNPC(BARTENDER, HeadE.HAPPY_TALKING, "No problemo. That'll be 2 coins.")
                                    .addNext(()->{
                                        if(p.getInventory().hasCoins(2)) {
                                            p.getInventory().removeCoins(2);
                                            p.getInventory().addItem(1917, 1);
                                            p.startConversation(new Conversation(p) { {
                                                addSimple("The bartender hands you a beer...");
                                                create();
                                            } });
                                        } else
                                            p.startConversation(new Conversation(p) { {
                                                addNPC(BARTENDER, HeadE.SKEPTICAL_THINKING, "You have 2 coins don't you?");
                                                addPlayer(HeadE.SAD_MILD_LOOK_DOWN, "No..");
                                                addNPC(BARTENDER, HeadE.FRUSTRATED, "That's too bad...");
                                                create();
                                            } });
                                    }));
                            option("Can you recommend where an adventurer might make his fortune?", new Dialogue()
                                    .addPlayer(HeadE.HAPPY_TALKING, "Can you recommend where an adventurer might make his fortune?")
                                    .addNPC(BARTENDER, HeadE.HAPPY_TALKING, "Ooh I don't know if I should be giving away information, makes the game too easy.")
                                    .addOptions("Choose an option:", new Options() {
                                        @Override
                                        public void create() {
                                            option("Oh ah well...", new Dialogue()
                                                    .addPlayer(HeadE.SAD_MILD, "Oh ah well..."));
                                            option("Game? What are you talking about?", new Dialogue()
                                                    .addPlayer(HeadE.SKEPTICAL_THINKING, "Game? What are you talking about?")
                                                    .addNPC(BARTENDER, HeadE.TALKING_ALOT, "This world around us... is an online game... called RuneScape.")
                                                    .addPlayer(HeadE.SKEPTICAL_THINKING, "Nope, still don't understand what you are talking about. What does 'online' mean?")
                                                    .addNPC(BARTENDER, HeadE.TALKING_ALOT, "It's a sort of connection between magic boxes across the world, big " +
                                                            "boxes on people's desktops and little ones people can carry. They can talk to each other to play games.")
                                                    .addPlayer(HeadE.AMAZED_MILD, "I give up. You're obviously completely mad!"));
                                            option("Just a small clue?", new Dialogue()
                                                    .addPlayer(HeadE.HAPPY_TALKING, "Just a small clue?")
                                                    .addNPC(BARTENDER, HeadE.HAPPY_TALKING, "Go and talk to the bartender at the Jolly Boar Inn, he doesn't " +
                                                            "seem to mind giving away clues.")
                                            );
                                        }
                                    }));
                            option("Do you know where I can get some good equipment", new Dialogue()
                                    .addPlayer(HeadE.HAPPY_TALKING, "Do you know where I can get some good equipment?")
                                    .addNPC(BARTENDER, HeadE.HAPPY_TALKING, "Well, there's the sword shop across the road, or there's also all sorts of " +
                                            "shops up around the market."));
                        }
                    });

                    create();
                }
            });

        }, false));
    });
}
