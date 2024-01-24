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

import static com.rs.game.content.miniquests.FromTinyAcorns.Darren.FromTinyAcornsOptions;
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
        int thievingLevel = player.getSkills().getLevelForXp(Skills.THIEVING);
        int agilityLevel = player.getSkills().getLevelForXp(Skills.AGILITY);
        int herbloreLevel = player.getSkills().getLevelForXp(Skills.HERBLORE);
        player.startConversation(new Dialogue()
                .addNPC(npcid, HeadE.HAPPY_TALKING, "Greetings, my young recruit!")
                .addOptions(ops -> {
                    ops.add("Can we try out that testing dummy again?")
                            .addPlayer(HeadE.CALM_TALK, "Can we try out that testing dummy again?")
                            .addNext(() -> {
                                player.npcDialogue(npcid, HeadE.HAPPY_TALKING, "Of course!");
                                player.walkToAndExecute(Tile.of(4664, 5903, 0), () -> player.getActionManager().setAction(new PickPocketDummy(new GameObject(52316, 1, 4665, 5903, 0))));
                            });


                    ops.add("How's the guild coming along these days?", () -> {
                        if (player.isMiniquestComplete(Miniquest.A_GUILD_OF_OUR_OWN)) {
                            //AGuildOfOurOwnTXT
                            return;
                        }
                        if (player.isMiniquestComplete(Miniquest.LOST_HER_MARBLES)) {
                            //LostHerMarblesOptionsTXT
                            return;
                        }
                        if (player.isMiniquestComplete(Miniquest.FROM_TINY_ACORNS)) {
                            player.startConversation(new Dialogue()
                                    .addPlayer(HeadE.CALM_TALK, "How's the guild coming along these days?")
                                    .addNPC(npcid, HeadE.HAPPY_TALKING, "A coshing tutor has moved in and we've now opened a store. We'll need more funds if we're to continue with renovations. Anything else I can do for you? ")
                            );
                            return;
                        } else {
                            player.startConversation(new Dialogue()
                                    .addPlayer(HeadE.CALM_TALK, "How's the guild coming along these days?")
                                    .addNPC(npcid, HeadE.HAPPY_TALKING, "We're really only getting started at the moment. I've made a training dummy to practice on, but we'll need funds if we're to begin to command any respect. Anything else I can do for you?")
                            );
                        }
                    });


                    ops.add("I'd like to talk about capers.")
                            .addNext(() -> {
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
}

