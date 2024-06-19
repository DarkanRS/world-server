package com.rs.game.content.world.areas.rimmington;

import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.utils.shop.ShopsHandler;

@PluginEventHandler
public class Rimmington {
    /**
     * Shopkeepers and simple NPCs
     */
    public static NPCClickHandler GENERAL_STORES = new NPCClickHandler(new Object[] { 530, 531 }, e -> {
        switch (e.getOption()) {
            case "Trade" -> ShopsHandler.openShop(e.getPlayer(), "rimmington_general_store");
            case "Talk-to" -> e.getPlayer().startConversation(new Dialogue()
                    .addNPC(530, HeadE.HAPPY_TALKING, "Can I help you at all?")
                    .addOptions(ops -> {
                        ops.add("Yes, please. What are you selling?", () -> ShopsHandler.openShop(e.getPlayer(), "rimmington_general_store"));

                        ops.add("How should I use your shop?")
                                .addNPC(530, HeadE.HAPPY_TALKING, "I'm glad you ask! You can buy as many of the items stocked as you wish. You can also sell most items to the shop.")
                                .addNext(() -> ShopsHandler.openShop(e.getPlayer(), "rimmington_general_store"));

                        ops.add("I think I'll give it a miss.")
                                .addPlayer(HeadE.CALM_TALK, "I think I'll give it a miss.")
                                .addNPC(530, HeadE.HAPPY_TALKING, "Okay, come back soon.");
                    })
            );
        }
    });

    public static NPCClickHandler BRIAN = new NPCClickHandler(new Object[]{ 1860 }, e -> {
        switch (e.getOption()) {
            case "Trade" -> ShopsHandler.openShop(e.getPlayer(), "brians_archery_supplies");
            case "Talk-to" -> e.getPlayer().startConversation(new Dialogue()
                    .addNPC(1860, HeadE.HAPPY_TALKING, "Would you like to buy some archery equipment?")
                    .addOptions(ops -> {
                        ops.add("Let's see what you've got then.", () -> ShopsHandler.openShop(e.getPlayer(), "brians_archery_supplies"));
                        ops.add("No thanks, I've got all the archery equipment I need.")
                                .addPlayer(HeadE.CALM_TALK, "No thanks, I've got all the archery equipment I need.")
                                .addNPC(1860, HeadE.HAPPY_TALKING, "Okay. Fare well on your travels.");
                    }));
        }
    });

    public static NPCClickHandler ROMMIK = new NPCClickHandler(new Object[]{ 585 }, e -> {
        switch (e.getOption()) {
            case "Trade" -> ShopsHandler.openShop(e.getPlayer(), "rommiks_crafting_shop");
            case "Talk-to" -> e.getPlayer().startConversation(new Dialogue()
                    .addNPC(585, HeadE.HAPPY_TALKING, "Would you like to buy some archery equipment?")
                    .addOptions(ops -> {
                        ops.add("Let's see what you've got then.", () -> ShopsHandler.openShop(e.getPlayer(), "rommiks_crafting_shop"));
                        ops.add("No thanks, I've got all the crafting equipment I need.")
                                .addPlayer(HeadE.CALM_TALK, "No thanks, I've got all the crafting equipment I need.")
                                .addNPC(585, HeadE.HAPPY_TALKING, "Okay. Fare well on your travels.");
                    }));
        }
    });
}
