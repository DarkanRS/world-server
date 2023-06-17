package com.rs.game.content.world.areas.falador.npcs;

import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.utils.shop.ShopsHandler;

@PluginEventHandler
public class Falador_Farm {
    private static int MILK_SELLER = 11547;
    private static int SARAH = 2304;

    public static NPCClickHandler HandleMilkSeller= new NPCClickHandler(new Object[]{ MILK_SELLER }, e -> {
        switch (e.getOption()) {
            case "Trade" -> ShopsHandler.openShop(e.getPlayer(), "milk_shop");
            case "Talk-to" -> e.getPlayer().startConversation(new Dialogue()
                    .addNPC(MILK_SELLER, HeadE.HAPPY_TALKING, "Would you like to buy some milk?")
                    .addOptions(ops -> {
                        ops.add("Sure.", () -> ShopsHandler.openShop(e.getPlayer(), "milk_shop"));
                        ops.add("No, thanks.")
                                .addPlayer(HeadE.CALM_TALK, "No, thanks.")
                                .addNPC(MILK_SELLER, HeadE.HAPPY_TALKING, "If you change your mind, you know where we are.");
                    }));
        }
    });

    public static NPCClickHandler HandleSarah = new NPCClickHandler(new Object[]{ SARAH }, e -> {
        switch (e.getOption()) {
            case "Trade" -> ShopsHandler.openShop(e.getPlayer(), "sarahs_farming_shop");
            case "Talk-to" -> e.getPlayer().startConversation(new Dialogue()
                    .addNPC(SARAH, HeadE.HAPPY_TALKING, "Would you like to buy some farming supplies?")
                    .addOptions(ops -> {
                        ops.add("Let's see what you've got then.", () -> ShopsHandler.openShop(e.getPlayer(), "sarahs_farming_shop"));
                        ops.add("No, thanks.")
                                .addPlayer(HeadE.CALM_TALK, "No, thanks.")
                                .addNPC(SARAH, HeadE.HAPPY_TALKING, "Okay. Fare well on your travels.");
                    }));
        }
    });

}
