package com.rs.game.content.world.areas.dwarven_mine.npcs;


import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.game.model.entity.player.Skills;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.utils.shop.ShopsHandler;

@PluginEventHandler
public class DwarvenMineNPC {
    private static final int DwarfGeneralStore = 582;
    private static final int DwarfGuard = 4316;
    private static final int Hura = 4563;
    private static final int Rolad = 1841;

    public static NPCClickHandler Dwarf = new NPCClickHandler(new Object[]{ DwarfGeneralStore }, e -> {
        switch (e.getOption()) {
            case "Trade" -> ShopsHandler.openShop(e.getPlayer(), "dwarven_shopping_store");
            case "Talk-to" -> e.getPlayer().startConversation(new Dialogue()
                    .addNPC(DwarfGeneralStore, HeadE.HAPPY_TALKING, "Can I help you at all?")
                    .addOptions(ops -> {
                        ops.add("Yes please, what are you selling?", () -> ShopsHandler.openShop(e.getPlayer(), "dwarven_shopping_store"));

                        ops.add("No thanks.")
                                .addPlayer(HeadE.SHAKING_HEAD, "No thanks.");
                    })
            );
        }
    });
    public static NPCClickHandler GangMember = new NPCClickHandler(new Object[]{ 1795, 1796, 1797 }, e -> {
        if (e.getOption().equalsIgnoreCase("Talk-to"))
            e.getPlayer().startConversation(new Dialogue()
                    .addNPC(e.getNPCId(), HeadE.FRUSTRATED, "Yeah... whada you want?")
                    .addOptions(ops -> {
                        ops.add("I'm looking for Hammerspike.")
                                .addNPC(e.getNPCId(), HeadE.FRUSTRATED, "Well, he's around here somewhere, just use your eyes, I'm sure you'll get there one day.")

                                .addPlayer(HeadE.CALM_TALK, "Ok, thanks.");
                        ops.add("What're you doing here?")
                                .addNPC(e.getNPCId(), HeadE.FRUSTRATED, "Well, as if it's any of your business, I'm an associate of Hammerspike. He's a great dwarf you know. You could learn a lot from a dwarf like him.")
                                .addPlayer(HeadE.CALM_TALK, "Ok, thanks.");

                        ops.add("Who do you work for?")
                                .addNPC(e.getNPCId(), HeadE.FRUSTRATED, "I have an ongoing contract with Hammerspike, when he gives the word, the hammer starts flying.")
                                .addPlayer(HeadE.CALM_TALK, "Ok, thanks.");
                    })
            );
    });
    public static NPCClickHandler DwarfGuildGuard = new NPCClickHandler(new Object[]{ DwarfGuard }, e -> {
        if (e.getOption().equalsIgnoreCase("Talk-to"))
             e.getPlayer().startConversation(new Dialogue()
                    .addNPC(DwarfGuard, HeadE.HAPPY_TALKING, "Welcome to the Mining Guild. Can I help you with anything?")
                    .addOptions(ops -> {
                        ops.add("What have you got in the guild?")
                                .addPlayer(HeadE.CONFUSED, "What have you got in the guild?")
                                .addNPC(DwarfGuard, HeadE.HAPPY_TALKING, "Ooh, it's WONDERFUL! There are orichalcite, runite and coal rocks, all exclusively for people with at least level 60 mining.")
                                .addNext(() -> {
                                    if(e.getPlayer().getSkills().getLevel(Skills.MINING) >= 60)
                                        e.getPlayer().startConversation(new Dialogue()
                                                .addPlayer(HeadE.SAD_MILD_LOOK_DOWN, "It's a good thing I have level " + e.getPlayer().getSkills().getLevel(Skills.MINING) + " Mining.")
                                                .addNPC(DwarfGuard, HeadE.HAPPY_TALKING, "Yes, that's amazing!")
                                        );
                                        else
                                        e.getPlayer().startConversation(new Dialogue()
                                                .addPlayer(HeadE.SAD_MILD_LOOK_DOWN, "So you won't let me go in there?")
                                                .addNPC(DwarfGuard, HeadE.HAPPY_TALKING, "Sorry, but rules are rules. Go do some more training first. Can I help you with anything else?"));
                                        }
                                );

                        ops.add("What do you dwarves do with the ore you mine?")
                                .addPlayer(HeadE.CONFUSED, "What do you dwarves do with the ore you mine?")
                                .addNPC(DwarfGuard, HeadE.LAUGH, "What do you think? We smelt it into bars, smith the metal to make armour and weapons, then we exchange them for goods and services.")
                                .addPlayer(HeadE.CONFUSED, "I don't see many dwarves selling armour or weapons here.")
                                .addNPC(DwarfGuard,HeadE.SHAKING_HEAD, "No, this is only a mining outpost. We dwarves don't much like to settle in human cities. Most of the ore is carted off to Keldagrim, the great dwarven city. They've got a special blast furnace up there - it makes smelting the ore so much easier.")
                                .addNPC(DwarfGuard, HeadE.HAPPY_TALKING, "There are plenty of dwarven traders working in Keldagrim.")
                                .addNPC(DwarfGuard, HeadE.HAPPY_TALKING, "Anyway, can I help you with anything else?");

                        ops.add("No thanks, I'm fine.")
                                .addPlayer(HeadE.SHAKING_HEAD, "No thanks, I'm fine.");
                    })
            );
    });
    public static NPCClickHandler HandleHura = new NPCClickHandler(new Object[]{ Hura }, e -> {
        switch (e.getOption()) {
            case "Trade" -> ShopsHandler.openShop(e.getPlayer(), "dwarven_mine_crossbow_shop");
            case "Talk-to" -> e.getPlayer().startConversation(new Dialogue()
                    .addNPC(Hura, HeadE.HAPPY_TALKING, "'Ello " + e.getPlayer().getDisplayName() + ".")
                    .addPlayer(HeadE.CONFUSED, "Hello, what's that you've got there?")
                    .addNPC(Hura, HeadE.HAPPY_TALKING, "A crossbow, are you interested?")
                    .addPlayer(HeadE.SKEPTICAL, "Maybe, are they any good?")
                    .addNPC(Hura, HeadE.LAUGH, "Are they any good?! They're dwarven engineering at its best!")
                    .addOptions(ops -> {
                        ops.add("How do I make one for myself?.")
                                .addPlayer(HeadE.SKEPTICAL_THINKING, "How do I make one for myself?")
                                .addNPC(Hura, HeadE.HAPPY_TALKING, "Well, firstly you'll need to chop yourself some wood, then use a knife on the wood to whittle out a nice crossbow stock like these here.")
                                .addPlayer(HeadE.HAPPY_TALKING, "Wood fletched into stock... check.")
                                .addNPC(Hura, HeadE.HAPPY_TALKING, "Then get yourself some metal and a hammer and smith yourself some limbs for the bow, mind that you use the right metals and woods though as some wood is too light to use with some metal and vice versa.")
                                .addPlayer(HeadE.CONFUSED, "Which goes with which?")
                                .addNPC(Hura, HeadE.HAPPY_TALKING, "Wood and Bronze as they're basic materials, Oak and Blurite, Willow and Iron, Steel and Teak, Mithril and Maple, Adamantite and Mahogany and finally Runite and Yew.")
                                .addPlayer(HeadE.CONFUSED, "Ok, so I have my stock and a pair of limbs... what now?")
                                .addNPC(Hura, HeadE.HAPPY_TALKING, "Simply take a hammer and smack the limbs firmly onto the stock. You'll then need a string, only they're not the same as normal bows.")
                                .addNPC(Hura, HeadE.HAPPY_TALKING, "You'll need to dry some large animal's meat to get sinew, then spin that on a spinning wheel, it's the only thing we've found to be strong enough for a crossbow.")
                                .addOptions(ops2 ->{
                                    ops2.add("What about magic logs?")
                                            .addPlayer(HeadE.CONFUSED, "What about magic logs?")
                                            .addNPC(Hura, HeadE.SKEPTICAL_THINKING, "Well.. I don't rightly know... us dwarves don't work with magic, we prefer gold and rock. Much more stable.")
                                            .addNPC(Hura, HeadE.SKEPTICAL_HEAD_SHAKE, "I guess you could ask the humans at the rangers guild to see if they can do something but I don't want anything to do with it!")
                                            .addPlayer(HeadE.HAPPY_TALKING, "Thanks for telling me. Bye!")
                                            .addNPC(Hura, HeadE.HAPPY_TALKING, "Take care, straight shooting.");

                                    ops2.add("Thanks for telling me. Bye!")
                                            .addPlayer(HeadE.HAPPY_TALKING, "Thanks for telling me. Bye!")
                                            .addNPC(Hura, HeadE.HAPPY_TALKING, "Take care, straight shooting.");
                                });

                        ops.add("What about ammo?")
                                        .addPlayer(HeadE.CONFUSED, "What about ammo?")
                                .addNPC(Hura, HeadE.HAPPY_TALKING, "You can smith yourself lots of different bolts, don't forget to flight them with feathers like you do arrows though.")
                                .addNPC(Hura, HeadE.HAPPY_TALKING, "You can poison any untipped bolt but there's also the option of tipping them with gems then enchanting them with runes. This can have some pretty powerful effects.")
                                .addPlayer(HeadE.SAD_CRYING, "Oh my poor bank, how will I store all those?!")
                                .addNPC(Hura, HeadE.SKEPTICAL_THINKING ,"Find Hirko in Keldagrim, he also sells crossbow parts and I'm sure he has something you can use to store bolts in.")
                                .addPlayer(HeadE.HAPPY_TALKING, "Thanks for the info.");

                        ops.add("Can I buy some crossbow supplies?", () -> ShopsHandler.openShop(e.getPlayer(), "dwarven_mine_crossbow_shop"));

                        ops.add("Thanks for telling me. Bye!")
                                .addPlayer(HeadE.HAPPY_TALKING, "Thanks for telling me. Bye!")
                                .addNPC(Hura, HeadE.HAPPY_TALKING, "Take care, straight shooting.");
                    })
            );
        }
    });

    public static NPCClickHandler handleRolad = new NPCClickHandler(new Object[]{ Rolad }, e -> {
        if(e.getOption().equalsIgnoreCase("Talk-to"))
            e.getPlayer().startConversation(new Dialogue()
                    .addNPC(DwarfGeneralStore, HeadE.FRUSTRATED, "Can you leave me alone please? I'm trying to study.")
            );
    });
}

