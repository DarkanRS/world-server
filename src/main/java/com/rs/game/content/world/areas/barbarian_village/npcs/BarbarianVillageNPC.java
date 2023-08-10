package com.rs.game.content.world.areas.barbarian_village.npcs;

import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.quest.Quest;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class BarbarianVillageNPC {
    private static final int CHEIFTAIN_GUNTHOR = 2876;
    private static final int GUDRUN = 2864;
    private static final int HAAKON = 3090;
    private static final int KJELL = 2872;
    private static final int LITARA = 4376;
    private static final int TASSIE_SLIPCAST = 1793;

    public static NPCClickHandler talkToBarbarians = new NPCClickHandler(new Object[]{3246, 3247, 3248, 3249, 3250, 3251, 3252, 3253, 3255, 3256, 3257, 3258, 3259, 3260, 3261, 3262, 3263}, new String[] { "Talk-to" }, e -> {
        String[] responses = new String[]{
                "Ah, you come for fight, ja?!",
                "You look funny!",
                "Wanna fight?",
                "Grrr!",
                "What you want?",
                "Go Away!"
        };
        e.getPlayer().startConversation(new Dialogue()
                .addNPC(e.getNPCId(), HeadE.VERY_FRUSTRATED, responses[(Utils.random(1,6))])
                .addNext(() -> e.getNPC().setTarget(e.getPlayer()))
        );
    });

    public static NPCClickHandler talkToGunthor = new NPCClickHandler(new Object[]{ CHEIFTAIN_GUNTHOR }, new String[] { "Talk-to" }, e -> {
        String[] responses = new String[]{
                "Ah, you've come for fight!",
                "You look funny!",
                "Wanna fight?",
                "Grrr!",
                "What you want?",
                "Go Away!"
        };
        e.getPlayer().startConversation(new Dialogue()
                .addNPC(CHEIFTAIN_GUNTHOR, HeadE.VERY_FRUSTRATED, responses[(Utils.random(1,6))])
                .addNext(() -> e.getNPC().setTarget(e.getPlayer()))
        );
    });


    public static NPCClickHandler HandleGudrun = new NPCClickHandler(new Object[]{ GUDRUN }, new String[] { "Talk-to" }, e -> {
        if (!Quest.GUNNARS_GROUND.isImplemented() || !e.getPlayer().getQuestManager().isComplete(Quest.GUNNARS_GROUND))
            e.getPlayer().startConversation(new Dialogue()
                    .addNPC(GUDRUN, HeadE.SKEPTICAL, "Can I help you, stranger?")
                    .addNPC(KJELL, HeadE.ANGRY, "Why are you talking to that outerlander?")
                    .addNPC(GUDRUN, HeadE.ANGRY, "It's none of your business, Kjell! Just guard the hut!")
                    .addNPC(GUDRUN, HeadE.CALM_TALK, "Sorry about that. Did you want something?")
                    .addOptions(ops -> {
                        ops.add("What is this place?")
                                .addPlayer(HeadE.CALM_TALK, "What is this place?")
                                .addNPC(GUDRUN, HeadE.SKEPTICAL, "Outerlanders call this the barbarian village. It doesn't have a name because... it's complicated.")
                                .addNPC(GUDRUN, HeadE.CALM_TALK, "If you wish to know more, you should talk to Hunding. He's up in the tower at the east entrance.");

                        ops.add("Who are you?")
                                .addPlayer(HeadE.CALM_TALK, "Who are you?")
                                .addNPC(GUDRUN, HeadE.CALM_TALK, "My name is Gudrun. My father, Gunthor, is chieftain of the village.");


                        ops.add("Goodbye.")
                                .addPlayer(HeadE.CALM_TALK, "Goodbye.")
                                .addNPC(GUDRUN, HeadE.CALM_TALK, "Goodbye.");
                    })
            );
        if (Quest.GUNNARS_GROUND.isImplemented() && e.getPlayer().getQuestManager().isComplete(Quest.GUNNARS_GROUND)) {
            e.getPlayer().startConversation(new Dialogue()
                    .addNPC(GUDRUN, HeadE.HAPPY_TALKING, "Hello!")

                    .addOptions(ops -> {
                        ops.add("I want to ask you something.")
                                .addNPC(GUDRUN, HeadE.SKEPTICAL, "Of course, what is it?")
                                .addOptions(ops2 -> {
                                    ops2.add("How are things with Dororan?")
                                            .addNPC(GUDRUN, HeadE.CALM_TALK, "I really like him. He's funny, vulnerable and nothing like my people.")
                                            .addPlayer(HeadE.CONFUSED, "You're going to stay together then?")
                                            .addNPC(GUDRUN, HeadE.HAPPY_TALKING, "Of course!");

                                    ops2.add("Where did this house come from?")
                                            .addNPC(GUDRUN, HeadE.CALM_TALK, "I don't know. Papa said the previous owners left it to him. I don't know why they would do that.")
                                            .addPlayer(HeadE.CONFUSED, "Do you have a theory?")
                                            .addNPC(GUDRUN, HeadE.HAPPY_TALKING, "Gunnar always said 'A warrior does not barter; he simply takes!'. I think papa bought the house, but doesn't want anyone to know.");

                                    ops2.add("Did you like your present?")
                                            .addNPC(GUDRUN, HeadE.HAPPY_TALKING, "Look at this bracelet he got for me! 'With beauty blessed.' When I recited that line to papa, it took my breath away.");

                                    ops2.add("Goodbye.")
                                            .addNPC(GUDRUN, HeadE.CALM_TALK, "Goodbye.");
                                });

                        ops.add("Just passing through.")
                                .addNPC(GUDRUN, HeadE.CALM_TALK, "Goodbye.");

                    })
            );
        }
    });

    public static NPCClickHandler talkToHaakon = new NPCClickHandler(new Object[]{ HAAKON }, new String[] { "Talk-to" }, e -> {
        if (!Quest.GUNNARS_GROUND.isImplemented() || !e.getPlayer().getQuestManager().isComplete(Quest.GUNNARS_GROUND))
            e.getPlayer().startConversation(new Dialogue()
                    .addNPC(HAAKON, HeadE.EVIL_LAUGH, "I am Haakon, champion of this village. Do you seek to challenge me?")
                    .addOptions(ops -> {
                        ops.add("I challenge you!")
                                .addNPC(HAAKON, HeadE.EVIL_LAUGH, "Make peace with your god, outerlander!")
                                .addNext(() -> e.getNPC().setTarget(e.getPlayer()));

                        ops.add("Er, no.")
                                .addPlayer(HeadE.SHAKING_HEAD, "Er, no.");
                    }));
        if (Quest.GUNNARS_GROUND.isImplemented() && e.getPlayer().getQuestManager().isComplete(Quest.GUNNARS_GROUND)) {
            e.getPlayer().startConversation(new Dialogue()
                    .addNPC(HAAKON, HeadE.EVIL_LAUGH, "I am Haakon, champion of this village. Do you seek to challenge me?")
                    .addOptions(ops -> {
                        ops.add("I challenge you!")
                                .addNPC(HAAKON, HeadE.EVIL_LAUGH, "I am Haakon, champion of this village. Do you seek to challenge me?")
                                .addNext(() -> e.getNPC().setTarget(e.getPlayer()));

                        ops.add("Are you glad the village has settled finally?")
                                .addPlayer(HeadE.SHAKING_HEAD, "I do as my chieftain commands. I respect his wisdom.");

                        ops.add("Er, no.")
                                .addPlayer(HeadE.SHAKING_HEAD, "Er, no.");
                    })
            );
        }
    });

    public static NPCClickHandler HandleKjell = new NPCClickHandler(new Object[]{ KJELL }, new String[] { "Talk-to" }, e -> {
        String[] responses = new String[]{
                "...love you like the stars above...",
                "...fall for pretty strangers...",
                "...but I'd do anything for you...",
                "...there's a place for us...",
                "...you exploded into my heart...",
                "...I dreamed your dream for you...",
                "...fall for chains of gold...",
                "...when you gonna realise..."
        };
        if (!Quest.GUNNARS_GROUND.isImplemented() || !e.getPlayer().getQuestManager().isComplete(Quest.GUNNARS_GROUND))
            e.getPlayer().startConversation(new Dialogue()
                    .addNPC(KJELL, HeadE.ANGRY, "Get out of here, outerlander!")
                    .addOptions(ops -> {
                        ops.add("What is this place?")
                                .addPlayer(HeadE.CALM_TALK, "What is this place?")
                                .addNPC(KJELL, HeadE.ANGRY, "The barbarian village. Go away.");

                        ops.add("Who are you?")
                                .addPlayer(HeadE.CALM_TALK, "Who are you?")
                                .addNPC(KJELL, HeadE.ANGRY, "My name is Kjell. Go away.");

                        ops.add("What is in this hut that you're guarding?")
                                .addPlayer(HeadE.CALM_TALK, "What is in this hut that you're guarding?")
                                .addNPC(KJELL, HeadE.ANGRY, "Nothing yet. Once there is, no one will get in or out! Go away!");

                        ops.add("Goodbye then.?")
                                .addPlayer(HeadE.CONFUSED, "Goodbye then.");
                    }));
        if (Quest.GUNNARS_GROUND.isImplemented() && e.getPlayer().getQuestManager().isComplete(Quest.GUNNARS_GROUND)) {
            e.getPlayer().startConversation(new Dialogue()
                    .addNPC(KJELL, HeadE.VERY_FRUSTRATED, responses[(Utils.random(1,8))])
                    .addNPC(KJELL, HeadE.ANGRY, "Blast!")
                    .addOptions(ops -> {
                        ops.add("Having trouble there?")
                                .addPlayer(HeadE.HAPPY_TALKING,"Having trouble there?")
                                .addNPC(KJELL, HeadE.VERY_FRUSTRATED, "I don't need the advice of an outerlander.")
                                .addOptions(ops2-> {
                                    ops2.add("This music isn't very restful.")
                                            .addPlayer(HeadE.HAPPY_TALKING,"This music isn't very restful.")
                                            .addNPC(KJELL, HeadE.ANGRY, "Get out of here!");

                                    ops2.add("Maybe you should take some lessons.")
                                            .addPlayer(HeadE.HAPPY_TALKING,"Maybe you should take some lessons.")
                                            .addNPC(KJELL, HeadE.ANGRY, "Get out of here!");

                                    ops2.add("I'll leave you in peace.")
                                            .addPlayer(HeadE.HAPPY_TALKING,"I'll leave you in peace.")
                                            .addNPC(KJELL, HeadE.ANGRY, "Get out of here!");
                                });

                        ops.add("I'll leave you in peace.")
                                .addPlayer(HeadE.HAPPY_TALKING, "I'll leave you in peace.");

                    })
            );
        }

    });

    public static NPCClickHandler HandleLitara = new NPCClickHandler(new Object[]{ LITARA }, new String[] { "Talk-to" }, e -> {
                e.getPlayer().startConversation(new Dialogue()
                        .addNPC(LITARA, HeadE.SKEPTICAL, "Hello there. You look lost - are you okay?")
                        .addOptions(ops -> {
                            ops.add("I'm looking for a stronghold, or something.")
                                    .addPlayer(HeadE.CALM_TALK, "I'm looking for a stronghold, or something.")
                                    .addNPC(LITARA, HeadE.CALM_TALK, "Ahh, the Stronghold of Security. It's down there.")
                                    .addPlayer(HeadE.NERVOUS, "Looks kind of...deep and dark.")
                                    .addNPC(LITARA, HeadE.SKEPTICAL_THINKING, "Yeah, tell that to my brother. He still hasn't come back.")
                                    .addPlayer(HeadE.CONFUSED, "Your brother?")
                                    .addNPC(LITARA, HeadE.SAD_MILD, "He's a explorer too. When the miner fell down that hole he'd made and came back babbling about treasure, my brother went to explore. No one has seen him since.")
                                    .addPlayer(HeadE.SAD_MILD_LOOK_DOWN, "Oh, that's not good.")
                                    .addNPC(LITARA, HeadE.SKEPTICAL_THINKING, "Lots of people have been down there, but none of them have seen him. Let me know if you do, will you?\n")
                                    .addPlayer(HeadE.HAPPY_TALKING, "I'll certainly keep my eyes open.");

                            ops.add("I'm fine, just passing through.")
                                    .addPlayer(HeadE.CALM_TALK, "I'm fine, just passing through.");
                        })
                );

    });

    public static NPCClickHandler HandleTassie= new NPCClickHandler(new Object[]{ TASSIE_SLIPCAST }, new String[] { "Talk-to" }, e -> {
         e.getPlayer().startConversation(new Dialogue()
                    .addNPC(TASSIE_SLIPCAST, HeadE.HAPPY_TALKING, "Please feel free to use the pottery wheel, I won't be using it all the time. Put your pots in the kiln when you've made one.")
                    .addNPC(TASSIE_SLIPCAST, HeadE.CALM_TALK, "And make sure you tidy up after yourself!")
            );
    });

}
