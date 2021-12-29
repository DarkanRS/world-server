package com.rs.game.player.quests.handlers.scorpioncatcher;

import static com.rs.game.player.quests.handlers.scorpioncatcher.ScorpionCatcher.EMPTY_CAGE;
import static com.rs.game.player.quests.handlers.scorpioncatcher.ScorpionCatcher.LOOK_FOR_SCORPIONS;
import static com.rs.game.player.quests.handlers.scorpioncatcher.ScorpionCatcher.NOT_STARTED;
import static com.rs.game.player.quests.handlers.scorpioncatcher.ScorpionCatcher.ONE_SCORP_CAGE;
import static com.rs.game.player.quests.handlers.scorpioncatcher.ScorpionCatcher.SCORP_COUNT_ATTR;
import static com.rs.game.player.quests.handlers.scorpioncatcher.ScorpionCatcher.TWO_SCORP_CAGE;
import static com.rs.game.player.quests.handlers.scorpioncatcher.ScorpionCatcher.removeCages;

import com.rs.game.player.Player;
import com.rs.game.player.content.dialogue.Conversation;
import com.rs.game.player.content.dialogue.Dialogue;
import com.rs.game.player.content.dialogue.HeadE;
import com.rs.game.player.content.dialogue.Options;
import com.rs.game.player.quests.Quest;
import com.rs.lib.Constants;
import com.rs.lib.game.Item;
import com.rs.lib.util.GenericAttribMap;
import com.rs.plugin.annotations.PluginEventHandler;

@PluginEventHandler
public class ThormacScorpionCatcherD extends Conversation {
    private final static int NPC = 389;
    public ThormacScorpionCatcherD(Player p) {
        super(p);
        switch(p.getQuestManager().getStage(Quest.SCORPION_CATCHER)) {
            case NOT_STARTED -> {
                addNPC(NPC, HeadE.CALM_TALK, "Hello I am Thormac the Sorcerer, I don't suppose you could be of assistance to me?");
                addOptions("Choose an option:", new Options() {
                    @Override
                    public void create() {
                        if(p.getSkills().getLevel(Constants.PRAYER) < 31)
                            option("What do you need assistance with?", new Dialogue()
                                    .addSimple("You need 31 prayer to start Scorpion Catcher.")
                            );
                        else
                            option("What do you need assistance with?", new Dialogue()
                                .addPlayer(HeadE.HAPPY_TALKING, "What do you need assistance with?")
                                .addNPC(NPC, HeadE.CALM_TALK, "I've lost my pet scorpions. They're lesser Kharid scorpions, a very rare breed. I left " +
                                        "their cage door open, now I don't know where they've gone. ")
                                .addNPC(NPC, HeadE.CALM_TALK, "There's three of them, and they're quick little beasties. They're all over Runescape.")
                                .addPlayer(HeadE.HAPPY_TALKING, "What's in it for me?")
                                .addNPC(NPC, HeadE.CALM_TALK, "Well I suppose I can aid you with my skills as a staff sorcerer. Most battlestaffs around here" +
                                        " are a bit puny.")
                                .addNPC(NPC, HeadE.CALM_TALK, "I can beef them up for you a bit. If you go up to the village of Seers, to the North of here, " +
                                        "one of them will be able to tell you where the scorpions are now.")
                                .addOptions("Start Scorption Catcher?", new Options() {
                                    @Override
                                    public void create() {
                                        option("Yes", new Dialogue()
                                                .addPlayer(HeadE.HAPPY_TALKING, "So how would I go about catching them then?")
                                                .addNPC(NPC, HeadE.CALM_TALK, "If you go up to the village of Seers, to the North of here, one of them " +
                                                        "will be able to tell you where the scorpions are now.")
                                                .addNPC(NPC, HeadE.CALM_TALK, "Also, I have a scorpion cage here which you can use to catch them in.")
                                                .addSimple("Thormac gives you a cage", ()->{
                                                    p.getQuestManager().setStage(Quest.SCORPION_CATCHER, LOOK_FOR_SCORPIONS);
                                                    p.getInventory().addItem(new Item(EMPTY_CAGE, 1), true);
                                                })
                                        );
                                        option("No", new Dialogue());
                                    }
                                }));
                        option("I'm a little busy.", new Dialogue()
                                .addPlayer(HeadE.FRUSTRATED, "I'm a little busy")
                                .addNPC(NPC, HeadE.CALM_TALK, "Okay then.")
                        );
                    }
                });

            }
            case LOOK_FOR_SCORPIONS -> {
                GenericAttribMap attr = p.getQuestManager().getAttribs(Quest.SCORPION_CATCHER);
                if(attr.getI(SCORP_COUNT_ATTR) == 3) {
                    addNPC(NPC, HeadE.CALM_TALK, "How goes your quest?");
                    addPlayer(HeadE.HAPPY_TALKING, "I have retrieved all your scorpions.");
                    addNPC(NPC, HeadE.CALM_TALK, "Aha, my little scorpions home at last!");
                    addNext(()->{
                        p.getQuestManager().completeQuest(Quest.SCORPION_CATCHER);
                        removeCages(p);
                    });
                }
                else if(ScorpionCatcher.hasLostCage(p)) {
                    addNPC(NPC, HeadE.CALM_TALK, p.getDisplayName() + "!");
                    addPlayer(HeadE.HAPPY_TALKING, "Yes?");
                    if(p.getInventory().hasFreeSlots()) {
                        addNPC(NPC, HeadE.CALM_TALK, "Here is the cage, I found it!", () -> {
                            if (attr.getI(SCORP_COUNT_ATTR) == 2)
                                p.getInventory().addItem(TWO_SCORP_CAGE, 1);
                            else if (attr.getI(SCORP_COUNT_ATTR) == 1)
                                p.getInventory().addItem(ONE_SCORP_CAGE, 1);
                            else
                                p.getInventory().addItem(EMPTY_CAGE, 1);
                        });
                        addPlayer(HeadE.FRUSTRATED, "Great...");
                    } else {
                        addNPC(NPC, HeadE.CALM_TALK, "I have the cage but you need room for me to give it to you...");
                        addPlayer(HeadE.HAPPY_TALKING, "Great!");
                    }

                } else {
                    addPlayer(HeadE.HAPPY_TALKING, "I've not caught all the scorpions yet.");
                    addNPC(NPC, HeadE.CALM_TALK, "Well remember to go speak to the Seers, North of here, if you need any help.");
                }
            }
        }
    }
}
