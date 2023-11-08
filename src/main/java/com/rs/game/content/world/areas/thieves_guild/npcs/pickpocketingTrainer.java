package com.rs.game.content.world.areas.thieves_guild.npcs;

import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.game.content.skills.thieving.HankyPoints;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class pickpocketingTrainer {
    public static NPCClickHandler pickpocket = new NPCClickHandler(new Object[] { 11281 }, new String[] {"Talk-to"}, e -> {
        int npcid = e.getNPCId();
        Player player = e.getPlayer();
        player.startConversation(new Dialogue()
                .addPlayer(HeadE.HAPPY_TALKING, "Hello there.")
                .addNPC(npcid, HeadE.CALM_TALK, "Ah, Player. I'm doing classes on picking pockets, if you're interested.")
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

                    ops.add("Bye for now.")
                            .addPlayer(HeadE.HAPPY_TALKING, "Bye for now.");
                    ops.add("Not at the moment, thanks.")
                            .addNPC(npcid, HeadE.CALM_TALK, "You can give it a try with myself or my colleagues, although we've not got anything but hankies.");
                    ops.add(" Give me a few pointers, then.")
                            .addNPC(npcid, HeadE.CALM_TALK, "Picking a pocket is a simple thing, really. Just act casual, keep your fingers loose, and be sure you can take a beating if you're spotted. Most people don;t take kindly to someone running a hand through their belongings, and they'll give you a good whack if they catch you at it.")
                            .addPlayer(HeadE.HAPPY_TALKING, "Thank you for that stunning revelation.");
                }));
    });

}
