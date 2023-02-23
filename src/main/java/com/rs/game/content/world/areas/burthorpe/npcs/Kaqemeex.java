package com.rs.game.content.world.areas.burthorpe.npcs;

import com.rs.game.content.Skillcapes;
import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class Kaqemeex extends Conversation {

    private static final int npcId = 455;

    public static NPCClickHandler Kaqemeex = new NPCClickHandler(new Object[]{npcId}, e -> {
        switch (e.getOption()) {
            //Start Conversation
            case "Talk-to" -> e.getPlayer().startConversation(new Kaqemeex(e.getPlayer()));
        }
    });

    public Kaqemeex(Player player) {
        super(player);
        switch(player.getQuestManager().getStage(Quest.DRUIDIC_RITUAL)) {
            case 0:
                addPlayer(HeadE.CALM_TALK, "Hello there.");
                addNPC(npcId, HeadE.CALM_TALK, "What brings you to our holy monument?");
                addOptions(new Options() {
                    @Override
                    public void create() {
                        option("Who are you?", new Dialogue()
                                .addPlayer(HeadE.CALM_TALK, "Who are you?")
                                .addNPC(npcId, HeadE.CALM_TALK, "We are the druids of Guthix. We worship our god at our famous stone circles. You will find them located throughout these lands.")
                                .addOptions(new Options() {
                                    @Override
                                    public void create() {
                                        option("So what's so good about Guthix?", new Dialogue()
                                                .addPlayer(HeadE.CALM_TALK, "So what's so good about Guthix?")
                                                .addNPC(npcId, HeadE.CALM_TALK, "Guthix is the oldest and most powerful god in Gielinor. His existence is vital to this world. He is the god of balance, and nature; he is also a very part of this world.")
                                                .addNPC(npcId, HeadE.CALM_TALK, "He exists in the trees, and the flowers, the water and the rocks. He is everywhere. His purpose is to ensure balance in everything in this world, and as such we worship him.")
                                                .addPlayer(HeadE.CALM_TALK, "He sounds kind of boring...")
                                                .addNPC(npcId, HeadE.CALM_TALK, "Some day when your mind achieves enlightenment you will see the true beauty of his power."));
                                        option("Well, I'll be on my way now.");
                                    }
                                }));
                        option("I'm in search of a quest.",  new Dialogue()
                                .addPlayer(HeadE.CALM_TALK, "I'm in search of a quest")
                                .addNPC(npcId, HeadE.CALM_TALK, "Hmm. I think I may have a worthwhile quest for you actually. I don't know if you are familiar with the stone circle south of Varrock or not, but...")
                                .addPlayer(HeadE.CALM_TALK, "What about the stone circle full of dark wizards?")
                                .addNPC(npcId, HeadE.CALM_TALK, "That used to be OUR stone circle. Unfortunately, many many years ago, dark wizards cast a wicked spell upon it so that they could corrupt its power for their own evil ends.")
                                .addNPC(npcId, HeadE.CALM_TALK, "When they cursed the rocks for their rituals they made them useless to us and our magics. We require a brave adventurer to go on a quest for us to help purify the circle of Varrock.")
                                .addOptions(new Options() {
                                    @Override
                                    public void create() {
                                        option("Okay, I will try and help.", new Dialogue()
                                                .addPlayer(HeadE.CALM_TALK, "Okay, I will try and help.")
                                                .addNPC(npcId, HeadE.CALM_TALK, "Excellent. Go to the village south of this place and speak to my fellow " +
                                                        "Sanfew who is working on the purification ritual. He knows better than I what is required to complete it.")
                                                .addPlayer(HeadE.CALM_TALK, "Will do.")
                                                .addNext(()->{
                                                    player.getQuestManager().setStage(Quest.DRUIDIC_RITUAL, 1, true);
                                                }));
                                        option("No, that doesn't sound very interesting.", ()->{});
                                    }
                                }));
                        option("What is that cape you're wearing?", new Dialogue()
                                .addNext(Skillcapes.Herblore.getOffer99CapeDialogue(player, npcId)));
                    }
                });
                break;
            case 1:
            case 2:
                addPlayer(HeadE.CALM_TALK, "Hello there.");
                addNPC(npcId, HeadE.CALM_TALK, "What brings you to our holy monument?");
                addOptions(new Options() {
                    @Override
                    public void create() {
                        option("Who are you?", new Dialogue()
                                .addPlayer(HeadE.CALM_TALK, "Who are you?")
                                .addNPC(npcId, HeadE.CALM_TALK, "We are the druids of Guthix. We worship our god at our famous stone circles. You will find them located throughout these lands.")
                                .addOptions(new Options() {
                                    @Override
                                    public void create() {
                                        option("So what's so good about Guthix?", new Dialogue()
                                                .addPlayer(HeadE.CALM_TALK, "So what's so good about Guthix?")
                                                .addNPC(npcId, HeadE.CALM_TALK, "Guthix is the oldest and most powerful god in Gielinor. His existence is vital to this world. He is the god of balance, and nature; he is also a very part of this world.")
                                                .addNPC(npcId, HeadE.CALM_TALK, "He exists in the trees, and the flowers, the water and the rocks. He is everywhere. His purpose is to ensure balance in everything in this world, and as such we worship him.")
                                                .addPlayer(HeadE.CALM_TALK, "He sounds kind of boring...")
                                                .addNPC(npcId, HeadE.CALM_TALK, "Some day when your mind achieves enlightenment you will see the true beauty of his power."));
                                        option("Well, I'll be on my way now.");
                                    }
                                }));
                        option("About druidic ritual",  new Dialogue()
                                .addPlayer(HeadE.CALM_TALK, "What did you want me to do again?")
                                .addNPC(npcId, HeadE.CALM_TALK, "Go to the village south of this place and speak to my fellow " +
                                        "Sanfew who is working on the purification ritual. He knows better than I what is required to complete it.")
                                .addPlayer(HeadE.CALM_TALK, "Will do."));
                        option("What is that cape you're wearing?", new Dialogue()
                                .addNext(Skillcapes.Herblore.getOffer99CapeDialogue(player, npcId)));
                    }
                });
                break;
            case 3:
                addPlayer(HeadE.CALM_TALK, "Hello there.");
                addNPC(npcId, HeadE.CALM_TALK, "I have word from Sanfew that you have been very helpful in assisting him with his preparations for " +
                        "the purification ritual. As promised I will now teach you the ancient arts of Herblore.");
                addNext(()->{
                    player.getQuestManager().completeQuest(Quest.DRUIDIC_RITUAL);
                });
                break;
            default:
                addPlayer(HeadE.CALM_TALK, "Hello there.");
                addOptions(new Options() {
                    @Override
                    public void create() {
                        option("Can you explain herblore?", new Dialogue()
                                .addNPC(npcId, HeadE.CALM_TALK, "Herblore is the skill of working with herbs and other ingredients, to make useful potions and poison. ")
                                .addNPC(npcId, HeadE.CALM_TALK, "First you will need a vial, which can be found or made with the crafting skill. Then you must gather the herbs needed to make the potion you want.")
                                .addNPC(npcId, HeadE.CALM_TALK, "Refer to the Council's instructions in the Skills section of the website for the items needed to make a particular kind of potion. You must fill the vial with water and add the ingredients you need.")
                                .addNPC(npcId, HeadE.CALM_TALK, "There are normally 2 ingredients to each type of potion. Bear in mind, you must first identify each herb, to see what it is. You may also have to grind some herbs before you can use them.")
                                .addNPC(npcId, HeadE.CALM_TALK, "You will need a pestle and mortar in order to do this. Herbs can be found on the ground, and are also dropped by some monsters when you kill them. ")
                                .addNPC(npcId, HeadE.CALM_TALK, "Let's try an example Attack potion: The first ingredient is Guam leaf; the next is Eye of Newt. Mix these in your water-filled vial and you will produce an Attack potion. ")
                                .addNPC(npcId, HeadE.CALM_TALK, "Drink this potion to increase your Attack level. Different potions also require different Herblore levels before you can make them. ")
                                .addNPC(npcId, HeadE.CALM_TALK, "Once again, check the instructions found on the Council's website for the levels needed to make a particular potion.Good luck with your Herblore practices, Good day Adventurer.")
                                .addPlayer(HeadE.CALM_TALK, "Thanks for your help."));
                        option("What is that cape you're wearing?", new Dialogue()
                                .addNext(Skillcapes.Herblore.getOffer99CapeDialogue(player, npcId)));
                    }
                });
                break;
        }
        create();
    }
}
