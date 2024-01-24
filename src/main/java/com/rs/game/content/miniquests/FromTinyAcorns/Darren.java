package com.rs.game.content.miniquests.FromTinyAcorns;

import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.miniquest.Miniquest;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.Skills;
import com.rs.lib.game.Tile;

public class Darren {
    public static final int npcid = 11273;
    public static void FromTinyAcornsOptions(Player player) {
        switch (player.getMiniquestStage(Miniquest.FROM_TINY_ACORNS)) {
            default -> player.startConversation(new Dialogue()
                    .addNPC(npcid, HeadE.HAPPY_TALKING, "Ah, " + player.getDisplayName() + " Can I borrow you for a moment?")
                    .addNPC(npcid, HeadE.HAPPY_TALKING, "I've got some work in dire need of a hero and you're the best agent I have to take it on!")
                    .addNPC(npcid, HeadE.HAPPY_TALKING, "I'll put it to you very directly, " + player.getDisplayName() + " it's an expensive business expanding the guild.")
                    .addNPC(npcid, HeadE.HAPPY_TALKING, "The price of construction work is exorbitant, and if we're to get the premises up to the size of our eventual status deserves we shall be needing more money.")
                    .addPlayer(HeadE.CALM_TALK, "I understand well.")
                    .addNPC(npcid, HeadE.HAPPY_TALKING, "That's why I've bought a very expensive toy dragon, which is quite nearly completed.")
                    .addPlayer(HeadE.CALM_TALK, "Wait, what?")
                    .addNPC(npcid, HeadE.HAPPY_TALKING, "Allow me to explain... There is a master craftsman recently arrived in Varrock, a dwarf by the name of Urist Loric.")
                    .addNPC(npcid, HeadE.HAPPY_TALKING, "He does clockwork and delicate crafts, and works extensively in precious stones.")
                    .addNPC(npcid, HeadE.HAPPY_TALKING, "I have commissioned him to construct a red dragon - worth the entirety of our available monies - out of ruby.")
                    .addPlayer(HeadE.SKEPTICAL_HEAD_SHAKE, "This sounds more like madness than adventure at the moment.")
                    .addNPC(npcid, HeadE.HAPPY_TALKING, "I have no intention of buying it. What I need you to do, my dear " + player.getPronoun("fellow", "lady") + ", is steal the dragon from his stall in Varrock.")
                    .addNPC(npcid, HeadE.HAPPY_TALKING, "You can then be very surprised and dismayed at him and demand my money returned.")
                    .addPlayer(HeadE.CALM_TALK, "Then I bring you the toy and your money, and you fence the toy and double your investment?")
                    .addNPC(npcid, HeadE.HAPPY_TALKING, "Double? Ha! I'm having a bad day.")
                    .addOptions(ops -> {
                        ops.add("Alright, I'll do it.")
                                .addPlayer(HeadE.CALM_TALK, "Alright, I'll do it.")
                                .addNPC(npcid, HeadE.HAPPY_TALKING, "I knew I could count on you.", () -> player.getMiniquestManager().setStage(Miniquest.FROM_TINY_ACORNS, 1));
                        ops.add("Let me think about it and come back.")
                                .addPlayer(HeadE.CALM_TALK, "Let me think about it and come back.")
                                .addNPC(npcid, HeadE.HAPPY_TALKING, "Don't be too long; if he finishes the thing I'll have to take delivery of it.");
                    }));
            case 1 -> player.startConversation(new Dialogue()
                    .addPlayer(HeadE.CALM_TALK, "I'd like to talk about the caper I'm doing for you.")
                    .addPlayer(HeadE.CALM_TALK, "I've not got the baby toy dragon and initial investment back yet, I'm afraid.")
                    .addNPC(npcid, HeadE.HAPPY_TALKING, "Well, do hop to it, there's a good chap/lass. Time's a ticking!")
                    .addPlayer(HeadE.CALM_TALK, "Do you have any practical advice for how I should go about this?")
                    .addNPC(npcid, HeadE.HAPPY_TALKING, "Practical? My word, no. Robin's your fellow if you want practical matters attended to; I mostly do strategy.")
            );
            case 2 -> player.startConversation(new Dialogue()
                    .addPlayer(HeadE.CALM_TALK, "I'd like to talk about the caper I'm doing for you.")
                    .addPlayer(HeadE.CALM_TALK, "I've got the baby toy dragon but I don't initial investment back yet, I'm afraid.")
                    .addNPC(npcid, HeadE.HAPPY_TALKING, "Excellent! Well, do hop to it, there's a good chap/lass. Time's a ticking!")
            );
            case 3 -> {
                player.startConversation(new Dialogue()
                        .addPlayer(HeadE.CALM_TALK, "I'd like to talk about the caper I'm doing for you.")
                        .addNext(() -> {
                            int dragonID = 18651;
                            int noteID = 18652;
                            if (!player.getInventory().containsItem(dragonID) && player.getBank().containsItem(dragonID, 1)) {
                                player.playerDialogue(HeadE.SHAKING_HEAD, "I have the dragon! I'll just need a moment to visit the bank before I hand it over.");
                                return;
                            }
                            if (!player.getInventory().containsItem(noteID) && player.getBank().containsItem(noteID, 1)) {
                                player.playerDialogue(HeadE.SHAKING_HEAD, "I have the bankers note! I'll just need a moment to visit the bank before I hand it over.");
                                return;
                            }
                            player.startConversation(new Dialogue()
                                    .addNPC(npcid, HeadE.HAPPY_TALKING, "I knew you wouldn't let me down! Fairly simple caper, was it?")
                                    .addPlayer(HeadE.CALM_TALK, "Just needed a little finesse, that's all.")
                                    .addNPC(npcid, HeadE.HAPPY_TALKING, "Ah, finesse! Very well done indeed. Some day I hope to show you myself in action; for now, however, I shall be rather busy paying the builders.")
                                    .addNext(() -> {
                                        player.fadeScreen(() -> {
                                            player.getInventory().deleteItem(18651, 1);
                                            player.getInventory().deleteItem(18652, 1);
                                            player.getSkills().addXp(Skills.THIEVING, 1000);
                                            player.tele(Tile.of(3223, 3269, 0));
                                            player.getMiniquestManager().complete(Miniquest.FROM_TINY_ACORNS);
                                        });
                                    })
                            );
                        })
                );
            }
        }
    }
}
