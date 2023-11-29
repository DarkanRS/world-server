package com.rs.game.content.skills.runecrafting;

import java.util.HashMap;

public enum RunecraftingTalisman {
	AIR(2478, 1438, 5527, 25, 13630, 1, 25),
	MIND(2479, 1448, 5529, 27.5, 13631, 1, 27),
	WATER(2480, 1444, 5531, 30, 13632, 7, 30),
	EARTH(2481, 1440, 5535, 32.5, 13633, 9, 33),
	FIRE(2482, 1442, 5537, 35, 13634, 14, 35),
	BODY(2483, 1446, 5533, 37.5, 13635, 1, 37.5),
	COSMIC(2484, 1454, 5539, 40, 13636, 27, 40),
	LAW(2485, 1458, 5545, 47.5, 13639, 1, 27),
	NATURE(2486, 1462, 5541, 45, 13638, 1, 45),
	CHAOS(2487, 1452, 5543, 42.5, 13637, 35, 42),
	DEATH(2488, 1456, 5547, 50, 13640, 65, 50),
	BLOOD(30624, 1450, 5549, 52.5, 13641, 1, 52.5);

	private static final HashMap<Integer, RunecraftingTalisman> BY_OBJ_ID = new HashMap<>();
	private static final HashMap<Integer, RunecraftingTalisman> BY_ITEM_ID = new HashMap<>();

	static {
		for (RunecraftingTalisman talisman : values()) {
			BY_OBJ_ID.put(talisman.altarId, talisman);
			BY_ITEM_ID.put(talisman.talismanId, talisman);
		}
	}

	public static RunecraftingTalisman forObjectId(int objectId) {
		return BY_OBJ_ID.get(objectId);
	}
	public static RunecraftingTalisman forItemId(int objectId) {
		return BY_ITEM_ID.get(objectId);
	}

	private final int altarId;

	private final int talismanId;

	private final int tiaraId;

	private final double tiaraExp;

	private final int staffId;

	private final int staffLevelReq;

	private final double staffExp;

	RunecraftingTalisman(int altarId, int talismanId, int tiaraId, double tiaraExp, int staffId, int staffLevelReq, double staffExp) {
		this.altarId = altarId;
		this.talismanId = talismanId;
		this.tiaraId = tiaraId;
		this.tiaraExp = tiaraExp;
		this.staffId = staffId;
		this.staffLevelReq = staffLevelReq;
		this.staffExp = staffExp;
	}

	public int getAltarId() {
		return altarId;
	}

	public int getTalismanId() {
		return talismanId;
	}

	public int getTiaraId() {
		return tiaraId;
	}

	public double getTiaraExp() {
		return tiaraExp;
	}

	public int getStaffId() {
		return staffId;
	}

	public int getStaffLevelReq() {
		return staffLevelReq;
	}

	public double getStaffExp() {
		return staffExp;
	}

}
