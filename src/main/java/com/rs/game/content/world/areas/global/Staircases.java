package com.rs.game.content.world.areas.global;

import com.rs.game.model.entity.player.Player;
import com.rs.game.model.object.GameObject;
import com.rs.lib.game.Tile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class Staircases {
    public static ObjectClickHandler worldStaircasesUP = new ObjectClickHandler(new Object[] { 24357, 24358, 11734 }, e ->  {
        Player player = e.getPlayer();
        switch (e.getObject().getRotation()) {
            case 0 -> player.useStairs(-1, player.transform(0, 4, 1), 1, 1);
            case 1 -> player.useStairs(-1, player.transform(4, 0, 1), 1, 1);
            case 2 -> player.useStairs(-1, player.transform(0, -4, 1), 1, 1);
            case 3 -> player.useStairs(-1, player.transform(-4, 0, 1), 1, 1);
        }
    });

    public static ObjectClickHandler worldStaircasesDOWN = new ObjectClickHandler(new Object[] { 24359, 24360, 35783 }, e ->  {
        Player player = e.getPlayer();
        if (e.getObject().getTile().isAt(3189, 3432)) {
            player.useStairs(-1, player.transform(2, 6400, 0), 1, 1);
            return;
        }
        switch (e.getObject().getRotation()) {
            case 0 -> player.useStairs(-1, player.transform(0, -4, -1), 1, 1);
            case 1 -> player.useStairs(-1, player.transform(-4, 0, -1), 1, 1);
            case 2 -> player.useStairs(-1, player.transform(0, 4, -1), 1, 1);
            case 3 -> player.useStairs(-1, player.transform(4, 0, -1), 1, 1);
        }
    });

    public static ObjectClickHandler stairs = new ObjectClickHandler(new Object[] { "Staircase", "Stairs" }, e -> handleStaircases(e.getPlayer(), e.getObject(), e.getOption()));

    public static void handleStaircases(Player player, GameObject object, String option) {
        switch (option.toLowerCase()) {
            case "climb-up" -> {
                if (player.getPlane() == 3) return;
                player.useStairs(-1, Tile.of(player.getX(), player.getY(), player.getPlane() + 1), 0, 1);
            }
            case "climb-down" -> {
                if (player.getPlane() == 0) return;
                player.useStairs(-1, Tile.of(player.getX(), player.getY(), player.getPlane() - 1), 0, 1);
            }
            case "climb" -> {
                if (player.getPlane() == 3 || player.getPlane() == 0) return;
                player.promptUpDown("Go up the stairs.", player.transform(0, 0, 1), "Go down the stairs.", player.transform(0, 0, -1));
            }
        }
    }
}
