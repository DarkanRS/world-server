package com.rs.game.player.content.skills.hunter.tracking;

import java.util.ArrayList;
import java.util.List;

import com.rs.lib.game.WorldTile;

public class Burrow {
	
	private int burrowId;
	private WorldTile burrowTile;
	private List<Trail> next;
	
	public Burrow(int burrowId, WorldTile burrowTile, Trail... next) {
		this.burrowId = burrowId;
		this.burrowTile = burrowTile;
		this.next = new ArrayList<>();
		for (Trail t : next)
			this.next.add(t);
	}
}
