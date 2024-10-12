package com.rs.game.content.world.areas.quest_instances;

import com.rs.Settings;
import com.rs.lib.game.Tile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class WithinTheLight {
    public static ObjectClickHandler handleZarosPortal = new ObjectClickHandler(new Object[] { 46500 }, new Tile[] { Tile.of(Settings.getConfig().getPlayerRespawnTile().getX(), Settings.getConfig().getPlayerRespawnTile().getY(), Settings.getConfig().getPlayerRespawnTile().getPlane()) }, e -> {
            e.getPlayer().useStairs(-1, Tile.of(Settings.getConfig().getPlayerRespawnTile().getX(), Settings.getConfig().getPlayerRespawnTile().getY(), Settings.getConfig().getPlayerRespawnTile().getPlane()), 2, 3, "You found your way back to home.");
            e.getPlayer().addWalkSteps(3351, 3415, -1, false);
    });
}
