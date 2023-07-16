package com.rs.game.content.quests.whatliesbelow.npcs;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.quest.Quest;
import com.rs.game.content.quests.whatliesbelow.PlayerVsKingFight;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Tile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class SurokMagis extends Conversation {
    public static final int ID = 5853;
    public static NPCClickHandler handle = new NPCClickHandler(new Object[]{ID}, e -> e.getPlayer().startConversation(new SurokMagis(e.getPlayer(), e.getNPC())));

    public SurokMagis(Player player, NPC npc) {
        super(player);
        switch (player.getQuestStage(Quest.WHAT_LIES_BELOW)) {
            case 0, 1 -> {
                addPlayer(HeadE.CONFUSED, "Excuse me?");
                addNPC(ID, HeadE.ANGRY, "What do you want? ...Oh wait. I know! You're probably just like all the others, aren't you? After some fancy spell or potion from me, I bet!");
                addPlayer(HeadE.AMAZED, "What? No! At least, I don't think so. What sort of spells do you have?");
                addNPC(ID, HeadE.ANGRY, "Hah! I knew it! I expect you want my Aphro-Dizzy-Yak spell! Want someone to fall madly in love with you, eh?");
                addPlayer(HeadE.CONFUSED, "That spell sounds very interesting, but I didn't mean to disturb you!");
                addNPC(ID, HeadE.CALM_TALK, "Well, I see that you do have some manners. I'm glad to see that you use them. Now, if it's all the same, I am very busy at the moment. Unless you want something specific, please come back another time.");
                addPlayer(HeadE.CHEERFUL, "Yes, of course. Goodbye!");
            }
            case 2 -> {
                addNPC(ID, HeadE.ANGRY, "Hah! Come for my Aphro-Dizzy-Yak spell! Want someone to fall madly in love with you, eh? Not surprised with a face like that, to be honest!");
                addPlayer(HeadE.ANGRY, "I didn't come here to be insulted!");
                addNPC(ID, HeadE.AMAZED, "Really? Well, with ears like that, you do surprise me!");
                addPlayer(HeadE.ANGRY, "No, look. I have a letter for you.");
                addNPC(ID, HeadE.CONFUSED, "Really? Well then, let me see it!");
                if (player.getInventory().containsItem(11009)) {
                    addPlayer(HeadE.CHEERFUL, "Here it is!");
                    addNext(() -> {
                        player.lock();
                        player.getInventory().deleteItem(11009, 1);
                        player.setQuestStage(Quest.WHAT_LIES_BELOW, 3);
                        npc.stopAll();
                        npc.sync(6096, 1037);
                        WorldTasks.delay(3, () -> {
                            player.unlock();
                            player.startConversation(new SurokMagis(player, npc));
                        });
                    });
                } else {
                    addPlayer(HeadE.SAD_MILD, "Oh. I had it here a moment ago. Where is it?");
                    addNPC(ID, HeadE.CHEERFUL, "Hah! You've lost it already? You can't get good help these days!");
                    addPlayer(HeadE.SAD_MILD, "I'm very sorry! Let me go get another one!");
                    addNPC(ID, HeadE.CALM_TALK, "As you must! Now be off with you! I'm busy with studying here!");
                }
            }
            case 3 -> {
                addPlayer(HeadE.CONFUSED, "Why did you destroy the letter?");
                addNPC(ID, HeadE.ANGRY, "Never you mind! It's a secret!");
                addPlayer(HeadE.SAD_MILD, "Yes, there seems to be a lot of them going around at the moment.");
                addNPC(ID, HeadE.CALM_TALK, "Of course. Hmmmm. However, I could let you in on a secret, if you like?");
                addOptions(ops -> {
                    ops.add("Go on then!")
                            .addPlayer(HeadE.CHEERFUL, "Go on then!")
                            .addGotoStage("theBigSecret", this);

                    ops.add("Is it a BIG secret?")
                            .addPlayer(HeadE.CONFUSED, "Is it a BIG secret?")
                            .addNPC(ID, HeadE.CHEERFUL, "It's a very big secret!")
                            .addPlayer(HeadE.CHEERFUL, "Bigger than a house?")
                            .addNPC(ID, HeadE.CONFUSED, "Er, yes! It's...")
                            .addPlayer(HeadE.CHEERFUL, "Bigger than a volcano?")
                            .addNPC(ID, HeadE.ANGRY, "Yes. Look, it's big, alright? Now listen and I'll tell you.")
                            .addGotoStage("theBigSecret", this);

                    ops.add("No thanks.")
                            .addPlayer(HeadE.CALM_TALK, "No thanks. I have other things to do for now!")
                            .addNPC(ID, HeadE.CALM_TALK, "Very well, then. I hope these other matters of 'great' importance outweigh the need to make a LOT of gold. A WHOLE lot of gold. Gold. Yes, gold.")
                            .addPlayer(HeadE.CONFUSED, "Uh, why are you repeating yourself like that?")
                            .addNPC(ID, HeadE.CALM_TALK, "Dramatic effect.")
                            .addPlayer(HeadE.CONFUSED, "Oh.");
                });

                addStage("theBigSecret", new Dialogue()
                        .addNPC(ID, HeadE.CALM_TALK, "My secret is this. I have been spending time here in the palace library trying to discover some ancient spells and magics.")
                        .addNPC(ID, HeadE.CALM_TALK, "In my research, I have uncovered a most astounding spell that will allow me to transform simple clay into solid gold bars!")
                        .addNPC(ID, HeadE.CALM_TALK, "Now I am ready to use the spell to create all the gold I...uh...the city wants. I would gladly share this gold with you; I simply need a few more things!")
                        .addPlayer(HeadE.CONFUSED, "Okay, what do you need?")
                        .addNPC(ID, HeadE.CALM_TALK, "I will only need a couple of items. The first is very simple: an ordinary bowl to use as a casting vessel.")
                        .addNPC(ID, HeadE.CHEERFUL, "You should be able to find one of these at any local store here in Varrock. I would go myself but I am...uh...busy with my research.")
                        .addNPC(ID, HeadE.CALM_TALK, "The other item is much harder. I need a metal wand infused with chaos magic.")
                        .addPlayer(HeadE.CONFUSED, "How would I get something like that?")
                        .addNPC(ID, HeadE.CALM_TALK, "Take this metal wand. You will also need 15 chaos runes. When you get to the Chaos Altar, use the wand on the altar itself. This should infuse the runes into the wand.", () -> {
                            player.setQuestStage(Quest.WHAT_LIES_BELOW, 4);
                            player.getInventory().addItemDrop(11002, 1);
                            player.getInventory().addItemDrop(11012, 1);
                        })
                        .addPlayer(HeadE.AMAZED, "How on earth do you know about Runecrafting? I thought only a few people knew of it.")
                        .addNPC(ID, HeadE.ANGRY, "Hah! Don't presume to think that those wizards in their fancy tower are the only people to have heard of Runecrafting! Now pay attention!")
                        .addNPC(ID, HeadE.CALM_TALK, "You will need to have the 15 chaos runes in your inventory. Make sure you also have either a chaos talisman or chaos tiara to complete the infusion.")
                        .addPlayer(HeadE.CONFUSED, "Where can I get a talisman or a tiara?")
                        .addNPC(ID, HeadE.CALM_TALK, "I'm afraid I don't know. You will need to search for one.")
                        .addNPC(ID, HeadE.CHEERFUL, "Bring the infused wand and a bowl back to me and I will make us both rich!")
                        .addNPC(ID, HeadE.CHEERFUL, "One more thing, I have uncovered information here in the library which may be of use to you. It tells of a safe route to the Chaos Altar that avoids the Wilderness.")
                        .addPlayer(HeadE.CHEERFUL, "Great! What is it?")
                        .addNPC(ID, HeadE.CALM_TALK, "It is an old tome...a history book of sorts. It's somewhere here in the library. I forget where I left it, but it should be easy enough for you to find.")
                        .addNPC(ID, HeadE.CALM_TALK, "I have also given you a copy of a diary I...uh...acquired. It may also help you to find that which you seek.")
                        .getHead());
            }
            case 4 -> {
                boolean hasItems = player.getInventory().containsItems(1923, 11013);
                if (hasItems)
                    addNPC(ID, HeadE.CONFUSED, "Well?");
                else
                    addNPC(ID, HeadE.CALM_TALK, "Ah! You're back. Have you found the things I need yet?");
                addOptions(ops -> {
                    if (hasItems)
                        ops.add("I have the things you wanted!")
                                .addPlayer(HeadE.CHEERFUL, "I have the things you wanted!")
                                .addNPC(ID, HeadE.CHEERFUL, "Excellent! Well done! I knew that you would not let me down.")
                                .addPlayer(HeadE.CONFUSED, "So...about this gold that you're going to give me?")
                                .addNPC(ID, HeadE.CHEERFUL, "All in good time. I must prepare the spell first, and that will take a little time. While I am doing that, please take this letter to Rat, the trader outside the city who sent you here.")
                                .addPlayer(HeadE.CALM_TALK, "Okay, but I'll be back for my gold.")
                                .addNPC(ID, HeadE.CHEERFUL, "Yes, yes, yes. Now off you go!", () -> {
                                    player.getInventory().deleteItem(1923, 1);
                                    player.getInventory().deleteItem(11013, 1);
                                    player.getInventory().addItemDrop(11010, 1);
                                    player.setQuestStage(Quest.WHAT_LIES_BELOW, 5);
                                });
                    if (!hasItems)
                        ops.add("Remind me what I am doing again?")
                                .addPlayer(HeadE.CALM_TALK, "Remind me what I am doing again?")
                                .addNPC(ID, HeadE.CALM_TALK, "Take this metal wand. You will also need 15 chaos runes. When you get to the Chaos Altar, use the wand on the altar itself. This should infuse the runes into the wand.", () -> {
                                    if (!player.containsAnyItems(11012, 11013))
                                        player.getInventory().addItemDrop(11012, 1);
                                })
                                .addPlayer(HeadE.AMAZED, "How on earth do you know about Runecrafting? I thought only a few people knew of it.")
                                .addNPC(ID, HeadE.ANGRY, "Hah! Don't presume to think that those wizards in their fancy tower are the only people to have heard of Runecrafting! Now pay attention!")
                                .addNPC(ID, HeadE.CALM_TALK, "You will need to have the 15 chaos runes in your inventory. Make sure you also have either a chaos talisman or chaos tiara to complete the infusion.")
                                .addPlayer(HeadE.CONFUSED, "Where can I get a talisman or a tiara?")
                                .addNPC(ID, HeadE.CALM_TALK, "I'm afraid I don't know. You will need to search for one.")
                                .addNPC(ID, HeadE.CHEERFUL, "Bring the infused wand and a bowl back to me and I will make us both rich!");

                    if (!hasItems)
                        ops.add("I lost the wand!")
                                .addPlayer(HeadE.SAD_MILD, "I lost the wand!")
                                .addNPC(ID, HeadE.ANGRY, "Somehow, I knew that would happen so I have made a few spares for just such an occasion.")
                                .addNextIf(() -> player.getInventory().containsItem(11012), new Dialogue()
                                        .addNPC(ID, HeadE.CALM_TALK, "Here you g...hang on! There's still one in your inventory! I just saw it! You don't need two and they're not worth anything...yet! Now get going already; there's not much time!")
                                        .addNext(() -> {
                                        }))
                                .addNPC(ID, HeadE.CHEERFUL, "Here you are. Try not to lose this one!", () -> {
                                    if (!player.containsAnyItems(11012, 11013))
                                        player.getInventory().addItemDrop(11012, 1);
                                });

                    if (!hasItems)
                        ops.add("How much gold will I get?")
                                .addPlayer(HeadE.CONFUSED, "How much gold will I get?")
                                .addNPC(ID, HeadE.CALM_TALK, "Hmmmmm. That all depends on how quickly you get me that infused wand! Work fast and well for me, " + player.getDisplayName() + ", and I will make you rich, famous and powerful.")
                                .addPlayer(HeadE.CHEERFUL, "Really? Powerful, eh? How powerful?")
                                .addNPC(ID, HeadE.CALM_TALK, "That depends.")
                                .addPlayer(HeadE.CONFUSED, "Depends on what?")
                                .addNPC(ID, HeadE.ANGRY, "On how soon you stop yapping and get me that wand! Now get moving!");

                    ops.add("Can I ask about the diary?")
                            .addPlayer(HeadE.CONFUSED, "Can I ask about the diary?")
                            .addNPC(ID, HeadE.CONFUSED, "Of course. What would you like to know?")
                            .addOptions(this, "diaryQuestions", diaryOps -> {
                                diaryOps.add("Who is Sin'keth?")
                                        .addPlayer(HeadE.CONFUSED, "Who is this Sin'keth?")
                                        .addNPC(ID, HeadE.CHEERFUL, "Sin'keth Magis? From what I have read, it would appear he was a great leader of the Dagon'hai Mages.")
                                        .addPlayer(HeadE.CONFUSED, "The Dagon-what?")
                                        .addNPC(ID, HeadE.ANGRY, "Hmph! If you read the diary or perused that tome I mentioned, you would know who they are.")
                                        .addPlayer(HeadE.CONFUSED, "Hey, wait! Isn't your last name Magis too? Are you related to him?")
                                        .addNPC(ID, HeadE.ANGRY, "No. Not at all. Purely coincidence! Now, haven't you got work to do already?")
                                        .addGotoStage("diaryQuestions", this);

                                diaryOps.add("Where did it come from?")
                                        .addPlayer(HeadE.CONFUSED, "Where did it come from?")
                                        .addNPC(ID, HeadE.ANGRY, "How I came to have the diary is of no concern to you!")
                                        .addPlayer(HeadE.CALM_TALK, "Oooo, tetchy!")
                                        .addNPC(ID, HeadE.CALM_TALK, "I suggest that you concentrate on getting the things I need and stop asking pointless questions!")
                                        .addGotoStage("diaryQuestions", this);

                                diaryOps.add("Where is the safe route?")
                                        .addPlayer(HeadE.CONFUSED, "Where is the safe route?")
                                        .addNPC(ID, HeadE.CHEERFUL, "I could tell you, of course; I know more or less exactly where it is. However, that would take all the fun out of this, wouldn't it?")
                                        .addPlayer(HeadE.CALM_TALK, "No, really! It's okay! You can tell me where it is!")
                                        .addNPC(ID, HeadE.CHEERFUL, "No, I think you should see if you can find it for yourself. I have given you the diary and, along with the tome around here, you should be able to find that alternate route easily enough on your own!")
                                        .addGotoStage("diaryQuestions", this);

                                diaryOps.add("Can I have another one?")
                                        .addPlayer(HeadE.CONFUSED, "Can I have another one?")
                                        .addNPC(ID, HeadE.CONFUSED, "You want another copy of the diary?")
                                        .addPlayer(HeadE.CHEERFUL, "Yes, I seem to have misplaced the last one.")
                                        .addNPC(ID, HeadE.CALM_TALK, "Fine, but this diary is important you know! It has secrets that you should not share around with just anybody!", () -> player.getInventory().addItemDrop(11002, 1))
                                        .addGotoStage("diaryQuestions", this);

                                diaryOps.add("Never mind, I forgot the question.");
                            });

                    ops.add("Where is that tome you mentioned again?")
                            .addPlayer(HeadE.CONFUSED, "Where is that tome you mentioned, again?")
                            .addNPC(ID, HeadE.CALM_TALK, "Like I said, it's lying around here in the library somewhere. It should be easy enough to find.");
                });
            }
            case 5 -> {
                if (!player.containsItem(11010)) {
                    addPlayer(HeadE.ANGRY, "That letter was treasonous so I destroyed it!");
                    addNPC(ID, HeadE.CONFUSED, "Really? You destroyed it?");
                    addPlayer(HeadE.ANGRY, "Yes!");
                    addNPC(ID, HeadE.CALM_TALK, "You want another one, don't you?");
                    addPlayer(HeadE.CONFUSED, "Ah...uh...yes...yes, I do.");
                    addNPC(ID, HeadE.CALM_TALK, "Fine. Here you are. And stop all this complaining; it's getting me down.", () -> player.getInventory().addItemDrop(11010, 1));
                } else {
                    addPlayer(HeadE.AMAZED, "This letter is treasonous! I'm going to report you to the king!");
                    addNPC(ID, HeadE.CHEERFUL, "Hah! And what would make him believe you? They would think it was a fake! Besides, if you still want the gold I promised, you would do well to deliver that letter for me. Join me and together we can rule Misthalin!");
                }
                addOptions(ops -> {
                    ops.add("Who are you really?")
                            .addPlayer(HeadE.CONFUSED, "Who are you really?")
                            .addNPC(ID, HeadE.CALM_TALK, "That is none of your concern! I am Surok Magis and I am more powerful than you can possibly imagine!")
                            .addPlayer(HeadE.CONFUSED, "So what's in it for me?")
                            .addNPC(ID, HeadE.CALM_TALK, "You get to live. For now.")
                            .addPlayer(HeadE.CALM_TALK, "Not that I'm complaining about the living part, but I was expecting something of more...er...valuable?")
                            .addNPC(ID, HeadE.CALM_TALK, "Ah, yes. The gold. Don't you worry, I am still working on that. Just deliver my letter for me, first. Now be off!")
                            .addPlayer(HeadE.CALM_TALK, "Fine. I will deliver the letter, but I will be back soon.")
                            .addNPC(ID, HeadE.CALM_TALK, "I am sure you will.");

                    ops.add("Do I still get the gold?")
                            .addPlayer(HeadE.CONFUSED, "Do I still get the gold?")
                            .addNPC(ID, HeadE.CALM_TALK, "If that is your only concern, then yes, you do. But I must warn you, my patience with you is wearing thin. So go now and do my bidding.")
                            .addPlayer(HeadE.CALM_TALK, "Fine. I will deliver the letter, but I will be back soon.")
                            .addNPC(ID, HeadE.CALM_TALK, "I am sure you will.");

                    ops.add("I'll never join you!")
                            .addPlayer(HeadE.ANGRY, "I'll never join you!")
                            .addNPC(ID, HeadE.CALM_TALK, "My, my! How heroic! Not that it matters. I don't need you for my plans. However, if you want this gold, then you would do well to do as you are told.")
                            .addPlayer(HeadE.CALM_TALK, "Fine. I will deliver the letter, but I will be back soon.")
                            .addNPC(ID, HeadE.CALM_TALK, "I am sure you will.");

                    ops.add("Nevermind, I forgot the question.");
                });
            }

            case 6 -> {
                addPlayer(HeadE.ANGRY, "I've been told of your foul plans, Surok! You lied to me about the gold, so I've come to stop you!");
                addNPC(ID, HeadE.CHEERFUL, "Oh really? And what makes you think you have even the slightest chance of doing that?");
                addPlayer(HeadE.ANGRY, "I'll fight you if I have to!");
                addNPC(ID, HeadE.ANGRY, "Enough! You have been of use to me before but now you are wasting my time! Be gone!");
            }

            case 7 -> {
                addPlayer(HeadE.ANGRY, "Surok!! Your plans have been uncovered! You are hereby under arrest on the authority of the Varrock Palace Secret Guard!");
                if (!player.getInventory().containsItem(11014) && !player.getEquipment().containsOneItem(11014)) {
                    addNPC(ID, HeadE.ANGRY, "You fool! You are no match for my power! You don't have the means to stop me! Get out of my sight!", () -> player.setNextTile(Tile.of(3214, 3378, 0)));
                    create();
                    return;
                }
                addNPC(ID, HeadE.ANGRY, "So! You're with the Secret Guard, eh? I should have known! I knew you had ugly ears from the start...and your nose is too short!");
                addPlayer(HeadE.ANGRY, "Give yourself up, Surok!");
                addNPC(ID, HeadE.ANGRY, "Never! I am Surok Magis, descendant of the High Elder Sin'keth Magis, rightful heir of the Dagon'hai Order! I will have my revenge on those who destroyed my people!");
                addPlayer(HeadE.ANGRY, "The place is surrounded. There is nowhere to run!");
                addNPC(ID, HeadE.ANGRY, "Do you really wish to die so readily? Are you prepared to face your death?");
                addOptions(ops -> {
                   ops.add("Bring it on!")
                           .addPlayer(HeadE.ANGRY, "Bring it on!")
                           .addNPC(ID, HeadE.ANGRY, "I am a Dagon'hai! I run from nothing. My spell has been completed and it is time for you to meet your end, " + player.getDisplayName() +"! The king is now under my control!")
                           .addNext(() -> player.getControllerManager().startController(new PlayerVsKingFight()));

                   ops.add("Fine! You win this time!")
                           .addPlayer(HeadE.ANGRY, "Fine! You win this time!")
                           .addNPC(ID, HeadE.ANGRY, "Get out of my sight then!");
                });
            }

            case 8 -> {
                addNPC(ID, HeadE.CALM_TALK, "You have foiled my plans, " + player.getDisplayName()+"... I obviously underestimated you.");
                addPlayer(HeadE.CALM_TALK, "Yes. Let this be a lesson to you.");
                addNPC(ID, HeadE.AMAZED, "...");
            }
        }
        create();
    }
}
