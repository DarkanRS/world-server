package com.rs.game.content.quests.whatliesbelow;

import com.rs.engine.quest.Quest;
import com.rs.engine.quest.QuestHandler;
import com.rs.engine.quest.QuestOutline;
import com.rs.game.World;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Item;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ItemClickHandler;
import com.rs.plugin.handlers.ItemOnItemHandler;
import com.rs.plugin.handlers.ItemOnObjectHandler;
import com.rs.plugin.handlers.NPCDeathHandler;

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
            case 1 -> {
                lines.add("I need to kill outlaws west of Varrock so that I can collect 5 of Rat's Papers.");
                lines.add("I have to deliver those papers back to Rat in a folder he gave me.");
                lines.add("");
            }
            case 2 -> {
                lines.add("I need to deliver Rat's letter to Surok Magis in Varrock.");
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
                "Safe access to the chaos tunnels";
    }

    @Override
    public void updateStage(Player player, int stage) {
        if (player.getVars().getVarBit(3524) >= 0 && stage == getCompletedStage())
            player.getVars().saveVarBit(4312, 1);
        if (stage >= 6)
            player.getVars().setVarBit(4314, 1);
        //4314 varbit - 0 surok the gray, 1 surok the dagonhai, 2 surok the disappeared man
    }

    public static ItemClickHandler readFolders = new ItemClickHandler(new Object[] { 11003, 11006, 11007, 11008, 11009, 11010 }, new String[] { "Read" }, e -> {
        switch(e.getItem().getId()) {
            case 11003 -> e.getPlayer().sendMessage("The folder is empty at the moment so there is nothing inside to read!");
            case 11008, 11006 -> e.getPlayer().simpleDialogue("The piece of paper appears to contain lots of facts and figures. They look like accounts and lists of items. You're not sure what they all mean.");
            case 11007 -> e.getPlayer().sendMessage("A folder full of pages of facts and figures. You have found all the pages for which Rat was looking. You need to deliver the folder to him now.");
            case 11009 -> e.getPlayer().getInterfaceManager().sendInterface(249);
            case 11010 -> e.getPlayer().getInterfaceManager().sendInterface(250);
        }
    });

    public static NPCDeathHandler outlawDeath = new NPCDeathHandler(new Object[] { "Outlaw" }, e -> {
        if (e.getKiller() instanceof Player player && player.getQuestStage(Quest.WHAT_LIES_BELOW) == 1)
            World.addGroundItem(new Item(11008, 1), e.getNPC().getTile(), player);
    });

    public static ItemOnItemHandler addPapersToFolder = new ItemOnItemHandler(11008, new int[] { 11003, 11006 }, e -> {
        Item folder = e.getUsedWith(11008);
        e.getPlayer().getInventory().deleteItem(11008, 1);
        if (folder.getId() == 11003) {
            folder.setId(11006);
            folder.addMetaData("wlbPapersAdded", 1);
        } else if (folder.incMetaDataI("wlbPapersAdded") >= 5) {
            folder.setId(11007);
            folder.deleteMetaData();
            e.getPlayer().simpleDialogue("You have added all the pages to the folder that Rat gave to you. You should that this folder back to Rat.");
        } else
            e.getPlayer().sendMessage("You add the page to the folder that Rat gave to you.<br>You need to find " + (5 - folder.getMetaDataI("wlbPapersAdded")) + " more pages.");
        e.getPlayer().getInventory().refresh(folder.getSlot());
    });

    public static ItemOnObjectHandler chargeWand = new ItemOnObjectHandler(new Object[] { 2487 }, e -> {
        if (e.getItem().getId() == 11012) {
            if (e.getPlayer().getInventory().containsItem(562, 15)) {
                e.getPlayer().sync(6104, 1038);
                e.getPlayer().delayLock(13, () -> {
                    e.getPlayer().getInventory().deleteItem(562, 15);
                    e.getItem().setId(11013);
                    e.getPlayer().getInventory().refresh(e.getItem().getSlot());
                    e.getPlayer().simpleDialogue("The metal wand bursts into life and crackles with arcane power. This is a powerful instrument indeed!");
                });
            } else
                e.getPlayer().simpleDialogue("The wand sparks and glows, but the infusion does not appear to take hold. It looks like you will need more chaos runes to complete the infusion.");
        }
    });
}
