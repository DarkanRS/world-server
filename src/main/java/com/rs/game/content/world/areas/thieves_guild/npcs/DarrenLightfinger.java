package com.rs.game.content.world.areas.thieves_guild.npcs;

import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.miniquest.Miniquest;
import com.rs.engine.quest.Quest;
import com.rs.game.content.skills.thieving.thievesGuild.PickPocketDummy;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.Skills;
import com.rs.game.model.object.GameObject;
import com.rs.lib.game.Tile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

import static com.rs.game.content.quests.buyersandcellars.npcs.DarrenLightfinger.*;

@PluginEventHandler
public class DarrenLightfinger {
    public static final int npcid = 11273;

    public static NPCClickHandler DarrenLightfingerI = new NPCClickHandler(new Object[]{npcid}, new String[]{"Talk-to"}, e -> {
        if (!e.getPlayer().isQuestStarted(Quest.BUYERS_AND_CELLARS)) {
            preQuest(e.getPlayer());
            return;
        }
        switch (e.getPlayer().getQuestStage(Quest.BUYERS_AND_CELLARS)) {
            case 1 -> preQuest(e.getPlayer());
            case 2 -> stage2(e.getPlayer());
            default -> stage3(e.getPlayer());
        }
    });

    public static void stage3(Player player) {
        player.startConversation(new Dialogue()
                .addNPC(npcid, HeadE.HAPPY_TALKING, "Greetings, my young recruit!")
                .addOptions(ops -> {
                    ops.add("Can we try out that testing dummy again?")
                            .addPlayer(HeadE.CALM_TALK, "Can we try out that testing dummy again?")
                            .addNext(() -> {
                                player.npcDialogue(npcid, HeadE.HAPPY_TALKING, "Of course!");
                                player.walkToAndExecute(Tile.of(4664, 5903, 0), () -> player.getActionManager().setAction(new PickPocketDummy(new GameObject(52316, 1, 4665, 5903, 0))));
                            });


                    ops.add("How's the guild coming along these days?")
                            .addPlayer(HeadE.CALM_TALK, "How's the guild coming along these days?")
                            .addNPC(npcid, HeadE.HAPPY_TALKING, "We're really only getting started at the moment. I've made a training dummy to practice on, but we'll need funds if we're to begin to command any respect. Anything else I can do for you?");


                    ops.add("I'd like to talk about the caper I'm doing for you.")
                            .addPlayer(HeadE.CALM_TALK, "I'd like to talk about the caper I'm doing for you.")
                            .addNext(() -> {
                                int thievingLevel = player.getSkills().getLevelForXp(Skills.THIEVING);
                                int agilityLevel = player.getSkills().getLevelForXp(Skills.AGILITY);
                                int herbloreLevel = player.getSkills().getLevelForXp(Skills.HERBLORE);
                                if (player.isQuestStarted(Quest.BUYERS_AND_CELLARS) && !player.isQuestComplete(Quest.BUYERS_AND_CELLARS)) {
                                    BuyersAndCellarsOptions(player);
                                    return;
                                }
                                if (thievingLevel >= 24 && !player.isMiniquestComplete(Miniquest.FROM_TINY_ACORNS)) {
                                    FromTinyAcornsOptions(player);
                                    return;
                                }
                                if (thievingLevel >= 41 && !player.isMiniquestComplete(Miniquest.LOST_HER_MARBLES)) {
                                    //LostHerMarblesOptions(player);
                                    return;
                                }
                                if (thievingLevel >= 62 && agilityLevel >= 40 && herbloreLevel >= 46 && !player.isMiniquestComplete(Miniquest.A_GUILD_OF_OUR_OWN)) {
                                    //AGuildOfOurOwnOptions(player);
                                    return;
                                }
                                else {
                                    player.startConversation(new Dialogue()
                                            .addNPC(npcid, HeadE.HAPPY_TALKING, "I don't have any capers for you at the moment, come back and see me in a little while"));
                                }

                            });
                    ops.add("Sorry, I was just leaving.")
                            .addPlayer(HeadE.CALM, "Sorry, I was just leaving.");
                }));
    }

    public static void BuyersAndCellarsOptions(Player player) {
        player.startConversation(new Dialogue()
                .addNPC(npcid, HeadE.HAPPY_TALKING, "Have you retrieved the chalice?", () -> {
                    switch (player.getQuestStage(Quest.BUYERS_AND_CELLARS)) {
                        case 3 -> player.startConversation(new Dialogue()
                                .addPlayer(HeadE.CALM_TALK, "Not yet.")
                                .addNPC(npcid, HeadE.HAPPY_TALKING, "Head to Lumbridge Castle as soon as you may, then; Robin will meet you there."));

                        case 4 -> player.startConversation(new Dialogue()
                                .addPlayer(HeadE.CALM_TALK, "Not yet, but I'm on its trail"));

                        case 5, 6 -> player.startConversation(new Dialogue()
                                .addPlayer(HeadE.CALM_TALK, "I've tracked it down, but I've not yet retrieved it."));

                        case 7 -> player.startConversation(new Dialogue()
                                .addPlayer(HeadE.CALM_TALK, "I have the key, but not the chalice.")
                                .addNPC(npcid, HeadE.CALM_TALK, "You've used several keys in the past, I'm sure; one more should pose no difficulty."));
                        case 8 -> player.startConversation(new Dialogue()
                                .addPlayer(HeadE.HAPPY_TALKING, "I have!")
                                .addNPC(npcid, HeadE.HAPPY_TALKING, "Fantastic work! I knew I had chosen wisely when I recruited you. Now we can expand the guild and do some proper training around here.")
                                .addPlayer(HeadE.SKEPTICAL_HEAD_SHAKE, "Your buyer is still interested, I hope?")
                                .addNPC(npcid, HeadE.CALM_TALK, "Yes, of course, why?")
                                .addPlayer(HeadE.CALM_TALK, "Well, the chalice wasn't where you said it was, nor was the owner; I just wanted to make sure you had something right in all of this.")
                                .addNPC(npcid, HeadE.LAUGH, "Ha! I do appreciate a sense of humor in my members.")
                                .addPlayer(HeadE.CALM_TALK, "It wasn't actually a joke, to be honest.")
                                .addNPC(npcid, HeadE.SKEPTICAL_HEAD_SHAKE, "To be honest? You don't want to be honest; you're a member of the illustrious Thieves' Guild! Now get out there and make me proud... and both of us rich!")
                                .addNext(() -> player.fadeScreen(() -> {
                                    player.getInventory().deleteItem(18648, 1);
                                    player.tele(Tile.of(3223, 3269, 0));
                                    player.getVars().saveVarBit(7792, 10);
                                    player.getVars().setVarBit(7793, 0);
                                    player.getQuestManager().completeQuest(Quest.BUYERS_AND_CELLARS);
                                })));
                    }
                }));
    }

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
                    .addNPC(npcid, HeadE.HAPPY_TALKING, "Allow me to explain… There is a master craftsman recently arrived in Varrock, a dwarf by the name of Urist Loric.")
                    .addNPC(npcid, HeadE.HAPPY_TALKING, "He does clockwork and delicate crafts, and works extensively in precious stones.")
                    .addNPC(npcid, HeadE.HAPPY_TALKING, "I have commissioned him to construct a red dragon – worth the entirety of our available monies – out of ruby.")
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
                    .addPlayer(HeadE.CALM_TALK, "I'd like to talk about the caper I'm doing for you. I've not got the baby toy dragon and initial investment back yet, I'm afraid.")
                    .addPlayer(HeadE.CALM_TALK, "Well, do hop to it, there's a good chap/lass. Time's a ticking!")
                    .addPlayer(HeadE.CALM_TALK, "Do you have any practical advice for how I should go about this?")
                    .addPlayer(HeadE.HAPPY_TALKING, "Practical? My word, no. Robin's your fellow if you want practical matters attended to; I mostly do strategy.")
            );
        }
    }
}

