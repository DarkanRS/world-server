package com.rs.game.content.skills.runecrafting;

import com.rs.game.content.skills.magic.Rune;

public enum CombinationRunes {

	MIST(new int[] {AIR_ALTAR, WATER_ALTAR}, new int[] {Runecrafting.WATER_TALISMAN, Runecrafting.AIR_TALISMAN}, new int[] { Rune.WATER.id(), Rune.AIR.id() }, MIST_RUNE, 6, new double[]{8.0, 8.5}),
	DUST(new int[] {AIR_ALTAR, EARTH_ALTAR}, new int[] {Runecrafting.EARTH_TALISMAN, Runecrafting.AIR_TALISMAN}, new int[] { Rune.EARTH.id(), Rune.AIR.id() }, DUST_RUNE, 10, new double[]{8.3, 9.0}),
	MUD(new int[] {WATER_ALTAR, EARTH_ALTAR}, new int[] {Runecrafting.EARTH_TALISMAN, Runecrafting.WATER_TALISMAN}, new int[] { Rune.EARTH.id(), Rune.WATER.id() }, MUD_RUNE, 13, new double[]{9.3, 9.5}),
	SMOKE(new int[] {AIR_ALTAR, FIRE_ALTAR}, new int[] {Runecrafting.FIRE_TALISMAN, Runecrafting.AIR_TALISMAN}, new int[] { Rune.FIRE.id(), Rune.AIR.id() }, SMOKE_RUNE, 15, new double[]{8.5, 9.5}),
	STEAM(new int[] {WATER_ALTAR, FIRE_ALTAR}, new int[] {Runecrafting.FIRE_TALISMAN, Runecrafting.WATER_TALISMAN}, new int[] { Rune.FIRE.id(), Rune.WATER.id() }, STEAM_RUNE, 19, new double[]{9.3, 10.0}),
	LAVA(new int[] {EARTH_ALTAR, FIRE_ALTAR}, new int[] {Runecrafting.FIRE_TALISMAN, Runecrafting.EARTH_TALISMAN}, new int[] { Rune.FIRE.id(), Rune.EARTH.id() }, LAVA_RUNE, 23, new double[]{10.0, 10.5});

	public static final int STEAM_RUNE = 4694;
	public static final int MIST_RUNE = 4695;
	public static final int DUST_RUNE = 4696;
	public static final int SMOKE_RUNE = 4697;
	public static final int MUD_RUNE = 4698;
	public static final int LAVA_RUNE = 4699;

	public static final int AIR_ALTAR = 2478;
	public static final int WATER_ALTAR = 2480;
	public static final int EARTH_ALTAR = 2481;
	public static final int FIRE_ALTAR = 2482;

	private int[] altars;
	private int[] talismans;
	private int[] runes;
	private int combinationRune;
	private int level;
	private double[] xp;

	private CombinationRunes(int[] altars, int[] talismans, int[] runes, int combinationRune, int level, double[] xp) {
		this.altars = altars;
		this.talismans = talismans;
		this.runes = runes;
		this.combinationRune = combinationRune;
		this.level = level;
		this.xp = xp;
	}

	public int[] getAltars() {
		return altars;
	}

	public int[] getTalismans() {
		return talismans;
	}

	public int[] getRunes() {
		return runes;
	}

	public int getCombinationRune() {
		return combinationRune;
	}

	public int getLevel() {
		return level;
	}

	public double[] getXP() {
		return xp;
	}
}