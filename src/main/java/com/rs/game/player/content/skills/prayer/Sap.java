package com.rs.game.player.content.skills.prayer;

import com.rs.game.Entity;
import com.rs.game.player.Player;

public enum Sap {
	WARRIOR(Prayer.SAP_WARRIOR, 2214, 2215, 2216),
	RANGE(Prayer.SAP_RANGE, 2217, 2218, 2219),
	MAGE(Prayer.SAP_MAGE, 2220, 2221, 2222),
	SPIRIT(Prayer.SAP_SPIRIT, 2223, 2224, 2225);
	
	private Prayer prayer;

	private int spotAnimStart, projAnim, spotAnimHit;
	
	private Sap(Prayer prayer, int spotAnimStart, int projAnim, int spotAnimHit) {
		this.prayer = prayer;
		this.spotAnimStart = spotAnimStart;
		this.projAnim = projAnim;
		this.spotAnimHit = spotAnimHit;
	}
	
	public Prayer getPrayer() {
		return prayer;
	}

	public int getSpotAnimStart() {
		return spotAnimStart;
	}

	public int getProjAnim() {
		return projAnim;
	}

	public int getSpotAnimHit() {
		return spotAnimHit;
	}
	
	public void activate(Player player, Entity target) {
		//	if (target.getPrayer().reachedMax(0)) {
		//		target.sendMessage("Your opponent has been weakened so much that your sap curse has no effect.", true);
		//	} else {
		//		target.prayer.increaseLeechBonus(0);
		//		target.sendMessage("Your curse drains Attack from the enemy, boosting your Attack.", true);
		//	}
	}
}
