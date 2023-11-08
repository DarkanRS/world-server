package com.rs.game.content.world.areas.thieves_guild.npcs;

import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.game.content.skills.thieving.HankyPoints;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class LocksAndTrapsTrainer {
    public static NPCClickHandler locksandtraps = new NPCClickHandler(new Object[] { 11294 }, new String[] {"Talk-to"}, e -> {
        int npcid = e.getNPCId();
        Player player = e.getPlayer();
        player.startConversation(new Dialogue()
                .addPlayer(HeadE.HAPPY_TALKING, "What can you teach me?")
                .addNPC(npcid, HeadE.CALM_TALK, "I'm the locks and traps trainer. Need any advice?")
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

                    ops.add("That's alright, thanks.")
                            .addNPC(npcid, HeadE.CALM_TALK, "Feel free to practise on my coffers; the ones in the south are examples of more complex locks.");
                    ops.add("I'm always happy to learn.")
                            .addPlayer(HeadE.HAPPY_TALKING, "I'm always happy to learn.")
                            .addNPC(npcid, HeadE.CALM_TALK, "You'll come across a fair few doors and chests around the world, and people tend to lock 'em. " +
                                    "With a bit of skill, though, and sometimes some specialised tools, you can crack them open and disarm the triggers on the traps they hold. " +
                                    "If something looks suspicious, examine it closely before you touch it.")
                            .addPlayer(HeadE.CONFUSED, "Specialised tools?")
                            .addNPC(npcid, HeadE.CALM_TALK, "Some locks are more complex than others; trickier ones need a lockpick. You can buy one in the shop if you lose yours or it breaks.")
                            .addPlayer(HeadE.HAPPY_TALKING, "Another mystery unlocked. Thank you.")
                            .addNPC(npcid, HeadE.HAPPY_TALKING, "Feel free to practise on my coffers; the ones in the south are examples of more complex locks.");
                }));
    });

}
