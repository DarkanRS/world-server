package com.rs.game.npc.others;

import com.rs.game.player.Player;
import com.rs.lib.game.WorldTile;

public class Ugi extends OwnedNPC {

	public Ugi(Player target, int id, WorldTile tile) {
		super(target, id, tile, false);
	}

	@Override
	public void onDespawnEarly() {
		getOwner().getTreasureTrailsManager().setPhase(0);
	}
}
