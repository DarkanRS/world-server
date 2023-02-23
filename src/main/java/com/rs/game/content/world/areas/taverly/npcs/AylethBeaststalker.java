package com.rs.game.content.world.areas.taverly.npcs;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.utils.shop.ShopsHandler;

@PluginEventHandler
public class AylethBeaststalker extends Conversation {
    private static final int npcId = 14864;

    public AylethBeaststalker(Player player) {
        super(player);
        addNPC(npcId, HeadE.TALKING_ALOT,
                "I walk the lonely path of the hunter. Will you walk with me? Wait, did that make sense?");
        addOptions(new Options() {
            @Override
            public void create() {
                option("I need hunter supplies", () -> {
                    ShopsHandler.openShop(player, "ayleths_hunting_supplies");
                });
                option("Farewell");
            }
        });
        create();
    }

    public static NPCClickHandler AylethBeaststalkerHandler = new NPCClickHandler(new Object[] { npcId }, e -> {
        if (e.getOption().equalsIgnoreCase("talk-to"))
            e.getPlayer().startConversation(new AylethBeaststalker(e.getPlayer()));
        if (e.getOption().equalsIgnoreCase("trade"))
            ShopsHandler.openShop(e.getPlayer(), "ayleths_hunting_supplies");
    });

}
