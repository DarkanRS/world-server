package com.rs.game.content.quests.whatliesbelow.npcs;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.engine.quest.Quest;
import com.rs.game.content.achievements.AchievementSystemDialogue;
import com.rs.game.content.achievements.SetReward;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class RatBurgiss extends Conversation {
    private static final int ID = 5833;

    public static NPCClickHandler handleRatBurgiss = new NPCClickHandler(new Object[] { ID }, e -> e.getPlayer().startConversation(new RatBurgiss(e.getPlayer())));

    public RatBurgiss(Player player) {
        super(player);
        addOptions(ops -> {
            ops.add("About the Achievement System...", new AchievementSystemDialogue(player, ID, SetReward.VARROCK_ARMOR).getStart());
            if (!player.isQuestStarted(Quest.WHAT_LIES_BELOW) && Quest.WHAT_LIES_BELOW.meetsReqs(player)) {
                Dialogue startQuestChain = new Dialogue()
                        .addPlayer(HeadE.CHEERFUL, "Of course! Tell me what you need me to do.")
                        .addNPC(ID, HeadE.CHEERFUL, "Right, now I heard those outlaws say something about having a small campsite somewhere to the west of the Grand Exchange. They headed off to the north-west of here, taking five pages with them.")
                        .addNPC(ID, HeadE.CHEERFUL, "Kill the outlaws and get those papers back from them for me. Here's a folder in which you can put the pages. Be careful, though; those outlaws are tough.", () -> {
                            player.getInventory().addItem(11003);
                            player.setQuestStage(Quest.WHAT_LIES_BELOW, 1);
                        })
                        .addNPC(ID, HeadE.CHEERFUL, "When you find all 5 pages, put them in the folder and bring them back to me!")
                        .addPlayer(HeadE.CHEERFUL, "Don't worry, Ratty! I won't let you down!")
                        .addNPC(ID, HeadE.SAD_MILD, "...")
                        .getHead();

                ops.add("Hello there!")
                        .addPlayer(HeadE.CHEERFUL, "Hello there!")
                        .addNPC(ID, HeadE.SAD_MILD, "Oh, hello. I'm Rat.")
                        .addPlayer(HeadE.CONFUSED, "You're a what?")
                        .addNPC(ID, HeadE.SAD_MILD, "No, no. My name is Rat. Rat Burgiss.")
                        .addPlayer(HeadE.CHEERFUL, "Ohhhhh, well, what's up, Ratty?")
                        .addNPC(ID, HeadE.SAD_MILD, "It's Rat, thank you. And, I uhh...heh...I seem to be in a bit of trouble here, as you can probably see.")
                        .addPlayer(HeadE.CONFUSED, "Why, what seems to be the matter?")
                        .addNPC(ID, HeadE.SKEPTICAL, "Well, I'm a trader by nature and I was on the way to Varrock with my cart here when I was set upon by outlaws! They ransacked my cart and stole some very important papers that I must get back.")
                        .addOptions(ops1 -> {
                            ops1.add("That's awful! You're lucky to be alive!")
                                    .addPlayer(HeadE.AMAZED, "That's awful! You're lucky to be alive!")
                                    .addNPC(ID, HeadE.SAD_MILD, "Yes, I know! I don't know how I survived.")
                                    .addPlayer(HeadE.CALM_TALK, "Maybe you weren't worth killing?")
                                    .addNPC(ID, HeadE.ANGRY, "...")
                                    .addNPC(ID, HeadE.ANGRY, "Look, do you want to help me or not?")
                                    .addQuestStart(Quest.WHAT_LIES_BELOW)
                                    .addNext(startQuestChain);

                            ops1.add("The papers were the only valuable thing you had?")
                                    .addPlayer(HeadE.CONFUSED, "The papers were the only valuable thing you had?")
                                    .addNPC(ID, HeadE.CONFUSED, "Uh...yes. I guess so.")
                                    .addPlayer(HeadE.CONFUSED, "So you don't sell anything worth having?")
                                    .addNPC(ID, HeadE.CONFUSED, "Ah! Uhh...no, not really.")
                                    .addPlayer(HeadE.CONFUSED, "You're not much of a trader are you?")
                                    .addNPC(ID, HeadE.ANGRY, "No...I mean yes... Look, that's beside the point.")
                                    .addNPC(ID, HeadE.SAD_MILD, "Can you help me?")
                                    .addQuestStart(Quest.WHAT_LIES_BELOW)
                                    .addNext(startQuestChain);

                            ops1.add("Shall I get them back for you?")
                                    .addPlayer(HeadE.CONFUSED, "Shall I get them back for you?")
                                    .addNPC(ID, HeadE.CONFUSED, "You mean you want to help?")
                                    .addQuestStart(Quest.WHAT_LIES_BELOW)
                                    .addNext(startQuestChain);

                            ops1.add("Oh dear. Well, I hope you get to Varrock okay!")
                                    .addPlayer(HeadE.CALM_TALK, "Oh dear. Well, I hope you get to Varrock okay!")
                                    .addNPC(ID, HeadE.CONFUSED, "Uh...so you can't help me?")
                                    .addPlayer(HeadE.CALM_TALK, "Sorry. I'm a bit busy right now.")
                                    .addNPC(ID, HeadE.CONFUSED, "Don't you want another quest?")
                                    .addPlayer(HeadE.CALM_TALK, "No thanks. I'm too busy right now.")
                                    .addNPC(ID, HeadE.ANGRY, "Well, fine!")
                                    .addPlayer(HeadE.ANGRY, "Fine!")
                                    .addNPC(ID, HeadE.ANGRY, "Good!")
                                    .addPlayer(HeadE.ANGRY, "Good!!")
                                    .addNPC(ID, HeadE.ANGRY, "So go then!")
                                    .addPlayer(HeadE.ANGRY, "I'm going!")
                                    .addNPC(ID, HeadE.ANGRY, "Go then!")
                                    .addPlayer(HeadE.ANGRY, "I'm gone!");
                        });
            }
        });
        create();
    }
}
