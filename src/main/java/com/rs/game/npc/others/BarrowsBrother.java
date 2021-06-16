package com.rs.game.npc.others;

import com.rs.game.Entity;
import com.rs.game.player.controllers.BarrowsController;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;

public class BarrowsBrother extends OwnedNPC {

	private BarrowsController barrows;

	public BarrowsBrother(int id, WorldTile tile, BarrowsController barrows) {
		super(barrows.getPlayer(), id, tile, false);
		this.barrows = barrows;
		this.setIntelligentRouteFinder(true);
	}

	@Override
	public void sendDeath(Entity source) {
		if (barrows != null) {
			barrows.targetDied();
			barrows = null;
		}
		super.sendDeath(source);
	}

	@Override
	public double getMeleePrayerMultiplier() {
		return getId() != 2030 ? 0 : Utils.random(3) == 0 ? 1 : 0;
	}

	public void disappear() {
		barrows = null;
		finish();
	}

	@Override
	public void finish() {
		if (hasFinished())
			return;
		if (barrows != null) {
			barrows.targetFinishedWithoutDie();
			barrows = null;
		}
		super.finish();
	}

}
