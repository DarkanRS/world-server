package com.rs.game.content.world.areas.thieves_guild.npcs;

import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.game.content.skills.thieving.HankyPoints;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class AdvPickpocketingTrainer {

    static String[] Lure = new String[]{
            "Watch out! The fellow behind you has a club!",
            "Behind you! A three-headed monkey!",
            "That's the third biggest platypus I've ever seen!",
            "Look over THERE!",
            "Look! An eagle!",
            "Your shoelace is untied.",
    };
    public static NPCClickHandler pickpocketTrainer = new NPCClickHandler(new Object[]{11296}, new String[]{"Talk-to"}, e -> {
        int npcid = e.getNPCId();
        Player player = e.getPlayer();
        if (player.getEquipment().getWeaponId() != 18644) {
            player.sendMessage("The trainer does his best to focus on you.");
            player.npcDialogue(npcid, HeadE.SKEPTICAL_THINKING, "I teach 'Aggressive Pickpocketing'. You need t' be be'er equipped before I can 'elp yer - git yerself a blackjack.");
        } else {
            player.startConversation(new Dialogue()
                    .addNPC(npcid, HeadE.CALM_TALK, "A cosh is a fan'astic tool, and that's no 'yperbole. Know 'ow to use it?")
                    .addOptions(ops -> {
                        ops.add("About those hankies...")
                                .addOptions(ops2 -> {
                                    ops2.add("What are these handkerchiefs for?")
                                            .addNPC(npcid, HeadE.CALM_TALK, "Oh, we use them to keep track of our members' training regimen. Blue handkerchiefs will earn you one point, red ones are worth four. You can claim a reward from any trainer with your hanky points.");
                                    ops2.add("How often can I do this?")
                                            .addNPC(npcid, HeadE.CALM_TALK, "The number of hanky points you can claim for depends on your dedication to the art. At the moment, you can claim [number] hanky points' worth of training per week.");
                                    ops2.add("Which activities give which handkerchiefs?")
                                            .addNPC(npcid, HeadE.CALM_TALK, "Our pickpocketing trainers, safes and easy chests carry blue hankies; red hankies can be found on the coshing trainers and behind harder locks.");
                                    ops2.add("Bye for now.")
                                            .addPlayer(HeadE.HAPPY_TALKING, "Bye for now.");
                                });


                        ops.add("How many hanky points do I have?", () -> HankyPoints.checkPoints(player, e.getNPC()));

                        ops.add("I'd like to claim a reward for my hanky points.", () -> {
                            int availablePoints = player.getWeeklyI("HankyPoints") - player.getWeeklyI("ClaimedHankyPoints");
                            if (availablePoints == 0)
                                HankyPoints.claimHankyPoints(player, e.getNPC());
                            else
                                player.startConversation(new Dialogue()
                                        .addNPC(npcid, HeadE.CALM_TALK, "Sure things! Let's see now...", () -> HankyPoints.claimHankyPoints(player, e.getNPC())
                                        ));
                        });

                        ops.add("I've had some experience, yes.")
                                .addPlayer(HeadE.HAPPY_TALKING, "I've had some experience, yes.")
                                .addNPC(npcid, HeadE.CALM_TALK, "You can prac'ise on me if yer want, but only wiv a training cosh...");

                        ops.add("Remind me of the finer points, please.")
                                .addPlayer(HeadE.HAPPY_TALKING, "Remind me of the finer points, please?")
                                .addNPC(npcid, HeadE.CALM_TALK, "A blackjack, cosh or sap is a tool for openin' the pockets of people who keep a be'er eye on their wallet than on the back of their own 'ed.")
                                .addNPC(npcid, HeadE.CALM_TALK, "First you need to get 'em out of the reach of pryin' eyes, then just give 'em a good whack right 'ere and down they'll go.")
                                .addNPC(npcid, HeadE.CALM_TALK, "Then you can see what they've got that you migh' want. Got it?")
                                .addPlayer(HeadE.HAPPY_TALKING, "Yes, that was blunt enough. Thank you.");
                    }));
        }
    });

    public static NPCClickHandler TrainerLure = new NPCClickHandler(new Object[]{11296}, new String[]{"Lure"}, e -> {
        Player player = e.getPlayer();
        NPC npc = e.getNPC();
        player.startConversation(new Dialogue()
                .addPlayer(HeadE.AMAZED_MILD, Lure[Utils.random(6)])
                .addNPC(npc.getId(), HeadE.SCARED, "Wha?")
                .addNext(() -> {
                    //TODO Lure
                }));
    });

    public static NPCClickHandler TrainerCosh = new NPCClickHandler(new Object[]{11296}, new String[]{"Knock-out"}, e -> {
        Player player = e.getPlayer();
        NPC npc = e.getNPC();
        if (player.getEquipment().getWeaponId() != 18644) {
            player.playerDialogue(HeadE.SKEPTICAL_THINKING, "I'll need a training cosh to practise my technique with.");
            return;
        }
        //TODO blackjacking
        player.sendMessage("Blackjacking has not been added yet");
    });
}
