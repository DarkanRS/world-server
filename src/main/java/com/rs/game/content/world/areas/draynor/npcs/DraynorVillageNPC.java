package com.rs.game.content.world.areas.draynor.npcs;

import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.quest.Quest;
import com.rs.game.World;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Item;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.annotations.ServerStartupEvent;
import com.rs.plugin.handlers.ItemOnNPCHandler;
import com.rs.plugin.handlers.ItemOnObjectHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.plugin.handlers.ObjectClickHandler;
import com.rs.utils.shop.ShopsHandler;

@PluginEventHandler
public class DraynorVillageNPC {
    private static final int BANK_GUARD = 2574;
    private static final int DIANGO = 970;
    private static final int FORTUNATO = 3671;
    private static final int MARKET_GUARD = 2236;
    private static final int MISS_SCHISM = 2634;
    private static final int OLIVIA = 2233;
    private static final int SHADY_STRANGER = 8339;
    private static final int SUSPICIOUS_OUTSIDER = 8436;

    @ServerStartupEvent
    public static void addLoSOverrides() {
        Entity.addLOSOverride( MISS_SCHISM );
    }

    public static NPCClickHandler HandleBankGuard= new NPCClickHandler(new Object[]{ BANK_GUARD }, e -> {
        switch (e.getOption()) {
            case "Talk-to" -> {
                if (Quest.LOVE_STORY.isImplemented())
                    e.getPlayer().startConversation(new Dialogue()
                            .addSimple("Placeholder")
                    );
                else e.getPlayer().startConversation(new Dialogue()
                        .addNPC(BANK_GUARD, HeadE.FRUSTRATED, "Yes?")
                        .addOptions(ops -> {
                            ops.add("Can I deposit my stuff here?.")
                                    .addPlayer(HeadE.HAPPY_TALKING, "Can I deposit my stuff here?")
                                    .addNPC(BANK_GUARD, HeadE.ANGRY, "No. I'm a security guard, not a bank clerk.");

                            ops.add("That wall doesn't look very good.")
                                    .addPlayer(HeadE.HAPPY_TALKING, "That wall doesn't look very good.")
                                    .addNPC(BANK_GUARD, HeadE.ANGRY, "No, it doesn't.")
                                    .addOptions(ops2 -> {
                                        ops2.add("Are you going to tell me what happened?")
                                                .addPlayer(HeadE.CONFUSED, "Are you going to tell me what happened?")
                                                .addNPC(BANK_GUARD, HeadE.FRUSTRATED, "I could do.")
                                                .addPlayer(HeadE.AMAZED, "Okay, go on!")
                                                .addNPC(BANK_GUARD, HeadE.FRUSTRATED, "Someone smashed the wall when they were robbing the bank.")
                                                .addPlayer(HeadE.CONFUSED, "Someone's robbed the bank?")
                                                .addNPC(BANK_GUARD, HeadE.VERY_FRUSTRATED, "Yes.")
                                                .addPlayer(HeadE.AMAZED, "But... was anyone hurt? Did they get anything valuable?")
                                                .addNPC(BANK_GUARD, HeadE.SKEPTICAL_HEAD_SHAKE, "Yes, but we were able to get more staff and mend the wall easily enough. The Bank has already replaced all the stolen items that belonged to customers.")
                                                .addPlayer(HeadE.UPSET, "Oh, good... but the bank staff got hurt?")
                                                .addNPC(BANK_GUARD, HeadE.SKEPTICAL_THINKING, "Yes, but the new ones are just as good.")
                                                .addPlayer(HeadE.ANGRY, "You're not very nice, are you?")
                                                .addNPC(BANK_GUARD, HeadE.LAUGH, "No-one's expecting me to be nice.");

                                        ops2.add("Alright, I'll stop bothering you now.")
                                                .addPlayer(HeadE.HAPPY_TALKING, "Alright, I'll stop bothering you now.")
                                                .addNPC(BANK_GUARD, HeadE.VERY_FRUSTRATED, "Good day, " + e.getPlayer().getPronoun("sir", "ma'am")+ ".");
                                    });

                            ops.add("Sorry, I don't want anything.")
                                    .addPlayer(HeadE.HAPPY_TALKING, "Sorry, I don't want anything.")
                                    .addNPC(BANK_GUARD, HeadE.VERY_FRUSTRATED, "Ok.");
                        })
                );
            }
        }
    });

    public static NPCClickHandler HandleDiango= new NPCClickHandler(new Object[]{ DIANGO }, e -> {
        switch (e.getOption()) {
            case "Trade" -> ShopsHandler.openShop(e.getPlayer(), "diangos_toy_store");
            case "Talk-to" -> e.getPlayer().startConversation(new Dialogue()
                    .addNPC(DIANGO, HeadE.HAPPY_TALKING, "Howdy there partner! Want to see my spinning plates? Or did ya want a holiday item back?")
                    .addOptions(ops -> {
                        ops.add("Spinning plates?")
                                .addPlayer(HeadE.CONFUSED, "Spinning plates?")
                                .addNPC(DIANGO, HeadE.HAPPY_TALKING, "That's right. There's a funny story behind them, their shipment was held up by thieves.")
                                .addNPC(DIANGO, HeadE.LAUGH, "The crate was marked 'Dragon Plates'. Apparently they thought it was some kind of armour, when really it's just a plate with a dragon on it!")
                                .addNext(() ->
                                        ShopsHandler.openShop(e.getPlayer(), "diangos_toy_store"));

                        ops.add("I'd like to check holiday items please!")
                                .addPlayer(HeadE.CALM, "I'd like to check holiday items please!")
                                //TODO
                                //.addNPC(DIANGO, HeadE.HAPPY_TALKING, "Sure thing, let me just see what you're missing.")
                                //.addNext(() -> {
                                //    Open holiday item interface (468)
                                //        })
                                .addNPC(DIANGO, HeadE.SHAKING_HEAD, "I seem to have mislaid my crate of holiday items, Sorry!");

                        ops.add("What else are you selling?")
                                .addPlayer(HeadE.CALM_TALK, "What else are you selling?")
                                .addNext(() ->
                                        ShopsHandler.openShop(e.getPlayer(), "diangos_toy_store"));

                        ops.add("I'm fine, thanks.")
                                .addPlayer(HeadE.HAPPY_TALKING, "I'm fine, thanks.");
                    })
            );
        }
    });

    public static NPCClickHandler HandleFortunato= new NPCClickHandler(new Object[]{ FORTUNATO }, e -> {
        switch (e.getOption()) {
            case "Trade" -> ShopsHandler.openShop(e.getPlayer(), "fortunatos_fine_wine");
            case "Talk-to" -> e.getPlayer().startConversation(new Dialogue()
                    .addPlayer(HeadE.CALM_TALK, "Hello. What are you doing here?")
                    .addNPC(FORTUNATO, HeadE.SKEPTICAL_THINKING, "Can I help you at all?")
                    .addOptions(ops -> {
                        ops.add("Yes, what are you selling?")
                                .addNext(() ->
                                        ShopsHandler.openShop(e.getPlayer(), "fortunatos_fine_wine"));

                        if (Quest.RAG_AND_BONE_MAN.isImplemented() && e.getPlayer().getQuestManager().isComplete(Quest.RAG_AND_BONE_MAN))
                            ops.add("Talk about Rag and Bone Man")
                                    .addNPC(FORTUNATO, HeadE.SCARED, "Oh, you've come back. Please, I haven't got any more vinegar. Give me some time, I beg you.")
                                    .addPlayer(HeadE.CALM, "I wasn't after vinegar today.")
                                    .addNPC(FORTUNATO, HeadE.SKEPTICAL_THINKING, "Oh, wonderful.I will have some in soon if 'he' needs it.");

                        ops.add("Wine merchant, huh?")
                                .addNPC(FORTUNATO, HeadE.HAPPY_TALKING, "Yes, indeed. The finest wines in Misthalin. Care to take a look at my wares?")
                                .addOptions(ops2 -> {
                                    ops2.add("Yes.")
                                            .addNext(() ->
                                                    ShopsHandler.openShop(e.getPlayer(), "fortunatos_fine_wine"));

                                    ops2.add("Not at the moment.");
                                });
                        if (Quest.VAMPYRE_SLAYER.isImplemented() && e.getPlayer().getQuestManager().isComplete(Quest.VAMPYRE_SLAYER))
                            ops.add("Talk about Vampyre Slayer.")
                                    .addNPC(FORTUNATO, HeadE.AMAZED_MILD, "Well done killing that vampyre. Business has certainly improved since the attacks have stopped.")
                                    .addPlayer(HeadE.HAPPY_TALKING, "Happy to help.");
                    })
            );
        }
    });

    public static ItemOnNPCHandler BottleonFortunato = new ItemOnNPCHandler(FORTUNATO, e -> {
        if (e.getItem().getId() != 7921)
            return;
        e.getPlayer().getInventory().deleteItem(7921, 1);
        e.getPlayer().getInventory().addCoins(2);
        e.getPlayer().startConversation(new Dialogue()
                .addNPC(FORTUNATO, HeadE.SKEPTICAL_THINKING, "Ah! I buy wine bottles back if you want to hand them all over.")
        );
    });


    public static NPCClickHandler HandleMissSchism= new NPCClickHandler(new Object[]{ MISS_SCHISM }, e -> {
        switch (e.getOption()) {
            case "Talk-to" -> e.getPlayer().startConversation(new Dialogue()
                    .addNPC(MISS_SCHISM, HeadE.CALM, "Oooh, my dear, have you heard the news?")
                    .addOptions(ops -> {
                        ops.add("Ok, tell me about the news.")
                                .addNPC(MISS_SCHISM, HeadE.UPSET, "It's terrible, absolutely terrible! Those poor people!")
                                .addPlayer(HeadE.CONFUSED, "Ok, yeah.")
                                .addNPC(MISS_SCHISM, HeadE.UPSET, "And who'd have ever thought such a sweet old gentleman would do such a thing?")
                                .addPlayer(HeadE.CONFUSED, "Are we talking about the bank robbery?")
                                .addNPC(MISS_SCHISM, HeadE.UPSET, "Oh yes, my dear. It was terrible! TERRIBLE!")
                                .addNPC(MISS_SCHISM, HeadE.UPSET_SNIFFLE, "But tell me - have you been around here before, or are you new to these parts?")
                                .addOptions(ops2 -> {
                                    ops2.add("I'm quite new.")
                                            .addPlayer(HeadE.HAPPY_TALKING, " I'm quite new.")
                                            .addNPC(MISS_SCHISM, HeadE.UPSET, "Aah, perhaps you missed the excitement. It's that old man in this house here. Do you know him?")
                                            .addPlayer(HeadE.CONFUSED, "Well, I know of him.")
                                            .addNPC(MISS_SCHISM, HeadE.UPSET, "When he first moved here, he didn't bring much. From the window you could see he just had some old furniture and a few dusty ornaments.")
                                            .addNPC(MISS_SCHISM, HeadE.UPSET, "He always seemed so poor. When I went round to collect donations for the Draynor Manor Restoration Fund, he couldn't spare them a penny!")
                                            .addPlayer(HeadE.CONFUSED, "So he's redecorated?")
                                            .addNPC(MISS_SCHISM, HeadE.ANGRY, "Well, just you look in there now!")
                                            .addNPC(MISS_SCHISM, HeadE.ANGRY, "You see? It's full of jewellery and decorations! And all those expensive things appeared just after the bank got robbed.")
                                            .addNPC(MISS_SCHISM, HeadE.ANGRY, "He changed his hat too - he used to wear a scruffy old black thing, but suddenly he was wearing that party hat!")
                                            .addPlayer(HeadE.AMAZED_MILD, "So that's why you're telling people he was the bank robber?")
                                            .addNPC(MISS_SCHISM, HeadE.SKEPTICAL_THINKING, "Oooh, my dear, I'm SURE of it! I went upstairs in his house once, while he was out walking, and do you know what I found?")
                                            .addPlayer(HeadE.CONFUSED, "A sign saying 'Trespassers will be prosecuted'?")
                                            .addNPC(MISS_SCHISM, HeadE.ANGRY, "No, it was a telescope! It was pointing right at the bank! He was spying on the bankers, planning the big robbery!")
                                            .addNPC(MISS_SCHISM, HeadE.SKEPTICAL, "I bet if you go and look through it now, you'll find it's pointing somewhere different now he's finished with the bank.")
                                            .addPlayer(HeadE.CONFUSED, "I'd like to go now.")
                                            .addNPC(MISS_SCHISM, HeadE.SKEPTICAL_THINKING, "Oh, really? Well, do keep an eye on him - I just KNOW he's planning something...");

                                    ops2.add("I've been around here for ages.")
                                            .addPlayer(HeadE.HAPPY_TALKING, " I've been around here for ages.")
                                            .addNPC(MISS_SCHISM, HeadE.UPSET, "Ah, so you'd have seen the changes here. It's that old man in this house here. Do you know him?")
                                            .addPlayer(HeadE.CONFUSED, "Well, I know of him.")
                                            .addNPC(MISS_SCHISM, HeadE.UPSET, "When he first moved here, he didn't bring much. From the window you could see he just had some old furniture and a few dusty ornaments.")
                                            .addNPC(MISS_SCHISM, HeadE.UPSET, "He always seemed so poor. When I went round to collect donations for the Draynor Manor Restoration Fund, he couldn't spare them a penny!")
                                            .addPlayer(HeadE.CONFUSED, "So he's redecorated?")
                                            .addNPC(MISS_SCHISM, HeadE.ANGRY, "Well, just you look in there now!")
                                            .addNPC(MISS_SCHISM, HeadE.ANGRY, "You see? It's full of jewellery and decorations! And all those expensive things appeared just after the bank got robbed.")
                                            .addNPC(MISS_SCHISM, HeadE.ANGRY, "He changed his hat too - he used to wear a scruffy old black thing, but suddenly he was wearing that party hat!")
                                            .addPlayer(HeadE.AMAZED_MILD, "So that's why you're telling people he was the bank robber?")
                                            .addNPC(MISS_SCHISM, HeadE.SKEPTICAL_THINKING, "Oooh, my dear, I'm SURE of it! I went upstairs in his house once, while he was out walking, and do you know what I found?")
                                            .addPlayer(HeadE.CONFUSED, "A sign saying 'Trespassers will be prosecuted'?")
                                            .addNPC(MISS_SCHISM, HeadE.ANGRY, "No, it was a telescope! It was pointing right at the bank! He was spying on the bankers, planning the big robbery!")
                                            .addNPC(MISS_SCHISM, HeadE.SKEPTICAL, "I bet if you go and look through it now, you'll find it's pointing somewhere different now he's finished with the bank.")
                                            .addPlayer(HeadE.CONFUSED, "I'd like to go now.")
                                            .addNPC(MISS_SCHISM, HeadE.SKEPTICAL_THINKING, "Oh, really? Well, do keep an eye on him - I just KNOW he's planning something...");

                                    ops2.add("I've had enough of talking to you.")
                                            .addPlayer(HeadE.CALM_TALK, "I've had enough of talking to you.")
                                            .addNPC(MISS_SCHISM, HeadE.CALM_TALK, "Maybe another time, my dear.");
                                });

                        ops.add("Who are you?")
                                .addPlayer(HeadE.CONFUSED, "Who are you?")
                                .addNPC(MISS_SCHISM, HeadE.CALM_TALK, "I, my dear, am a concerned citizen of Draynor Village. Ever since the Council allowed those farmers to set up their stalls here, we've had a constant flow of thieves and murderers through our fair village.")
                                .addNPC(MISS_SCHISM, HeadE.CALM_TALK, "I decided that someone HAD to stand up and keep an eye on the situation.")
                                .addNPC(MISS_SCHISM, HeadE.CALM_TALK, "I also do voluntary work for the Draynor Manor Restoration Fund. We're campaigning to have Draynor Manor turned into a museum before the wet- rot destroys it completely.")
                                .addPlayer(HeadE.CONFUSED, "Right...");


                        ops.add("I'm not talking to you, you horrible woman.")
                                .addPlayer(HeadE.ANGRY, "I'm not talking to you, you horrible woman.")
                                .addNPC(MISS_SCHISM, HeadE.ANGRY, "Oooh.");

                    })
            );
        }
    });

    public static NPCClickHandler HandleOlivia= new NPCClickHandler(new Object[]{ OLIVIA }, e -> {
        switch (e.getOption()) {
            case "Trade" -> ShopsHandler.openShop(e.getPlayer(), "draynor_seed_market");
            case "Talk-to" -> e.getPlayer().startConversation(new Dialogue()
                    .addNPC(DIANGO, HeadE.HAPPY_TALKING, "Would you like to trade in seeds?")
                    .addOptions(ops -> {
                        ops.add("Yes.")
                                .addNext(() ->
                                        ShopsHandler.openShop(e.getPlayer(), "draynor_seed_market"));

                        ops.add("No.!")
                                .addPlayer(HeadE.CALM, "No, thanks.");

                        ops.add("Where do I get rarer seeds from?")
                                .addPlayer(HeadE.CALM_TALK, "Where do I get rarer seeds from?")
                                .addNPC(OLIVIA, HeadE.CALM_TALK, "The Master Farmers usually carry a few rare seeds around with them, although I don't know if they'd want to part with them for any price to be honest.");
                    })
            );
        }
    });

    public static NPCClickHandler HandleStrangers= new NPCClickHandler(new Object[]{ SHADY_STRANGER, SUSPICIOUS_OUTSIDER }, e -> {
        switch (e.getOption()) {
            case "Talk-to" -> e.getPlayer().startConversation(new Dialogue()
                    .addPlayer(HeadE.CALM_TALK, "Hello. What are you doing here?")
                    .addNPC(e.getNPCId(), HeadE.SKEPTICAL_THINKING, "Err, nothing much, I'm just here on business. Heard Draynor's a nice place to visit.")
            );
        }
    });

    public static ObjectClickHandler HandleTreeGuard = new ObjectClickHandler(new Object[] { 10041 }, e -> {
        switch (e.getOption()) {
            case "Chop down" -> TreeGuardResponse(e.getPlayer());
            case "Talk to" -> {
                if(!e.getPlayer().getInventory().containsItem(2003))
                    e.getPlayer().startConversation(new Dialogue()
                            .addPlayer(HeadE.CALM_TALK, "Hello. What are you doing?")
                            .addNPC(MARKET_GUARD, HeadE.VERY_FRUSTRATED, "Ssshhh! What do you want?")
                            .addPlayer(HeadE.CONFUSED,"Well, it's not every day you see a man up a tree!")
                            .addNPC(MARKET_GUARD, HeadE.VERY_FRUSTRATED, "I'm trying to observe a suspect. Leave me alone!"));
                else {
                    e.getPlayer().startConversation(new Dialogue()
                            .addPlayer(HeadE.HAPPY_TALKING, "Hello?")
                            .addNPC(MARKET_GUARD, HeadE.SKEPTICAL, "Yes, what do you... I say, is that stew for me?")
                            .addOptions(ops -> {
                                ops.add("Ok, take the stew!")
                                        .addPlayer(HeadE.HAPPY_TALKING, "Ok, take the stew!")
                                        .addNPC(MARKET_GUARD,HeadE.HAPPY_TALKING,"Gosh, that's very kind of you! Here, have a few coins for your trouble...")
                                        .addNext(() -> {
                                            e.getPlayer().getInventory().deleteItem(2003, 1);
                                            e.getPlayer().getInventory().addCoins(30);
                                            e.getPlayer().sendMessage("The guard drops the empty bowl out of the tree!");
                                            World.addGroundItem(new Item(1923, 1), Tile.of(3085, 3244, 0));
                                        });
                                ops.add("No, I want to keep this stew!")
                                        .addPlayer(HeadE.SHAKING_HEAD, "No, I want to keep this stew!")
                                        .addNPC(MARKET_GUARD,HeadE.UPSET,"Fair enough. But if you change your mind, I'll probably still be here.");
                            })
                    );
                }
            }
        }
    });

    public static void TreeGuardResponse(Player player) {
        switch (Utils.random(1, 8)) {
            case 1 -> player.npcDialogue(MARKET_GUARD, HeadE.ANGRY,"Ooooch!");
            case 2 -> player.npcDialogue(MARKET_GUARD, HeadE.ANGRY,"Oi!");
            case 3 -> player.npcDialogue(MARKET_GUARD, HeadE.ANGRY,"Ow! That really hurt!");
            case 4 -> player.npcDialogue(MARKET_GUARD, HeadE.ANGRY,"Watch what you're doing with that axe, you nit!");
            case 5 -> player.npcDialogue(MARKET_GUARD, HeadE.ANGRY,"You'll blow my cover! I'm meant to be hidden!");
            case 6 -> player.npcDialogue(MARKET_GUARD, HeadE.ANGRY,"Will you stop that?");
            case 7 -> player.npcDialogue(MARKET_GUARD, HeadE.ANGRY,"Don't draw attention to me!");
            case 8 -> player.npcDialogue(MARKET_GUARD, HeadE.ANGRY,"Hey - gerroff me!");
        }
    }

    public static ItemOnObjectHandler HandleTreeGuardStew = new ItemOnObjectHandler(new Object[] { 10041 }, null, e -> {
        if (e.getItem().getId() != 2003)
            e.getPlayer().startConversation(new Dialogue()
                    .addNPC(MARKET_GUARD, HeadE.ANGRY, "Why the demoninikin are you waving THAT at me?")
                    .addPlayer(HeadE.UPSET, "Sorry, I thought you'd like it.")
                    .addNPC(MARKET_GUARD, HeadE.ANGRY, "Erm... No thanks! Now, if you don't mind, I'm trying to observe this suspect!")
                    .addPlayer(HeadE.UPSET, "Sorry."));
        else {
            e.getPlayer().getInventory().deleteItem(2003, 1);
            e.getPlayer().getInventory().addCoins(30);
            e.getPlayer().startConversation(new Dialogue()
                    .addPlayer(HeadE.HAPPY_TALKING, "Here's some stew.")
                    .addNPC(MARKET_GUARD, HeadE.HAPPY_TALKING, "Gosh, that's very kind of you! Here, have a few coins for your trouble..."));
            e.getPlayer().sendMessage("The guard drops the empty bowl out of the tree!");
            World.addGroundItem(new Item(1923, 1), Tile.of(3085,3244,0));
        }
    });

}
