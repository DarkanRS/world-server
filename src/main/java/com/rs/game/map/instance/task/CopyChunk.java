package com.rs.game.map.instance.task;

import com.rs.game.map.instance.InstanceBuilder;
import com.rs.game.map.instance.InstancedChunk;
import com.rs.lib.util.MapUtils;
import com.rs.lib.util.MapUtils.Structure;

public class CopyChunk implements InstanceBuilderTask {
    private int fromChunkId;
    private int toChunkId;
    private int rotation;
    private boolean copyNpcs = false;

    public CopyChunk(int fromChunkX, int fromChunkY, int fromPlane, int toChunkX, int toChunkY, int toPlane, int rotation, boolean copyNpcs) {
        this.fromChunkId =  MapUtils.encode(Structure.CHUNK, fromChunkX, fromChunkY, fromPlane);
        this.toChunkId = MapUtils.encode(Structure.CHUNK, toChunkX, toChunkY, toPlane);
        this.rotation = rotation;
        this.copyNpcs = copyNpcs;
    }

    @Override
    public Boolean call() throws Exception {
        try {
            InstancedChunk toChunk = InstanceBuilder.createInstancedChunk(fromChunkId, toChunkId, rotation);
            toChunk.loadMap(copyNpcs);
            return true;
        } catch(Exception e) {
            return false;
        }
    }
}
