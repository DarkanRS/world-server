package com.rs.game.content.quests.buyersandcellars.npcs;

import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Tile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class RobinCastle extends Dialogue {
    private static int npcid = 11280;

    public static NPCClickHandler RobinCastle = new NPCClickHandler(new Object[] { 7955, 11268, 11279, 11280 }, new String[] {"Talk-to"}, e -> {
        int questStage = e.getPlayer().getQuestStage(Quest.BUYERS_AND_CELLARS);
        switch (questStage) {
            case 0:
                preQuest(e.getPlayer());
                break;
            case 1, 2:
                someExtraHelp(e.getPlayer());
                break;
            case 3, 4:
                if (e.getNPC().getTile().equals(Tile.of(4762, 5904, 0))){
                    someExtraHelp(e.getPlayer());
                }
                else {
                    stage3(e.getPlayer(), e.getNPC());
                }
                break;
            case 5, 6:
                stage5(e.getPlayer());
                break;
            case 7:
                stage7(e.getPlayer());
                break;
            case 8:
                if (e.getNPC().getTile().equals(Tile.of(4762, 5904, 0))){
                    someExtraHelp(e.getPlayer());
                }
                else
                    stage8(e.getPlayer());
                break;
            default:
                break;
        }
    });

    public static void stage8(Player player) {
        player.startConversation(new Dialogue()
                .addPlayer(HeadE.HAPPY_TALKING, "I stole Father Urhney's chalice!")
                .addNPC(npcid, HeadE.CALM_TALK, "You might want to deliver it, then, I'll see you there.")
        );
    }

    public static void stage7(Player player) {
        player.startConversation(new Dialogue()
                .addPlayer(HeadE.HAPPY_TALKING, "I have Father Urhney's key!")
                .addNPC(npcid, HeadE.CALM_TALK, "Then what are you doing here? Nab the chalice from the old man and get back to the guild.")
        );
    }

    public static void stage5(Player player) {
        player.startConversation(new Dialogue()
                .addPlayer(HeadE.SHAKING_HEAD, "Father Urhney over in the swamp has the chalice in his hut, but I can't get the key off him.")
                .addNPC(npcid, HeadE.CALM_TALK, "You might have to engineer a crisis, then. Don't go setting fire to his house, though! A fire outside his window should do the trick.")
                .addPlayer(HeadE.AMAZED_MILD, "Is that not highly irresponsible?")
                .addNPC(npcid, HeadE.CALM_TALK, "Normally I'd say yes, but that swamp is so marshy there's little danger of burning his house down and rendering him homeless. There should be some nice damp wood in that swamp.")
                .addNext(() ->
                        player.setQuestStage(Quest.BUYERS_AND_CELLARS, 6)
                )
        );
    }

    public static void stage3(Player player, NPC npc) {
        if(npc.getTile().equals(Tile.of(4664, 5904, 0))){
            someExtraHelp(player);
        }
        else
            player.startConversation(new Dialogue()
                    .addNPC(npcid, HeadE.CALM_TALK, "The purple owl croaks at dawn...")
                    .addPlayer(HeadE.CONFUSED, "Um, does it?")
                    .addNPC(npcid, HeadE.CALM_TALK, "Oh, never mind. I've some information for you.")
                    .addOptions(ops -> {
                                ops.add("Go ahead.")

                                        .addNPC(npcid, HeadE.CALM_TALK, "The chalice is no longer being held by the bank. Seems that the owner withdrew it a couple of days ago and wandered off in the direction of Lumbridge Swamp...a wild-haired old man with a bad temper.")
                                        .addPlayer(HeadE.CONFUSED, "Who'd want to live in a swamp?")
                                        .addNPC(npcid, HeadE.CALM_TALK, "Someone who wants to be left alone, I imagine.")
                                        .addPlayer(HeadE.CALM_TALK, "Looks like that's my next stop, anyway.")
                                        .addNPC(npcid, HeadE.CALM_TALK, "No violence, if you please. We're thieves, not muggers, and priests tend to be well in with the gods. Be subtle...if an adventurer can be subtle. See if you can pick his pocket for the key. Good luck.")
                                        .addNext(() ->
                                                player.setQuestStage(Quest.BUYERS_AND_CELLARS, 4)
                                        );


                                ops.add("Not right now.")
                                        .addPlayer(HeadE.SHAKING_HEAD, "Not right now.");
                            }

                    ));
    }

    public static void someExtraHelp(Player player) {
        player.startConversation(new Dialogue()
                .addPlayer(HeadE.CALM_TALK, "Hello there.")
                .addNPC(npcid, HeadE.CALM_TALK, "The Guildmaster wanted me to be on hand in case you needed some more hints on picking pockets. Now, what can I do for you?")
                .addOptions(ops -> {
                    ops.add("I 'm always willing to learn.")
                            .addPlayer(HeadE.CALM_TALK, "I'm always willing to learn.")
                            .addNPC(npcid, HeadE.CALM_TALK, "When you're on the prowl for pickpocketing targets, it should be fairly obvious who's not paying enough attention to the world around them. " +
                                    "Just saunter up to them all casual-like, then dip your hand into their wallets as gently and as neatly as you can. " +
                                    "If you succeed, you'll get some of the contents of their pockets; it not, they'll likely punch you in the face, so be warned. " +
                                    "It stings, and you'll need a moment to gather your wits.")
                            .addPlayer(HeadE.CALM_TALK, "Thanks, Robin.")
                            .addNPC(npcid, HeadE.CALM_TALK, "You can use the training dummy if you'd like, but after a while you'll need to switch to real marks if you want to improve. " +
                                    "Now, what can I do for you?");
                    ops.add("I 've got it, thanks.")
                            .addPlayer(HeadE.CALM_TALK, "I’ve got it, thanks.")
                            .addNPC(npcid, HeadE.CALM_TALK, "You can use the training dummy if you’d like, but after a while you’ll need to switch to real marks if you want to improve. " +
                                    "Now, what can I do for you?");
                    ops.add("Any advice for me?")
                            .addPlayer(HeadE.CALM_TALK, "Any advice for me? The guildmaster says you'll be shadowing me on this operation.")
                            .addNPC(npcid, HeadE.CALM_TALK, "Yes, I'm heading out to the castle shortly to pick up any information that might help you.")
                            .addPlayer(HeadE.CALM_TALK, "Anything I should know?")
                            .addNPC(npcid, HeadE.CALM_TALK, "This caper should be simple enough, since your mark won't be on his guard. " +
                                    "I'll be able to tell you more once I've had a chance to look around, but it should be a matter of finding the chalice's owner, stealing the key, and taking the chalice out of the bank.")
                            .addPlayer(HeadE.CALM_TALK, "See you there, then.");
                    ops.add("Bye for now.")
                            .addPlayer(HeadE.CALM_TALK, "Bye for now.");
                })
        );
    }


    public static void preQuest(Player player) {
        player.startConversation(new Dialogue()
                .addPlayer(HeadE.CALM_TALK, "Hello there.")
                .addNPC(npcid, HeadE.CALM_TALK, "Greetings. I'm Robin, the Guildmaster's assistant. Now, what can I do for you?")
                .addOptions( ops -> {
                    ops.add("That 's an appropriate name.")
                            .addPlayer(HeadE.CALM_TALK, "That's an appropriate name.")
                            .addNPC(npcid, HeadE.CALM_TALK, "Yes, I've never heard that one before. " +
                                    "Still, it's not so bad... If I hadn't been mocked for my name as a lad, I might never have decided in a fit of ironic pique to learn how to rob from the rich and give to the poor.")
                            .addPlayer(HeadE.CALM_TALK, "Are you the poor in question?")
                            .addNPC(npcid, HeadE.CALM_TALK, "Well, I was. " +
                                    "Having given myself the goods stolen from several rich people, I'm now of limited but comfortable means. " +
                                    "Now, what can I do for you?");
                    ops.add("How long have you known the Guildmaster?")
                            .addPlayer(HeadE.CALM_TALK, "How long have you known the Guildmaster?")
                            .addNPC(npcid, HeadE.CALM_TALK, "Oh, some time now. We started in business together when he was a con artist, talking people into handing over their hard-earned valuables with lies and vague promises of reward.")
                            .addNPC(npcid, HeadE.SKEPTICAL_HEAD_SHAKE,  "Obviously, that's all behind us now. Now, what can I do for you?");

                    ops.add("Bye for now.")
                            .addPlayer(HeadE.CALM_TALK, "Bye for now.");
                }));
    }
}
