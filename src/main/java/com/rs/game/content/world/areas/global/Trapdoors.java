package com.rs.game.content.world.areas.global;

import com.rs.game.World;
import com.rs.game.model.object.GameObject;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ObjectClickHandler;
import com.rs.utils.Ticks;

@PluginEventHandler
public class Trapdoors {

    public static ObjectClickHandler handleTrapdoor = new ObjectClickHandler(new Object[] { "Trapdoor" }, e -> {
        GameObject object = e.getObject();
        if (object.getDefinitions().containsOption(0, "Open")) {
            GameObject openedTrapdoor = new GameObject(
                object.getId() + 1,
                object.getType(),
                object.getRotation(),
                object.getX(),
                object.getY(),
                object.getPlane()
            );
            e.getPlayer().anim(536);
            e.getPlayer().lock(2);
            e.getPlayer().faceObject(openedTrapdoor);
            World.spawnObjectTemporary(openedTrapdoor, Ticks.fromMinutes(1));
        }
    });

}
