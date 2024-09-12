package com.rs.game.content.world.areas.global;

import com.rs.game.World;
import com.rs.game.model.object.GameObject;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ObjectClickHandler;
import com.rs.utils.Ticks;

@PluginEventHandler
public class Chests {

    public static ObjectClickHandler handleClosedChest = new ObjectClickHandler(new Object[] { "Closed chest" }, e -> {
        GameObject object = e.getObject();

        if (object.getDefinitions().containsOption(0, "Open")) {
            GameObject openedChest = new GameObject(
                object.getId() + 1,
                object.getType(),
                object.getRotation(),
                object.getX(),
                object.getY(),
                object.getPlane()
            );
            if (!openedChest.getDefinitions().containsOption("Search") && !openedChest.getDefinitions().containsOption("Close")) {
                return;
            }
            e.getPlayer().anim(536);
            e.getPlayer().lock(2);
            e.getPlayer().faceObject(openedChest);
            World.spawnObjectTemporary(openedChest, Ticks.fromMinutes(1));
        }
    });

    public static ObjectClickHandler handleOpenChest = new ObjectClickHandler(new Object[] { "Open chest" }, e -> {
        if (e.getObject().getDefinitions().containsOption(0, "Search")) {
            e.getPlayer().sendMessage("You search the chest but find nothing.");
        }
    });
}
