package com.rs.game.player.content.minigames.shadesofmortton;

import com.rs.game.object.GameObject;
import com.rs.lib.util.Utils;

public class TempleWall extends GameObject {
	
	private int baseId;
	private int buildProgress;
	
	public TempleWall(GameObject object) {
		super(object);
		this.baseId = id;
		this.buildProgress = 5;
		ShadesOfMortton.addWall(this);
	}

	public void increaseProgress() {
		buildProgress += 4;
		update();
	}
	
	public void decreaseProgress() {
		buildProgress--;
		if (buildProgress <= 0) {
			destroy();
			return;
		}
		update();
	}
	
	public void destroy() {
		ShadesOfMortton.deleteWall(this);
	}
	
	public void update() {
		this.setId(baseId + Utils.clampI(buildProgress / 10, 0, 10));
	}
	
	public int getRepairPerc() {
		return Utils.clampI(buildProgress, 0, 100);
	}

}
