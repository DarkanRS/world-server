package com.rs.game.content.world.areas.rimmington;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.statements.NPCStatement;
import com.rs.game.content.world.doors.Doors;
import com.rs.lib.Constants;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.plugin.handlers.ObjectClickHandler;
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

    public static ObjectClickHandler handleCraftingDoor = new ObjectClickHandler(new Object[] { 2647 }, e -> {
        var player = e.getPlayer();
        if (player.getSkills().getLevel(Constants.CRAFTING) < 40 && player.getY() < 3288) {
            player.startConversation(new Conversation(player, new Dialogue(new NPCStatement(805, HeadE.CHEERFUL, "I'm sorry, you need to have a crafting level of 40 to use my facilities."))));
            return;
        }
        if (player.getEquipment().getChestId() != 1757 && player.getY() > 3288) {
            player.startConversation(new Conversation(player, new Dialogue(new NPCStatement(805, HeadE.CHEERFUL, "Don't forget to put on your brown apron! It can be a little messy in here."))));
            return;
        }
        Doors.handleDoor(player, e.getObject());
    });

    public static ObjectClickHandler handleStairs = new ObjectClickHandler(new Object[] { 71902, 71903 }, e -> {
        switch (e.getObjectId()) {
            case 71902 -> e.getPlayer().useStairs(-1, e.getPlayer().transform(e.getObject().getRotation() == 1 ? 4 : 0, e.getObject().getRotation() == 0 ? 4 : 0, 1), 1, 1);
            case 71903 -> e.getPlayer().useStairs(-1, e.getPlayer().transform(e.getObject().getRotation() == 1 ? -4 : 0, e.getObject().getRotation() == 0 ? -4 : 0, -1), 1, 1);
        }
    });
}
