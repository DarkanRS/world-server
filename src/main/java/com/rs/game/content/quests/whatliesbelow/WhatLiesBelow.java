package com.rs.game.content.quests.whatliesbelow;

import com.rs.engine.quest.Quest;
import com.rs.engine.quest.QuestHandler;
import com.rs.engine.quest.QuestOutline;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;

import java.util.ArrayList;
import java.util.List;

@QuestHandler(Quest.WHAT_LIES_BELOW)
@PluginEventHandler
public class WhatLiesBelow extends QuestOutline {

    /**
     * inter 250/251
     */

    @Override
    public int getCompletedStage() {
        return 6;
    }

    @Override
    public List<String> getJournalLines(Player player, int stage) {
        List<String> lines = new ArrayList<>();
        switch (stage) {
            case 0 -> {
                lines.add("I can start this quest by speaking to Rat Burgiss on the");
                lines.add("road south of Varrock.");
                lines.add("");
                lines.add("<u>Requirements</u>");
                lines.add("35 Runecrafting");
                lines.add("");
            }
        }
        return lines;
    }

    @Override
    public void complete(Player player) {
        //TODO
        sendQuestCompleteInterface(player, 11014);
    }

    @Override
    public String getStartLocationDescription() {
        return "Talk to Rat Burgiss, south of Varrock.";
    }

    @Override
    public String getRequiredItemsString() {
        return "Bowl, 15 chaos runes or 15 un-noted pure essence, access to the chaos altar.";
    }

    @Override
    public String getCombatInformationString() {
        return "You will need to defeat a level 47 enemy and 5 level 32 enemies.";
    }

    @Override
    public String getRewardsString() {
        return "8,000 Runecrafting XP<br>"+
                "2,000 Defence XP<br>"+
                "Beacon ring<br>" +
                "Access to the chaos tunnels";
    }
}
