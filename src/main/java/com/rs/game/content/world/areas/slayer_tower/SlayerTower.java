package com.rs.game.content.world.areas.slayer_tower;

import com.rs.cache.loaders.ObjectType;
import com.rs.game.World;
import com.rs.game.content.world.doors.Doors;
import com.rs.game.model.object.GameObject;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Tile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ObjectClickHandler;
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

    public static ObjectClickHandler slayerTowerStairs = new ObjectClickHandler(new Object[] { 4493, 4494, 4495, 4496 }, e -> {
        switch(e.getObjectId()) {
            case 4493, 4496 -> e.getPlayer().useStairs(Tile.of(e.getPlayer().getX() - 5, e.getPlayer().getY(), 1));
            case 4494 -> e.getPlayer().useStairs(Tile.of(e.getPlayer().getX() + 5, e.getPlayer().getY(), 0));
            case 4495 -> e.getPlayer().useStairs(Tile.of(e.getPlayer().getX() + 5, e.getPlayer().getY(), 2));
        }
    });

}
