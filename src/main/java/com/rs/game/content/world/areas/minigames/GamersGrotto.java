package com.rs.game.content.world.areas.minigames;

import com.rs.game.content.minigames.creations.StealingCreationLobbyController;
import com.rs.lib.game.Tile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class GamersGrotto {
    public static ObjectClickHandler enterGamersGrotto = new ObjectClickHandler(new Object[] { 20602 }, e -> e.getPlayer().useStairs(-1, Tile.of(2969, 9672, 0), 1, 1));

    public static ObjectClickHandler exitGamersGrotto = new ObjectClickHandler(new Object[] { 20604 }, e -> e.getPlayer().useStairs(-1, Tile.of(3018, 3404, 0), 0, 1));

    public static ObjectClickHandler enterStealingCreation = new ObjectClickHandler(new Object[] { 39508, 39509 }, e -> StealingCreationLobbyController.climbOverStile(e.getPlayer(), e.getObject(), true));
}
