package com.rs.game.region;

import com.rs.game.model.WorldProjectile;
import com.rs.game.model.entity.pathing.WorldCollision;
import com.rs.game.model.object.GameObject;
import com.rs.lib.game.GroundItem;
import com.rs.lib.util.MapUtils;
import com.rs.lib.util.MapUtils.Structure;
import com.rs.utils.music.Music;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.IntSets;
import it.unimi.dsi.fastutil.objects.ObjectLists;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class Chunk {
    private int chunkId;
    private int[] collisionMappings = new int[4];

    protected Set<Integer> players = IntSets.emptySet();
    protected Set<Integer> npcs = IntSets.emptySet();

    protected List<GameObject> baseObjects = ObjectLists.emptyList();
    protected Map<Integer, GameObject> removedBaseObjects = Int2ObjectMaps.emptyMap();
    protected List<GameObject> spawnedObjects = ObjectLists.emptyList();

    protected Map<Integer, Map<Integer, List<GroundItem>>> groundItems = Int2ObjectMaps.emptyMap();;
    protected List<GroundItem> groundItemList = ObjectLists.emptyList();
    protected List<WorldProjectile> projectiles = ObjectLists.emptyList();

    private int[] musicIds;

    public Chunk(int chunkId) {
        this.chunkId = chunkId;
        for (int i = 0;i < 4;i++)
            collisionMappings[i] = WorldCollision.getId(chunkId >> 14 & 0xfff, chunkId >> 3 & 0xfff, i);
        musicIds = Music.getRegionMusics(MapUtils.chunkToRegionId(chunkId));
    }

    public void destroy() {
        for (int mapping : collisionMappings)
            WorldCollision.clearChunk(mapping);
    }

    public void setMusicIds(int[] musicIds) {
        this.musicIds = musicIds;
    }
}
