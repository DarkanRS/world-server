package com.rs.game.npc.others;

import com.rs.game.player.Player;
import com.rs.lib.game.WorldTile;

public class ClueNPC extends OwnedNPC {

	public ClueNPC(Player target, int id, WorldTile tile) {
		super(target, id, tile, false);
	}

	@Override
	public void onDespawnEarly() {
		getOwner().getTreasureTrailsManager().setPhase(0);
	}

	@Override
	public void drop() {
		getOwner().getTreasureTrailsManager().setPhase(2);
	}
}
