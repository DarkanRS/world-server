package com.rs.game.player.content.skills.prayer;

import com.rs.game.Entity;
import com.rs.game.player.Player;

public enum Leech {
	ATTACK(Prayer.LEECH_ATTACK, 2231, 2232),
	STRENGTH(Prayer.LEECH_STRENGTH, 2248, 2250),
	DEFENSE(Prayer.LEECH_DEFENSE, 2244, 2246),
	RANGED(Prayer.LEECH_RANGE, 2236, 2238),
	MAGIC(Prayer.LEECH_MAGIC, 2240, 2242),
	SPECIAL(Prayer.LEECH_SPECIAL, 2256, 2258),
	ENERGY(Prayer.LEECH_ENERGY, 2252, 2254);
	
	private Prayer prayer;

	private int projAnim, spotAnimHit;
	
	private Leech(Prayer prayer, int projAnim, int spotAnimHit) {
		this.prayer = prayer;
		this.projAnim = projAnim;
		this.spotAnimHit = spotAnimHit;
	}
	
	public Prayer getPrayer() {
		return prayer;
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
