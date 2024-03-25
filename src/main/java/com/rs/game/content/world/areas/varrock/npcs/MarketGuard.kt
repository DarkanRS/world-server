package com.rs.game.content.world.areas.varrock.npcs;

import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.miniquest.Miniquest;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Tile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.utils.Ticks;

@PluginEventHandler
public class MarketGuard {
    private static final int npcID = 11269;

    public static NPCClickHandler MarketGuard = new NPCClickHandler(new Object[]{ npcID }, new String[]{"Talk-to"}, e -> {
        if (!e.getPlayer().isMiniquestStarted(Miniquest.FROM_TINY_ACORNS)) {
            preQuest(e.getPlayer());
            return;
        }
        if(e.getPlayer().getMiniquestManager().getAttribs(Miniquest.FROM_TINY_ACORNS).getB("UristDistracted")) {
            distracted(e.getPlayer(), e.getNPC());
            return;
        }
        switch (e.getPlayer().getMiniquestStage(Miniquest.FROM_TINY_ACORNS)) {
            case 1 -> stage1(e.getPlayer());
            //case 2 -> stage2(e.getPlayer());
            default -> stage1(e.getPlayer());
        }
    });

    private static void preQuest(Player player) {
        player.startConversation(new Dialogue()
                .addNPC(npcID, HeadE.CALM_TALK, "Greetings, citizen.")
                .addPlayer(HeadE.CHEERFUL, "Good day to you.")
                .addOptions(ops -> {
                    ops.add("How's everything going today?")
                            .addNPC(npcID, HeadE.CALM, "Fairly uneventful so far. Varrock's not the hotbed of crime that Ardougne is, after all.")
                            .addPlayer(HeadE.HAPPY_TALKING, "I'll let you get on with it then.")
                            .addNPC(npcID, HeadE.CALM, "Stay safe, citizen.");

                    ops.add("Goodbye.")
                            .addPlayer(HeadE.HAPPY_TALKING, "Goodbye.")
                            .addNPC(npcID, HeadE.CALM, "Stay safe, citizen.");

                })
        );
    }

    private static void stage1(Player player) {
        player.startConversation(new Dialogue()
                .addNPC(npcID, HeadE.CALM_TALK, "Greetings, citizen.")
                .addPlayer(HeadE.CHEERFUL, "Good day to you.")
                .addOptions(ops -> {
                    ops.add("How's everything going today?")
                            .addNPC(npcID, HeadE.CALM, "Fairly uneventful so far. Varrock's not the hotbed of crime that Ardougne is, after all.")
                            .addPlayer(HeadE.HAPPY_TALKING, "I'll let you get on with it then.")
                            .addNPC(npcID, HeadE.CALM, "Stay safe, citizen.");

                    ops.add("Who's the dwarf over there?")
                            .addPlayer(HeadE.SKEPTICAL, "Who's the dwarf over there?")
                            .addNPC(npcID, HeadE.SKEPTICAL, "Urist Loric. He's a craftsman from some fortress or other. Does some seriously delicate work, if the magnification on that monocle is anything to go by.")
                            .addPlayer(HeadE.SKEPTICAL, "Do you know what he's working on?")
                            .addNPC(npcID, HeadE.CALM_TALK, "It's a toy dragon of some sort. I've been keeping an eye on him 'cos he's working with some seriously valuable materials, but nobody's tried anything yet. Nobody's going to either, I reckon. Must have heard I'm on the case.")
                            .addPlayer(HeadE.CHEERFUL_EXPOSITION, "How's his dragon coming along?")
                            .addNPC(npcID, HeadE.CALM_TALK, "Looks like it's almost finished.")
                            .addPlayer(HeadE.CHEERFUL, "He seems very focused.")
                            .addNPC(npcID, HeadE.CALM_TALK, "There's only two things he cares more about than his work, and he's had his morning booze.")
                            .addPlayer(HeadE.SKEPTICAL, "What's the other thing?")
                            .addNPC(npcID, HeadE.CALM_TALK, "Oh, some sort of talisman he keeps in his back pocket. I've told him he shouldn't keep it there - it's asking to be stolen - but he won't listen.")
                            .addPlayer(HeadE.HAPPY_TALKING, "I'll let you get on with it, then.")
                            .addNPC(npcID, HeadE.CALM_TALK, "Stay safe, citizen.");

                    ops.add("Goodbye.")
                            .addPlayer(HeadE.HAPPY_TALKING, "Goodbye.")
                            .addNPC(npcID, HeadE.CALM, "Stay safe, citizen.");

                })
        );
    }

    private static void distracted(Player player, NPC npc) {
        player.startConversation(new Dialogue()
                .addPlayer(HeadE.CHEERFUL_EXPOSITION, "Gypsy Aris' sign is giving you the evil eye!")
                .addNPC(npcID, HeadE.CONFUSED, "What? Not again!", () -> {
                    final int[] i = {0};
                    WorldTasks.schedule(Ticks.fromSeconds(0),Ticks.fromSeconds(5), () -> {
                        boolean distracted = player.getMiniquestManager().getAttribs(Miniquest.FROM_TINY_ACORNS).getB("UristDistracted");
                        if (distracted) {
                            if(i[0] == 0) {
                                npc.faceTile(Tile.of(3208, 3423, 0));
                                npc.forceTalk("Hmm, it looks innocuous to me.");
                                player.getMiniquestManager().getAttribs(Miniquest.FROM_TINY_ACORNS).setB("GuardDistracted", true);
                                i[0]++;
                            }
                            else {
                                npc.forceTalk("Hmm..");
                            }
                        }
                        else {
                            player.getMiniquestManager().getAttribs(Miniquest.FROM_TINY_ACORNS).setB("GuardDistracted", false);
                            npc.faceEast();
                        }
                    });
                })
        );
    }
}
