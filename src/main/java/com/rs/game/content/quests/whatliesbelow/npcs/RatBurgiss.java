package com.rs.game.content.quests.whatliesbelow.npcs;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.quest.Quest;
import com.rs.game.content.achievements.AchievementSystemDialogue;
import com.rs.game.content.achievements.SetReward;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class RatBurgiss extends Conversation {
    private static final int ID = 5833;

    public static NPCClickHandler handleRatBurgiss = new NPCClickHandler(new Object[]{ID}, e -> e.getPlayer().startConversation(new RatBurgiss(e.getPlayer())));

    public RatBurgiss(Player player) {
        super(player);
        addOptions(ops -> {
            ops.add("About the Achievement System...", new AchievementSystemDialogue(player, ID, SetReward.VARROCK_ARMOR).getStart());

            if (!player.isQuestComplete(Quest.WHAT_LIES_BELOW) && Quest.WHAT_LIES_BELOW.meetsReqs(player)) {
                ops.add("Hello there!")
                        .addPlayer(HeadE.CHEERFUL, "Hello there!")
                        .addNext(getWhatLiesBelowDialogue(player).getHead());
            }
        });
        create();
    }

    private Dialogue getWhatLiesBelowDialogue(Player player) {
        return switch (player.getQuestStage(Quest.WHAT_LIES_BELOW)) {
            case 0 -> {
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

                yield new Dialogue().addNPC(ID, HeadE.SAD_MILD, "Oh, hello. I'm Rat.")
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

            case 1 -> {
                if (player.getInventory().containsItem(11007)) {
                    yield new Dialogue()
                            .addNPC(ID, HeadE.CHEERFUL, "Ah, hello again.")
                            .addPlayer(HeadE.CHEERFUL, "I got your pages back!")
                            .addNPC(ID, HeadE.CHEERFUL, "Excellent! I knew you could help! Let me take those from you, there.")
                            .addNPC(ID, HeadE.CHEERFUL, "Now, I liked the way you handled yourself on that last little 'mission' I gave you there, so I'm going to let you in on a little secret!")
                            .addPlayer(HeadE.CHEERFUL, "Wait! Wait! Let me guess! You're actually a rich prince in disguise who wants to help poor people like me!?")
                            .addNPC(ID, HeadE.CONFUSED, "Uhhh...no. No, that's not it. You know, on second thought, I think I'll keep my secret for now. Look, instead, you can do another job for me.")
                            .addPlayer(HeadE.SAD_MILD, "All work and no play makes " + player.getDisplayName() + " a dull adventurer!")
                            .addNPC(ID, HeadE.CALM_TALK, "Yes, well, I'm sure that may be the case. However, what I want you to do is take this letter to someone for me. It's in a different language so, trust me, you won't be able to read it.")
                            .addNPC(ID, HeadE.CALM_TALK, "Take it to a wizard named Surok Magis who resides in the Varrock Palace Library. I'll see about some sort of reward for your work when I get myself sorted out here.")
                            .addPlayer(HeadE.CHEERFUL, "Letter. Wizard. Varrock. Got it!", () -> {
                                player.getInventory().deleteItem(11007, 1);
                                player.getInventory().addItemDrop(11009, 1);
                                player.setQuestStage(Quest.WHAT_LIES_BELOW, 2);
                            })
                            .addNPC(ID, HeadE.CHEERFUL, "Yes, good luck then.");
                } else
                    yield new Dialogue()
                            .addNPC(ID, HeadE.CHEERFUL, "Hello again! How are things going?")
                            .addPlayer(HeadE.CONFUSED, "I can't remember what I am supposed to do!")
                            .addNPC(ID, HeadE.CHEERFUL, "Head north-west from here into the forest west of the Grand Exchange. Find the camp of outlaws and kill them.")
                            .addNPC(ID, HeadE.CHEERFUL, "Collect the pages of my document for me and put them into the folder I gave you. When the folder contains five pages, bring it to me!")
                            .addNextIf(() -> !player.containsAnyItems(11003, 11006, 11007), new Dialogue()
                                    .addPlayer(HeadE.SAD, "I lost the folder you gave me. Do you have another one?")
                                    .addNPC(ID, HeadE.CHEERFUL, "Sure. Here you go. I'll add it to your account.", () -> player.getInventory().addItem(11003))
                                    .addPlayer(HeadE.AMAZED, "My account? Am I in debt?")
                                    .addNPC(ID, HeadE.CHEERFUL, "No, it's just the way you're standing."))
                            .addPlayer(HeadE.CHEERFUL, "Okay, thanks!");
            }

            case 2 -> new Dialogue()
                    .addNPC(ID, HeadE.CHEERFUL, "Ah, hello. How is your task going?")
                    .addNextIf(() -> !player.containsItem(11009), new Dialogue()
                            .addPlayer(HeadE.SAD, "I think I lost that letter you gave me!")
                            .addNPC(ID, HeadE.CHEERFUL, "Goodness me! Not much of a messenger, are you? Here's another one; try not to lose it this time! I've charged the parchment to your account.", () -> player.getInventory().addItem(11009))
                            .addPlayer(HeadE.CONFUSED, "Will you take a check?")
                            .addNPC(ID, HeadE.CHEERFUL, "No thanks. I prefer tartan."))
                    .addPlayer(HeadE.CONFUSED, "What am I doing again?")
                    .addNPC(ID, HeadE.CALM_TALK, "Take that letter I gave you to Surok Magis, the wizard found in the Varrock Palace Library.")
                    .addPlayer(HeadE.CHEERFUL, "Oh yes! It's all flooding back now!")
                    .addNPC(ID, HeadE.CALM_TALK, "I see. Well, try not to drown yourself with all that brain usage.");

            case 3, 4 -> new Dialogue()
                    .addNPC(ID, HeadE.CHEERFUL, "Ah, " + player.getDisplayName() + "! Good to see you! I heard you got my letter to Surok. Well done!")
                    .addPlayer(HeadE.CHEERFUL, "That's right! I did your leg work. Now, how about a reward?")
                    .addNPC(ID, HeadE.CHEERFUL, "Yes, of course! I'd be happy to give you a reward, but I have a lot on my mind with...you know...trader...stuff!")
                    .addPlayer(HeadE.CONFUSED, "Indeed!");

            case 5 -> {
                Dialogue giveLetter = new Dialogue()
                        .addNextIf(() -> !player.getInventory().containsItem(11010), new Dialogue()
                                .addPlayer(HeadE.SAD_MILD, "Oh, wait! I've lost the letter! I guess I better go and get another one for you!")
                                .addStop())
                        .addNPC(ID, HeadE.SAD_MILD, "This letter is treasonous! This does indeed confirm my worst fears. It is time I let you into my secret and hopefully this will answer any questions you may have.")
                        .addPlayer(HeadE.CALM_TALK, "Okay. Go on.")
                        .addNPC(ID, HeadE.CALM_TALK, "I am not really a trader. I am the Commander of the Varrock Palace Secret Guard. VPSG for short.")
                        .addPlayer(HeadE.CALM_TALK, "Okay, I had a feeling you weren't a real trader due to the fact that you had nothing to sell! So why the secrecy?")
                        .addNPC(ID, HeadE.CALM_TALK, "I'm just getting to that. A short while ago, we received word that Surok had discovered a powerful mind-control spell and intended to use it on King Roald himself!")
                        .addNPC(ID, HeadE.CALM_TALK, "He could control the whole kingdom that way!")
                        .addPlayer(HeadE.AMAZED, "I think I can believe that. Surok's not the nicest person in Misthalin.")
                        .addNPC(ID, HeadE.CALM_TALK, "Yes, but until now, the spell has been useless to him as he is currently under guard at the palace and not allowed to leave. He could not get the tools for the spell because if he left the palace, he would be arrested.")
                        .addPlayer(HeadE.SAD_MILD, "Uh oh! I think I may have helped him by mistake, here. He promised me a big reward if I collected some items for him...but he said it was for a spell to make gold!")
                        .addNPC(ID, HeadE.CALM_TALK, "Yes, we heard that somehow Surok had obtained the things he needed but we were not sure how. I thought it might have been you.")
                        .addNPC(ID, HeadE.CALM_TALK, "However, I assumed you did not know of his plans; that is why you weren't arrested!")
                        .addPlayer(HeadE.CONFUSED, "Thank you! How can I help fix this mistake?")
                        .addNPC(ID, HeadE.CHEERFUL, "Okay, here's what I need you to do. One of my contacts has devised a spell that he is sure will be able to counteract the effects of the mind-control spell. I need you to visit him.")
                        .addPlayer(HeadE.CHEERFUL, "Okay, who is it?")
                        .addNPC(ID, HeadE.CHEERFUL, "His name is Zaff. He runs a staff shop in Varrock. Go and speak to him and he will tell you what you should do. I will send word to him to let him know that you are coming.")
                        .addPlayer(HeadE.CHEERFUL, "Yes, sir! I'm on my way!", () -> {
                            player.getInventory().deleteItem(11010, 1);
                            player.setQuestStage(Quest.WHAT_LIES_BELOW, 6);
                        })
                        .getHead();

                yield new Dialogue()
                        .addNPC(ID, HeadE.CHEERFUL, "Ah, " + player.getDisplayName() + "! You've returned!")
                        .addOptions(ops -> {
                            ops.add("Yes! I have a letter for you.")
                                    .addPlayer(HeadE.CHEERFUL, "Yes! I have a letter for you.")
                                    .addNPC(ID, HeadE.CONFUSED, "A letter for me? Let me see.")
                                    .addNext(giveLetter);

                            ops.add("Yes, and you have some explaining to do!")
                                    .addPlayer(HeadE.ANGRY, "Yes, and you have some explaining to do!")
                                    .addNPC(ID, HeadE.AMAZED, "Why? Whatever do you mean?")
                                    .addPlayer(HeadE.ANGRY, "This letter from Surok to you implies that you're involved in a treasonous plot! I should tell the guards about you!")
                                    .addNPC(ID, HeadE.AMAZED, "I'm sure I don't know what you mean. Let me see the letter, please.")
                                    .addNext(giveLetter);

                            ops.add("What, you're still here?")
                                    .addPlayer(HeadE.AMAZED, "What, you're still here?")
                                    .addNPC(ID, HeadE.CHEERFUL, "Yes. I still haven't found someone to help me yet, but I hear you've been very busy yourself!")
                                    .addPlayer(HeadE.CONFUSED, "You have? Who told you that?")
                                    .addNPC(ID, HeadE.CHEERFUL, "Oh, I have my sources. Was there anything else?");
                        });
            }

            case 6, 7 -> new Dialogue()
                    .addNPC(ID, HeadE.CONFUSED, "Yes, " + player.getDisplayName() + "?")
                    .addOptions(ops -> {
                       ops.add("What am I doing now?")
                               .addPlayer(HeadE.CONFUSED, "What am I doing now?")
                               .addNPC(ID, HeadE.CHEERFUL, "You need to go and see Zaff. He can be found at \"Zaff's Staffs\", the staff shop in Varrock. He will tell you what to do next.");

                       ops.add("Do you think we can stop Surok?")
                               .addPlayer(HeadE.CONFUSED, "Do you think we can stop Surok?")
                               .addNPC(ID, HeadE.CALM_TALK, "Surok is a powerful mage and ruthless too. Who knows what will happen. I am sure that we will prevail, though. Don't worry, " + player.getDisplayName() + "! You'll be fine!");

                       ops.add("Do you have anything to trade yet?")
                               .addPlayer(HeadE.CONFUSED, "Do you have anything to trade yet?")
                               .addNPC(ID, HeadE.SAD_MILD, "No, I'm afraid I have nothing in stock, largely due to the fact that I'm not really a trader, as I just mentioned.")
                               .addPlayer(HeadE.CHEERFUL, "Ah yes, I remember!");

                       ops.add("Hey, your cart has no donkey!")
                               .addPlayer(HeadE.AMAZED, "Hey, your cart has no donkey!")
                               .addNPC(ID, HeadE.CHEERFUL, "Yes, that would be because it's a rickshaw cart.")
                               .addPlayer(HeadE.CHEERFUL, "Oh. I used to know a guy called Rick Shaw!")
                               .addNPC(ID, HeadE.CHEERFUL, "You don't say...!");
                    });

            case 8 -> new Dialogue()
                    .addNPC(ID, HeadE.CONFUSED, "Well, " + player.getDisplayName() + ", how did it go?")
                    .addPlayer(HeadE.CHEERFUL, "You should have been there! There was this...and Surok was like...and I was...and then the king...and, and...uh...ahem! The mission was accomplished and the king has been saved.")
                    .addNPC(ID, HeadE.CHEERFUL, "I take it that it went alright, then? That's great news!")
                    .addNPC(ID, HeadE.CHEERFUL, "Zaff has already briefed me on the events. We will arrange for Surok to be fed and watched. I think he will not be a problem any more.")
                    .addPlayer(HeadE.CONFUSED, "You know, one thing bothers me. He's now stuck in the library, but wasn't that the reason we were in this mess in the first place?")
                    .addNPC(ID, HeadE.CHEERFUL, "Yes, you are right. But rest assured, we will be watching him much more closely from now on.")
                    .addNPC(ID, HeadE.CHEERFUL, "You've done very well and have been a credit to the VPSG; perhaps one day there may be a place for you here!")
                    .addNPC(ID, HeadE.CHEERFUL, "In the meantime, let me reward you for what you've done. I will be sure to call on you if we ever need help in the future.")
                    .addNext(() -> player.getQuestManager().completeQuest(Quest.WHAT_LIES_BELOW));

            case 9 -> new Dialogue()
                    .addNPC(ID, HeadE.CHEERFUL, "Ah! " + player.getDisplayName() + "! You did a fine service to use. You might make a good member of the VPSG one day, with a little training and a bit more muscle!")
                    .addPlayer(HeadE.CONFUSED, "So, do you have any more jobs for me to do?")
                    .addNPC(ID, HeadE.CALM_TALK, "At the moment, no. Things seem pretty quiet. However, I have heard a rumor about something strange going on in...hmm, no, I think we can handle this one for now.")
                    .addNPC(ID, HeadE.CHEERFUL, "But, who knows? We may need your assistance again soon. Thank you, " + player.getDisplayName() + ".")
                    .addPlayer(HeadE.CHEERFUL, "Any time, Rat!");

            default ->
                    throw new IllegalStateException("Unexpected value: " + player.getQuestStage(Quest.WHAT_LIES_BELOW));
        };
    }
}
