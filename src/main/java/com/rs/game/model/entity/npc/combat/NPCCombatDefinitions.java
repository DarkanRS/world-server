// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.
//
//  Copyright (C) 2021 Trenton Kress
//  This file is part of project: Darkan
//
package com.rs.game.model.entity.npc.combat;

import java.io.File;
import java.util.HashMap;

import com.rs.cache.loaders.Bonus;
import com.rs.cache.loaders.NPCDefinitions;
import com.rs.game.content.skills.summoning.Summoning;
import com.rs.lib.file.JsonFileManager;
import com.rs.lib.util.Logger;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.annotations.ServerStartupEvent;

@PluginEventHandler
public class NPCCombatDefinitions {

	private final static String PATH = "data/npcs/combatdefs/";
	public static HashMap<Object, NPCCombatDefinitions> COMBAT_DEFINITIONS = new HashMap<>();
	public static NPCCombatDefinitions DEFAULT_DEF;

	static {
		NPCCombatDefinitions def = new NPCCombatDefinitions();
		def.attackStyle = AttackStyle.MELEE;
		def.agressivenessType = AggressiveType.PASSIVE;
		DEFAULT_DEF = def;
	}

	public enum AttackStyle {
		MELEE, RANGE, MAGE,
		SPECIAL, //Ranged special
		SPECIAL2 //Melee special
	}

	public enum AggressiveType {
		PASSIVE,
		AGGRESSIVE
	}

	public static int ATTACK = 0, DEFENSE = 1, STRENGTH = 2, RANGE = 3, MAGIC = 4;
	private static final int[] DEFAULT_LEVELS = { 0, 0, 0, 0, 0 };

	private transient boolean realStats = true;
	private int[] ids;
	private String[] names;
	private int hitpoints;
	private int attackAnim;
	private int defenceAnim;
	private int deathAnim;
	private int deathDelay;
	private int respawnDelay;
	private int maxHit;
	private AttackStyle attackStyle;
	private int attackGfx;
	private int attackProjectile;
	private AggressiveType agressivenessType;
	private Bonus attackBonus;
	private int aggroDistance = -1; //4 for melee, 8 for range default
	private int deAggroDistance = -1; //16 by default
	private int maxDistFromSpawn = -1; //16 by default 64 for special/special2
	private int attackRange = -1; //10 by default for range
	private int[] levels;

	public NPCCombatDefinitions() {
		hitpoints = 1;
		attackAnim = -1;
		deathAnim = -1;
		defenceAnim = -1;
		deathDelay = 3;
		respawnDelay = 60;
		maxHit = 1;
		attackGfx = -1;
		attackProjectile = -1;
	}

	public NPCCombatDefinitions(int... ids) {
		this();
		this.ids = ids;
	}

	public NPCCombatDefinitions(String... names) {
		this();
		this.names = names;
	}

	public NPCCombatDefinitions(NPCCombatDefinitions defs) {
		ids = defs.ids == null ? null : defs.ids.clone();
		names = defs.names == null ? null : defs.names.clone();
		hitpoints = defs.hitpoints;
		attackAnim = defs.attackAnim;
		defenceAnim = defs.defenceAnim;
		deathAnim = defs.deathAnim;
		deathDelay = defs.deathDelay;
		respawnDelay = defs.respawnDelay;
		maxHit = defs.maxHit;
		attackStyle = defs.attackStyle;
		attackGfx = defs.attackGfx;
		attackProjectile = defs.attackProjectile;
		agressivenessType = defs.agressivenessType;
		attackBonus = defs.attackBonus;
		levels = defs.levels == null ? null : defs.levels.clone();
	}

	@ServerStartupEvent
	public static final void init() {
		loadPackedCombatDefinitions();
	}

	public static void reload() {
		COMBAT_DEFINITIONS.clear();
		loadPackedCombatDefinitions();
	}

	public static NPCCombatDefinitions getDefs(int npcId) {
		NPCCombatDefinitions defs = COMBAT_DEFINITIONS.get(npcId);
		if (defs == null && (NPCDefinitions.getDefs(npcId).hasAttackOption() || Summoning.isFamiliar(npcId)))
			defs = COMBAT_DEFINITIONS.get(NPCDefinitions.getDefs(npcId).getName());
		if (defs == null)
			defs = DEFAULT_DEF;
		if (defs.levels == null) {
			if (NPCDefinitions.getDefs(npcId).combatLevel > 0)
				defs.levels = generateLevels(NPCDefinitions.getDefs(npcId).combatLevel, defs.hitpoints/10);
			defs.realStats = false;
		} else
			defs.realStats = true;
		return defs;
	}

	private static void loadPackedCombatDefinitions() {
		try {
			File[] dropFiles = new File(PATH).listFiles();
			for (File f : dropFiles)
				loadFile(f);
		} catch (Throwable e) {
			Logger.handle(e);
		}
	}

	private static void loadFile(File f) {
		try {
			if (f.isDirectory()) {
				for (File dir : f.listFiles())
					loadFile(dir);
				return;
			}
			NPCCombatDefinitions defs = (NPCCombatDefinitions) JsonFileManager.loadJsonFile(f, NPCCombatDefinitions.class);
			if (defs != null) {
				if (defs.getIds() != null)
					for (int id : defs.getIds())
						COMBAT_DEFINITIONS.put(id, defs);
				if (defs.getNames() != null)
					for (String name : defs.getNames())
						COMBAT_DEFINITIONS.put(name, defs);
			}
		} catch(Throwable e) {
			System.err.println("Error loading file: " + f.getPath());
		}
	}

	public int getRespawnDelay() {
		return respawnDelay;
	}

	public int getDeathEmote() {
		return deathAnim;
	}

	public int getDefenceEmote() {
		return defenceAnim;
	}

	public int getAttackEmote() {
		return attackAnim;
	}

	public int getAttackGfx() {
		return attackGfx;
	}

	public AggressiveType getAgressivenessType() {
		return agressivenessType;
	}

	public int getAttackProjectile() {
		return attackProjectile;
	}

	public AttackStyle getAttackStyle() {
		return attackStyle;
	}

	public Bonus getAttackBonus() {
		return attackBonus;
	}

	public int getMaxHit() {
		return maxHit;
	}

	public void setMaxHit(int maxHit) {
		this.maxHit = maxHit;
	}

	public int getHitpoints() {
		return hitpoints;
	}

	public void setHitpoints(int hitpoints) {
		this.hitpoints = hitpoints;
	}

	public int getDeathDelay() {
		return deathDelay;
	}

	public int[] getLevels() {
		if (levels == null)
			return DEFAULT_LEVELS.clone();
		return levels.clone();
	}

	public int[] getIds() {
		return ids;
	}

	public String[] getNames() {
		return names;
	}

	public int getAttackLevel() {
		return levels[ATTACK];
	}

	public int getStrengthLevel() {
		return levels[STRENGTH];
	}

	public int getDefenseLevel() {
		return levels[DEFENSE];
	}

	public int getMagicLevel() {
		return levels[MAGIC];
	}

	public int getRangeLevel() {
		return levels[RANGE];
	}

	public static int[] generateLevels(int combat, int hp) {
		int[] levels = new int[5];
		int def = (int) (((combat/1.32 + 1) / 0.25) - hp);
		combat -= (int) ((def + hp) * 0.25) + 1;
		int off = (int) ((combat/0.325)/1.5);

		int avg = (def+off)/2;

		levels[DEFENSE] = avg;
		levels[STRENGTH] = avg;
		levels[ATTACK] = avg;
		levels[RANGE] = avg;
		levels[MAGIC] = avg;
		return levels;
	}

	public int getCombatLevel() {
		int attack = getLevels()[ATTACK];
		int defence = getLevels()[DEFENSE];
		int strength = getLevels()[STRENGTH];
		int hp = hitpoints/10;
		int ranged = getLevels()[RANGE];
		int magic = getLevels()[MAGIC];
		int combatLevel = 3;
		combatLevel = (int) ((defence + hp) * 0.25) + 1;
		double melee = (attack + strength) * 0.325;
		double ranger = Math.floor(ranged * 1.5) * 0.325;
		double mage = Math.floor(magic * 1.5) * 0.325;
		if (melee >= ranger && melee >= mage)
			combatLevel += melee;
		else if (ranger >= melee && ranger >= mage)
			combatLevel += ranger;
		else if (mage >= melee && mage >= ranger)
			combatLevel += mage;
		return combatLevel;
	}

	public void setLevels(int[] levels) {
		this.levels = levels;
	}

	public void setAttackBonus(Bonus bonus) {
		attackBonus = bonus;
	}

	public void addId(int npcId) {
		names = null;
		if (ids == null) {
			ids = new int[] { npcId };
			return;
		}
		for (int id : ids)
			if (id == npcId)
				return;
		int[] newIds = new int[ids.length+1];
		for (int i = 0;i < ids.length;i++)
			newIds[i] = ids[i];
		newIds[ids.length] = npcId;
		ids = newIds;
	}

	public void addName(String name) {
		ids = null;
		names = new String[] { name };
	}

	public boolean isRealStats() {
		return realStats;
	}

	public int getMaxDistFromSpawn() {
		if (maxDistFromSpawn <= 0)
			return getAttackStyle() == AttackStyle.SPECIAL || getAttackStyle() == AttackStyle.SPECIAL2 ? 64 : 16;
		return maxDistFromSpawn;
	}

	public int getDeAggroDistance() {
		if (deAggroDistance <= 0)
			return 16;
		return deAggroDistance;
	}

	public int getAggroDistance() {
		if (aggroDistance <= 0)
			return getAttackStyle() == AttackStyle.MELEE ? 4 : getAttackStyle() == AttackStyle.SPECIAL ? 64 : 8;
		return aggroDistance;
	}

	public int getAttackRange() {
		if (attackRange <= 0)
			return getAttackStyle() == AttackStyle.MELEE || getAttackStyle() == AttackStyle.SPECIAL2 ? 0 : 10;
		return attackRange;
	}
}
