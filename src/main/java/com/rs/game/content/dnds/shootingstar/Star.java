package com.rs.game.content.dnds.shootingstar;

import com.rs.cache.loaders.ObjectType;
import com.rs.game.World;
import com.rs.game.map.Chunk;
import com.rs.game.map.ChunkManager;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.object.GameObject;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Utils;

public class Star extends GameObject {
    public NPC sprite;
    public final long landingTime;
    public final ShootingStars.Location location;
    public boolean discovered = false;
    public int life = 0;
    public boolean minedThisTick = false;

    public Star(int tier, ShootingStars.Location location) {
        super(38668-tier, ObjectType.SCENERY_INTERACT, 0, location.tile);
        this.location = location;
        this.landingTime = World.getServerTicks();
        World.sendWorldMessage("<col=FF0000><shad=000000>A shooting star has crashed near " + location.description + "!", false);
        World.spawnObject(this);
        Chunk chunk = ChunkManager.getChunk(getTile().getChunkId(), true);
        chunk.flagForProcess(this);
        life = getMaxLife();
        ShootingStars.addDiscoveredStar(this, "Not discovered yet...");
    }

    public int getTier() {
        return 10-(id-38659);
    }

    public void degrade() {
        if (id >= 38668) {
            spawnSprite();
            setId(38670);
            return;
        }
        setId(id+1);
        life = getMaxLife();
    }

    public int getMaxLife() {
        return (int) (2258.2932 * (Math.pow(0.5726, getTier())));
    }

    public String getLife() {
        return Utils.formatDouble((double) life / (double) getMaxLife() * 100.0);
    }

    @Override
    public boolean process() {
        if (life <= -1)
            return false;
        if (minedThisTick && life > 0) {
            if (--life <= 0)
                degrade();
            minedThisTick = false;
        }
        return true;
    }

    private void spawnSprite() {
        sprite = new NPC(8091, Tile.of(tile));
    }

    public void destroy() {
        if (sprite != null)
            sprite.finish();
        World.removeObject(this);
        life = -1;
    }
}
