package com.rs.game.content.world.areas.lumbridge.npcs;

import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;
@PluginEventHandler
public class FatherUrhney {
    private static final int NPC = 458;
    public static NPCClickHandler Urhney = new NPCClickHandler(new Object[]{NPC}, new String[]{"Talk-to"}, e -> {
        Player player = e.getPlayer();
        boolean hasRestlessGhost = player.isQuestComplete(Quest.RESTLESS_GHOST);
        boolean hasBuyersAndCellars = player.isQuestComplete(Quest.BUYERS_AND_CELLARS);
        int stageRestlessGhost = player.getQuestManager().getStage(Quest.RESTLESS_GHOST);
        int stageBuyersAndCellars = player.getQuestManager().getStage(Quest.BUYERS_AND_CELLARS);
        player.startConversation(new Dialogue()
                .addNPC(NPC, HeadE.FRUSTRATED, "Get out of my house!")
                .addOptions(questOptions -> {
                    if (stageRestlessGhost == 1)
                        questOptions.add("Talk about Restless Ghost.", () -> restlessGhostStage1(player));

                    if (hasRestlessGhost || stageRestlessGhost >= 2)
                        questOptions.add("Talk about Restless Ghost.", () -> afterRestlessGhost(player));

                    if (stageBuyersAndCellars >= 4 && !hasBuyersAndCellars)
                        questOptions.add("Talk about Buyers and Cellars.", () -> {
                            if (stageBuyersAndCellars == 4 || stageBuyersAndCellars == 5) {
                                com.rs.game.content.quests.buyersandcellars.npcs.FatherUrhney.stage4(player);
                            }
                            else
                                com.rs.game.content.quests.buyersandcellars.npcs.FatherUrhney.stage6(player, e.getNPC());
                        });

                    questOptions.add("Nevermind")
                            .addPlayer(HeadE.CALM_TALK, "Erm nothing, Nevermind.")
                            .addNPC(NPC, HeadE.FRUSTRATED, "Bah.");
                })
        );
    });

    public static void restlessGhostStage1(Player player) {
        player.startConversation(new Dialogue()
                .addNext(() -> {
                    if (!player.getInventory().containsItem(552, 1)) {
                        player.startConversation(new Dialogue()
                                .addPlayer(HeadE.HAPPY_TALKING, "Father Aereck told me to come talk to you about a ghost haunting his graveyard.")
                                .addNPC(NPC, HeadE.FRUSTRATED, "Oh the silly old fool. Here, take this amulet and see if you can communicate with the spectre", () -> {
                                    player.getInventory().addItem(552, 1);
                                    player.getQuestManager().setStage(Quest.RESTLESS_GHOST, 2);
                                })
                                .addPlayer(HeadE.HAPPY_TALKING, "Thank you. I'll try.")
                        );
                    }
                    player.getQuestManager().setStage(Quest.RESTLESS_GHOST, 2);
                }));
    };

    public static void afterRestlessGhost(Player player) {
        player.startConversation(new Dialogue()
                .addNext(() -> {
                    if (player.getInventory().containsItem(552, 1) || player.getEquipment().getNeckId() == 552) {
                        player.npcDialogue(NPC, HeadE.FRUSTRATED, "What are you talking about? I can see you've got it with you.");
                        return;
                    }
                    if (player.getBank().containsItem(552, 1)) {
                        player.npcDialogue(NPC, HeadE.FRUSTRATED, "Why do you insist on wasting my time? Has it even occurred to you to look in your bank? Now go away!");
                        return;
                    } else {
                        player.startConversation(new Dialogue()
                                .addPlayer(HeadE.HAPPY_TALKING, "I've lost my amulet of ghostspeak.")
                                .addNPC(NPC, HeadE.CALM_TALK, "Have another one then. But be more careful next time!", () -> player.getInventory().addItem(552, 1))
                                .addPlayer(HeadE.HAPPY_TALKING, "Thank you. I'll try.")
                        );
                    }
                }));
    }
}
