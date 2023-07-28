package com.rs.game.content.quests.whatliesbelow;

import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.engine.miniquest.Miniquest;
import com.rs.engine.quest.Quest;
import com.rs.engine.quest.QuestHandler;
import com.rs.engine.quest.QuestOutline;
import com.rs.game.World;
import com.rs.game.content.world.areas.varrock.npcs.Zaff;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.Skills;
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
        return 9;
    }

    @Override
    public List<String> getJournalLines(Player player, int stage) {
        List<String> lines = new ArrayList<>();
        switch (stage) {
            case 1 -> {
                lines.add("I need to kill outlaws west of Varrock so that I can collect 5 ");
                lines.add("of Rat's Papers. I have to deliver those papers back to Rat in");
                lines.add("a folder he gave me.");
				lines.add("");
            }
            case 2 -> {
                lines.add("I need to deliver Rat's letter to Surok Magis in Varrock.");
                lines.add("");
            }
            case 3 -> {
                lines.add("Surok destroyed Rat's letter immediately, I should ask him");
                lines.add("what that was all about.");
                lines.add("");
            }
            case 4 -> {
                lines.add("Surok told me he was working on a spell to create gold and");
                lines.add("that I could be a part of it!");
                lines.add("");
                lines.add("He gave me a metal wand that I need to charge using 15 chaos");
                lines.add("runes at the chaos altar. He wants me to bring both the");
                lines.add("charged wand, and an empty bowl to him.");
                lines.add("");
            }
            case 5 -> {
                lines.add("I gave the items that Surok needed to him and he gave me");
                lines.add("a letter I need to deliver to Rat.");
                lines.add("");
            }
            case 6 -> {
                lines.add("Rat uncovered his secret about being a part of the VPSG to me");
                lines.add("and that I could be a part of stopping Surok with them.");
                lines.add("He told me to go speak to Zaff about what to do next.");
                lines.add("");
            }
            case 7 -> {
                lines.add("Zaff told me to go arrest Surok. He gave me a beacon ring to");
                lines.add("summon him when Roald is 'all but dead'.");
                lines.add("");
            }
            case 8 -> {
                lines.add("I have defeated Surok with Zaff's help and should go report");
                lines.add("back to Rat Burgiss.");
                lines.add("");
            }
        }
        return lines;
    }

    @Override
    public void complete(Player player) {
        player.getSkills().addXpQuest(Skills.RUNECRAFTING, 8000);
        player.getSkills().addXpQuest(Skills.DEFENSE, 2000);
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
        if (player.isMiniquestStarted(Miniquest.HUNT_FOR_SUROK))
            player.getVars().setVarBit(4314, 2);
    }

    public static ItemClickHandler readFolders = new ItemClickHandler(new Object[] { 11003, 11006, 11007, 11008, 11009, 11010, 11011 }, new String[] { "Read" }, e -> {
        switch(e.getItem().getId()) {
            case 11003 -> e.getPlayer().sendMessage("The folder is empty at the moment so there is nothing inside to read!");
            case 11008, 11006 -> e.getPlayer().simpleDialogue("The piece of paper appears to contain lots of facts and figures. They look like accounts and lists of items. You're not sure what they all mean.");
            case 11007 -> e.getPlayer().sendMessage("A folder full of pages of facts and figures. You have found all the pages for which Rat was looking. You need to deliver the folder to him now.");
            case 11009 -> e.getPlayer().getInterfaceManager().sendInterface(249);
            case 11010 -> e.getPlayer().getInterfaceManager().sendInterface(250);
            case 11011 -> e.getPlayer().getInterfaceManager().sendInterface(251);
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
            e.getPlayer().simpleDialogue("You have added all the pages to the folder that Rat gave to you. You should take this folder back to Rat.");
        } else
            e.getPlayer().sendMessage("You add the page to the folder that Rat gave to you.<br>You need to find " + (5 - folder.getMetaDataI("wlbPapersAdded")) + " more pages.");
        e.getPlayer().getInventory().refresh(folder.getSlot());
    });

    public static ItemOnObjectHandler chargeWand = new ItemOnObjectHandler(new Object[] { 2487 }, new Object[] { 11012 }, e -> {
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
    });

    public static void addZaffOptions(Player player, Options ops) {
        switch(player.getQuestStage(Quest.WHAT_LIES_BELOW)) {
            case 6 -> ops.add("Rat Burgiss sent me.")
                    .addPlayer(HeadE.CHEERFUL, "Rat Burgiss sent me!")
                    .addNPC(Zaff.ID, HeadE.CHEERFUL, "Ah, yes; You must be " + player.getDisplayName() + "! Rat sent word that you would be coming. Everything is prepared. I have created a spell that will remove the mind control from the king.")
                    .addPlayer(HeadE.CONFUSED, "Okay, so what's the plan?")
                    .addNPC(Zaff.ID, HeadE.CHEERFUL, "Listen carefully. For the spell to succeed, the king must be made very weak. If his mind becomes controlled, you will need to fight him until he is all but dead.")
                    .addNPC(Zaff.ID, HeadE.CHEERFUL, "Then and ONLY then, use your ring to summon me. I will teleport to you and cast the spell that will cure the king.")
                    .addPlayer(HeadE.CONFUSED, "Why must I summon you? Can't you come with me?")
                    .addNPC(Zaff.ID, HeadE.SAD_MILD, "I cannot. I must look after my shop here and I have lots to do. Rest assured, I will come when you summon me.")
                    .addPlayer(HeadE.CHEERFUL, "Okay, so what do I do now?")
                    .addNPC(Zaff.ID, HeadE.CHEERFUL, "Take this beacon ring and some instructions.")
                    .addNPC(Zaff.ID, HeadE.CHEERFUL, "Once you have read the instructions, it will be time for you to arrest Surok.")
                    .addPlayer(HeadE.AMAZED, "Won't he be disinclined to acquiesce to that request?")
                    .addNPC(Zaff.ID, HeadE.CONFUSED, "Won't he what?")
                    .addPlayer(HeadE.CONFUSED, "Won't he refuse?")
                    .addNPC(Zaff.ID, HeadE.CALM_TALK, "I very much expect so. It may turn nasty, so be on your guard. I hope we can stop him before he can cast his spell! Make sure you have that ring I gave you.")
                    .addPlayer(HeadE.CHEERFUL, "Okay, thanks, Zaff!")
                    .addNPC(Zaff.ID, HeadE.CHEERFUL, "Rat has told me that you are to be made an honorary member of the VPSG so that you can arrest Surok. If you have any questions about this, ask Rat.")
                    .addNPC(Zaff.ID, HeadE.CHEERFUL, "One last thing: you must remember that as a part of the VPSG, we must remain secretive at all times. For this reason, I cannot discuss matters such as this unless absolutely necessary.")
                    .addPlayer(HeadE.CHEERFUL, "Of course! Thanks again!", () -> {
                        player.setQuestStage(Quest.WHAT_LIES_BELOW, 7);
                        player.getInventory().addItemDrop(11014, 1);
                        player.getInventory().addItemDrop(11011, 1);
                    });

            case 7 -> ops.add("I need to ask you something else.")
                    .addPlayer(HeadE.CALM_TALK, "I need to ask you something else.")
                    .addNPC(Zaff.ID, HeadE.CONFUSED, "Go ahead!")
                    .addOptions(questions -> {
                        questions.add("What am I doing again?")
                                .addPlayer(HeadE.CONFUSED, "What am I doing again?")
                                .addNPC(Zaff.ID, HeadE.CHEERFUL, "Make sure you have read the instructions I gave you about the ring. Then, when you are ready, go to Surok and arrest him!")
                                .addPlayer(HeadE.CHEERFUL, "Okay, thanks!");

                        questions.add("Can I have another ring?")
                                .addPlayer(HeadE.CONFUSED, "Can I have another ring?")
                                .addNextIf(() -> player.containsItem(11014), new Dialogue()
                                        .addNPC(Zaff.ID, HeadE.CALM_TALK, "You have one already.")
                                        .addStop())
                                .addNPC(Zaff.ID, HeadE.CALM_TALK, "Well, I do happen to have another one here that you can have. Please try and be a bit more careful with this one!", () -> player.getInventory().addItemDrop(11014, 1));

                        questions.add("Can I have the instructions again?")
                                .addPlayer(HeadE.CONFUSED, "Can I have the instructions again?")
                                .addNextIf(() -> player.containsItem(11011), new Dialogue()
                                        .addNPC(Zaff.ID, HeadE.CALM_TALK, "You have one already.")
                                        .addStop())
                                .addNPC(Zaff.ID, HeadE.CALM_TALK, "Well, I do happen to have another one here that you can have. Please try and be a bit more careful with this one!", () -> player.getInventory().addItemDrop(11011, 1));
                        ;
                    });

            case 8 -> ops.add("We did it! We beat Surok!")
                    .addPlayer(HeadE.CHEERFUL, "We did it! We beat Surok!")
                    .addNPC(Zaff.ID, HeadE.CHEERFUL, "Yes. You have done well, " + player.getDisplayName() + ". You are to be commended for your actions!")
                    .addPlayer(HeadE.CHEERFUL, "It was all in the call of duty!")
                    .addPlayer(HeadE.CONFUSED, "What will happen with Surok now?")
                    .addNPC(Zaff.ID, HeadE.CHEERFUL, "Well, when I disrupted Surok's spell, he will have been sealed in the library, but we will still need to keep an eye on him, just in case.")
                    .addNPC(Zaff.ID, HeadE.CHEERFUL, "When you are ready, report back to Rat and he will reward you.")
                    .addPlayer(HeadE.CHEERFUL, "Okay, I will!")
                    .addNPC(Zaff.ID, HeadE.SAD_MILD, "Sadly, however, after this I will not be able to discuss these matters with you again. They are secret and we must be vigilant. The VPSG will prevail!")
                    .addPlayer(HeadE.CHEERFUL, "Of course. I understand. Goodbye!");
        }
    }

    public static ItemClickHandler summonBeaconRing = new ItemClickHandler(new Object[] { 11014 }, new String[] { "Summon" }, e -> {
        PlayerVsKingFight ctrl = e.getPlayer().getControllerManager().getController(PlayerVsKingFight.class);
        if (ctrl == null) {
            e.getPlayer().sendMessage("You don't know how many charges the ring has left. You should probably not use it yet!");
            return;
        }
        ctrl.attemptZaffSummon(e.getPlayer());
    });
}
