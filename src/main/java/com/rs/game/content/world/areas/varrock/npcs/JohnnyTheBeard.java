package com.rs.game.content.world.areas.varrock.npcs;

import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class JohnnyTheBeard {
    public static NPCClickHandler handleJohnnyTheBeard = new NPCClickHandler(new Object[] { 645 }, new String[] { "Talk-to" }, e -> {
        e.getPlayer().sendMessage("Johnny the beard is not interested in talking.");
    });
}
