package com.rs.game.content.quests.buyersandcellars.npcs;

import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.quest.Quest;
import com.rs.game.World;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.object.GameObject;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Tile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;
@PluginEventHandler
public class FatherUrhney {
    private static final int npcid = 458;

    public static NPCClickHandler Urhney = new NPCClickHandler(new Object[] { npcid }, new String[] {"Pickpocket"}, e -> {
        if(!e.getPlayer().getInventory().hasFreeSlots()){
            e.getPlayer().sendMessage("You have no room in your inventory");
            return;
        }
        if(!fireIsLit(e.getPlayer()) && e.getPlayer().getQuestStage(Quest.BUYERS_AND_CELLARS) == 7) {
            e.getPlayer().sendMessage("For an old man, he's very alert. You can't get an opportunity to pick his pocket.");
            return;
        }
        if(fireIsLit(e.getPlayer()) && e.getPlayer().getQuestStage(Quest.BUYERS_AND_CELLARS) == 7){
            e.getPlayer().faceEntity(e.getNPC());
            WorldTasks.delay(0, () -> {
                e.getPlayer().setNextAnimation(new Animation(881));
            });
            e.getPlayer().getInventory().addItem(18647);
            e.getPlayer().sendMessage("You take advantage of Urhney's panic to lift a complex-looking key from his pocket.");
            e.getPlayer().setQuestStage(Quest.BUYERS_AND_CELLARS, 7);
        }
        else
            e.getPlayer().sendMessage("For an old man, he's very alert. You can't get an opportunity to pick his pocket.");
    });

    private static final Tile[] FIRE_TILES = new Tile[] {
            Tile.of(3205, 3152, 0),
            Tile.of(3209, 3152, 0),
            Tile.of(3211, 3150, 0),
            Tile.of(3211, 3148, 0),
            Tile.of(3209, 3146, 0),
            Tile.of(3204, 3146, 0),
            Tile.of(3202, 3148, 0),
            Tile.of(3202, 3150, 0)
    };

    public static boolean fireIsLit(Player player) {
        for (GameObject obj : World.getAllObjectsInChunkRange(819593, 5)) {
            if (obj == null || obj.getDefinitions() == null || !obj.getDefinitions().getName().equals("Fire")) {
                continue;
            }
            for (Tile fireTile : FIRE_TILES) {
                if (fireTile.equals(obj.getTile())) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void stage4(Player player) {
        player.startConversation(new Dialogue()
                .addNPC(npcid, HeadE.ANGRY, "Go away! I'm meditating!")
                .addOptions(ops -> {
                    ops.add("Well, that's friendly.")
                            .addNPC(npcid, HeadE.FRUSTRATED, "I said go away!")
                            .addPlayer(HeadE.CALM_TALK, "Okay, okay, Sheesh, what a grouch.");
                    ops.add("I've come to repossess your house.")
                            .addNPC(npcid, HeadE.SCARED, "On what grounds?")
                            .addOptions(ops2 -> {
                                ops2.add("Repeated failiture to make morgage repayments.")
                                        .addNPC(npcid, HeadE.FRUSTRATED, "What")
                                        .addNPC(npcid, HeadE.FRUSTRATED, "But I don't have a morgage - I built this house myself.")
                                        .addPlayer(HeadE.CALM, "Sorry, I must have got the wrong address. All the houses look the same around here.")
                                        .addNPC(npcid, HeadE.FRUSTRATED, "What? What houses? This is the only one. What are you talking about?")
                                        .addPlayer(HeadE.CALM, "Never mind.");
                                ops2.add("I don't know, I just wanted this house.")
                                        .addNPC(npcid, HeadE.FRUSTRATED, "Oh, go away and stop wasting my time.");
                            });
                    ops.add("Nice chalice.")
                            .addPlayer(HeadE.CALM_TALK, "That's a nice chalice.")
                            .addOptions(ops2 -> {
                                ops2.add("Aren't you afraid it will be taken in lieu of mortgage payments?")
                                        .addPlayer(HeadE.CALM_TALK, "Aren't you afraid it will be taken in lieu of mortgage payments?")
                                        .addNPC(npcid,HeadE.FRUSTRATED, "Are you just here to bother me?");

                                ops2.add("The bailiffs are coming! Quick: hide the valuables!")
                                        .addPlayer(HeadE.SCARED, "The bailiffs are coming! Quick: hide the valuables!")
                                        .addNPC(npcid, HeadE.FRUSTRATED, "The thoughts I am entertaining about you are worth another three months' mediation, young " + player.getPronoun("man", "lady") + ".");

                                ops2.add("Can I have a look at that chalice?")
                                        .addPlayer(HeadE.SKEPTICAL_THINKING, "Can I have a look at that chalice?")
                                        .addNPC(npcid, HeadE.FRUSTRATED, "If you must. It's in the display case over there.")
                                        .addPlayer(HeadE.SKEPTICAL_HEAD_SHAKE, "I meant...a closer look.")
                                        .addNPC(npcid, HeadE.FRUSTRATED, "It's only a couple of inches from the glass.")
                                        .addPlayer(HeadE.CALM_TALK, "Can I hold it?")
                                        .addNPC(npcid, HeadE.ANGRY, "And get grubby fingermarks over it? I think not.")
                                        .addPlayer(HeadE.CALM_TALK, "Hmm. I'll need something more urgent to draw his eye. Perhaps Robin can help")
                                        .addNPC(npcid, HeadE.CALM_TALK, "What was that?")
                                        .addPlayer(HeadE.SKEPTICAL_HEAD_SHAKE, "Mumble mumble mumble!")
                                        .addNPC(npcid, HeadE.CALM_TALK, "Quite.", () -> {
                                            player.sendMessage("Perhaps you should ask Robin how best to distract the priest.");
                                            player.setQuestStage(Quest.BUYERS_AND_CELLARS, 5);
                                        });
                                ops2.add("Bye, then.")
                                        .addPlayer(HeadE.CALM_TALK, "Bye, then.")
                                        .addNPC(npcid, HeadE.FRUSTRATED, "Bah.");
                            });
                    ops.add("Bye, then.")
                            .addPlayer(HeadE.CALM_TALK, "Bye, then.")
                            .addNPC(npcid, HeadE.FRUSTRATED, "Bah.");
                })
        );
    }
    public static void stage6(Player player, NPC npc) {
        player.startConversation(new Dialogue()
                .addNPC(npcid, HeadE.ANGRY, "Go away! I'm meditating!")
                .addOptions(ops -> {
                    ops.add("Well, that's friendly.")
                            .addNPC(npcid, HeadE.FRUSTRATED, "I said go away!")
                            .addPlayer(HeadE.CALM_TALK, "Okay, okay, Sheesh, what a grouch.");
                    ops.add("I've come to repossess your house.")
                            .addNPC(npcid, HeadE.SCARED, "On what grounds?")
                            .addOptions(ops2 -> {
                                ops2.add("Repeated failiture to make morgage repayments.")
                                        .addNPC(npcid, HeadE.FRUSTRATED, "What")
                                        .addNPC(npcid, HeadE.FRUSTRATED, "But I don't have a morgage - I built this house myself.")
                                        .addPlayer(HeadE.CALM, "Sorry, I must have got the wrong address. All the houses look the same around here.")
                                        .addNPC(npcid, HeadE.FRUSTRATED, "What? What houses? This is the only one. What are you talking about?")
                                        .addPlayer(HeadE.CALM, "Never mind.");
                                ops2.add("I don't know, I just wanted this house.")
                                        .addNPC(npcid, HeadE.FRUSTRATED, "Oh, go away and stop wasting my time.");
                            });

                    if(player.getQuestStage(Quest.BUYERS_AND_CELLARS) == 8)
                        ops.add("Nice chalice.")
                                .addPlayer(HeadE.SKEPTICAL_THINKING, "Nice.. um.. never mind. Bye!");

                    if(player.getQuestStage(Quest.BUYERS_AND_CELLARS) == 6 || player.getQuestStage(Quest.BUYERS_AND_CELLARS) == 7)
                        ops.add("Nice chalice.")
                                .addPlayer(HeadE.CALM_TALK, "That's a nice chalice.")
                                .addOptions(ops2 -> {
                                    ops2.add("Aren't you afraid it will be taken in lieu of mortgage payments?")
                                            .addPlayer(HeadE.CALM_TALK, "Aren't you afraid it will be taken in lieu of mortgage payments?")
                                            .addNPC(npcid,HeadE.FRUSTRATED, "Are you just here to bother me?");
                                    ops2.add("The bailiffs are coming! Quick: hide the valuables!")
                                            .addPlayer(HeadE.SCARED, "The bailiffs are coming! Quick: hide the valuables!")
                                            .addNPC(npcid, HeadE.FRUSTRATED, "The thoughts I am entertaining about you are worth another three months' mediation, young " + player.getPronoun("man", "lady") + ".");
                                    ops2.add("Can I have a look at that chalice?")
                                            .addPlayer(HeadE.SKEPTICAL_THINKING, "Can I have a look at that chalice?")
                                            .addNPC(npcid, HeadE.FRUSTRATED, "If you must. It's in the display case over there.")
                                            .addPlayer(HeadE.SKEPTICAL_HEAD_SHAKE, "I meant...a closer look.")
                                            .addNPC(npcid, HeadE.FRUSTRATED, "It's only a couple of inches from the glass.")
                                            .addPlayer(HeadE.CALM_TALK, "Can I hold it?")
                                            .addNPC(npcid, HeadE.ANGRY, "And get grubby fingermarks over it? I think not.");

                                    if(!fireIsLit(player))
                                        ops2.add("Fire! Fire!")
                                                .addPlayer(HeadE.SCARED,"Fire! FIRE!")
                                                .addNPC(npcid, HeadE.FRUSTRATED, "Don't be foolish.");
                                    else
                                        ops2.add("Fire! Fire!")
                                                .addPlayer(HeadE.SCARED,"Fire! FIRE!")
                                                .addNPC(npcid, HeadE.SCARED, "Oh, no! My house...that I built with my own two hands!")
                                                .addPlayer(HeadE.LAUGH, "Ha, now's my chance to pick his pocket while he's distracted...", () -> {
                                                    player.setQuestStage(Quest.BUYERS_AND_CELLARS, 7);
                                                    npc.forceTalk("Accursed kids. Light a fire under my window? I'll teach them a lesson when I find them...");
                                                });
                                });
                    ops.add("Bye, then.")
                            .addPlayer(HeadE.CALM_TALK, "Bye, then.")
                            .addNPC(npcid, HeadE.FRUSTRATED, "Bah.");
                })
        );
    }
}
