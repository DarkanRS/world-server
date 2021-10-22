package com.rs.utils.spawns;

import com.rs.game.World;
import com.rs.game.pathing.Direction;
import com.rs.lib.game.WorldTile;

public class NPCSpawn {

	private String comment;
	private int npcId;
	private WorldTile tile;
    private Direction dir;
	private String customName;

    public NPCSpawn(int npcId, WorldTile tile, Direction dir, String comment) {
        this.npcId = npcId;
        this.tile = tile;
        this.dir = dir;
        this.comment = comment;
    }

    public NPCSpawn(int npcId, WorldTile tile, String comment) {
		this(npcId, tile, Direction.SOUTH, comment);
	}
	
	public void spawn() {
		World.spawnNPC(npcId, tile, dir, false, true, customName);
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

	public String getCustomName() {
		return customName;
	}

	public NPCSpawn setCustomName(String customName) {
		this.customName = customName;
		return this;
	}

	public Direction getDir() {
		return dir;
	}

	public void setDir(Direction dir) {
		this.dir = dir;
	}
}
