package com.rs.game.player.quests.handlers.heroesquest;

import com.rs.game.player.Player;
import com.rs.game.player.Skills;
import com.rs.game.player.quests.Quest;
import com.rs.game.player.quests.QuestHandler;
import com.rs.game.player.quests.QuestManager;
import com.rs.game.player.quests.QuestOutline;
import com.rs.lib.Constants;
import com.rs.plugin.annotations.PluginEventHandler;

import java.util.ArrayList;

@QuestHandler(Quest.HEROES_QUEST)
@PluginEventHandler
public class HeroesQuest extends QuestOutline {
    public final static int NOT_STARTED = 0;
    public final static int GET_ITEMS = 1;
    public final static int QUEST_COMPLETE = 2;


    @Override
    public int getCompletedStage() {
        return QUEST_COMPLETE;
    }

    @Override
    public ArrayList<String> getJournalLines(Player player, int stage) {
        ArrayList<String> lines = new ArrayList<>();
        switch (stage) {
            case NOT_STARTED -> {
                lines.add("You will first have to prove you are worthy to enter the");
                lines.add("Heroes' Guild. To prove your status as a hero, you will");
                lines.add("need to obtain a number of items. There are many challenges");
                lines.add("standing between you and these items.");
                lines.add("");
                lines.add("~~~Quest Requirements~~~");
                lines.add((player.getQuestManager().getQuestPoints() >= 56 ? "<str>" : "") + "56 Quest Points");
                lines.add((player.getQuestManager().isComplete(Quest.SHIELD_OF_ARRAV) ? "<str>" : "") + "Shield Of Arrav");
                lines.add((player.getQuestManager().isComplete(Quest.LOST_CITY) ? "<str>" : "") + "Lost City");
                lines.add((player.getQuestManager().isComplete(Quest.DRAGON_SLAYER) ? "<str>" : "") + "Dragon Slayer");
                lines.add((player.getQuestManager().isComplete(Quest.MERLINS_CRYSTAL) ? "<str>" : "") + "Merlin's Crystal");
                lines.add((player.getQuestManager().isComplete(Quest.DRUIDIC_RITUAL) ? "<str>" : "") + "Druidic Ritual");
                lines.add("");
                lines.add("~~~Skill Requirements~~~");
                lines.add((player.getSkills().getLevel(Constants.COOKING) >= 53 ? "<str>" : "") + "53 Cooking");
                lines.add((player.getSkills().getLevel(Constants.FISHING) >= 53 ? "<str>" : "") + "53 Fishing");
                lines.add((player.getSkills().getLevel(Constants.HERBLORE) >= 25 ? "<str>" : "") + "25 Herblore");
                lines.add((player.getSkills().getLevel(Constants.DEFENSE) >= 25 ? "<str>" : "") + "25 Defence");
                lines.add((player.getSkills().getLevel(Constants.MINING) >= 50 ? "<str>" : "") + "50 Mining");
                lines.add("");
                if (meetsRequirements(player)) {
                    lines.add("You meet the requirements for this quest!");
                    lines.add("");
                }
            }
            case GET_ITEMS -> {
                if (player.getInventory().containsItem(995, 1)) {
                    lines.add("");
                    lines.add("");
                } else {
                    lines.add("");
                    lines.add("");
                }

                if (player.getInventory().containsItem(995, 1)) {
                    lines.add("");
                    lines.add("");
                } else {
                    lines.add("");
                    lines.add("");
                }

                if (player.getInventory().containsItem(995, 1)) {
                    lines.add("");
                    lines.add("");
                } else {
                    lines.add("");
                    lines.add("");
                }
                lines.add("");
            }
            case QUEST_COMPLETE -> {
                lines.add("");
                lines.add("");
                lines.add("QUEST COMPLETE!");
            }
            default -> {
                lines.add("Invalid quest stage. Report this to an administrator.");
            }
        }
        return lines;
    }

    public static boolean meetsRequirements(Player p) {
        QuestManager questManager = p.getQuestManager();
        Skills skills = p.getSkills();
        boolean[] requirements = new boolean[]{
                questManager.getQuestPoints() >= 56,
                questManager.isComplete(Quest.SHIELD_OF_ARRAV),
                questManager.isComplete(Quest.LOST_CITY),
                questManager.isComplete(Quest.DRAGON_SLAYER),
                questManager.isComplete(Quest.MERLINS_CRYSTAL),
                questManager.isComplete(Quest.DRUIDIC_RITUAL),
                skills.getLevel(Constants.COOKING) >= 53,
                skills.getLevel(Constants.FISHING) >= 53,
                skills.getLevel(Constants.HERBLORE) >= 25,
                skills.getLevel(Constants.DEFENSE) >= 25,
                skills.getLevel(Constants.MINING) >= 50,
        };
        for (boolean hasRequirement : requirements)
            if (!hasRequirement)
                return false;
        return true;
    }

    @Override
    public void complete(Player player) {
        getQuest().sendQuestCompleteInterface(player, 778, "Total of 29,232XP over twelve xp lamps", "Access to the heroes guild",
                "Can wield Dragon Battleaxe & Mace", "Access to Heroes Guild Shop");
    }

}
