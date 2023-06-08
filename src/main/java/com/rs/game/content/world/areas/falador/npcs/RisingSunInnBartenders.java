package com.rs.game.content.world.areas.falador.npcs;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Item;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.plugin.handlers.NPCInteractionDistanceHandler;

@PluginEventHandler
public class RisingSunInnBartenders extends Conversation {

    public static NPCInteractionDistanceHandler RisingSunInnEmilyDistance = new NPCInteractionDistanceHandler(new Object[] { 736 }, (player, npc) -> 2);
    public static NPCClickHandler handleBartenders = new NPCClickHandler(new Object[] { 736, 3217, 3218 }, e -> {
        if (e.getOption().equalsIgnoreCase("talk-to"))
            e.getPlayer().startConversation(new RisingSunInnBartenders(e.getPlayer(), e.getNPC()));
    });

    public RisingSunInnBartenders(Player player, NPC npc) {
        super(player);
        boolean hasCoins = player.getInventory().hasCoins(3);
        boolean hasSlots = player.getInventory().hasFreeSlots();
        String name = npc.getName();
        addNPC(npc.getId(), HeadE.HAPPY_TALKING, "Heya! What can I get you?");
        addPlayer(HeadE.HAPPY_TALKING, "What ales are you serving?");
        addNPC(npc.getId(), HeadE.HAPPY_TALKING, "Well, we've got Asgarnian Ale, Wizard's Mind Bomb and Dwarven Stout, all for only 3 coins.")
                .addOptions(ops -> {
                    ops.add("One Asgarnian Ale, please.")
                            .addNext(() -> {
                                if (!hasCoins)
                                    player.startConversation(new Dialogue()
                                            .addPlayer(HeadE.HAPPY_TALKING, "One Asgarnian Ale, please.")
                                            .addNPC(npc.getId(), HeadE.ANGRY, "I said 3 coins! You haven't got 3 coins!"));
                                if (!hasSlots)
                                    player.startConversation(new Dialogue()
                                            .addPlayer(HeadE.HAPPY_TALKING, "One Asgarnian Ale, please.")
                                            .addNPC(npc.getId(), HeadE.SHAKING_HEAD, "You don't have the space for a beer!"));
                                else {
                                    player.getInventory().removeCoins(3);
                                    player.getInventory().addItem(1905);
                                    player.startConversation(new Dialogue()
                                            .addPlayer(HeadE.HAPPY_TALKING, "One Asgarnian Ale, please.")
                                            .addNPC(npc.getId(), HeadE.HAPPY_TALKING, "There you go.")
                                            .addPlayer(HeadE.HAPPY_TALKING, "Thanks, " + name + ""));
                                }
                            });
                    ops.add("I'll try the Mind Bomb.")
                            .addNext(() -> {
                                if (!hasCoins)
                                    player.startConversation(new Dialogue()
                                            .addPlayer(HeadE.HAPPY_TALKING, "I'll try the Mind Bomb.")
                                            .addNPC(npc.getId(), HeadE.ANGRY, "I said 3 coins! You haven't got 3 coins!"));
                                if (!hasSlots)
                                    player.startConversation(new Dialogue()
                                            .addPlayer(HeadE.HAPPY_TALKING, "I'll try the Mind Bomb.")
                                            .addNPC(npc.getId(), HeadE.SHAKING_HEAD, "You don't have the space for a beer!"));
                                else {
                                    player.getInventory().removeCoins(3);
                                    player.getInventory().addItem(1907);
                                    player.startConversation(new Dialogue()
                                            .addPlayer(HeadE.HAPPY_TALKING, "I'll try the Mind Bomb.")
                                            .addNPC(npc.getId(), HeadE.HAPPY_TALKING, "There you go.")
                                            .addPlayer(HeadE.HAPPY_TALKING, "Thanks, " + name + ""));
                                }
                            });
                    ops.add("Can I have a Dwarven Stout?")
                            .addNext(() -> {
                                if (!hasCoins)
                                    player.startConversation(new Dialogue()
                                            .addPlayer(HeadE.HAPPY_TALKING, "Can I have a Dwarven Stout?")
                                            .addNPC(npc.getId(), HeadE.ANGRY, "I said 3 coins! You haven't got 3 coins!"));
                                if (!hasSlots)
                                    player.startConversation(new Dialogue()
                                            .addPlayer(HeadE.HAPPY_TALKING, "Can I have a Dwarven Stout?")
                                            .addNPC(npc.getId(), HeadE.SHAKING_HEAD, "You don't have the space for a beer!"));
                                else {
                                    player.getInventory().removeCoins(3);
                                    player.getInventory().addItem(1913);
                                    player.startConversation(new Dialogue()
                                            .addPlayer(HeadE.HAPPY_TALKING, "Can I have a Dwarven Stout?")
                                            .addNPC(npc.getId(), HeadE.HAPPY_TALKING, "There you go.")
                                            .addPlayer(HeadE.HAPPY_TALKING, "Thanks, " + name + ""));
                                }
                            });
                    if (player.getInventory().containsItem(1919)) {
                        ops.add("I've got this beer glass...")
                                .addPlayer(HeadE.CONFUSED, "I've got this beer glass...")
                                .addNPC(npc.getId(), HeadE.HAPPY_TALKING, "We'll buy it for a couple of coins if you're interested.")
                                .addOptions(ops2 -> {
                                    ops2.add("Okay, sure.")
                                            .addPlayer(HeadE.HAPPY_TALKING, "Okay, sure.")
                                            .addNext(() -> {
                                                player.getInventory().removeItems(new Item(1919 ,1));
                                                player.getInventory().addCoins(2);
                                            })
                                            .addNPC(npc.getId(), HeadE.HAPPY_TALKING, "There you go.")
                                            .addPlayer(HeadE.HAPPY_TALKING, "Thanks!");
                                    ops2.add("No thanks, I like empty beer glasses.")
                                            .addPlayer(HeadE.SHAKING_HEAD, "No thanks, I like empty beer glasses.");
                                });
                    }
                    ops.add("I don't feel like any of those.")
                            .addPlayer(HeadE.SHAKING_HEAD, " I don't feel like any of those.");
                });
    }
}
