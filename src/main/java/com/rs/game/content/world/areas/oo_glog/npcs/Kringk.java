package com.rs.game.content.world.areas.oo_glog.npcs;

import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class Kringk {

    public static NPCClickHandler Kringk = new NPCClickHandler(new Object[]{ 7102, 7099 }, new String[]{"Talk-to"}, e -> {
        if (e.getPlayer().isQuestComplete(Quest.AS_A_FIRST_RESORT))
            afterAsAFirstResort(e.getPlayer(), e.getNPC());
        else
            beforeAsAFirstResort(e.getPlayer(), e.getNPC());
    });

    public static ObjectClickHandler Grimechin = new ObjectClickHandler(new Object[]{ 29108 }, e -> {
        if(e.getOption().equalsIgnoreCase("Talk-to"))
            e.getPlayer().startConversation(new Dialogue()
                    .addPlayer(HeadE.CONFUSED, "So, how do you like what's, um, being done to your head?")
                    .addNPC(7063, HeadE.CHILD_ANGRY_HEADSHAKE, "Quit talking to goblin, Player. If she moves her head, she mess up work.")
                    .addPlayer(HeadE.SKEPTICAL, "Oh, sorry! I would never dream of interfering in the creative process.")
            );
    });

    public static ObjectClickHandler SalonCustomer = new ObjectClickHandler(new Object[]{ 4105 }, e -> {
        if(e.getOption().equalsIgnoreCase("Talk-to"))
            e.getPlayer().startConversation(new Dialogue()
                    .addPlayer(HeadE.CALM_TALK, "Hello, there.")
                    .addPlayer(HeadE.CALM_TALK, "Hello?")
                    .addPlayer(HeadE.CALM_TALK, "Hello.")
                    .addPlayer(HeadE.SHAKING_HEAD, "I don't think she can hear me under there.")
            );
    });

    private static void beforeAsAFirstResort(Player player, NPC npc) {
        player.startConversation(new Dialogue()
                .addPlayer(HeadE.CALM_TALK, "What's going on here?")
                .addNPC(7063, HeadE.CHILD_FRUSTRATED, "Me very busy. No have time to talk to puny creature like you.")
                .addPlayer(HeadE.FRUSTRATED, "Well, excuse me!")
                .addNPC(7063, HeadE.CHILD_FRUSTRATED, "No excuse for you - you in my way.")
        );
    }

    private static void afterAsAFirstResort(Player player, NPC npc) {
        player.startConversation(new Dialogue()
                .addNPC(7063, HeadE.CHILD_CALM_TALK, "I would offer haircut, but it hard to do style for puny human with puny head. Me know. You want wig intead? Me give you big-big discount!")
                .addOptions(ops -> {
                    ops.add("Would you like to buy an ogre wig for 50 gp?")
                            .addPlayer(HeadE.HAPPY_TALKING, "Yes, please.")
                            .addNext(() -> {
                                if (!player.getInventory().hasCoins(50)) {
                                    player.startConversation(new Dialogue()
                                            .addNPC(7063, HeadE.CHILD_CALM_TALK, "You no have enough shiny pretties, human."));
                                    return;
                                }

                                if (!player.getInventory().hasFreeSlots()) {
                                    player.startConversation(new Dialogue()
                                            .addNPC(7063, HeadE.CHILD_CALM_TALK, "You no have enough room to hold it, human. Come back when you have space."));
                                } else {
                                    player.getInventory().removeCoins(50);
                                    player.getInventory().addItem(12559);
                                    player.startConversation(new Dialogue()
                                            .addNPC(7063, HeadE.CHILD_CALM_TALK, "There you go. Nice wig for you, made from de freshest wolfsie bones."));
                                }
                            });
                    ops.add("No, thank you.")
                            .addPlayer(HeadE.SCARED, "No, thank you.");
                }));
    }
}

