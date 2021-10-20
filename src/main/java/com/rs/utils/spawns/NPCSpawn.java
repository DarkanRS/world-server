package com.rs.utils.spawns;

import com.rs.game.World;
import com.rs.game.pathing.Direction;
import com.rs.lib.game.WorldTile;

public class NPCSpawn {

	private String comment;
	private int npcId;
	private WorldTile tile;
    private String direction;

    public NPCSpawn(int npcId, WorldTile tile, String direction, String comment) {
        this.npcId = npcId;
        this.tile = tile;
        this.direction = direction;
        this.comment = comment;
    }

    public NPCSpawn(int npcId, WorldTile tile, String comment) {
		this(npcId, tile, "SOUTH", comment);
	}
	
	public void spawn() {
        int dir;
        if(direction.equalsIgnoreCase("north"))
            dir = 0;
        else if(direction.equalsIgnoreCase("northeast"))
            dir = 1;
        else if(direction.equalsIgnoreCase("east"))
            dir = 2;
        else if(direction.equalsIgnoreCase("southeast"))
            dir = 3;
        else if(direction.equalsIgnoreCase("south"))
            dir = 4;
        else if(direction.equalsIgnoreCase("southwest"))
            dir = 5;
        else if(direction.equalsIgnoreCase("west"))
            dir = 6;
        else if(direction.equalsIgnoreCase("northwest"))
            dir = 7;
        else
            dir = 4;
		World.spawnNPC(npcId, tile, dir, false, true, null);
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

    public String getDirection() {
        return direction;
    }
}
