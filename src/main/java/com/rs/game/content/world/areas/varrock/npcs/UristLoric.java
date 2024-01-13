package com.rs.game.content.world.areas.varrock.npcs;

import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.miniquest.Miniquest;
import com.rs.engine.quest.Quest;
import com.rs.game.World;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.object.GameObject;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.GroundItem;
import com.rs.lib.game.Tile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.utils.Ticks;

import java.util.Objects;

@PluginEventHandler
public class UristLoric {
    private static final int npcid = 11270;

    public static NPCClickHandler UristLoric = new NPCClickHandler(new Object[]{ npcid }, new String[]{"Talk-to"}, e -> {
        if (!e.getPlayer().isMiniquestStarted(Miniquest.FROM_TINY_ACORNS)) {
            //preQuest(e.getPlayer());
            return;
        }
        switch (e.getPlayer().getQuestStage(Quest.BUYERS_AND_CELLARS)) {
            case 1 -> stage1(e.getPlayer(), e.getNPC());
            //case 2 -> stage2(e.getPlayer());
            //default -> stage3(e.getPlayer());
        }
    });

    public static Tile getFloorDrop(Player player) {
        for (GroundItem groundItem : World.getAllGroundItemsInChunkRange(823724, 1)) {
            if (groundItem == null || groundItem.getDefinitions() == null || groundItem.getId() != 18649) {
                continue;
            }
            if (!groundItem.getTile().withinArea(3220, 3427, 3228, 3432)) {
                player.sendMessage("I should find a suitable spot to put this. Maybe just north of him…");
                continue;
            }
            return Tile.of(groundItem.getTile());
        }
        return null;
    }

    public static void stage1(Player player, NPC npc) {
        if(getFloorDrop(player) != null){
            player.startConversation(new Dialogue()
                    .addPlayer(HeadE.CALM_TALK, "That thing on the ground… Is it yours?")
                    .addNPC(npcid, HeadE.SCARED, "What thing? Oh! Thank you kindly, I'd have been sad if I'd lost that.", () -> {
                        npc.walkToAndExecute(getFloorDrop(player), () ->{
                            int loop = 0;
                            WorldTasks.schedule(Ticks.fromSeconds(5),Ticks.fromSeconds(5), () -> {
                            switch (loop) {
                                case 0 -> {
                                    npc.forceTalk("Hmm, how'd this get over here?");
                                    //World.removeGroundItem(player, item at tile getFloorDrop(player))
                                }
                            }
                            });
                        });
                    }));
            return;
        }
        if(player.getInventory().containsItem(18651)) {
            player.startConversation(new Dialogue()
                    .addPlayer(HeadE.CALM_TALK, "I'm just checking up on progress for Mr. Lightfinger. You said it would be ready in a couple of days?")
                    .addNPC(npcid, HeadE.HAPPY_TALKING, "Aye, it's nearly done. It's right he-")
                    .addPlayer(HeadE.CONFUSED, "...")
                    .addNPC(npcid, HeadE.SCARED, "By all the gods! Where's it gone? Where's it got to?")
                    .addPlayer(HeadE.CONFUSED, "It's not walked off, has it?")
                    .addNPC(npcid, HeadE.FRUSTRATED, "It couldn't walk that far on one turn of the spring.")
                    .addPlayer(HeadE.CALM_TALK, "I hope it turns up, then; it's due very soon. Could it have been stolen?")
                    .addNPC(npcid, HeadE.FRUSTRATED, "In Varrock? With that guard watching like a hawk?")
                    .addPlayer(HeadE.CALM_TALK, "He doesn't seem all that hawklike.")
                    .addNPC(npcid, HeadE.SKEPTICAL, "Now that you mention it…")
                    .addPlayer(HeadE.CALM_TALK, "Will you be able to finish by the deadline?")
                    .addNPC(npcid, HeadE.SHAKING_HEAD, "Are you serious? Not a chance!")
                    .addPlayer(HeadE.CALM_TALK, "Then what will you tell Mr. Lightfinger?")
                    .addNPC(npcid, HeadE.VERY_FRUSTRATED, "I don't see that I have much choice; I shall have to give him his money back.")
                    .addPlayer(HeadE.CALM_TALK, "That sounds fair.")
                    .addNPC(npcid, HeadE.HAPPY_TALKING, "Since you're working for him, can you take this banker's note to him with my sincerest apologies?")
                    .addPlayer(HeadE.CALM_TALK, "Urist hands you a banker's note. The figure on it is astronomical.", () -> {
                        player.getInventory().addItem(18652, 1); // Add Banker's note to player's inventory
                        player.getMiniquestManager().setStage(Miniquest.FROM_TINY_ACORNS, 2);
                    })
                    .addPlayer(HeadE.CALM_TALK, "I should think so.")
                    .addNPC(npcid, HeadE.CALM_TALK, "I appreciate your understanding.")
                    .addPlayer(HeadE.CALM_TALK, "I appreciate your cooperation.")
            );
        }
        else {
            player.startConversation(new Dialogue()
                    .addPlayer(HeadE.CALM_TALK, "Are you the master craftsman working on a commission for Darren Lightfinger?")
                    .addNPC(npcid, HeadE.HAPPY_TALKING, "That I am. What can I do for you?")
                    .addPlayer(HeadE.CALM_TALK, "How's it coming along?")
                    .addNPC(npcid, HeadE.HAPPY_TALKING, "It's very nearly done. Just needs a bath in preserving oil to protect the mechanisms, and then a good polish.")
                    .addPlayer(HeadE.CALM_TALK, "Is this it here on your stall?")
                    .addNPC(npcid, HeadE.HAPPY_TALKING, "Aye, the baby red dragon there. As ordered, its scales are perfect rubies and it's capable of walking and breathing fire.")
                    .addNPC(npcid, HeadE.HAPPY_TALKING, "Could have made it fly too, given another six months or so.")
                    .addPlayer(HeadE.CALM_TALK, "Very nice. Well, see you later.")
                    .addNPC(npcid, HeadE.HAPPY_TALKING, "Aye, that you will.")
            );
        }
    }
}
