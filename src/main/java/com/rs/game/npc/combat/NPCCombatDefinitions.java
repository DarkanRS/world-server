package com.rs.game.npc.combat;

import java.io.File;
import java.util.HashMap;

import com.rs.cache.loaders.Bonus;
import com.rs.cache.loaders.NPCDefinitions;
import com.rs.game.player.content.skills.summoning.Summoning;
import com.rs.lib.file.JsonFileManager;
import com.rs.lib.util.Logger;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.annotations.ServerStartupEvent;

@PluginEventHandler
public class NPCCombatDefinitions {

	private final static String PATH = "data/npcs/combatdefs/";
	public static HashMap<Object, NPCCombatDefinitions> COMBAT_DEFINITIONS = new HashMap<Object, NPCCombatDefinitions>();
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
		this.hitpoints = 1;
		this.attackAnim = -1;
		this.deathAnim = -1;
		this.defenceAnim = -1;
		this.deathDelay = 3;
		this.respawnDelay = 60;
		this.maxHit = 1;
		this.attackGfx = -1;
		this.attackProjectile = -1;
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
		this.ids = defs.ids == null ? null : defs.ids.clone();
		this.names = defs.names == null ? null : defs.names.clone();
		this.hitpoints = defs.hitpoints;
		this.attackAnim = defs.attackAnim;
		this.defenceAnim = defs.defenceAnim;
		this.deathAnim = defs.deathAnim;
		this.deathDelay = defs.deathDelay;
		this.respawnDelay = defs.respawnDelay;
		this.maxHit = defs.maxHit;
		this.attackStyle = defs.attackStyle;
		this.attackGfx = defs.attackGfx;
		this.attackProjectile = defs.attackProjectile;
		this.agressivenessType = defs.agressivenessType;
		this.attackBonus = defs.attackBonus;
		this.levels = defs.levels == null ? null : defs.levels.clone();
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
		} else {
			defs.realStats = true;
		}
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
				if (defs.getIds() != null) {
					for (int id : defs.getIds())
						COMBAT_DEFINITIONS.put(id, defs);
				}
				if (defs.getNames() != null) {
					for (String name : defs.getNames())
						COMBAT_DEFINITIONS.put(name, defs);
				}
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
		if (melee >= ranger && melee >= mage) {
			combatLevel += melee;
		} else if (ranger >= melee && ranger >= mage) {
			combatLevel += ranger;
		} else if (mage >= melee && mage >= ranger) {
			combatLevel += mage;
		}
		return combatLevel;
	}

	public void setLevels(int[] levels) {
		this.levels = levels;
	}
	
	public void setAttackBonus(Bonus bonus) {
		this.attackBonus = bonus;
	}

	public void addId(int npcId) {
		names = null;
		if (ids == null) {
			ids = new int[] { npcId };
			return;
		}
		for (int i = 0;i < ids.length;i++) {
			if (ids[i] == npcId)
				return;
		}
		int[] newIds = new int[ids.length+1];
		for (int i = 0;i < ids.length;i++) {
			newIds[i] = ids[i];
		}
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
