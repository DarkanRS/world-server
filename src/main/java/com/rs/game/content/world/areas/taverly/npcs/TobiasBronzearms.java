package com.rs.game.content.world.areas.taverly.npcs;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.utils.shop.ShopsHandler;

@PluginEventHandler
public class TobiasBronzearms extends Conversation {
    private static final int npcId = 14870;

    public TobiasBronzearms(Player player) {
        super(player);
        addNPC(npcId, HeadE.HAPPY_TALKING, "Tobias Bronzearms at your service.");
        addOptions(new Options() {
            @Override
            public void create() {
                option("I need mining supplies", () -> {
                    ShopsHandler.openShop(player, "tobias_mining_shop");
                });
                option("Farewell");
            }
        });
        create();
    }

    public static NPCClickHandler TobiasBronzearmsHandler = new NPCClickHandler(new Object[] { npcId }, e -> {
        if (e.getOption().equalsIgnoreCase("talk-to"))
            e.getPlayer().startConversation(new TobiasBronzearms(e.getPlayer()));
        if (e.getOption().equalsIgnoreCase("trade"))
            ShopsHandler.openShop(e.getPlayer(), "tobias_mining_shopp");
    });

}
