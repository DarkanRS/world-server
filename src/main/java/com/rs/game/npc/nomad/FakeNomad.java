package com.rs.game.npc.nomad;

import com.rs.game.Hit;
import com.rs.game.npc.NPC;
import com.rs.lib.game.WorldTile;

public class FakeNomad extends NPC {

	private Nomad nomad;

	public FakeNomad(WorldTile tile, Nomad nomad) {
		super(8529, tile, true);
		this.nomad = nomad;
		setForceMultiArea(true);
	}

	@Override
	public void handlePreHit(Hit hit) {
		nomad.destroyCopy(this);
	}

}
