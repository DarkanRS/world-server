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
	JEWELLED_DIAMOND(21570, 12500),
	NOTED_IVORY_COMB(9027, 50),
	NOTED_POTTERY_SCARAB(9033, 75),
	NOTED_POTTERY_STATUETTE(9037, 100),
	NOTED_STONE_SEAL(9043, 150),
	NOTED_STONE_SCARAB(9031, 175),
	NOTED_STONE_STATUETTE(9039, 200),
	NOTED_GOLD_SEAL(9041, 750),
	NOTED_GOLD_SCARAB(9029, 1000),
	NOTED_GOLD_STATUETTE(9035, 1250 );
	
	private static final Map<Integer, PPArtefact> MAP = new HashMap<>();
	
	static {
		for (PPArtefact p : PPArtefact.values())
			MAP.put(p.id, p);
	}

	public static PPArtefact forId(int id) {
		return MAP.get(id);
	}
	
	private final int id;
	private final int tradeInValue;


	private PPArtefact(int id, int tradeInValue) {
		this.id = id;
		this.tradeInValue = tradeInValue;
	}

	public int getArtefactId() {
		return id;
	}

	public int getTradeInValue() {
		return tradeInValue;
	}
	
}
