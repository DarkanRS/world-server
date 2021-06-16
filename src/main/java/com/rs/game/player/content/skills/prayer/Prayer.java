package com.rs.game.player.content.skills.prayer;

import java.util.HashMap;
import java.util.Map;

public enum Prayer {
	DEF_T1				(false, 0, 5942, 5971, 1, 1d/2),
	STR_T1				(false, 1, 5943, 5972, 4, 1d/2),
	ATK_T1				(false, 2, 5944, 5973, 7, 1d/2),
	RNG_T1				(false, 3, 5960, 5989, 8, 1d/2),
	MAG_T1				(false, 4, 5961, 5990, 9, 1d/2),
	DEF_T2				(false, 5, 5945, 5974, 10, 1),
	STR_T2				(false, 6, 5946, 5975, 13, 1),
	ATK_T2				(false, 7, 5947, 5976, 16, 1),
	RAPID_RESTORE		(false, 8, 5948, 5977, 19, 1d/6),
	RAPID_HEAL			(false, 9, 5949, 5978, 22, 1d/3),
	PROTECT_ITEM_N		(false, 10, 5950, 5979, 25, 1d/3),
	RNG_T2				(false, 11, 5962, 5991, 26, 1),
	MAG_T2				(false, 12, 5963, 5992, 27, 1),
	DEF_T3				(false, 13, 5951, 5980, 28, 2),
	STR_T3				(false, 14, 5952, 5981, 31, 2),
	ATK_T3				(false, 15, 5953, 5982, 34, 2),
	PROTECT_SUMMONING	(false, 16, 5966, 5995, 35, 2),
	PROTECT_MAGIC		(false, 17, 5954, 5983, 37, 2),
	PROTECT_RANGE		(false, 18, 5955, 5984, 40, 2),
	PROTECT_MELEE		(false, 19, 5956, 5985, 43, 2),
	RNG_T3				(false, 20, 5964, 5993, 44, 2),
	MAG_T3				(false, 21, 5965, 5994, 45, 2),
	RETRIBUTION			(false, 22, 5957, 5986, 46, 1d/2),
	REDEMPTION			(false, 23, 5958, 5987, 49, 1),
	SMITE				(false, 24, 5959, 5988, 52, 10d/3),
	CHIVALRY			(false, 25, 5967, 5996, 60, 10d/3),
	RAPID_RENEWAL		(false, 26, 7768, 7770, 65, 5d/2),
	PIETY				(false, 27, 5968, 5997, 70, 4),
	RIGOUR				(false, 28, 7381, 7382, 74, 3),
	AUGURY				(false, 29, 7769, 7771, 77, 10d/3),
	
	PROTECT_ITEM_C		(true, 0, 6820, 6862, 50, 1d/3),
	SAP_WARRIOR			(true, 1, 6821, 6863, 50, 5d/2),
	SAP_RANGE			(true, 2, 6822, 6864, 52, 5d/2),
	SAP_MAGE			(true, 3, 6823, 6865, 54, 5d/2),
	SAP_SPIRIT			(true, 4, 6824, 6866, 56, 5d/2),
	BERSERKER			(true, 5, 6825, 6867, 59, 1d/3),
	DEFLECT_SUMMONING	(true, 6, 6826, 6868, 62, 2),
	DEFLECT_MAGIC		(true, 7, 6827, 6869, 65, 2),
	DEFLECT_RANGE		(true, 8, 6828, 6870, 68, 2),
	DEFLECT_MELEE		(true, 9, 6829, 6871, 71, 2),
	LEECH_ATTACK		(true, 10, 6830, 6872, 74, 5d/3),
	LEECH_RANGE			(true, 11, 6831, 6873, 76, 5d/3),
	LEECH_MAGIC			(true, 12, 6832, 6874, 78, 5d/3),
	LEECH_DEFENSE		(true, 13, 6833, 6875, 80, 5d/3),
	LEECH_STRENGTH		(true, 14, 6834, 6876, 82, 5d/3),
	LEECH_ENERGY		(true, 15, 6835, 6877, 84, 5d/3),
	LEECH_SPECIAL		(true, 16, 6836, 6878, 86, 5d/3),
	WRATH				(true, 17, 6837, 6879, 89, 1d/2),
	SOUL_SPLIT			(true, 18, 6838, 6880, 92, 3),
	TURMOIL				(true, 19, 6839, 6881, 95, 3);
	
	private static Map<Integer, Prayer> NORMALS = new HashMap<>();
	private static Map<Integer, Prayer> CURSES = new HashMap<>();
	
	static {
		for (Prayer p : Prayer.values()) {
			if (p.curse)
				CURSES.put(p.slotId, p);
			else
				NORMALS.put(p.slotId, p);
		}
	}
	
	public static Prayer forSlot(int slotId, boolean curse) {
		if (curse)
			return CURSES.get(slotId);
		return NORMALS.get(slotId);
	}
	
	private boolean curse;
	private int slotId, req, varBit, qpVarBit;
	private double drain;
	
	private Prayer(boolean curse, int slotId, int varBit, int qpVarBit, int req, double drain) {
		this.curse = curse;
		this.slotId = slotId;
		this.varBit = varBit;
		this.qpVarBit = qpVarBit;
		this.req = req;
		this.drain = drain;
	}
	
	public int getSlotId() {
		return slotId;
	}
	
	public int getReq() {
		return req;
	}
	
	public int getVarBit() {
		return varBit;
	}
	
	public int getQPVarBit() {
		return qpVarBit;
	}
	
	public double getDrain() {
		return drain;
	}
	
	public boolean isCurse() {
		return curse;
	}
}
