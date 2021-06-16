package com.rs.utils.spawns;

import com.rs.game.World;
import com.rs.lib.game.WorldTile;

public class NPCSpawn {

	private String comment;
	private int npcId;
	private WorldTile tile;
	
	public NPCSpawn(int npcId, WorldTile tile, String comment) {
		this.npcId = npcId;
		this.tile = tile;
		this.comment = comment;
	}
	
	public void spawn() {
		World.spawnNPC(npcId, tile, false, true, null);
	}
	
	public WorldTile getTile() {
		return tile;
	}
	
	public int getNPCId() {
		return npcId;
	}

	public String getComment() {
		return comment;
	}
}
