package com.rs.game.content.minigames.pyramidplunder;

import java.util.HashMap;
import java.util.Map;

public enum PPArtefact {

	IVORY_COMB(9026, 50),
	POTTERY_SCARAB(9032, 75),
	POTTERY_STATUETTE(9036, 100),
	STONE_SEAL(9042, 150),
	STONE_SCARAB(9030, 175),
	STONE_STATUETTE(9038, 200),
	GOLD_SEAL(9040, 750),
	GOLD_SCARAB(9028, 1000),
	GOLD_STATUETTE(9034, 1250),
	JEWELLED_GOLDEN(20661, 7500),
	JEWELLED_DIAMOND(21570, 12500);
	
	private static Map<Integer, PPArtefact> MAP = new HashMap<>();
	
	static {
		for (PPArtefact p : PPArtefact.values())
			MAP.put(p.id, p);
	}
	
	public static PPArtefact forId(int id) {
		return MAP.get(id);
	}
	
	private int id;
	private int tradeInValue;
	
	private PPArtefact(int id, int tradeInValue) {
		this.id = id;
		this.tradeInValue = tradeInValue;
	}

	public int getId() {
		return id;
	}

	public int getTradeInValue() {
		return tradeInValue;
	}
	
}
