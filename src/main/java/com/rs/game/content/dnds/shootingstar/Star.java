package com.rs.game.content.dnds.shootingstar;

import com.rs.cache.loaders.ObjectType;
import com.rs.game.model.object.GameObject;
import com.rs.lib.game.Tile;

public class Star extends GameObject {
    public int life = 0;
    public Star(int tier, Tile tile) {
        super(38659+tier, ObjectType.SCENERY_INTERACT, 0, tile);
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

    }
}
