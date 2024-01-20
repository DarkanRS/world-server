package com.rs.game.content.skills.runecrafting.runespan;

import com.rs.game.content.skills.runecrafting.Runecrafting;

public enum Creature {
	AIR_ESSLING(15403, 9.5, 16596, Runecrafting.RCRune.AIR, 16634, 10, 1, 16571, 0.1),
	MIND_ESSLING(15404, 10, 16596, Runecrafting.RCRune.MIND, 16634, 10, 1, 16571, 0.2),
	WATER_ESSLING(15405, 12.6, 16596, Runecrafting.RCRune.WATER, 16634, 10, 5, 16571, 0.3),
	EARTH_ESSLING(15406, 14.3, 16596, Runecrafting.RCRune.EARTH, 16634, 10, 9, 16571, 0.4),
	FIRE_ESSLING(15407, 17.4, 16596, Runecrafting.RCRune.FIRE, 16634, 10, 14, 16571, 0.5),
	BODY_ESSHOUND(15408, 23.1, 16596, Runecrafting.RCRune.BODY, 16650, 10, 20, 16661, 0.7),
	COSMIC_ESSHOUND(15409, 26.6, 16596, Runecrafting.RCRune.COSMIC, 16650, 10, 27, 16661, 0.9),
	CHOAS_ESSHOUND(15410, 30.8, 16596, Runecrafting.RCRune.CHAOS, 16650, 10, 35, 16661, 1.1),
	ASTRAL_ESSHOUND(15411, 35.7, 16596, Runecrafting.RCRune.ASTRAL, 16650, 10, 40, 16661, 1.3),
	NATURE_ESSHOUND(15412, 43.4, 16596, Runecrafting.RCRune.NATURE, 16650, 10, 44, 16661, 1.5),
	LAW_ESSHOUND(15413, 53.9, 16596, Runecrafting.RCRune.LAW, 16650, 10, 54, 16661, 1.7),
	DEATH_ESSWRAITH(15414, 60, 16596, Runecrafting.RCRune.DEATH, 16644, 10, 65, 16641, 2.5),
	BLOOD_ESSWRAITH(15415, 73.1, 16596, Runecrafting.RCRune.BLOOD, 16644, 10, 77, 16641, 3),
	SOUL_ESSWRAITH(15416, 106.5, 16596, Runecrafting.RCRune.SOUL, 16644, 10, 90, 16641, 3.5);

	public final Runecrafting.RCRune rune;
	public final int npcId;
    public final int playerEmoteId;
    public final int npcEmoteId;
    public final int npcLife;
    public final int levelRequired;
    public final int deathEmote;
	public final double pointValue;

	Creature(int npcId, double xp, int playerEmoteId, Runecrafting.RCRune rune, int npcEmoteId, int npcLife, int levelRequired, int deathEmote, double pointValue) {
		this.npcId = npcId;
		//this.xp = xp;
		this.playerEmoteId = playerEmoteId;
		this.rune = rune;
		this.npcEmoteId = npcEmoteId;
		this.npcLife = npcLife;
		this.levelRequired = levelRequired;
		this.deathEmote = deathEmote;
		this.pointValue = pointValue;
	}
}
