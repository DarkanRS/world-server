package com.rs.game.npc.pest;

import com.rs.game.player.content.minigames.pest.PestControl;
import com.rs.lib.game.WorldTile;

public class Ravager extends PestMonsters {

    boolean destroyingObject = false;

    public Ravager(int id, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea, boolean spawned, int index, PestControl manager) {
	super(id, tile, -1, false, false, index, manager);
    }
    
    @Override
    public void processNPC() {
	super.processNPC();
	
    }
}
