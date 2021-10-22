package com.rs.game.region;

import com.rs.lib.game.WorldTile;

public class Instance {
    private int[] boundChunks;
    private final int CHUNK_SIZE = 8;
    int originalX;
    int originalY;
    int chunkCount;

	public Instance(int originalX, int originalY) {
	    this.originalX = originalX;
	    this.originalY = originalY;
	    this.chunkCount = 8;
	    buildInstance();
    }

    public Instance(int originalX, int originalY, int chunkCount) {
        this.originalX = originalX;
        this.originalY = originalY;
        this.chunkCount = chunkCount;
        buildInstance();
    }
	


	private void buildMap() {
		RegionBuilder.copyMap(originalX, originalY, boundChunks[0], boundChunks[1], chunkCount);
	}

	public void destroyInstance() {
        RegionBuilder.destroyRegion(getWorldTile(0, 0).getRegionId());
    }

	public WorldTile getWorldTile(int localInstanceX, int localInstanceY) {
		return new WorldTile(boundChunks[0] * CHUNK_SIZE + localInstanceX, boundChunks[1] * CHUNK_SIZE + localInstanceY, 0);
	}

    public WorldTile getWorldTile(int localInstanceX, int localInstanceY, int plane) {
        return new WorldTile(boundChunks[0] * CHUNK_SIZE + localInstanceX, boundChunks[1] * CHUNK_SIZE + localInstanceY, plane);
    }
	
	private void buildInstance() {
		Runnable event = new Runnable() {
			@Override
			public void run() { 			    
                    boundChunks = RegionBuilder.findEmptyChunkBound(chunkCount, chunkCount);
                    buildMap();
                }
            };
		event.run();
	}

}
