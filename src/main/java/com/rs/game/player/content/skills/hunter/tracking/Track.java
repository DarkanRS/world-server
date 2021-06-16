package com.rs.game.player.content.skills.hunter.tracking;

import com.rs.lib.game.WorldTile;

public enum Track {
	PISC_1(new Burrow(19439, new WorldTile(2353, 3595, 0),
					new Trail(19375, new WorldTile(2347, 3607, 0), 2976, 4,
							new Trail(19428, new WorldTile(2355, 3601, 0), 2978, 3),
							new Trail(19428, new WorldTile(2354, 3609, 0), 2983, 3),
							new Trail(19376, new WorldTile(2348, 3612, 0), 2988, 3,
									new Trail(19428, new WorldTile(2354, 3609, 0), 2989, 3),
									new Trail(29428, new WorldTile(2351, 3619, 0), 2990, 3))),
					new Trail(19375, new WorldTile(2347, 3607, 0), 2976, 4)
					));
	
	private Burrow burrow;
	
	private Track(Burrow burrow) {
		this.burrow = burrow;
	}
}
