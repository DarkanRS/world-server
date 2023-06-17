package com.rs.game.content.world.areas.rimmington;

import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.miniquest.Miniquest;
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

    /**
     * Should be in a Witches Potion class eventually when the miniquest is added. Would be
     * an absurdly easy miniquest to add too.
     */
    public static NPCClickHandler HETTY = new NPCClickHandler(new Object[] { 307 }, e -> e.getPlayer().startConversation(new Dialogue()
            .addNPC(307, HeadE.HAPPY_TALKING, "What could you want with an old woman like me?")
            .addOptions(ops -> {
                if (!e.getPlayer().isMiniquestComplete(Miniquest.WITCHES_POTION)) {
                    switch(e.getPlayer().getMiniquestManager().getStage(Miniquest.WITCHES_POTION)) {
                        case 0 -> ops.add("I'm looking for work.");
                    }
                }

                ops.add("You look like a witch.")
                        .addPlayer(HeadE.CALM_TALK, "You look like a witch.")
                        .addNPC(307, HeadE.HAPPY_TALKING, "Yes, I suppose I'm not being very subtle about it. I fear I may get a visit from the witch hunters of Falador before long.");

                ops.add("Nothing, thanks.")
                        .addPlayer(HeadE.CALM_TALK, "Nothing, thanks.")
                        .addNPC(307, HeadE.FRUSTRATED, "Hmph.");
            })));

    /**
     * Purely Biohazard related Quest NPCs that will need to be eventually moved into
     * a Biohazard class once the quest gets added.
     */
    public static NPCClickHandler CHANCY = new NPCClickHandler(new Object[]{ 338 }, e ->
            e.getPlayer().startConversation(new Dialogue()
                    .addPlayer(HeadE.HAPPY_TALKING, "Hello! Playing Solitaire?")
                    .addNPC(338, HeadE.FRUSTRATED, "Hush - I'm trying to perfect the art of dealing off the bottom of the deck. Whatever you want, come back later.")
                    .addSimple("Chancy doesn't feel like talking.")));

    public static NPCClickHandler DA_VINCI = new NPCClickHandler(new Object[]{ 336 }, e ->
            e.getPlayer().startConversation(new Dialogue()
                    .addPlayer(HeadE.HAPPY_TALKING, "Hello!")
                    .addNPC(336, HeadE.FRUSTRATED, "Bah! A great artist such as myself should not have to suffer the HUMILIATION of spending time where commoners wander everywhere!")
                    .addSimple("Da Vinci does not feel sufficiently moved to talk.")));

    public static NPCClickHandler HOPS = new NPCClickHandler(new Object[]{ 340 }, e ->
            e.getPlayer().startConversation(new Dialogue()
                    .addPlayer(HeadE.HAPPY_TALKING, "Hello!")
                    .addNPC(340, HeadE.DRUNK, "Hops don't wanna talk now. HIC!")
                    .addSimple("He isn't in a fit state to talk.")));
}
