package com.rs.game.content.world.areas.port_sarim.npcs;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.quest.Quest;
import com.rs.game.content.achievements.AchievementSystemDialogue;
import com.rs.game.content.achievements.SetReward;
import com.rs.game.content.quests.piratestreasure.RedbeardFrankPiratesTreasureD;
import com.rs.game.model.entity.Entity;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.annotations.ServerStartupEvent;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.utils.shop.ShopsHandler;

@PluginEventHandler
public class PortSarimNPC {
    private static final int AHAB = 2692;
    private static final int BARD_ROBERTS = 3509;
    private static final int BELLEMORDE = 2942;
    private static final int BETTY = 583;
    private static final int BRIAN = 559;
    private static final int DRUNKEN_SAILOR = 1207;
    private static final int FELKRASH = 2951;
    private static final int GRUM = 556;
    private static final int JACK_SEAGULL = 2690;
    private static final int LONGBOW_BEN = 2691;
    private static final int REDBEARD_FRANK = 375;
    private static final int BARTENDER = 734;
    private static final int STANKY_MORGAN = 6667;
    private static final int THAKI = 7115;
    private static final int THE_FACE = 2950;

    @ServerStartupEvent
    public static void addLoSOverrides() {
        Entity.addLOSOverride( AHAB );
    }

    public static NPCClickHandler Ahab = new NPCClickHandler(new Object[]{ AHAB }, e -> {
        switch (e.getOption()) {
            case "Talk-to" -> e.getPlayer().startConversation(new Dialogue()
                    .addNPC(AHAB, HeadE.HAPPY_TALKING, "Arrr, matey!")
                    .addOptions(ops -> {
                        ops.add("Arrr!")
                                .addPlayer(HeadE.HAPPY_TALKING, "Arrr!")
                                .addNPC(AHAB, HeadE.HAPPY_TALKING, "Arrr, matey!");

                        ops.add("Are you going to sit there all day?!", new Dialogue()
                                .addPlayer(HeadE.SKEPTICAL, "Are you going to sit there all day?")
                                .addNPC(AHAB, HeadE.FRUSTRATED, "Aye, I am. I canna walk, ye see.")
                                .addPlayer(HeadE.SKEPTICAL, "What's stopping you from walking?")
                                .addNPC(AHAB, HeadE.FRUSTRATED, "Arrr, I 'ave only the one leg! I lost its twin when my last ship went down.")
                                .addPlayer(HeadE.SKEPTICAL_HEAD_SHAKE, "But I can see both your legs!")
                                .addNPC(AHAB, HeadE.FRUSTRATED, "Nay, young " + e.getPlayer().getPronoun("laddie", "lassie") + ", this be a false leg. For years I had me a sturdy wooden peg-leg, but now I wear this dainty little feller.")
                                .addNPC(AHAB, HeadE.FRUSTRATED, "Yon peg-leg kept getting stuck in the floorboards.")
                                .addPlayer(HeadE.SKEPTICAL, "Right...")
                        );
                    }));
        }
    });

    public static NPCClickHandler Bellemorde = new NPCClickHandler(new Object[]{ BELLEMORDE }, e -> {
        switch (e.getOption()) {
            case "Talk-to" -> {
                if (ItemDefinitions.getDefs(e.getPlayer().getEquipment().getNeckId()).getName().contains("Catspeak")) {
                    e.getPlayer().startConversation(new Dialogue()
                            .addPlayer(HeadE.HAPPY_TALKING, " Hello puss.")
                            .addNPC(BELLEMORDE, HeadE.CAT_CALM_TALK, " Hello human.")
                            .addPlayer(HeadE.HAPPY_TALKING, "Would you like a fish?")
                            .addPlayer(HeadE.CAT_SHOUTING, " I don't want your fish. I hunt and eat what I need by myself."));
                } else {
                    e.getPlayer().startConversation(new Dialogue()
                            .addPlayer(HeadE.HAPPY_TALKING, " Hello puss.")
                            .addNPC(BELLEMORDE, HeadE.CAT_SHOUTING, " Hiss!"));
                }
            }
        }
    });

    public static NPCClickHandler Betty = new NPCClickHandler(new Object[]{ BETTY }, e -> {
        switch (e.getOption()) {
            case "Trade" -> ShopsHandler.openShop(e.getPlayer(), "bettys_magic_emporium");
            case "Talk-to" -> {
                if (Quest.HAND_IN_SAND.isImplemented() && e.getPlayer().getQuestManager().isComplete(Quest.HAND_IN_SAND)) {
                    e.getPlayer().startConversation(new Dialogue()
                            .addNPC(BETTY, HeadE.HAPPY_TALKING, " Zavistic told me what a good job you did. If you want some more pink dye, I have made up a batch and you can have some for 20 gold.")
                            .addOptions(ops -> {
                                ops.add("Yes, please! ")
                                        .addNext(() -> {
                                            if (!e.getPlayer().getInventory().hasFreeSlots())
                                                e.getPlayer().startConversation(new Dialogue()
                                                        .addNPC(BETTY, HeadE.SHAKING_HEAD, "You don't have the space for any more dye."));
                                            if (!e.getPlayer().getInventory().hasCoins(20))
                                                e.getPlayer().startConversation(new Dialogue()
                                                        .addNPC(BETTY, HeadE.SHAKING_HEAD, "You don't have enough coins!"));
                                            if (e.getPlayer().getInventory().hasFreeSlots() && e.getPlayer().getInventory().hasCoins(20)) {
                                                e.getPlayer().getInventory().removeCoins(20);
                                                e.getPlayer().getInventory().addItem(6955);
                                                e.getPlayer().sendMessage("You receive some pink dye from Betty.");
                                            }
                                        });

                                ops.add("Can I see your wares?")
                                        .addNext(() -> {
                                            ShopsHandler.openShop(e.getPlayer(), "bettys_magic_emporium");
                                        });

                                ops.add("No thanks, Betty.")
                                        .addPlayer(HeadE.HAPPY_TALKING, "No thanks, Betty. Good luck with the shop. I might be back for some dye later.")
                                        .addNPC(BETTY, HeadE.HAPPY_TALKING, "Thanks, " + e.getPlayer().getDisplayName() + ". See you soon.");
                            }));
                } else {
                    e.getPlayer().startConversation(new Dialogue()
                            .addNPC(BETTY, HeadE.HAPPY_TALKING, "Welcome to the magic emporium.")
                            .addOptions(ops -> {

                                ops.add("Can I see your wares? ")
                                        .addNext(() -> {
                                            ShopsHandler.openShop(e.getPlayer(), "bettys_magic_emporium");
                                        });

                                ops.add("Sorry, I'm not into magic.")
                                        .addPlayer(HeadE.HAPPY_TALKING, "Sorry, I'm not into magic.")
                                        .addNPC(BETTY, HeadE.HAPPY_TALKING, " Well, if you see anyone who is into magic, please send them my way.");
                            }));
                }
            }
        }
    });

    public static NPCClickHandler BrianBattleaxe = new NPCClickHandler(new Object[]{ BRIAN }, e -> {
        switch (e.getOption()) {
            case "Trade" -> ShopsHandler.openShop(e.getPlayer(), "brians_battleaxe_bazaar");
            case "Talk-to" -> e.getPlayer().startConversation(new Dialogue()
                    .addNPC(BRIAN, HeadE.HAPPY_TALKING, "Hello.")
                    .addOptions(ops -> {

                        ops.add("So, are you selling something? ")
                                .addPlayer(HeadE.CALM_TALK, "So, are you selling something?")
                                .addNPC(BRIAN, HeadE.HAPPY_TALKING, "Yep, take a look at these great axes.")
                                .addNext(() -> {
                                    ShopsHandler.openShop(e.getPlayer(), "brians_battleaxe_bazaar");
                                });

                        ops.add("'Ello'")
                                .addPlayer(HeadE.HAPPY_TALKING, "'Ello.")
                                .addNPC(BRIAN, HeadE.HAPPY_TALKING, "'Ello");
                    }));
        }
    });

    public static NPCClickHandler DrunkenSailor = new NPCClickHandler(new Object[]{ DRUNKEN_SAILOR }, e -> {
        switch (e.getOption()) {
            case "Talk-to" -> {
                e.getPlayer().simpleDialogue("You shake the snoring sailor by the shoulder. It doesn't look like he's going to wake up any time soon. ");
            }
            case "Search" -> {
                if (e.getPlayer().getInventory().hasFreeSlots()) {
                    e.getPlayer().simpleDialogue("You find the sailor's hat in one of his pockets. You put it in your inventory. ");
                    e.getPlayer().getInventory().addItem(12595);
                } else
                    e.getPlayer().simpleDialogue("You don't enough space to carry that");
            }
        }
    });

    public static NPCClickHandler Felkrash = new NPCClickHandler(new Object[]{ FELKRASH }, e -> {
        switch (e.getOption()) {
            case "Talk-to" -> e.getPlayer().startConversation(new Dialogue()
                    .addNPC(FELKRASH, HeadE.ANGRY, "What are you doing in here? This is a private club, get out.")
                    .addPlayer(HeadE.CONFUSED, "What club? I don't see anything here.")
                    .addNPC(FELKRASH, HeadE.SAD_CRYING, "It is... It used to be my rat pits. You could bring your cats here to fight. It was going to be glorious! It was open for years. Not a single person visited! How could so many be so foolish as to ignore such brilliance?")
                    .addPlayer(HeadE.CONFUSED, "I guess brillance is one word for it.")
                    .addSimple("Felkrash glares at you angrily, you walk away while you can")
            );
        }
    });

    public static NPCClickHandler Grum = new NPCClickHandler(new Object[]{ GRUM }, e -> {
        switch (e.getOption()) {
            case "Trade" -> ShopsHandler.openShop(e.getPlayer(), "grums_gold_exchange");
            case "Talk-to" -> e.getPlayer().startConversation(new Dialogue()
                    .addNPC(GRUM, HeadE.HAPPY_TALKING, " Would you like to buy or sell some gold jewellery?")
                    .addOptions(ops -> {
                        ops.add("Yes, please.")
                                .addNext(() -> {
                                    ShopsHandler.openShop(e.getPlayer(), "grums_gold_exchange");
                                });
                        ops.add("No, I'm not that rich.")
                                .addPlayer(HeadE.SHAKING_HEAD, "No, I'm not that rich. ")
                                .addNPC(GRUM, HeadE.ANGRY, " Get out, then! We don't want any riff-raff in here.");
                    })
            );
        }
    });

    public static NPCClickHandler JackSeagull = new NPCClickHandler(new Object[]{ JACK_SEAGULL }, e -> {
        switch (e.getOption()) {
            case "Talk-to" -> e.getPlayer().startConversation(new Dialogue()
                    .addPlayer(HeadE.HAPPY_TALKING, "Arrr, matey!.")
                    .addNPC(JACK_SEAGULL, HeadE.HAPPY_TALKING, "Yo ho ho!")
                    .addPlayer(HeadE.HAPPY_TALKING, "So are you pirates?")
                    .addNPC(LONGBOW_BEN, HeadE.HAPPY_TALKING, "Aye " + e.getPlayer().getPronoun("laddie", "miss") + ", that we are.")
                    .addNPC(JACK_SEAGULL, HeadE.HAPPY_TALKING, "Aye, that we be.")
                    .addNPC(LONGBOW_BEN, HeadE.SHAKING_HEAD, "Nay, always ye say it wrong! Tis 'we are', not 'we be'.")
                    .addNPC(JACK_SEAGULL, HeadE.FRUSTRATED, "I be a pirate, not a scurvy schoolmaster!")
                    .addNPC(LONGBOW_BEN, HeadE.VERY_FRUSTRATED, "Ye be a fool, and a disgrace to piracy.")
                    .addNPC(JACK_SEAGULL, HeadE.CONFUSED, "Now ye be saying 'be' too!")
                    .addNPC(LONGBOW_BEN, HeadE.ANGRY, "Arrr! Tis thy fault.")
                    .addPlayer(HeadE.CONFUSED, "I think I'll leave you two to sort it out.")
            );
        }
    });

    public static NPCClickHandler LongbowBen = new NPCClickHandler(new Object[]{ LONGBOW_BEN }, e -> {
        switch (e.getOption()) {

            case "Talk-to" -> e.getPlayer().startConversation(new Dialogue()
                    .addNPC(LONGBOW_BEN, HeadE.HAPPY_TALKING, "Arrr, matey!")
                    .addPlayer(HeadE.HAPPY_TALKING, "Why are you called Longbow Ben?")
                    .addNPC(LONGBOW_BEN, HeadE.SKEPTICAL_THINKING, "Arrr, that's a strange yarn.")
                    .addNPC(LONGBOW_BEN, HeadE.FRUSTRATED, "I was to be marooned, ye see. A scurvy troublemaker had taken my ship, and he put me ashore on a little island.")
                    .addPlayer(HeadE.AMAZED, "Gosh, how did you escape?")
                    .addNPC(LONGBOW_BEN, HeadE.HAPPY_TALKING, "Arrr, ye see, he made one mistake! Before he sailed away, he gave me a bow and one arrow so that I wouldn't have to die slowly.")
                    .addNPC(LONGBOW_BEN, HeadE.LAUGH, "So I shot him and took my ship back.")
                    .addNPC(LONGBOW_BEN, HeadE.CONFUSED, "Right...")
            );
        }
    });

    public static NPCClickHandler RedBeardFrank = new NPCClickHandler(new Object[]{ REDBEARD_FRANK }, e -> {
        switch (e.getOption()) {
            case "Talk-to" -> e.getPlayer().startConversation(new Dialogue()
                    .addNPC(REDBEARD_FRANK, HeadE.CHEERFUL, "Arr, Matey!")
                    .addOptions(ops -> {
                        if (!e.getPlayer().isQuestComplete(Quest.PIRATES_TREASURE))
                            ops.add("About Pirate's Treasure", new Dialogue()
                                    .addNext(() -> {
                                        e.getPlayer().startConversation(new RedbeardFrankPiratesTreasureD(e.getPlayer()));
                                    }));

                        ops.add("About the Achievement System...",
                                new AchievementSystemDialogue(e.getPlayer(), REDBEARD_FRANK, SetReward.FALADOR_SHIELD)
                                        .getStart());
                    }));
        }
    });

    public static NPCClickHandler handleBartenders = new NPCClickHandler(new Object[]{ BARTENDER }, e -> {
        boolean hasCoins = e.getPlayer().getInventory().hasCoins(3);
        boolean hasSlots = e.getPlayer().getInventory().hasFreeSlots();
        String name = e.getNPC().getName();
        if (e.getOption().equalsIgnoreCase("talk-to"))
            e.getPlayer().startConversation(new Dialogue()
                    .addNPC(BARTENDER, HeadE.HAPPY_TALKING, "Heya! What can I get you?")
                    .addOptions(ops -> {
                        ops.add("Could I buy a beer please?")
                                .addPlayer(HeadE.HAPPY_TALKING, "Could I buy a beer please?")
                                .addNPC(BARTENDER, HeadE.HAPPY_TALKING, "Sure, that will be 2 gold coins please.")
                                .addNext(() -> {
                                    if (!hasCoins)
                                        e.getPlayer().startConversation(new Dialogue()
                                                .addNPC(BARTENDER, HeadE.FRUSTRATED, "I said 2 coins! You haven't got 2 coins!"));
                                    if (!hasSlots)
                                        e.getPlayer().startConversation(new Dialogue()
                                                .addNPC(BARTENDER, HeadE.SHAKING_HEAD, "You don't have the space for a beer!"));
                                    if (hasCoins && hasSlots) {
                                        e.getPlayer().getInventory().removeCoins(2);
                                        e.getPlayer().getInventory().addItem(1917);
                                        e.getPlayer().startConversation(new Dialogue()
                                                .addNPC(BARTENDER, HeadE.HAPPY_TALKING, "There you go.")
                                                .addPlayer(HeadE.HAPPY_TALKING, "Thanks, " + name + ""));
                                    }
                                });
                        ops.add("Have you heard any rumours here?")
                                .addPlayer(HeadE.HAPPY_TALKING, "Have you heard any rumours here?")
                                .addNPC(BARTENDER, HeadE.SHAKING_HEAD, "No, it hasn't been very busy lately.");
                    })
            );

    });

    public static NPCClickHandler StankyMorgan = new NPCClickHandler(new Object[]{ STANKY_MORGAN }, e -> {
        switch (e.getOption()) {
            case "Talk-to" -> e.getPlayer().startConversation(new Dialogue()
                    .addPlayer(HeadE.HAPPY_TALKING, "Good day.")
                    .addNPC(STANKY_MORGAN, HeadE.FRUSTRATED, "Is it, indeed.")
                    .addPlayer(HeadE.CONFUSED, "Erm...")
                    .addNPC(STANKY_MORGAN, HeadE.FRUSTRATED, " What's so good about it?")
                    .addPlayer(HeadE.CONFUSED, "Uh...")
                    .addNPC(BARD_ROBERTS, HeadE.FRUSTRATED, " Over here, lass.")
            );
        }
    });

    public static NPCClickHandler Thaki = new NPCClickHandler(new Object[]{ THAKI }, e -> {
        switch (e.getOption()) {
            case "Talk-to" -> {
                e.getNPC().forceTalk("HIC!");
                e.getPlayer().sendMessage("The dwarf hiccups, then burps a foul cloud of stench that turns your stomach. You feel it best to leave before something worse happens.");
            }
        }
    });

    public static NPCClickHandler TheFace = new NPCClickHandler(new Object[]{ THE_FACE }, e -> {
        switch (e.getOption()) {
            case "Talk-to" -> {
                e.getPlayer().startConversation(new Dialogue()
                        .addPlayer(HeadE.HAPPY_TALKING, "Hello"));
                e.getPlayer().sendMessage("She looks through as if you don't exist.");
            }
        }
    });


}

