package com.rs.game.content.world.areas.canifis;

import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class Canifis {
    public static ObjectClickHandler handleAltar = new ObjectClickHandler(new Object[] { 61336 }, e -> e.getPlayer().getPrayer().worshipAltar());
}
