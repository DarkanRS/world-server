package com.rs.game.content.minigames.castlewars.npcs;

import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;
@PluginEventHandler
public class Lanthus {
    public static NPCClickHandler Lanthus = new NPCClickHandler(new Object[] { 1526 }, e -> e.getPlayer().getInterfaceManager().sendInterface(60));
}
