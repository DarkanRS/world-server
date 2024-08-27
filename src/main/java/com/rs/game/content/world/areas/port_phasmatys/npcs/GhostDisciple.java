package com.rs.game.content.world.areas.port_phasmatys.npcs;

import com.rs.game.content.minigames.ectofuntus.Ectofuntus;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class GhostDisciple {
    public static NPCClickHandler GhostDiscipleCollect = new NPCClickHandler(new Object[] { 1686 }, new String[] {"Collect"}, e -> {
        Player player = e.getPlayer();
        if (player.getInventory().hasFreeSlots() && player.unclaimedEctoTokens > 0) {
            player.getInventory().addItem(Ectofuntus.ECTO_TOKEN, player.unclaimedEctoTokens);
            player.unclaimedEctoTokens = 0;
        }
    });
}
