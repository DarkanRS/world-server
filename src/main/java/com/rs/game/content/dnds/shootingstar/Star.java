package com.rs.game.content.dnds.shootingstar;

import com.rs.cache.loaders.ObjectType;
import com.rs.game.World;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.object.GameObject;
import com.rs.lib.game.Tile;

public class Star extends GameObject {
    public NPC sprite;
    public int life = 0;
    public Star(int tier, ShootingStars.Location location) {
        super(38659+tier, ObjectType.SCENERY_INTERACT, 0, location.tile);
        World.sendWorldMessage("<col=FF0000><shad=000000>A shooting star has crashed near " + location.description + "!", false);
    }

    public int getTier() {
        return 38659-id;
    }

    public void degrade() {
        if (id == 38659)
            return;
        setId(id-1);
        if (id == 38659)
            spawnSprite();
    }

    private void spawnSprite() {
        sprite = new NPC(8091, this.tile.transform(1, 1));
    }

    public void destroy() {
        if (sprite != null)
            sprite.finish();
        World.removeObject(this);
    }
}
