package com.rs.game.content.world.areas.varrock.npcs;

import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.miniquest.Miniquest;
import com.rs.game.World;
import com.rs.game.content.miniquests.FromTinyAcorns.pickpocketUrist;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
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
    private static final int talisman = 18649;
    private static final int bankNote = 18652;
    private static final int babyDragon = 18651;

    private static final Tile uristTile =  Tile.of(3222, 3424, 0);

    public static NPCClickHandler UristLoric = new NPCClickHandler(new Object[]{ npcid }, new String[]{"Talk-to"}, e -> {
        if(!Objects.equals(e.getNPC().getTile(), uristTile)){
            e.getPlayer().sendMessage("He looks busy.");
            return;
        }
        if (!e.getPlayer().isMiniquestStarted(Miniquest.FROM_TINY_ACORNS)) {
            preQuest(e.getPlayer());
            return;
        }
        if (e.getPlayer().isMiniquestComplete(Miniquest.FROM_TINY_ACORNS)) {
            postQuest(e.getPlayer());
            return;
        }
        switch (e.getPlayer().getMiniquestStage(Miniquest.FROM_TINY_ACORNS)) {
            case 1 -> stage1(e.getPlayer(), e.getNPC());
            case 2 -> stage2(e.getPlayer());
            case 3 -> stage3(e.getPlayer());
            default -> postQuest(e.getPlayer());
        }
    });

    public static NPCClickHandler UristLoricPickpocket = new NPCClickHandler(new Object[]{ npcid }, new String[]{"Pickpocket"}, e -> {
        if (!e.getPlayer().isMiniquestStarted(Miniquest.FROM_TINY_ACORNS)) {
            e.getPlayer().npcDialogue(npcid, HeadE.ANGRY, "Oi! Leave that alone.");
            return;
        }
        if(e.getPlayer().getInventory().containsItem(talisman)) {
            e.getPlayer().sendMessage("You've stolen his talisman already.");
            return;
        }
        if(getFloorDrop(e.getPlayer()) != null) {
            e.getPlayer().sendMessage("His talisman isn't in his pocket, it's on the ground nearby.");
            return;
        }
        if(e.getPlayer().getInventory().containsItem(babyDragon) || e.getPlayer().getMiniquestManager().isComplete(Miniquest.FROM_TINY_ACORNS)) {
            e.getPlayer().sendMessage("You don't need this; you've already got the dragon.");
            return;
        }
        e.getPlayer().getActionManager().setAction(new pickpocketUrist(e.getNPC()));
    });

    private static GroundItem getFloorDrop(Player player) {
        for (GroundItem groundItem : World.getAllGroundItemsInChunkRange(823724, 1)) {
            if (groundItem == null || groundItem.getDefinitions() == null || groundItem.getId() != talisman) {
                continue;
            }
            if (!groundItem.getTile().withinArea(3220, 3427, 3228, 3432)) {
                player.sendMessage("I should find a suitable spot to put this. Maybe just north of him…");
                continue;
            }
            player.sendMessage("Talisman Found");
            return groundItem;
        }
        return null;
    }

    private static void preQuest(Player player) {
        player.sendMessage("The Dwarf is hard at work on some sort of highly delicate construction, and doesn't pay you any notice.");
    }

    private static void stage1(Player player, NPC npc) {
        if(getFloorDrop(player) != null) {
            player.startConversation(new Dialogue()
                    .addPlayer(HeadE.CALM_TALK, "That thing on the ground... Is it yours?")
                    .addNPC(npcid, HeadE.SCARED, "What thing? Oh! Thank you kindly, I'd have been sad if I'd lost that.", () -> {
                        npc.walkToAndExecute((Objects.requireNonNull(getFloorDrop(player)).getTile()), () -> {
                            final int[] i = {0};
                            WorldTasks.schedule(Ticks.fromSeconds(0),Ticks.fromSeconds(5), () -> {
                                switch (i[0]) {
                                    case 0 -> {
                                        player.getMiniquestManager().getAttribs(Miniquest.FROM_TINY_ACORNS).setB("UristDistracted", true);
                                        npc.forceTalk("Hmm, how'd this get over here?");
                                        World.removeGroundItem(Objects.requireNonNull(getFloorDrop(player)));
                                        i[0]++;
                                    }
                                    case 1 -> {
                                        npc.forceTalk("Ugh it's all dirty. Lucky I've got my blue silk handkerchief on me.");
                                        i[0]++;
                                    }
                                    case 2 -> {
                                        npc.forceTalk("There, that's better.");
                                        i[0]++;
                                    }
                                    case 3 -> {
                                        npc.forceTalk("Well, back to work.");
                                        player.getMiniquestManager().getAttribs(Miniquest.FROM_TINY_ACORNS).setB("UristDistracted", false);
                                        i[0]++;
                                    }
                                    case 4 -> {
                                        npc.walkToAndExecute(uristTile, npc::resetDirection);
                                        return;
                                    }
                                    default -> throw new IllegalStateException("Unexpected value: " + i[0]);
                                }
                            });
                        });
                    }));
            return;
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
    private static void stage2(Player player) {
        if(player.getInventory().containsItem(babyDragon) || player.getBank().containsItem(babyDragon, 1)) {
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
                    .addNPC(npcid, HeadE.SKEPTICAL, "Now that you mention it...")
                    .addPlayer(HeadE.CALM_TALK, "Will you be able to finish by the deadline?")
                    .addNPC(npcid, HeadE.SHAKING_HEAD, "Are you serious? Not a chance!")
                    .addPlayer(HeadE.CALM_TALK, "Then what will you tell Mr. Lightfinger?")
                    .addNPC(npcid, HeadE.VERY_FRUSTRATED, "I don't see that I have much choice; I shall have to give him his money back.")
                    .addPlayer(HeadE.CALM_TALK, "That sounds fair.")
                    .addNPC(npcid, HeadE.HAPPY_TALKING, "Since you're working for him, can you take this banker's note to him with my sincerest apologies?")
                    .addPlayer(HeadE.CALM_TALK, "Urist hands you a banker's note. The figure on it is astronomical.", () -> {
                        player.getInventory().addItem(18652, 1);
                        player.getMiniquestManager().setStage(Miniquest.FROM_TINY_ACORNS, 3);
                    })
                    .addPlayer(HeadE.CALM_TALK, "I should think so.")
                    .addNPC(npcid, HeadE.CALM_TALK, "I appreciate your understanding.")
                    .addPlayer(HeadE.CALM_TALK, "I appreciate your cooperation.")
            );
        }
        else {
            player.startConversation(new Dialogue()
                    .addPlayer(HeadE.CALM_TALK, "Oh, nice dragon.")
                    .addNPC(npcid, HeadE.HAPPY_TALKING, "I thought I'd lost it for a while. Lucky I found it, eh?", () -> player.getMiniquestManager().setStage(Miniquest.FROM_TINY_ACORNS, 1))
            );
        }
    }
    private static void stage3(Player player) {
        player.startConversation(new Dialogue()
                .addNPC(npcid, HeadE.SAD_CRYING, "Ruined! Ruined! It makes me want to throw a tantrum!")
                .addNext( () -> {
                    if(!player.getInventory().containsItem(babyDragon) && !player.getBank().containsItem(babyDragon, 1)) {
                        player.startConversation(new Dialogue()
                                .addPlayer(HeadE.CALM_TALK, "Oh, nice dragon.")
                                .addNPC(npcid, HeadE.HAPPY_TALKING, "I thought I'd lost it for a while. Lucky I found it, eh?", () -> {
                                    player.getMiniquestManager().setStage(Miniquest.FROM_TINY_ACORNS, 1);
                                    if(player.getInventory().containsItem(bankNote)) {
                                        player.getInventory().deleteItem(bankNote, 1);
                                        player.sendMessage("The banker's note is now useless to you; you crumple it and throw it away.");
                                    }
                                    if(player.getBank().containsItem(bankNote, 1)) {
                                        player.getBank().deleteItem(bankNote, 1);
                                        player.sendMessage("The banker's note is now useless, I'm sure the bank will dispose of it.");
                                    }
                                })
                                .addPlayer(HeadE.FRUSTRATED, "How... fortunate.")
                        );
                    }
                    if(!player.getInventory().containsItem(bankNote) && !player.getBank().containsItem(bankNote, 1)) {
                        player.startConversation(new Dialogue()
                                .addPlayer(HeadE.CALM_TALK, "I'm afraid I can't find the banker's note you made out.")
                                .addNPC(npcid, HeadE.FRUSTRATED, "Then I'll have to write another.", () -> {
                                    player.getInventory().addItem(bankNote);
                                    player.sendMessage("Urist hands you another banker's note. The figure on it is still astronomical.");
                                }));
                    }
                })
        );
    }
    private static void postQuest(Player player) {
        player.startConversation(new Dialogue()
                .addNPC(npcid, HeadE.SAD_CRYING, "Ruined! Ruined! It makes me want to throw a tantrum!")
        );
    }
}
