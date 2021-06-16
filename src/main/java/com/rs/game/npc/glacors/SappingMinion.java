package com.rs.game.npc.glacors;

import com.rs.game.World;
import com.rs.game.npc.NPC;
import com.rs.lib.game.WorldTile;

public class SappingMinion extends NPC {

	public Glacor parent;
	public boolean defeated = false;

	public SappingMinion(int id, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea, boolean spawned, Glacor parent) {
		super(id, tile, spawned);
		this.parent = parent;
		this.setForceAgressive(true);
		this.setForceMultiAttacked(true);
	}

	@Override
	public void processEntity() {
		super.processEntity();
		if (getHitpoints() <= 0 || isDead()) {
			if (!defeated)
				World.sendProjectile(this, parent, 634, 34, 16, 30, 35, 16, 15);
			defeated = true;
		}
	}

}
