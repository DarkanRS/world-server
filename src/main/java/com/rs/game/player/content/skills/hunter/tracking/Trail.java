package com.rs.game.player.content.skills.hunter.tracking;

import java.util.ArrayList;
import java.util.List;

import com.rs.lib.game.WorldTile;

public class Trail {

	private Trail prev;
	private List<Trail> next;
	
	private int nextObj;
	private WorldTile nextObjTile;
	private int varbit;
	private int value;
	
	public Trail(int nextObj, WorldTile nextObjTile, int varbit, int value, Trail... nexts) {
		this.nextObj = nextObj;
		this.nextObjTile = nextObjTile;
		this.varbit = varbit;
		this.value = value;
		if (nexts.length <= 0)
			return;
		this.next = new ArrayList<>();
		for (Trail t : nexts) {
			t.prev = this;
			this.next.add(t);
		}
	}
	
}
