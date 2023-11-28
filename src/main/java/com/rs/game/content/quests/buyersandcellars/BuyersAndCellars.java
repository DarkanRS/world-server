package com.rs.game.content.quests.buyersandcellars;

import com.rs.engine.quest.Quest;
import com.rs.engine.quest.QuestHandler;
import com.rs.engine.quest.QuestOutline;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.Skills;
import com.rs.lib.game.Item;
import com.rs.plugin.annotations.PluginEventHandler;
import java.util.ArrayList;
import java.util.List;

@QuestHandler(Quest.BUYERS_AND_CELLARS)
@PluginEventHandler
public class BuyersAndCellars extends QuestOutline {

    @Override
    public int getCompletedStage() {
        return 9;
    }

    @Override
    public List<String> getJournalLines(Player player, int stage) {
        List<String> lines = new ArrayList<>();
        switch (stage) {
            case 0 -> {
                lines.add("Darren Lightfinger, in his cellar under the house north of");
                lines.add("Lumbridge's forge, has offered to test my pickpocketing skills");
                lines.add("and offer advice.");
                lines.add("");
            }
            case 1 -> {
                lines.add("I should practise my thieving technique");
                lines.add("and return to the Thieves' Guild when I am ready.");
                lines.add("");
            }
            case 2 -> {
                lines.add("Darren can now tell me his plan.");
                lines.add("");
            }
            case 3 -> {
                lines.add("I should go to the grounds of Lumbridge Castle");
                lines.add("and discover the identity of the chalice's owner from Robin.");
                lines.add("");
            }
            case 4 -> {
                lines.add("Robin tells me that the chalice has been taken into Lumbridge Swamp");
                lines.add("by an irritable old man.");
                lines.add("");
            }
            case 5 -> {
                lines.add("I need to find a way to divert Father Urhney's attention");
                lines.add("so I can get the key off him.");
                lines.add("Maybe Robin can advise me.");
                lines.add("");
            }
            case 6 -> {
                lines.add("If I light a fire outside one of the windows");
                lines.add("of Father Urhney's house.");
                lines.add(" it might distract him enough for me to be able to pick his pocket.");
                lines.add("");
            }
            case 7 -> {
                lines.add("I acquired Urhney's key.");
                lines.add("Now to steal the chalice from its display case...");
                lines.add("");
            }
            case 8 -> {
                lines.add("I have stolen the golden chalice. I should deliver it to Darren.");
                lines.add("");
            }
            case 9 -> {
                lines.add("I have given the chalice to Darren Lightfinger,");
                lines.add("Guildmaster of the Thieves' Guild.");
                lines.add("");
            }
        }
        return lines;
    }

    @Override
    public void complete(Player player) {
        player.getSkills().addXpQuest(Skills.THIEVING, 500);
        player.getSkills().addXpQuest(Skills.DEFENSE, 2000);
        if(player.getInventory().hasFreeSlots())
            player.getInventory().addItem(18646, 3);
        else {
            player.getBank().addItem(new Item(18646), false);
            player.getBank().addItem(new Item(18646), false);
            player.getBank().addItem(new Item(18646), false);
            player.sendMessage("You do not have enough free space so your rewards have been sent to the bank.");
        }
        sendQuestCompleteInterface(player, 18648);
    }

    @Override
    public void updateStage(Player player, int stage) {
        switch (stage) {
            case 1, 2 -> {
                player.getVars().setVarBit(7820, 1);
                player.getVars().setVarBit(7793, 0);
            }
            case 3, 8 -> {
                player.getVars().setVarBit(7820, 1);
                player.getVars().setVarBit(7793, 25);
            }
            case 4, 5, 6, 7 -> {
                player.getVars().setVarBit(7820, 1);
                player.getVars().setVarBit(7793, 10);
            }
        }
    }


    @Override
    public String getStartLocationDescription() {
        return "Speak to Darren Lightfinger in his cellar, accessed through a trapdoor next to a small house just north of the Lumbridge furnace.";
    }

    @Override
    public String getRequiredItemsString() {
        return "Logs.";
    }

    @Override
    public String getCombatInformationString() {
        return "None.";
    }

    @Override
    public String getRewardsString() {
        return "500 Thieving XP<br>"+
                "Access to the Thieves' guild<br>"+
                "3 Thieves' Guild pamphlets<br>" +
                "Ability to collect Hanky Points<br>";
    }

}
