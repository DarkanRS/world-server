package com.rs.game.content.skills.runecrafting;

import com.rs.game.content.skills.magic.Rune;

public enum CombinationRune {
	MIST(new int[] {2478, 2480}, new int[] {Runecrafting.WATER_TALISMAN, Runecrafting.AIR_TALISMAN}, new int[] { Rune.WATER.id(), Rune.AIR.id() }, 4695, 6, new double[]{8.0, 8.5}),
	DUST(new int[] {2478, 2481}, new int[] {Runecrafting.EARTH_TALISMAN, Runecrafting.AIR_TALISMAN}, new int[] { Rune.EARTH.id(), Rune.AIR.id() }, 4696, 10, new double[]{8.3, 9.0}),
	MUD(new int[] {2480, 2481}, new int[] {Runecrafting.EARTH_TALISMAN, Runecrafting.WATER_TALISMAN}, new int[] { Rune.EARTH.id(), Rune.WATER.id() }, 4698, 13, new double[]{9.3, 9.5}),
	SMOKE(new int[] {2478, 2482}, new int[] {Runecrafting.FIRE_TALISMAN, Runecrafting.AIR_TALISMAN}, new int[] { Rune.FIRE.id(), Rune.AIR.id() }, 4697, 15, new double[]{8.5, 9.5}),
	STEAM(new int[] {2480, 2482}, new int[] {Runecrafting.FIRE_TALISMAN, Runecrafting.WATER_TALISMAN}, new int[] { Rune.FIRE.id(), Rune.WATER.id() }, 4694, 19, new double[]{9.3, 10.0}),
	LAVA(new int[] {2481, 2482}, new int[] {Runecrafting.FIRE_TALISMAN, Runecrafting.EARTH_TALISMAN}, new int[] { Rune.FIRE.id(), Rune.EARTH.id() }, 4699, 23, new double[]{10.0, 10.5});

	private int[] altars;
	private int[] talismans;
	private int[] runes;
	private int combinationRune;
	private int level;
	private double[] xp;

	private CombinationRune(int[] altars, int[] talismans, int[] runes, int combinationRune, int level, double[] xp) {
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