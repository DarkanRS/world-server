package com.rs.game.content.quests.wolfwhistle;

import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.quest.Quest;
import com.rs.engine.quest.QuestHandler;
import com.rs.engine.quest.QuestOutline;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.Skills;
import com.rs.game.model.object.GameObject;
import com.rs.lib.game.Animation;
import com.rs.lib.net.packets.encoders.social.MessageGame.MessageType;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.LoginHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

import java.util.ArrayList;
import java.util.List;

@QuestHandler(Quest.WOLF_WHISTLE)
@PluginEventHandler
public class WolfWhistle extends QuestOutline {

    // stages
    public static final int NOT_STARTED = 0;
    public static final int FIND_SCALECTRIX = 1;
    public static final int PIKKUPSTIX_HELP = 2;
    public static final int WOLPERTINGER_MATERIALS = 3;
    public static final int WOLPERTINGER_CREATION = 4;
    public static final int WOLPERTINGER_POUCH_CHECK = 5;
    public static final int SAVE_BOWLOFTRIX = 6;
    public static final int QUEST_COMPLETE = 7;

    // animations
    public static final int POUCH_INFUSION = 725;
    // item ids
    public static final int WHITE_HARE_MEAT = 23067;
    public static final int EMBROIDERED_POUCH = 23068;
    public static final int RARE_SUMMONING_ITEMS = 23069;
    public static final int ANCIENT_WOLF_BONE_AMULET = 23066;
    public static final int GIANT_WOLPERTINGER_POUCH = 23070;
    public static final int GOLD_CHARM = 12158;
    // object ids
    static final int CLUTTERED_DRAWERS = 28641;
    static final int CLUTTERED_SHELVES = 67496;
    static final int UNTIDY_SHELVES = 67495;
    static final int CROWDED_SHELVES = 67497;
    static final int HEAPED_BOOKS = 67494;
    static final int DISCARDED_BOOKS = 65849;
    static final int STIKKLEBRIX_BODY = 67488; // varbit 10734
    public static LoginHandler handleLoginVarbit = new LoginHandler(e -> {
        Player p = e.getPlayer();

        if (p.getQuestManager().getStage(Quest.WOLF_WHISTLE) == WolfWhistle.WOLPERTINGER_MATERIALS) {
            p.getVars().setVarBit(10734, 1);
        }
    });
    public static ObjectClickHandler handlePikkupstixUpstairs = new ObjectClickHandler(new Object[]{CLUTTERED_DRAWERS, CLUTTERED_SHELVES, UNTIDY_SHELVES, CROWDED_SHELVES, HEAPED_BOOKS, DISCARDED_BOOKS}, e -> {
        Player p = e.getPlayer();

        if (e.getObjectId() == CLUTTERED_DRAWERS) {
            if (p.getQuestManager().getStage(Quest.WOLF_WHISTLE) == WolfWhistle.WOLPERTINGER_MATERIALS) {
                if (!p.getInventory().containsItem(EMBROIDERED_POUCH) && !p.getBank().containsItem(EMBROIDERED_POUCH, 1)) {
                    p.startConversation(new Dialogue()
                        .addItem(EMBROIDERED_POUCH, "You have found the embroidered pouch under some socks in this drawer.", () -> {
                            p.getInventory().addItem(EMBROIDERED_POUCH);
                            p.getQuestManager().getAttribs(Quest.WOLF_WHISTLE).setB("EMBROIDERED_POUCH", true);
                        }));
                    return;
                }
            }
            p.sendMessage(MessageType.GAME, "Despite searching, you cannot find anything.");
        } else {
            p.sendMessage(MessageType.GAME, "Despite searching, you cannot find anything.");
        }
    });
    public static ObjectClickHandler handleStikklebrixDeadBody = new ObjectClickHandler(new Object[]{STIKKLEBRIX_BODY}, e -> {
        Player p = e.getPlayer();

        if (!p.getInventory().containsItem(ANCIENT_WOLF_BONE_AMULET) && !p.getBank().containsItem(ANCIENT_WOLF_BONE_AMULET, 1)) {
            p.startConversation(new Dialogue()
                .addItem(ANCIENT_WOLF_BONE_AMULET, "You take the amulet from the sad remains of Stikklebrix.", () -> {
                    p.getInventory().addItem(ANCIENT_WOLF_BONE_AMULET);
                    p.getQuestManager().getAttribs(Quest.WOLF_WHISTLE).setB("ANCIENT_AMULET", true);
                }));
        }
    });

    public static void doWolpertingerPouchCreation(Player p, GameObject obelisk) {
        p.faceObject(obelisk);
        p.setNextAnimation(new Animation(WolfWhistle.POUCH_INFUSION));
        p.startConversation(new Dialogue()
            .addItem(WolfWhistle.GIANT_WOLPERTINGER_POUCH, "You craft the giant wolpertinger pouch. It thrums with barely contained power.", () -> {
                p.getInventory().removeAllItems(WolfWhistle.EMBROIDERED_POUCH, WolfWhistle.RARE_SUMMONING_ITEMS, WolfWhistle.WHITE_HARE_MEAT, WolfWhistle.ANCIENT_WOLF_BONE_AMULET);
                p.getInventory().addItem(WolfWhistle.GIANT_WOLPERTINGER_POUCH);
                p.getQuestManager().getAttribs(Quest.WOLF_WHISTLE).setB("WOLPERTINGER_POUCH", true);
                p.getQuestManager().setStage(Quest.WOLF_WHISTLE, WolfWhistle.WOLPERTINGER_POUCH_CHECK);
            })
            .addPlayer(HeadE.AMAZED, "I should check with Pikkupstix that this is how it is supposed to look.")
        );
    }

    public static boolean wolfWhistleObeliskReadyToInfusePouch(Player player) {
        return player.getInventory().containsItem(EMBROIDERED_POUCH)
            && player.getInventory().containsItem(ANCIENT_WOLF_BONE_AMULET)
            && player.getInventory().containsItem(WHITE_HARE_MEAT)
            && player.getInventory().containsItem(RARE_SUMMONING_ITEMS);
    }

    @Override
    public int getCompletedStage() {
        return QUEST_COMPLETE;
    }

    @Override
    public List<String> getJournalLines(Player player, int stage) {
        ArrayList<String> lines = new ArrayList<>();

        switch (stage) {
            case NOT_STARTED -> {
                lines.add("I can begin this quest by talking to Pikkupstix, who lives in");
                lines.add("Taverley.");
                lines.add("");
                lines.add("~~Requirements~~");
                lines.add("None");
                lines.add("");
            }
            case FIND_SCALECTRIX -> {
                lines.add("Having spoke to Pikkupstix, I need to find Scalectrix and");
                lines.add("Bowloftrix, who are somewhere near the old dry well in west");
                lines.add("Taverley, opposite the watermill.");
                lines.add("");
            }
            case PIKKUPSTIX_HELP -> {
                lines.add("I have found Scalectrix near the old well in Taverley, and she");
                lines.add("has warned me that there is a force of trolls in the well that");
                lines.add("need to be frightened off.");
                lines.add("She has requested I speak to Pikkupstix, as he will likely have");
                lines.add("a solution to the problem.");
                lines.add("");
            }
            case WOLPERTINGER_MATERIALS -> {
                lines.add("Pikkupstix has sent me to look for some items to make a giant");
                lines.add("wolpertinger pouch. These are:");
                lines.add("An ancient wolf bone amulet, held by his assistant Stikklebrix");
                lines.add("who is somewhere near White Wolf Mountain. This is a large");
                lines.add("mountain range to the west of Taverley.");
                lines.add("Some white hare meat from the Taverley Pet Shop, which is");
                lines.add("located in the building to the south of Pikkupstix's house.");
                lines.add("He also had some other items for me. These are:");
                lines.add("A precious grey charm.");
                lines.add("A very rare blessed spirit shard.");
                lines.add("");
            }
            case WOLPERTINGER_CREATION -> {
                lines.add("I have taken all of the items I was requested to collect and");
                lines.add("brought them to Pikkupstix. He has told me to use them on the");
                lines.add("obelisk to create the pouch.");
                lines.add("");
            }
            case WOLPERTINGER_POUCH_CHECK -> {
                lines.add("I have created the giant wolpertinger pouch. I should take it");
                lines.add("to Pikkupstix to make sure it worked correctly.");
                lines.add("");
            }
            case SAVE_BOWLOFTRIX -> {
                lines.add("Pikkupstix has told me that the pouch is working correctly. He");
                lines.add("has told me to take it to Scalectrix by the the old well."); // typo intentional
                lines.add("");
            }
            case QUEST_COMPLETE -> {
                lines.add("I have saved the druids in Taverley from a surprise attack by");
                lines.add("the trolls.");
                lines.add("");
                lines.add("");
                lines.add("QUEST COMPLETE!");
            }
            default -> lines.add("Invalid quest stage. Report this to an administrator.");
        }

        return lines;
    }

    @Override
    public void complete(Player player) {
        player.getInventory().addItem(GOLD_CHARM, 275);
        player.getInventory().removeAllItems(GIANT_WOLPERTINGER_POUCH);
        player.getSkills().addXp(Skills.SUMMONING, 276);
        getQuest().sendQuestCompleteInterface(player, GIANT_WOLPERTINGER_POUCH, "276 Summoning XP<br>275 gold charms");
    }

}
