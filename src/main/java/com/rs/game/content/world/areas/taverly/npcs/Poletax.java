package com.rs.game.content.world.areas.taverly.npcs;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.utils.shop.ShopsHandler;

@PluginEventHandler
public class Poletax extends Conversation {
    private static final int npcId = 14854;

    public Poletax(Player player) {
        super(player);
        addNPC(npcId, HeadE.HAPPY_TALKING, "Are you here to practice your herblore?");
        addOptions(new Options() {
            @Override
            public void create() {
                option("I need herblore supplies", () -> {
                    ShopsHandler.openShop(player, "poletaxs_herblore_shop");
                });
                option("Farewell");
            }
        });
        create();
    }

    public static NPCClickHandler PoletaxHandler = new NPCClickHandler(new Object[] { npcId }, e -> {
        if (e.getOption().equalsIgnoreCase("talk-to"))
            e.getPlayer().startConversation(new Poletax(e.getPlayer()));
        if (e.getOption().equalsIgnoreCase("trade"))
            ShopsHandler.openShop(e.getPlayer(), "poletaxs_herblore_shop");
    });

}
