package com.rs.game.content.miniquests.troll_warzone;

import com.rs.engine.miniquest.Miniquest;
import com.rs.engine.miniquest.MiniquestHandler;
import com.rs.engine.miniquest.MiniquestOutline;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.Skills;
import com.rs.plugin.annotations.PluginEventHandler;

import java.util.ArrayList;
import java.util.List;

@MiniquestHandler(Miniquest.TROLL_WARZONE)
@PluginEventHandler
public class TrollWarzone extends MiniquestOutline {
    @Override
    public int getCompletedStage() {
        return 4;
    }

    @Override
    public List<String> getJournalLines(Player player, int stage) {
        ArrayList<String> lines = new ArrayList<>();
        switch (stage) {
            case 0 -> {
                lines.add("I can start this miniquest by speaking to Major Nigel Corothers in");
                lines.add("northern Burthorpe.");
                lines.add("");
            }

            case 4 -> {
                lines.add("");
                lines.add("");
                lines.add("MINIQUEST COMPLETE!");
            }
            default -> lines.add("Invalid quest stage. Report this to an administrator.");
        }
        return lines;
    }

    @Override
    public void complete(Player player) {
        player.getSkills().addXpQuest(Skills.COOKING, 110);
        player.getSkills().addXpQuest(Skills.MINING, 110);
        player.getSkills().addXpQuest(Skills.WOODCUTTING, 110);
        player.getInventory().addItemDrop(23030, 1);
        player.getInventory().addItemDrop(8007, 5);
        player.getInventory().addItemDrop(8009, 5);
        player.getInventory().addItemDrop(2429, 5);
        player.getInventory().addItemDrop(113, 5);
        player.getInventory().addItemDrop(2433, 5);
        player.getInventory().addItemDrop(2434, 5);
        getQuest().sendQuestCompleteInterface(player, 23030, "A baby troll!", "110 Cooking XP", "110 Mining XP", "110 Woodcutting XP", "Some teleport tablets", "Some combat potions");
    }

    @Override
    public void updateStage(Player player) {
        //varbit 10683 updates corporal keymans to claim the baby troll
    }
}
