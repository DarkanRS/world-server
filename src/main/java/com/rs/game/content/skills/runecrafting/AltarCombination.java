package com.rs.game.content.skills.runecrafting;

import com.rs.game.content.skills.magic.Rune;

public enum AltarCombination {
	AIR_TO_MIST(2478, RunecraftingTalisman.WATER, Rune.WATER, 4695, 6, 8.0D),
	AIR_TO_DUST(2478, RunecraftingTalisman.EARTH, Rune.EARTH, 4696, 10, 8.3D),
	AIR_TO_SMOKE(2478, RunecraftingTalisman.FIRE, Rune.FIRE, 4697, 15, 8.5D),
	WATER_TO_MIST(2480, RunecraftingTalisman.AIR, Rune.AIR, 4695, 6, 8.5D),
	WATER_TO_MUD(2480, RunecraftingTalisman.EARTH, Rune.EARTH, 4698, 13, 9.3D),
	WATER_TO_STEAM(2480, RunecraftingTalisman.FIRE, Rune.FIRE, 4694, 19, 9.3D),
	EARTH_TO_DUST(2481, RunecraftingTalisman.AIR, Rune.AIR, 4696, 10,  9.0D),
	EARTH_TO_MUD(2481, RunecraftingTalisman.WATER, Rune.WATER, 4698, 13, 9.5D),
	EARTH_TO_LAVA(2481, RunecraftingTalisman.FIRE, Rune.FIRE, 4699, 23, 10.0D),
	FIRE_TO_SMOKE(2482, RunecraftingTalisman.AIR, Rune.AIR, 4697, 15, 9.5D),
	FIRE_TO_STEAM(2482, RunecraftingTalisman.WATER, Rune.WATER, 4694, 19, 10.0D),
	FIRE_TO_LAVA(2482, RunecraftingTalisman.EARTH, Rune.LAVA, 4699, 23, 10.5D);

	private final int altarId;
	private final RunecraftingTalisman talisman;
	private final Rune reagentRune;
	private final int outputRuneId;
	private final int levelReq;
	private final double xp;

	AltarCombination(int altarId, RunecraftingTalisman talisman, Rune reagentRune, int outputRuneId, int levelReq, double xp) {
		this.altarId = altarId;
		this.talisman = talisman;
		this.reagentRune = reagentRune;
		this.outputRuneId = outputRuneId;
		this.levelReq = levelReq;
		this.xp = xp;
	}

	public int getAltarId() {
		return altarId;
	}

	public RunecraftingTalisman getTalisman() {
		return talisman;
	}

	public Rune getReagentRune() {
		return reagentRune;
	}

	public int getOutputRuneId() {
		return outputRuneId;
	}

	public int getLevelReq() {
		return levelReq;
	}

	public double getXp() {
		return xp;
	}

}
