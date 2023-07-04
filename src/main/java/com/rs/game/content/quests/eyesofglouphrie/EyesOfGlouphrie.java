package com.rs.game.content.quests.eyesofglouphrie;

import com.rs.engine.quest.Quest;
import com.rs.engine.quest.QuestHandler;
import com.rs.engine.quest.QuestOutline;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;

import java.util.List;

//@QuestHandler(Quest.EYES_OF_GLOUPHRIE)
@PluginEventHandler
public class EyesOfGlouphrie extends QuestOutline {

    @Override
    public int getCompletedStage() {
        return 6;
    }

    @Override
    public List<String> getJournalLines(Player player, int stage) {
        return null;
    }

    @Override
    public void complete(Player player) {

    }

    @Override
    public String getStartLocationDescription() {
        return "Talk to Brimstail in his cave west of the Gnome Stronghold bank.";
    }

    @Override
    public String getRequiredItemsString() {
        return "1 mud rune, 1 maple log, 1 oak log, 1 bucket of sap (use an empty bucket on an evergreen tree).";
    }

    @Override
    public String getCombatInformationString() {
        return "You will need to defeat 6 evil creatures (1 life point each).";
    }

    @Override
    public String getRewardsString() {
        return "12,000 Magic XP<br>" +
                "2,500 Woodcutting XP<br>" +
                "6,000 Runecrafting XP<br>" +
                "250 Construction XP<br>"+
                "Mysterious small crystal seed<br>" +
                "Ability to use crystal singing bowls";
    }
}
