package com.rs.game.npc.godwars;

import com.rs.game.World;
import com.rs.game.npc.NPC;
import com.rs.lib.game.WorldTile;

public class GodWarMinion extends NPC {

	public GodWarMinion(int id, WorldTile tile, boolean spawned) {
		super(id, tile, spawned);
		setIgnoreDocile(true);
		setForceAgressive(true);
		setForceAggroDistance(64);
		setIntelligentRouteFinder(true);
	}

	public void respawn() {
		setFinished(false);
		World.addNPC(this);
		setLastRegionId(0);
		World.updateEntityRegion(this);
		loadMapRegions();
		checkMultiArea();
	}
}
