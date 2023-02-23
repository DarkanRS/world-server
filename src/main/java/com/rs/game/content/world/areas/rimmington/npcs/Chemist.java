package com.rs.game.content.world.areas.rimmington.npcs;

import com.rs.game.content.achievements.AchievementSystemDialogue;
import com.rs.game.content.achievements.SetReward;
import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.HeadE;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class Chemist extends Conversation {

    public static int CHEMIST = 367;

    public Chemist(Player player) {
        super(player);
        addOptions("Choose an option:", ops -> {
            ops.add("Ask about lamp oil.")
                .addPlayer(HeadE.CALM_TALK, "Hi, I need fuel for a lamp.")
                .addNPC(CHEMIST, HeadE.CALM_TALK, "Hello there, the fuel you need is lamp oil, do you need help making it?")
                .addOptions("Choose an option:", ops2 -> {
                    ops2.add("Yes please.")
                        .addPlayer(HeadE.CALM_TALK, "Yes please.")
                        .addNPC(CHEMIST, HeadE.CALM_TALK, "It's really quite simple. all set up, so there's no fiddling around with dials... Just put ordinary swamp tar in, and then use a lantern or lamp to get the oil out.")
                        .addPlayer(HeadE.CALM_TALK, "Thanks.");
                    ops2.add("No thanks.")
                        .addPlayer(HeadE.CALM_TALK, "No thanks.");
                });
            if (player.getInventory().containsItem(11262))
                ops.add("Ask about impling jars.")
                    .addNPC(CHEMIST, HeadE.CALM_TALK, "My lamp oil still may be able to do what you want. Use the oil and flower mix on the still.")
                    .addNPC(CHEMIST, HeadE.CALM_TALK, "Once that's done, get one of those butterfly jars to collect the distillate.")
                    .addPlayer(HeadE.CALM_TALK, "Thanks!");
            else
                if (player.getInventory().containsItem(11264))
                    ops.add("Ask about impling jars.")
                        .addPlayer(HeadE.CALM_TALK, "Do you know how I might distil a mix of anchovy oil and flowers so that it forms a layer on the inside of a butterfly jar?")
                        .addNPC(CHEMIST, HeadE.CALM_TALK, "My lamp oil still may be able to do what you want. " + (player.getInventory().containsOneItem(6010, 6012, 6014, 2460, 2462, 2466, 2468, 2470, 2472, 2474, 2474, 2476) ? "You'll need to mix your flowers to the anchovy oil first. Then use the oil and flower mix on the still." : "You'll need to get flowers first, then mix them to your anchovy oil. Then use the oil and flower mix on the still."))
                        .addNPC(CHEMIST, HeadE.CALM_TALK, "Once that's done, " + (player.getInventory().containsOneItem(10012) ? "use" : "get") + " one of those butterfly jars to collect the distillate.")
                        .addPlayer(HeadE.CALM_TALK, "Thanks!");
                else
                    ops.add("Ask about impling jars.")
                        .addPlayer(HeadE.CALM_TALK, "I have a slightly odd question.")
                        .addNPC(CHEMIST, HeadE.CALM_TALK, "Jolly good, the odder the better. I like oddities.")
                        .addPlayer(HeadE.CALM_TALK, "Do you know how I might distil a mix of anchovy oil and flowers so that it forms a layer on the inside of a butterfly jar?")
                        .addNPC(CHEMIST, HeadE.CALM_TALK, "That is an odd question. I commend you for it. Why would you want to do that?")
                        .addPlayer(HeadE.CALM_TALK, "Apparently, if I can make a jar like this it will be useful for storing implings in.")
                        .addNPC(CHEMIST, HeadE.CALM_TALK, "So, do you have any of this fish-and-flower flavoured oil and a butterfly jar then?")
                        .addPlayer(HeadE.CALM_TALK, "Actually, no.")
                        .addNPC(CHEMIST, HeadE.CALM_TALK, "If you go and get them then I may be able to help you. I'm better at coming up with answers if the questions are in my hands.")
                        .addNPC(CHEMIST, HeadE.CALM_TALK, "Is there anything else you want to ask?")
                        .addOptions("Choose an option:", ops2 -> {
                            ops2.add("So how do you make anchovy oil?")
                                .addPlayer(HeadE.CALM_TALK, "How do you make anchovy oil?")
                                .addNPC(CHEMIST, HeadE.CALM_TALK, "Anchovies are pretty oily fish. I'd have thought you could just grind them up using a pestle and mortar and sieve out the bits.")
                                .addNPC(CHEMIST, HeadE.CALM_TALK, "You'd probably want to remove any water first - Cooking should do that pretty well. I reckon you'll need to sieve 8 lots of anchovies[sic] paste to get one vial of anchovy oil.");
                            if (!player.getInventory().containsItem(6097))
                                ops2.add("Do you have a sieve I can use?")
                                    .addPlayer(HeadE.CALM_TALK, "Do you have a sieve I can use?")
                                    .addNPC(CHEMIST, HeadE.CALM_TALK, "Errm, yes. Here, have this one. It's only been used for sieving dead rats out of sewer water.")
                                    .addPlayer(HeadE.CALM_TALK, "Err, why? Actually, on second thoughts I don't want to know.")
                                    .addNPC(CHEMIST, HeadE.CALM_TALK, "Well, it should be ideally suited to your task.")
                                    .addItem(6097, "The chemist gives you a sieve.", () -> { player.getInventory().addItemDrop(6097, 1); });
                            else
                                ops2.add("Do you have a sieve I can use?")
                                    .addPlayer(HeadE.CALM_TALK, "Do you have a sieve I can use?")
                                    .addNPC(CHEMIST, HeadE.CALM_TALK, "Errm, yes. But you already have one. Two sieves is a bit excessive, don't you think?");
                            ops2.add("I'd better go and get the ingredients.")
                                .addPlayer(HeadE.CALM_TALK, "I'd better go and get the ingredients.")
                                .addNPC(CHEMIST, HeadE.CALM_TALK, "I think so.");
                        });
            ops.add("About the Task System...", new AchievementSystemDialogue(player, CHEMIST, SetReward.FALADOR_SHIELD).getStart());
        });
    }

    public static NPCClickHandler handleChemist = new NPCClickHandler(new Object[] { CHEMIST }, e -> {
        e.getPlayer().startConversation(new Chemist(e.getPlayer()));
    });
}