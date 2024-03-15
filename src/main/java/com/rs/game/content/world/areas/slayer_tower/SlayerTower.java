package com.rs.game.content.world.areas.slayer_tower;

import com.rs.cache.loaders.ObjectType;
import com.rs.game.World;
import com.rs.game.content.world.doors.Doors;
import com.rs.game.model.entity.pathing.Direction;
import com.rs.game.model.object.GameObject;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Tile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.PlayerStepHandler;

@PluginEventHandler
public class SlayerTower {

    public static PlayerStepHandler doors = new PlayerStepHandler(new Tile[] { Tile.of(3428, 3535, 0), Tile.of(3429, 3535, 0)}, e -> {
        e.getStep().setCheckClip(false);
        GameObject door = World.getObjectWithId(Tile.of(3428, 3535, 0), 4487);
        if (door != null)
            Doors.handleDoubleDoor(e.getPlayer(), door);
        World.sendObjectAnimation(World.getObject(Tile.of(3426, 3534, 0), ObjectType.SCENERY_INTERACT), new Animation(1533));
        World.sendObjectAnimation(World.getObject(Tile.of(3430, 3534, 0), ObjectType.SCENERY_INTERACT), new Animation(1533));
    });

}
