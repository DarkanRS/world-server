package com.rs.game.content.world.areas.pollnivneach.npcs;

import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.utils.shop.ShopsHandler;

@PluginEventHandler
public class AliKebabs {
    public static NPCClickHandler HandleAliTheKebabSeller = new NPCClickHandler(new Object[]{ 1865 }, new String[] { "Talk-to" }, e -> {
        Player player = e.getPlayer();
        NPC npc = e.getNPC();
        player.startConversation(new Dialogue()
                .addPlayer(HeadE.HAPPY_TALKING, "Hello.")
                .addNPC(npc, HeadE.HAPPY_TALKING, "Hello. What can I do for you?")
                .addPlayer(HeadE.CONFUSED, "I don't know, what can you do for me?")
                .addNPC(npc, HeadE.HAPPY_TALKING, "Well, that depends.")
                .addPlayer(HeadE.CONFUSED, "Depends on what?")
                .addNPC(npc, HeadE.HAPPY_TALKING, "It depends on whether you like kebabs or not.")
                .addPlayer(HeadE.CONFUSED, "Why is that?")
                .addNPC(npc, HeadE.HAPPY_TALKING, "Seeing as I'm in the kebab construction industry, I mainly help people in need of a kebab.")
                .addPlayer(HeadE.CONFUSED, "Well then, what kind of kebabs do you, er, construct?")
                .addNPC(npc, HeadE.HAPPY_TALKING, "I offer two different types of kebabs: the standard run- of-the-mill kebab seen throughout Gielinor and enjoyed by many an intoxicated dwarf, and my speciality, the extra-hot kebab. So which shall it be?")
                .addOptions(options -> {
                    if(!player.getInventory().hasCoins(3))
                        options.add("I want a standard kebab, please.")
                                .addPlayer(HeadE.SHAKING_HEAD, "I seem to be short on coins. Sorry.");
                    else if (!player.getInventory().hasFreeSlots())
                        options.add("I want a standard kebab, please.")
                                .addNPC(npc, HeadE.SHAKING_HEAD, "You do not have enough space to carry a kebab");
                    else
                        options.add("I want a standard kebab, please.")
                                .addNPC(npc, HeadE.HAPPY_TALKING, "That will be three gold coins.")
                                .addNPC(npc, HeadE.HAPPY_TALKING, "Here you go.")
                                .addPlayer(HeadE.HAPPY_TALKING, "Thanks.  ")
                                .addNext(() -> {
                                    if (player.getInventory().hasFreeSlots()) {
                                        player.getInventory().addItem(1971);
                                        player.getInventory().removeCoins(3);
                                    }
                                });

                    if(!player.getInventory().hasCoins(5))
                        options.add("Could I have an extra-hot kebab please?")
                                .addPlayer(HeadE.SHAKING_HEAD, "I seem to be short on coins. Sorry.");
                    else if (!player.getInventory().hasFreeSlots())
                        options.add("Could I have an extra-hot kebab please?")
                                .addNPC(npc, HeadE.SHAKING_HEAD, "You do not have enough space to carry a kebab");
                    else
                        options.add("Could I have an extra-hot kebab please?")
                                .addNPC(npc, HeadE.HAPPY_TALKING, "One super kebab coming up! Be careful, they really are as hot as they're made out to be.")
                                .addPlayer(HeadE.HAPPY_TALKING, "Sure, sure")
                                .addNext(() -> {
                                    if (player.getInventory().hasFreeSlots()) {
                                        player.getInventory().addItem(4608);
                                        player.getInventory().removeCoins(5);
                                    }
                                });
                })
        );
    });
}
