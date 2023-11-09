package com.rs.game.content.quests.buyersandcellars.npcs;

import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.quest.Quest;
import com.rs.game.content.skills.thieving.PickPocketDummy;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.object.GameObject;
import com.rs.lib.game.Tile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ItemOnNPCHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler

public class DarrenLightfingerI extends Dialogue {
    private static final int npcid = 11273;
    public static NPCClickHandler DarrenLightfingerI = new NPCClickHandler(new Object[]{npcid}, new String[]{"Talk-to"}, e -> {
        if (!e.getPlayer().isQuestStarted(Quest.BUYERS_AND_CELLARS)) {
            preQuest(e.getPlayer());
            return;
        }
        switch (e.getPlayer().getQuestStage(Quest.BUYERS_AND_CELLARS)) {
            case 1 -> preQuest(e.getPlayer());
            case 2 -> stage2(e.getPlayer());
            default -> stage3(e.getPlayer());
        }
    });

    public static ItemOnNPCHandler handleChaliceOnDarren = new ItemOnNPCHandler(new Object[] { npcid }, e -> {
        Player player = e.getPlayer();
        if(e.getItem().getId() == 18648)
            player.startConversation(new Dialogue()
                    .addNPC(npcid, HeadE.HAPPY_TALKING, "Have you retrieved the chalice?")
                    .addPlayer(HeadE.HAPPY_TALKING, "I have!")
                    .addNPC(npcid, HeadE.HAPPY_TALKING, "Fantastic work! I knew I had chosen wisely when I recruited you. Now we can expand the guild and do some proper training around here.")
                    .addPlayer(HeadE.SKEPTICAL_HEAD_SHAKE, "Your buyer is still interested, I hope?")
                    .addNPC(npcid, HeadE.CALM_TALK, "Yes, of course, why?")
                    .addPlayer(HeadE.CALM_TALK, "Well, the chalice wasn't where you said it was, nor was the owner; I just wanted to make sure you had something right in all of this.")
                    .addNPC(npcid, HeadE.LAUGH, "Ha! I do appreciate a sense of humor in my members.")
                    .addPlayer(HeadE.CALM_TALK, "It wasn't actually a joke, to be honest.")
                    .addNPC(npcid, HeadE.SKEPTICAL_HEAD_SHAKE, "To be honest? You don't want to be honest; you're a member of the illustrious Thieves' Guild! Now get out there and make me proud... and both of us rich!")
                    .addNext(() -> {
                        player.fadeScreen(() -> {
                            player.getInventory().deleteItem(18648, 1);
                            player.setNextTile(Tile.of(3223, 3269, 0));
                            player.getVars().saveVarBit(7792, 10);
                            player.getVars().setVarBit(7793, 0);
                        });
                        player.getQuestManager().completeQuest(Quest.BUYERS_AND_CELLARS);
                    }));
    });

    public static void stage3(Player player) {
        player.startConversation(new Dialogue()
                .addNPC(npcid, HeadE.HAPPY_TALKING, "Greetings, my young recruit!")
                .addOptions(ops -> {
                    ops.add("Can we try out that testing dummy again?")
                            .addPlayer(HeadE.CALM_TALK, "Can we try out that testing dummy again?")
                            .addNext(() -> {
                                player.npcDialogue(npcid, HeadE.HAPPY_TALKING, "Of course!");
                                player.walkToAndExecute(Tile.of(4664, 5903, 0), () -> player.getActionManager().setAction(new PickPocketDummy(new GameObject(52316, 1, 4665, 5903, 0))));
                            });


                    ops.add("How's the guild coming along these days?")
                            .addPlayer(HeadE.CALM_TALK, "How's the guild coming along these days?")
                            .addNPC(npcid, HeadE.HAPPY_TALKING, "We're really only getting started at the moment. I've made a training dummy to practice on, but we'll need funds if we're to begin to command any respect. Anything else I can do for you?");


                    ops.add("I'd like to talk about the caper I'm doing for you.")
                            .addPlayer(HeadE.CALM_TALK, "I'd like to talk about the caper I'm doing for you.")
                            .addNPC(npcid, HeadE.HAPPY_TALKING, "Have you retrieved the chalice?")
                            .addNext(() -> {
                                switch (player.getQuestStage(Quest.BUYERS_AND_CELLARS)) {
                                    case 3 -> player.startConversation(new Dialogue()
                                            .addPlayer(HeadE.CALM_TALK, "Not yet.")
                                            .addNPC(npcid, HeadE.HAPPY_TALKING, "Head to Lumbridge Castle as soon as you may, then; Robin will meet you there."));

                                    case 4 -> player.startConversation(new Dialogue()
                                            .addPlayer(HeadE.CALM_TALK, "Not yet, but I'm on its trail"));

                                    case 5, 6 -> player.startConversation(new Dialogue()
                                            .addPlayer(HeadE.CALM_TALK, "I've tracked it down, but I've not yet retrieved it."));

                                    case 7 -> player.startConversation(new Dialogue()
                                            .addPlayer(HeadE.CALM_TALK, "I have the key, but not the chalice.")
                                            .addNPC(npcid, HeadE.CALM_TALK, "You've used several keys in the past, I'm sure; one more should pose no difficulty."));
                                    case 8 -> player.startConversation(new Dialogue()
                                            .addPlayer(HeadE.HAPPY_TALKING, "I have!")
                                            .addNPC(npcid, HeadE.HAPPY_TALKING, "Fantastic work! I knew I had chosen wisely when I recruited you. Now we can expand the guild and do some proper training around here.")
                                            .addPlayer(HeadE.SKEPTICAL_HEAD_SHAKE, "Your buyer is still interested, I hope?")
                                            .addNPC(npcid, HeadE.CALM_TALK, "Yes, of course, why?")
                                            .addPlayer(HeadE.CALM_TALK, "Well, the chalice wasn't where you said it was, nor was the owner; I just wanted to make sure you had something right in all of this.")
                                            .addNPC(npcid, HeadE.LAUGH, "Ha! I do appreciate a sense of humor in my members.")
                                            .addPlayer(HeadE.CALM_TALK, "It wasn't actually a joke, to be honest.")
                                            .addNPC(npcid, HeadE.SKEPTICAL_HEAD_SHAKE, "To be honest? You don't want to be honest; you're a member of the illustrious Thieves' Guild! Now get out there and make me proud... and both of us rich!")
                                            .addNext(() -> player.fadeScreen(() -> {
                                                player.getInventory().deleteItem(18648, 1);
                                                player.setNextTile(Tile.of(3223, 3269, 0));
                                                player.getVars().saveVarBit(7792, 10);
                                                player.getVars().setVarBit(7793, 0);
                                                player.getQuestManager().completeQuest(Quest.BUYERS_AND_CELLARS);
                                            })));
                                }
                            });
                    ops.add("Sorry, I was just leaving.")
                            .addPlayer(HeadE.CALM, "Sorry, I was just leaving.");
                }));
    }

    public static void stage2(Player player) {
        player.startConversation(new Dialogue()
                .addNPC(npcid, HeadE.HAPPY_TALKING, "Greetings, my young recruit! You return!")
                .addPlayer(HeadE.CALM_TALK, "Can we get started? I'm ready.")
                .addNPC(npcid, HeadE.HAPPY_TALKING, "Excellent! I shall let Robin know he should expect you, then. You know what to do?")
                .addOptions(ops4 -> {
                    ops4.add("Yes.")
                            .addPlayer(HeadE.CALM_TALK, "Yes.")
                            .addNPC(npcid, HeadE.HAPPY_TALKING, "Best of luck then.")
                            .addPlayer(HeadE.CALM_TALK, "Thanks.")
                            .addNext(() -> {
                                player.setQuestStage(Quest.BUYERS_AND_CELLARS, 3);
                            });

                    ops4.add("Remind me again?")
                            .addPlayer(HeadE.CALM_TALK, "Remind me again?")
                            .addNPC(npcid, HeadE.CALM, "Head over to Lumbridge Castle's courtyard; Robin should have found out the identity of the chalice's owner by then. At that point, you just need to get the key by any means necessary, open the vault and then come back here with the chalice.")
                            .addPlayer(HeadE.CALM_TALK, "Why can't you do this?")
                            .addNPC(npcid, HeadE.CALM, "Oh, Robin and I are both too well known; anyone with valuables would be instantly on their guard. No, I'm afraid it will have to be you doing the dirty work this time. Don't worry, you'll get a chance to see me in action some other time!");
                })
        );
    }

    public static void preQuest(Player player) {
        player.startConversation(new Dialogue()
                .addNPC(npcid, HeadE.HAPPY_TALKING, "Ah, come in, come in! I was just about to get started.")
                .addOptions(ops -> {
                    ops.add("Don't let me stop you.")
                            .addPlayer(HeadE.CONFUSED, "Don't let me stop you.")
                            .addNPC(npcid, HeadE.HAPPY_TALKING, "Ladies and gentlemen of Lumbridge!")
                            .addNPC(npcid, HeadE.HAPPY_TALKING, "Tonight, I stand before you to offer you the ultimate in opportunities!")
                            .addNPC(npcid, HeadE.HAPPY_TALKING, "I offer you the chance to make your mark on a society rife with imbalance and folly.")
                            .addNPC(npcid, HeadE.HAPPY_TALKING, "I offer you the chance to redistribute the wealth of our very civilization!")
                            .addNPC(npcid, HeadE.HAPPY_TALKING, "I offer you the freedom to live your life without the need to worry whether your rent will be paid this month")
                            .addNPC(npcid, HeadE.HAPPY_TALKING, "I offer you the skills to pay your way through the costs everyone must face.")
                            .addNPC(npcid, HeadE.HAPPY_TALKING, "I offer you vengeance against those who take and take yet give nothing in return.")
                            .addNPC(npcid, HeadE.HAPPY_TALKING, "I offer you justice at its most fundamental level!")
                            .addNPC(npcid, HeadE.HAPPY_TALKING, "Some will say that I am a scofflaw, a thief, a brigand... These people are correct!")
                            .addNPC(npcid, HeadE.HAPPY_TALKING, "But if I scoff at the law, it is because the law as we know it is a tool that 'The Man' is using purely to keep us in our place.")
                            .addNPC(npcid, HeadE.HAPPY_TALKING, "If I thieve, it is from those who have more than they deserve and more than they need.")
                            .addNPC(npcid, HeadE.HAPPY_TALKING, "And if I am a brigand, it is only by the standards of those whom I, er, brig.")
                            .addNPC(npcid, HeadE.HAPPY_TALKING, "If you join me I can offer you every opportunity for reward and for fame, or at least infamy.")
                            .addNPC(npcid, HeadE.HAPPY_TALKING, "Ladies and gentlemen, welcome to your destiny.")
                            .addNPC(npcid, HeadE.HAPPY_TALKING, "Welcome to the preliminary course, you fine members-in-waiting of the underworld gentry!")
                            .addNPC(npcid, HeadE.HAPPY_TALKING, "Here you will discover the techniques, the tricks, the training and the trials that mark your passage into this hidden elite.")
                            .addNPC(npcid, HeadE.HAPPY_TALKING, "Here you will become the best of the best, joining the ranks of the steely-eyed exploiters of this world's bloated social parasites in a quest for community justice and personal enrichment!")
                            .addNPC(npcid, HeadE.HAPPY_TALKING, "You will - Yes, we have a question?")
                            .addPlayer(HeadE.SKEPTICAL_THINKING, "Is this it? The 'world-renowned' guild is a cellar with two blokes and a straw dummy lurking in it?")
                            .addNPC(npcid, HeadE.CONFUSED, "Well...")
                            .addPlayer(HeadE.CONFUSED, "How long has this guild been in operation?")
                            .addNPC(npcid, HeadE.CALM, "...")
                            .addPlayer(HeadE.CONFUSED, "Yes?")
                            .addNPC(npcid, HeadE.FRUSTRATED, "Two weeks.");

                    ops.add("What are you doing down here")
                            .addPlayer(HeadE.SKEPTICAL_HEAD_SHAKE, "What are you doing down here?")
                            .addNPC(npcid, HeadE.HAPPY_TALKING, "Why, recruiting! Recruiting agents for a glorious destiny and fantastical missions of derring-do. White suits, classy cocktails, fast carts.. a wealth of rewards await my guild's members!")
                            .addNPC(npcid, HeadE.SHAKING_HEAD, "If only I could have convinced Ozan to sign on with us. SSuch a pity.. A master thief like him would have been perfect for my plans.")
                            .addNPC(npcid, HeadE.HAPPY_TALKING, "Anyway, would you like to stay for the explanation?")
                            .addOptions(ops2 -> {
                                ops2.add("Do tell.")
                                        .addPlayer(HeadE.CONFUSED, "Don't let me stop you.")
                                        .addNPC(npcid, HeadE.HAPPY_TALKING, "Ladies and gentlemen of Lumbridge!")
                                        .addNPC(npcid, HeadE.HAPPY_TALKING, "Tonight, I stand before you to offer you the ultimate in opportunities!")
                                        .addNPC(npcid, HeadE.HAPPY_TALKING, "I offer you the chance to make your mark on a society rife with imbalance and folly.")
                                        .addNPC(npcid, HeadE.HAPPY_TALKING, "I offer you the chance to redistribute the wealth of our very civilization!")
                                        .addNPC(npcid, HeadE.HAPPY_TALKING, "I offer you the freedom to live your life without the need to worry whether your rent will be paid this month")
                                        .addNPC(npcid, HeadE.HAPPY_TALKING, "I offer you the skills to pay your way through the costs everyone must face.")
                                        .addNPC(npcid, HeadE.HAPPY_TALKING, "I offer you vengeance against those who take and take yet give nothing in return.")
                                        .addNPC(npcid, HeadE.HAPPY_TALKING, "I offer you justice at its most fundamental level!")
                                        .addNPC(npcid, HeadE.HAPPY_TALKING, "Some will say that I am a scofflaw, a thief, a brigand... These people are correct!")
                                        .addNPC(npcid, HeadE.HAPPY_TALKING, "But if I scoff at the law, it is because the law as we know it is a tool that 'The Man' is using purely to keep us in our place.")
                                        .addNPC(npcid, HeadE.HAPPY_TALKING, "If I thieve, it is from those who have more than they deserve and more than they need.")
                                        .addNPC(npcid, HeadE.HAPPY_TALKING, "And if I am a brigand, it is only by the standards of those whom I, er, brig.")
                                        .addNPC(npcid, HeadE.HAPPY_TALKING, "If you join me I can offer you every opportunity for reward and for fame, or at least infamy.")
                                        .addNPC(npcid, HeadE.HAPPY_TALKING, "Ladies and gentlemen, welcome to your destiny.")
                                        .addNPC(npcid, HeadE.HAPPY_TALKING, "Welcome to the preliminary course, you fine members-in-waiting of the underworld gentry!")
                                        .addNPC(npcid, HeadE.HAPPY_TALKING, "Here you will discover the techniques, the tricks, the training and the trials that mark your passage into this hidden elite.")
                                        .addNPC(npcid, HeadE.HAPPY_TALKING, "Here you will become the best of the best, joining the ranks of the steely-eyed exploiters of this world's bloated social parasites in a quest for community justice and personal enrichment!")
                                        .addNPC(npcid, HeadE.HAPPY_TALKING, "You will - Yes, we have a question?")
                                        .addPlayer(HeadE.SKEPTICAL_THINKING, "Is this it? The 'world-renowned' guild is a cellar with two blokes and a straw dummy lurking in it?")
                                        .addNPC(npcid, HeadE.CONFUSED, "Well...")
                                        .addPlayer(HeadE.CONFUSED, "How long has this guild been in operation?")
                                        .addNPC(npcid, HeadE.CALM, "...")
                                        .addPlayer(HeadE.CONFUSED, "Yes?")
                                        .addNPC(npcid, HeadE.FRUSTRATED, "Two weeks.");

                                ops2.add("What is it you need done?")
                                        .addPlayer(HeadE.CALM_TALK, "And what is it you need done?")
                                        .addNPC(npcid, HeadE.CALM_TALK, "Here's what we need: Money!")
                                        .addPlayer(HeadE.AMAZED_MILD, "Really? You amaze me.")
                                        .addNPC(npcid, HeadE.CALM, "I know it's hardly the most high-flying goal, but we really need to start somewhere. To be precise, we need to start by expanding this cellar into a headquarters befitting a major player on the global crime stage.")
                                        .addPlayer(HeadE.SKEPTICAL, "I take it you have some sort of plan for doing this?")
                                        .addNPC(npcid, HeadE.HAPPY_TALKING, "Of course I do. In Lumbridge Castle's bank there is a golden chalice of particular workmanship and value. I have found a willing buyer for it, and now it merely remains to collect the item in question.")
                                        .addPlayer(HeadE.SKEPTICAL, "From the bank's vault..")
                                        .addNPC(npcid, HeadE.HAPPY_TALKING, "Correct! And since the vault is not easily breached, we shall need the key from it's owner.")
                                        .addPlayer(HeadE.CALM_TALK, "It's owner being?")
                                        .addNPC(npcid, HeadE.HAPPY_TALKING, "I shall send my right hand man, Robin, to determine that as soon as I may. He shall be around to brief you in the castle ground.")
                                        .addNPC(npcid, HeadE.HAPPY_TALKING, "Then you must merely acquire the key by stealth or by force, open the vault, return the chalice and...")
                                        .addPlayer(HeadE.SKEPTICAL_THINKING, "You seem to be assuming a certain amount here.")
                                        .addNPC(npcid, HeadE.HAPPY_TALKING, "Oh, but of course you'll help! I can offer you the best of training and the greatest of rewards for your assistance.. In fact, Let me have a look at your technique and see what we can do with you.")
                                        .addOptions( ops3 -> {
                                            ops3.add("Accept quest")
                                                    .addPlayer(HeadE.CALM_TALK, "Oh very well!", () -> player.setQuestStage(Quest.BUYERS_AND_CELLARS, 1))
                                                    .addNPC(npcid, HeadE.HAPPY_TALKING, "Splendid! Let's get you set up then.")
                                                    .addNPC(npcid, HeadE.HAPPY_TALKING, "This is a Mark 1 training dummy.")
                                                    .addNPC(npcid, HeadE.CALM_TALK, "It's designed for maximum pocket size and minimum observation skills, which, seeing as it's made of wood, straw, and canvas, was not hard to achieve.")
                                                    .addNPC(npcid, HeadE.CALM_TALK, "It will suffice for early training and for testing, but if you have any talent at all it will not be of use to you for long.")
                                                    .addNPC(npcid, HeadE.CALM, "Right, I want you to pick the pocket of that dummy as sneakily and delicately as you possibly can.")
                                                    .addNext(() -> player.walkToAndExecute(Tile.of(4664, 5903, 0), () -> {
                                                        player.getActionManager().setAction(new PickPocketDummy(new GameObject(52316, 1, 4665, 5903, 0)));
                                                        player.lock();
                                                    }));
                                            ops3.add("Decline Quest");
                                        });

                                ops2.add("No, thank you.")
                                        .addPlayer(HeadE.SHAKING_HEAD, "No, thank you.");
                            });

                    ops.add("Sorry, I was just leaving.")
                            .addPlayer(HeadE.CALM, "Sorry, I was just leaving.");

                })
        );

    }

}
