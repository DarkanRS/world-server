package com.rs.game.content.world.areas.taverly.npcs;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.utils.shop.ShopsHandler;

@PluginEventHandler
public class MarcusEverburn extends Conversation {
    private static final int npcId = 14883;

    public MarcusEverburn(Player player) {
        super(player);
        addNPC(npcId, HeadE.HAPPY_TALKING, "Hey! Let's SET SOMETHING ON FIRE!");
        addOptions(new Options() {
            @Override
            public void create() {
                option("I need firemaking supplies", () -> {
                    ShopsHandler.openShop(player, "marcus_firemaking_shop");
                });
                option("Farewell");
            }
        });
        create();
    }

    public static NPCClickHandler MarcusEverburnHandler = new NPCClickHandler(new Object[] { npcId }, e -> {
        if (e.getOption().equalsIgnoreCase("talk-to"))
            e.getPlayer().startConversation(new MarcusEverburn(e.getPlayer()));
        if (e.getOption().equalsIgnoreCase("trade"))
            ShopsHandler.openShop(e.getPlayer(), "marcus_firemaking_shop");
    });

}
