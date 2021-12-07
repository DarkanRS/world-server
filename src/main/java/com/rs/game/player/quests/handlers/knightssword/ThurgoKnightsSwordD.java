package com.rs.game.player.quests.handlers.knightssword;

import static com.rs.game.player.quests.handlers.knightssword.KnightsSword.BLURITE_ORE;
import static com.rs.game.player.quests.handlers.knightssword.KnightsSword.BLURITE_SWORD;
import static com.rs.game.player.quests.handlers.knightssword.KnightsSword.FIND_DWARF;
import static com.rs.game.player.quests.handlers.knightssword.KnightsSword.GAVE_THRUGO_PIE_ATTR;
import static com.rs.game.player.quests.handlers.knightssword.KnightsSword.GET_MATERIALS;
import static com.rs.game.player.quests.handlers.knightssword.KnightsSword.GET_PICTURE;
import static com.rs.game.player.quests.handlers.knightssword.KnightsSword.IRON_BAR;
import static com.rs.game.player.quests.handlers.knightssword.KnightsSword.MADE_SWORD_ATTR;
import static com.rs.game.player.quests.handlers.knightssword.KnightsSword.PORTRAIT;
import static com.rs.game.player.quests.handlers.knightssword.KnightsSword.QUEST_COMPLETE;
import static com.rs.game.player.quests.handlers.knightssword.KnightsSword.REDBERRY_PIE;
import static com.rs.game.player.quests.handlers.knightssword.KnightsSword.THURGO;

import com.rs.game.player.Player;
import com.rs.game.player.content.dialogue.Conversation;
import com.rs.game.player.content.dialogue.Dialogue;
import com.rs.game.player.content.dialogue.HeadE;
import com.rs.game.player.content.dialogue.Options;
import com.rs.game.player.quests.Quest;
import com.rs.lib.game.Item;
import com.rs.plugin.annotations.PluginEventHandler;

@PluginEventHandler
public class ThurgoKnightsSwordD extends Conversation {
    public static final int WANT_PIE = 0;
    public static final int ABUNDANT_WITH_SWORDS_CONVO = 1;
    public static final int DWARVES_MAKE_ANOTHER_CONVO = 2;
    public static final int VAGUE_AREA_CONVO = 3;

    public ThurgoKnightsSwordD(Player p) {
        super(p);


        switch (p.getQuestManager().getStage(Quest.KNIGHTS_SWORD)) {
            case FIND_DWARF -> {
                if(p.getQuestManager().getAttribs(Quest.KNIGHTS_SWORD).getB(GAVE_THRUGO_PIE_ATTR)) {
                    addPlayer(HeadE.HAPPY_TALKING, "Can you make a special sword for me?");
                    addNPC(THURGO, HeadE.CALM_TALK, "Well, after bringing me my favorite food I guess I should give it a go. What sort of sword is it?");
                    addPlayer(HeadE.HAPPY_TALKING, "I need you to make a sword for one of Falador's knights. He had one which was passed down through five " +
                            "generations, but his squire has lost it.");
                    addPlayer(HeadE.HAPPY_TALKING, "So we need an identical one to replace it.");
                    addNPC(THURGO, HeadE.CALM_TALK, "A knight's sword eh? Well, I'd need to know exactly how it looked before I could make a new one.");
                    addNPC(THURGO, HeadE.CALM_TALK, "All the Faladian knights used to have swords with unique designs according to their position. Could you" +
                            " bring me a picture or something?");
                    addPlayer(HeadE.HAPPY_TALKING, "I'll go and ask his squire and see if I can find one.", ()->{
                        p.getQuestManager().setStage(Quest.KNIGHTS_SWORD, GET_PICTURE);
                    });
                    return;
                }
                if(!p.getInventory().containsItem(REDBERRY_PIE)){
                    addPlayer(HeadE.HAPPY_TALKING, "Are you an Imcando dwarf? I need a special sword.");
                    addNPC(THURGO, HeadE.CALM_TALK, "I don't talk about that sort of thing anymore. I'm getting old.");
                    addPlayer(HeadE.HAPPY_TALKING, "I'll come back another time.");
                } else {
                    addOptions("Choose an option:", new Options() {
                        @Override
                        public void create() {
                            option("Are you an Imcando dwarf? I need a special sword.", new Dialogue()
                                .addPlayer(HeadE.HAPPY_TALKING, "Are you an Imcando dwarf? I need a special sword.")
                                .addNPC(THURGO, HeadE.CALM_TALK, "I don't talk about that sort of thing anymore. I'm getting old.")
                                .addOptions("Choose an option:", new Options() {
                                    @Override
                                    public void create() {
                                        option("Would you like a redberry pie?", new Dialogue()
                                                .addNext(()->{p.startConversation(new ThurgoKnightsSwordD(p, WANT_PIE).getStart());}));
                                        option("I'll come back another time.", new Dialogue()
                                                .addPlayer(HeadE.HAPPY_TALKING, "I'll come back another time."));
                                    }
                                    }));
                            option("Would you like a redberry pie?", new Dialogue()
                                    .addNext(()->{p.startConversation(new ThurgoKnightsSwordD(p, WANT_PIE).getStart());}));
                        }
                    });

                }
            }
            case GET_PICTURE -> {
                addNPC(THURGO, HeadE.CALM_TALK, "Did you get the picture of the sword?");
                if(p.getInventory().containsItem(PORTRAIT)) {
                    addPlayer(HeadE.HAPPY_TALKING, "Yes, I got it here!");
                    addNPC(THURGO, HeadE.CALM_TALK, "Great, let me take a look at it");
                    addSimple("Thrugo looks at the portrait...");
                    addNPC(THURGO, HeadE.CALM_TALK, "I can make this.");
                    addPlayer(HeadE.HAPPY_TALKING, "Really? Great!");
                    addNPC(THURGO, HeadE.CALM_TALK, "I am going to need 1 blurite ore and 2 iron bars though");
                    addPlayer(HeadE.HAPPY_TALKING, "Awesome I will fetch that.");
                    addNPC(THURGO, HeadE.CALM_TALK, "Great.");
                    addPlayer(HeadE.HAPPY_TALKING, "But, where do I find blurite ore?");
                    addNPC(THURGO, HeadE.CALM_TALK, "Oh there is some underground around here by all the ice.");
                    addPlayer(HeadE.HAPPY_TALKING, "Great... I have to go underground?");
                    addNPC(THURGO, HeadE.CALM_TALK, "Yep.", ()->{
                        p.getInventory().removeItems(new Item(PORTRAIT, 1));
                        p.getQuestManager().setStage(Quest.KNIGHTS_SWORD, GET_MATERIALS);
                    });
                }
                else
                    addPlayer(HeadE.SAD, "Not yet...");
            }
            case GET_MATERIALS -> {
                if(p.getInventory().containsItem(BLURITE_SWORD)) {
                    addPlayer(HeadE.HAPPY_TALKING, "Thank you for the sword!");
                    addNPC(THURGO, HeadE.CALM_TALK, "No problem!");
                    return;
                }

                if(p.getQuestManager().getAttribs(Quest.KNIGHTS_SWORD).getB(MADE_SWORD_ATTR))
                    addPlayer(HeadE.SAD, "I lost the sword! Can I have another?");
                addNPC(THURGO, HeadE.CALM_TALK, "Do you have the materials?");
                if(p.getInventory().containsItem(BLURITE_ORE, 1) && p.getInventory().containsItem(IRON_BAR, 2)) {
                    addPlayer(HeadE.HAPPY_TALKING, "Yes, I have them right here!");
                    addNPC(THURGO, HeadE.CALM_TALK, "Great, okay I will get started...");
                    addSimple("Thurgo makes you a blurite sword", () -> {
                        p.getInventory().removeItems(new Item(BLURITE_ORE, 1), new Item(IRON_BAR, 2));
                        p.getInventory().addItem(new Item(BLURITE_SWORD, 1));
                        p.getQuestManager().getAttribs(Quest.KNIGHTS_SWORD).setB(MADE_SWORD_ATTR, true);
                    });
                }
                else {
                    addPlayer(HeadE.HAPPY_TALKING, "No, not yet.");
                    addSimple("You need 1 blurite ore and 2 iron bars...");
                }
            }
            case QUEST_COMPLETE -> {
                addPlayer(HeadE.HAPPY_TALKING, "Can you make me another of Sir Vyvin's swords?");
                addNPC(THURGO, HeadE.CALM_TALK, "You want that knight's sword again? I suppose you brought me a lovely pie, so I don't mind. I'll need a blurite ore and two iron bars, like before.");

                if(p.getInventory().containsItem(BLURITE_ORE, 1) && p.getInventory().containsItem(IRON_BAR, 2))
                    addSimple("Thurgo makes you another blurite sword", ()->{
                        p.getInventory().removeItems(new Item(BLURITE_ORE, 1), new Item(IRON_BAR, 2));
                        p.getInventory().addItem(new Item(BLURITE_SWORD, 1));
                    });
                else
                    addSimple("You need 1 blurite ore and 2 iron bars...");
            }
        }
    }

    public ThurgoKnightsSwordD(Player p, int convoID) {
        super(p);

        switch(convoID) {
            case WANT_PIE:
                wantPie(p);
                break;
            case ABUNDANT_WITH_SWORDS_CONVO:
                abundantWithSwords(p);
                break;
            case DWARVES_MAKE_ANOTHER_CONVO:
                dwarvesMakeAnother(p);
                break;
            case VAGUE_AREA_CONVO:
                vagueArea(p);
                break;
        }

    }

    private void wantPie(Player p) {
        addPlayer(HeadE.HAPPY_TALKING, "Would you like a redberry pie?");
        addSimple("You see Thurgo's eyes light up.");
        addNPC(THURGO, HeadE.HAPPY_TALKING, "I'd never say no to a redberry pie! We Imcando dwarves love them - they're GREAT!");
        addSimple("You hand over the pie.");
        addSimple("Thurgo eats the pie.");
        addSimple("Thurgo pats his stomach.");
        addNPC(THURGO, HeadE.HAPPY_TALKING, "By Guthix! THAT was good pie! Anyone who makes pie like THAT has got to be alright!", ()->{
            p.getInventory().removeItems(new Item(REDBERRY_PIE, 1));
            p.getQuestManager().getAttribs(Quest.KNIGHTS_SWORD).setB(GAVE_THRUGO_PIE_ATTR, true);
        });
    }

    private void abundantWithSwords(Player p) {



    }

    private void dwarvesMakeAnother(Player p) {

    }

    private void vagueArea(Player p) {

    }
}
