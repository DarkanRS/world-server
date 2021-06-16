package com.rs.game.npc.glacors;

import com.rs.game.Hit;
import com.rs.game.World;
import com.rs.game.npc.NPC;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;

public class EnduringMinion extends NPC {

	public Glacor parent;
	public boolean defeated = false;

	public EnduringMinion(int id, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea, boolean spawned, Glacor parent) {
		super(id, tile, spawned);
		this.parent = parent;
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

	@Override
	public void handlePreHit(Hit hit) {
		int distance = (int) Utils.getDistance(parent.getX(), parent.getY(), this.getX(), this.getY());
		double damageReduction = distance * 0.1;
		hit.setDamage((int) (hit.getDamage() * damageReduction));

		super.handlePreHit(hit);
	}

}
