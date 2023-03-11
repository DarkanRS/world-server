package com.rs.game.content.world.areas.varrock.npcs;

import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.utils.shop.ShopsHandler;
@PluginEventHandler
public class Dealga {
    public static NPCClickHandler handleDealga = new NPCClickHandler(new Object[] { 11475 }, e -> {
        ShopsHandler.openShop(e.getPlayer(), "dealgas_scimitar_emporium");
    });
}
