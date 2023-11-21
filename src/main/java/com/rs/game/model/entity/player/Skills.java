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
package com.rs.game.model.entity.player;

import com.rs.Settings;
import com.rs.game.World;
import com.rs.game.content.Effect;
import com.rs.game.content.Skillcapes;
import com.rs.game.content.randomevents.RandomEvents;
import com.rs.game.content.world.areas.wilderness.WildernessController;
import com.rs.game.model.entity.player.managers.AuraManager.Aura;
import com.rs.game.model.entity.player.managers.InterfaceManager.Sub;
import com.rs.lib.Constants;
import com.rs.lib.game.Item;
import com.rs.lib.game.Rights;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.Tile;
import com.rs.lib.net.ClientPacket;
import com.rs.lib.util.Utils;
import com.rs.plugin.PluginManager;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.XPGainEvent;
import com.rs.plugin.handlers.ButtonClickHandler;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSets;

import java.util.HashSet;
import java.util.Set;

@PluginEventHandler
public final class Skills {

	public static final double MAXIMUM_EXP = 200000000;
	public static final int
	ATTACK = 0,
	DEFENSE = 1,
	STRENGTH = 2,
	HITPOINTS = 3,
	RANGE = 4,
	PRAYER = 5,
	MAGIC = 6,
	COOKING = 7,
	WOODCUTTING = 8,
	FLETCHING = 9,
	FISHING = 10,
	FIREMAKING = 11,
	CRAFTING = 12,
	SMITHING = 13,
	MINING = 14,
	HERBLORE = 15,
	AGILITY = 16,
	THIEVING = 17,
	SLAYER = 18,
	FARMING = 19,
	RUNECRAFTING = 20,
	HUNTER = 21,
	CONSTRUCTION = 22,
	SUMMONING = 23,
	DUNGEONEERING = 24;

	public static int SIZE = 25;

	public static final String[] SKILL_NAME = { "Attack", "Defence", "Strength", "Constitution", "Ranged", "Prayer", "Magic", "Cooking", "Woodcutting", "Fletching", "Fishing", "Firemaking", "Crafting", "Smithing", "Mining", "Herblore", "Agility",
			"Thieving", "Slayer", "Farming", "Runecrafting", "Hunter", "Construction", "Summoning", "Dungeoneering" };

	public static final int NONE = -1;

	public static final int[] SKILLING = { PRAYER, COOKING, WOODCUTTING, FLETCHING, FISHING, FIREMAKING, CRAFTING, SMITHING, MINING, HERBLORE, AGILITY, THIEVING, RUNECRAFTING, SLAYER, FARMING, HUNTER, CONSTRUCTION, DUNGEONEERING };
	public static final int[] COMBAT = { ATTACK, DEFENSE, STRENGTH, RANGE, MAGIC };

	private short level[];
	private double xp[];
	private double[] xpTracks;
	private boolean[] trackSkills;
	private byte[] trackSkillsIds;
	private boolean[] enabledSkillsTargets;
	private boolean[] skillsTargetsUsingLevelMode;
	private int[] skillTargetStartValues;
	private int[] skillsTargetsValues;
	private boolean xpDisplay, xpPopup;

	private transient int currentCounter;
	private transient Player player;
	private transient Set<Integer> markedForUpdate = IntSets.synchronize(new IntOpenHashSet());
	private transient double bonusXpDrop;
	private transient int markedForLevelUp = -1;
	private transient int[] lastCheckedLevels = new int[25];

	public void passLevels(Player p) {
		level = p.getSkills().level;
		xp = p.getSkills().xp;
	}

	public Skills() {
		level = new short[25];
		xp = new double[25];
		for (int i = 0; i < level.length; i++) {
			level[i] = 1;
			xp[i] = 0;
		}
		level[3] = 10;
		xp[3] = 1184;
		enabledSkillsTargets = new boolean[25];
		skillsTargetsUsingLevelMode = new boolean[25];
		skillsTargetsValues = new int[25];
		skillTargetStartValues = new int[25];
		xpPopup = true;
		xpTracks = new double[3];
		trackSkills = new boolean[3];
		trackSkillsIds = new byte[3];
		trackSkills[0] = true;
		for (int i = 0; i < trackSkillsIds.length; i++)
			trackSkillsIds[i] = 30;
	}

	public boolean checkMulti99s() {
		int num99s = 0;
		for (int i = 0;i < xp.length;i++)
			if (getLevelForXp(i) >= 99)
				num99s++;
		return num99s > 1;
	}

	public void trimCapes() {
		if (checkMulti99s())
			for (Skillcapes cape : Skillcapes.values()) {
				if (player.getEquipment().getCapeId() == cape.untrimmed) {
					player.getEquipment().setSlot(Equipment.CAPE, new Item(cape.trimmed, 1));
					player.getAppearance().generateAppearanceData();
				}
				for(int i = 0;i < player.getInventory().getItems().getSize();i++) {
					if (player.getInventory().getItem(i) != null && player.getInventory().getItem(i).getId() == cape.untrimmed)
						player.getInventory().replaceItem(cape.trimmed, 1, i);
					player.getInventory().refresh();
				}
				for(int tab = 0;tab < player.getBank().getBankTabs().length;tab++) {
					if (player.getBank().getBankTabs()[tab] == null)
						continue;
					for (int i = 0;i < player.getBank().getBankTabs()[tab].length;i++) {
						if (player.getBank().getBankTabs()[tab][i] == null)
							continue;
						if (player.getBank().getBankTabs()[tab][i].getId() == cape.untrimmed) {
							player.getBank().getBankTabs()[tab][i].setId(cape.trimmed);
							player.getBank().refreshItems();
						}
					}
				}
			}
	}

	public static int getTargetIdByComponentId(int componentId) {
		switch (componentId) {
		case 150: // Attack
			return 0;
		case 9: // Strength
			return 1;
		case 40: // Range
			return 2;
		case 71: // Magic
			return 3;
		case 22: // Defence
			return 4;
		case 145: // Constitution
			return 5;
		case 58: // Prayer
			return 6;
		case 15: // Agility
			return 7;
		case 28: // Herblore
			return 8;
		case 46: // Theiving
			return 9;
		case 64: // Crafting
			return 10;
		case 84: // Runecrafting
			return 11;
		case 140: // Mining
			return 12;
		case 135: // Smithing
			return 13;
		case 34: // Fishing
			return 14;
		case 52: // Cooking
			return 15;
		case 130: // Firemaking
			return 16;
		case 125: // Woodcutting
			return 17;
		case 77: // Fletching
			return 18;
		case 90: // Slayer
			return 19;
		case 96: // Farming
			return 20;
		case 102: // Construction
			return 21;
		case 108: // Hunter
			return 22;
		case 114: // Summoning
			return 23;
		case 120: // Dungeoneering
			return 24;
		default:
			return -1;
		}
	}

	public static int getSkillIdByTargetId(int targetId) {
		switch (targetId) {
		case 0: // Attack
			return Constants.ATTACK;
		case 1: // Strength
			return Constants.STRENGTH;
		case 2: // Range
			return Constants.RANGE;
		case 3: // Magic
			return Constants.MAGIC;
		case 4: // Defence
			return Constants.DEFENSE;
		case 5: // Constitution
			return Constants.HITPOINTS;
		case 6: // Prayer
			return Constants.PRAYER;
		case 7: // Agility
			return Constants.AGILITY;
		case 8: // Herblore
			return Constants.HERBLORE;
		case 9: // Thieving
			return Constants.THIEVING;
		case 10: // Crafting
			return Constants.CRAFTING;
		case 11: // Runecrafting
			return Constants.RUNECRAFTING;
		case 12: // Mining
			return Constants.MINING;
		case 13: // Smithing
			return Constants.SMITHING;
		case 14: // Fishing
			return Constants.FISHING;
		case 15: // Cooking
			return Constants.COOKING;
		case 16: // Firemaking
			return Constants.FIREMAKING;
		case 17: // Woodcutting
			return Constants.WOODCUTTING;
		case 18: // Fletching
			return Constants.FLETCHING;
		case 19: // Slayer
			return Constants.SLAYER;
		case 20: // Farming
			return Constants.FARMING;
		case 21: // Construction
			return Constants.CONSTRUCTION;
		case 22: // Hunter
			return Constants.HUNTER;
		case 23: // Summoning
			return Constants.SUMMONING;
		case 24: // Dungeoneering
			return Constants.DUNGEONEERING;
		default:
			return -1;
		}
	}

	public static int getTargetIdBySkillId(int targetId) {
		switch (targetId) {
		case Constants.ATTACK:
			return 1;
		case Constants.STRENGTH:
			return 2;
		case Constants.RANGE:
			return 3;
		case Constants.MAGIC:
			return 4;
		case Constants.DEFENSE:
			return 5;
		case Constants.HITPOINTS:
			return 6;
		case Constants.PRAYER:
			return 7;
		case Constants.AGILITY:
			return 8;
		case Constants.HERBLORE:
			return 9;
		case Constants.THIEVING:
			return 10;
		case Constants.CRAFTING:
			return 11;
		case Constants.RUNECRAFTING:
			return 12;
		case Constants.MINING:
			return 13;
		case Constants.SMITHING:
			return 14;
		case Constants.FISHING:
			return 15;
		case Constants.COOKING:
			return 16;
		case Constants.FIREMAKING:
			return 17;
		case Constants.WOODCUTTING:
			return 18;
		case Constants.FLETCHING:
			return 19;
		case Constants.SLAYER:
			return 20;
		case Constants.FARMING:
			return 21;
		case Constants.CONSTRUCTION:
			return 22;
		case Constants.HUNTER:
			return 23;
		case Constants.SUMMONING:
			return 24;
		case Constants.DUNGEONEERING:
			return 25;
		default:
			return -1;
		}
	}

	public static ButtonClickHandler handleLevelupButtons = new ButtonClickHandler(741, e -> {
		if (e.getComponentId() == 9)
			e.getPlayer().getInterfaceManager().sendInterface(499);
	});

	public static ButtonClickHandler handleGuideButtons = new ButtonClickHandler(499, e -> {
		e.getPlayer().getVars().setVarBit(3289, e.getComponentId()-9);
	});

	public static ButtonClickHandler handleSkillTabButtons = new ButtonClickHandler(320, e -> {
		if (e.getPacket() == ClientPacket.IF_OP1) {
			e.getPlayer().stopAll();
			int targetId = Skills.getTargetIdByComponentId(e.getComponentId());
			int skillId = Skills.getSkillIdByTargetId(targetId);
			e.getPlayer().getVars().setVarBit(3289, 0);
			e.getPlayer().getVars().setVarBit(3288, targetId+1);
			if (e.getPlayer().getSkills().lastCheckedLevels[skillId] != e.getPlayer().getSkills().getLevelForXp(skillId)) {
				e.getPlayer().getPackets().sendVarc(getVarcIdFromTarget(targetId), e.getPlayer().getSkills().lastCheckedLevels[skillId]);
				e.getPlayer().getVars().setVarBit(4729, targetId+1);
			}
			e.getPlayer().getVars().syncVarsToClient();

			if (e.getPlayer().getSkills().lastCheckedLevels[skillId] != e.getPlayer().getSkills().getLevelForXp(skillId)) {
				e.getPlayer().getSkills().lastCheckedLevels[skillId] = e.getPlayer().getSkills().getLevelForXp(skillId);
				e.getPlayer().getInterfaceManager().sendInterface(741);
			} else
				e.getPlayer().getInterfaceManager().sendInterface(499);

			if (targetId != -1)
				switchFlash(e.getPlayer(), skillId, false);
		} else if (e.getPacket() == ClientPacket.IF_OP2 || e.getPacket() == ClientPacket.IF_OP3) {
			int skillId = Skills.getSkillIdByTargetId(Skills.getTargetIdByComponentId(e.getComponentId()));
			final boolean usingLevel = e.getPacket() == ClientPacket.IF_OP2;
			e.getPlayer().sendInputInteger("Please enter target " + (usingLevel ? "level" : "xp") + " you want to set: ", integer -> {
				if (!usingLevel) {
					int xpTarget = integer;
					if (xpTarget < e.getPlayer().getSkills().getXp(skillId) || e.getPlayer().getSkills().getXp(skillId) >= 200000000)
						return;
					if (xpTarget > 200000000)
						xpTarget = 200000000;
					e.getPlayer().getSkills().setSkillTarget(false, skillId, xpTarget);

				} else {
					int levelTarget = integer;
					int curLevel = e.getPlayer().getSkills().getLevel(skillId);
					if (curLevel >= (skillId == 24 ? 120 : 99))
						return;
					if (levelTarget > (skillId == 24 ? 120 : 99))
						levelTarget = skillId == 24 ? 120 : 99;
					if (levelTarget < e.getPlayer().getSkills().getLevel(skillId))
						return;
					e.getPlayer().getSkills().setSkillTarget(true, skillId, levelTarget);
				}
			});
		} else if (e.getPacket() == ClientPacket.IF_OP4) {
			int skillId = Skills.getSkillIdByTargetId(Skills.getTargetIdByComponentId(e.getComponentId()));
			e.getPlayer().getSkills().setSkillTargetEnabled(skillId, false);
			e.getPlayer().getSkills().setSkillTargetValue(skillId, 0);
			e.getPlayer().getSkills().setSkillTargetUsingLevelMode(skillId, false);
		}
	});

	private static int getVarcIdFromTarget(int targetId) {
		switch(targetId) {
		case 0:
			return 1469;
		case 1:
			return 1470;
		case 4:
			return 1471;
		case 2:
			return 1472;
		case 6:
			return 1473;
		case 3:
			return 1474;
		case 5:
			return 1475;
		case 7:
			return 1476;
		case 8:
			return 1477;
		case 9:
			return 1478;
		case 10:
			return 1479;
		case 18:
			return 1480;
		case 12:
			return 1481;
		case 13:
			return 1482;
		case 14:
			return 1483;
		case 15:
			return 1484;
		case 16:
			return 1485;
		case 17:
			return 1486;
		case 11:
			return 1487;
		case 19:
			return 1488;
		case 20:
			return 1489;
		case 21:
			return 1490;
		case 22:
			return 1491;
		case 23:
			return 1492;
		case 24:
			return 1493;
		}
		return -1;
	}

	@Deprecated
	public static ButtonClickHandler handleSkillGuideButtons = new ButtonClickHandler(1218, e -> {
//		if ((e.getComponentId() >= 33 && e.getComponentId() <= 55) || e.getComponentId() == 120 || e.getComponentId() == 151 || e.getComponentId() == 189)
//			e.getPlayer().getInterfaceManager().setInterface(false, 1218, 1, 1217);
	});

	public void refreshEnabledSkillsTargets() {
		int value = 0;
		for (int i = 0;i < enabledSkillsTargets.length;i++)
			if (enabledSkillsTargets[i])
				value |= 1 << getTargetIdBySkillId(i);
		player.getVars().setVar(1966, value);
	}

	public void refreshUsingLevelTargets() {
		int value = 0;
		for (int i = 0;i < skillsTargetsUsingLevelMode.length;i++)
			if (skillsTargetsUsingLevelMode[i])
				value |= 1 << getTargetIdBySkillId(i);
		player.getVars().setVar(1968, value);
	}

	public void refreshSkillsTargetsValues() {
		for (int i = 0; i < 25; i++)
			player.getVars().setVar(1969 + getTargetIdBySkillId(i)-1, skillsTargetsValues[i]);
	}

	public void refreshSkillTargetStartValues() {
		for (int i = 0; i < 25; i++)
			player.getVars().setVar(1994 + getTargetIdBySkillId(i)-1, skillTargetStartValues[i]);
	}

	public void setSkillTargetEnabled(int id, boolean enabled) {
		enabledSkillsTargets[id] = enabled;
		refreshEnabledSkillsTargets();
	}

	public void setSkillTargetUsingLevelMode(int id, boolean using) {
		skillsTargetsUsingLevelMode[id] = using;
		refreshUsingLevelTargets();
	}

	public void setSkillTargetValue(int skillId, int value) {
		skillsTargetsValues[skillId] = value;
		refreshSkillsTargetsValues();
	}

	public void setSkillTargetStartValue(int skillId, int value) {
		if (skillTargetStartValues == null)
			skillTargetStartValues = new int[25];
		skillTargetStartValues[skillId] = value;
		refreshSkillTargetStartValues();
	}

	public void setSkillTarget(boolean usingLevel, int skillId, int target) {
		setSkillTargetEnabled(skillId, true);
		setSkillTargetUsingLevelMode(skillId, usingLevel);
		setSkillTargetValue(skillId, target);
		setSkillTargetStartValue(skillId, (int) (usingLevel ? player.getSkills().getLevelForXp(skillId) : player.getSkills().getXp(skillId)));
	}

	public void sendXPDisplay() {
		for (int i = 0; i < trackSkills.length; i++) {
			player.getVars().setVarBit(10444 + i, trackSkills[i] ? 1 : 0);
			player.getVars().setVarBit(10440 + i, trackSkillsIds[i] + 1);
			refreshCounterXp(i);
		}
	}

	public void setupXPCounter() {
		player.getInterfaceManager().sendInterface(1214);
	}

	public void refreshCurrentCounter() {
		player.getVars().setVar(2478, currentCounter + 1);
	}

	public void setCurrentCounter(int counter) {
		if (counter != currentCounter) {
			currentCounter = counter;
			refreshCurrentCounter();
		}
	}

	public void switchTrackCounter() {
		trackSkills[currentCounter] = !trackSkills[currentCounter];
		player.getVars().setVarBit(10444 + currentCounter, trackSkills[currentCounter] ? 1 : 0);
	}

	public void resetCounterXP() {
		xpTracks[currentCounter] = 0;
		refreshCounterXp(currentCounter);
	}

	public void setCounterSkill(int skill) {
		xpTracks[currentCounter] = 0;
		trackSkillsIds[currentCounter] = (byte) skill;
		player.getVars().setVarBit(10440 + currentCounter, trackSkillsIds[currentCounter] + 1);
		refreshCounterXp(currentCounter);
	}

	public void refreshCounterXp(int counter) {
		player.getVars().setVar(counter == 0 ? 1801 : 2474 + counter, (int) (xpTracks[counter] * 10));
	}

	public static ButtonClickHandler handleSetupXPCounter = new ButtonClickHandler(1214, e -> {
		if (e.getComponentId() == 18)
			e.getPlayer().closeInterfaces();
		else if (e.getComponentId() >= 22 && e.getComponentId() <= 24)
			e.getPlayer().getSkills().setCurrentCounter(e.getComponentId() - 22);
		else if (e.getComponentId() == 27)
			e.getPlayer().getSkills().switchTrackCounter();
		else if (e.getComponentId() == 61)
			e.getPlayer().getSkills().resetCounterXP();
		else if (e.getComponentId() >= 31 && e.getComponentId() <= 57)
			if (e.getComponentId() == 33)
				e.getPlayer().getSkills().setCounterSkill(4);
			else if (e.getComponentId() == 34)
				e.getPlayer().getSkills().setCounterSkill(2);
			else if (e.getComponentId() == 35)
				e.getPlayer().getSkills().setCounterSkill(3);
			else if (e.getComponentId() == 42)
				e.getPlayer().getSkills().setCounterSkill(18);
			else if (e.getComponentId() == 49)
				e.getPlayer().getSkills().setCounterSkill(11);
			else
				e.getPlayer().getSkills().setCounterSkill(e.getComponentId() >= 56 ? e.getComponentId() - 27 : e.getComponentId() - 31);
	});

	public void switchXPDisplay() {
		xpDisplay = !xpDisplay;
	}

	public void switchXPPopup() {
		xpPopup = !xpPopup;
		player.sendMessage("XP pop-ups are now " + (xpPopup ? "en" : "dis") + "abled.");
		if (xpPopup)
			player.getInterfaceManager().sendSub(Sub.XP_DROPS, 1213);
		else
			player.getInterfaceManager().removeSub(Sub.XP_DROPS);
	}

	public void restoreSkills() {
		for (int skill = 0; skill < level.length; skill++) {
			level[skill] = (short) getLevelForXp(skill);
			markForRefresh(skill);
		}
	}

	public void setPlayer(Player player) {
		this.player = player;
		for (int i = 0;i < Constants.SKILL_NAME.length;i++) {
			markForRefresh(i);
			lastCheckedLevels[i] = getLevelForXp(i);
		}
		if (xpTracks == null) {
			xpPopup = true;
			xpTracks = new double[3];
			trackSkills = new boolean[3];
			trackSkillsIds = new byte[3];
			trackSkills[0] = true;
			for (int i = 0; i < trackSkillsIds.length; i++)
				trackSkillsIds[i] = 30;
		}
	}

	public short[] getLevels() {
		return level;
	}

	public double[] getXp() {
		return xp;
	}

	public int getLevel(int skill) {
		return level[skill];
	}

	public double getXp(int skill) {
		return xp[skill];
	}

	public boolean hasRequirements(int... skills) {
		for (int i = 0; i < skills.length; i += 2) {
			int skillId = skills[i];
			if (skillId == Constants.CONSTRUCTION || skillId == Constants.FARMING)
				continue;
			int skillLevel = skills[i + 1];
			if (getLevelForXp(skillId) < skillLevel)
				return false;

		}
		return true;
	}

	public int getCombatLevel() {
		return getCombatLevel(false);
	}

	public int getCombatLevelWithSummoning() {
		return getCombatLevel(true);
	}

	public int getCombatLevel(boolean summ) {
		int meleeBased = getLevelForXp(Constants.ATTACK) + getLevelForXp(Constants.STRENGTH);
		int rangeBased = (int) (getLevelForXp(Constants.RANGE) * 1.5);
		int magicBased = (int) (getLevelForXp(Constants.MAGIC) * 1.5);
		int realBase = meleeBased;
		if (rangeBased > realBase)
			realBase = rangeBased;
		if (magicBased > realBase)
			realBase = magicBased;
		realBase *= 1.3;
		realBase = (realBase + getLevelForXp(Constants.DEFENSE) + getLevelForXp(Constants.HITPOINTS) + (getLevelForXp(Constants.PRAYER) / 2) + (summ ? (getLevelForXp(Constants.SUMMONING) / 2) : 0)) / 4;
		return realBase;
	}

	public void set(int skill, int newLevel) {
		level[skill] = (short) newLevel;
		markForRefresh(skill);
	}

	public int drainLevel(int skill, int drain) {
		int drainLeft = drain - level[skill];
		if (drainLeft < 0)
			drainLeft = 0;
		level[skill] -= drain;
		if (level[skill] < 0)
			level[skill] = 0;
		markForRefresh(skill);
		return drainLeft;
	}

	public void drainSummoning(int amt) {
		if (player.getNSV().getB("infPrayer"))
			return;
		int level = getLevel(Constants.SUMMONING);
		if (level == 0)
			return;
		set(Constants.SUMMONING, amt > level ? 0 : level - amt);
	}

	public static int getXPForLevel(int level) {
		int points = 0;
		int output = 0;
		for (int lvl = 1; lvl <= level; lvl++) {
			points += Math.floor(lvl + 300.0 * Math.pow(2.0, lvl / 7.0));
			if (lvl >= level)
				return output;
			output = (int) Math.floor(points / 4);
		}
		return 0;
	}

	public int getLevelForXp(int skill) {
		double exp = Math.floor(xp[skill]);
		int points = 0;
		int output = 0;
		for (int lvl = 1; lvl <= (skill == Constants.DUNGEONEERING ? 120 : 99); lvl++) {
			points += Math.floor(lvl + 300.0 * Math.pow(2.0, lvl / 7.0));
			output = (int) Math.floor(points / 4);
			if ((output - 1) >= exp)
				return lvl;
		}
		return skill == Constants.DUNGEONEERING ? 120 : 99;
	}

	public static int getLevelForXp(int skill, long xp) {
		int points = 0;
		int output = 0;
		for (int lvl = 1; lvl <= (skill == Constants.DUNGEONEERING ? 120 : 99); lvl++) {
			points += Math.floor(lvl + 300.0 * Math.pow(2.0, lvl / 7.0));
			output = (int) Math.floor(points / 4);
			if ((output - 1) >= Math.floor(xp))
				return lvl;
		}
		return skill == Constants.DUNGEONEERING ? 120 : 99;
	}

	public int getTotalLevel() {
		int totalLevel = 0;
		for (int i = 0; i < level.length; i++)
			totalLevel += getLevelForXp(i);
		return totalLevel;
	}

	public void init() {
		for (int skill = 0; skill < level.length; skill++)
			markForRefresh(skill);
		if (enabledSkillsTargets == null)
			enabledSkillsTargets = new boolean[25];
		if (skillsTargetsUsingLevelMode == null)
			skillsTargetsUsingLevelMode = new boolean[25];
		if (skillsTargetsValues == null)
			skillsTargetsValues = new int[25];
		if (xp[3] < 1184) {
			xp[3] = 1184;
			markForRefresh(3);
		}
		sendXPDisplay();
		refreshEnabledSkillsTargets();
		refreshUsingLevelTargets();
		refreshSkillsTargetsValues();
		refreshSkillTargetStartValues();
		updateXPDrops();
	}

	public void queueBonusXPDrop(double amount) {
		bonusXpDrop += amount * 10.0;
	}

	public void markForRefresh(int skill) {
		markedForUpdate.add(skill);
	}

	public void updateXPDrops() {
		if (bonusXpDrop > 0)
			player.getVars().setVar(2044, (int) bonusXpDrop);
		else
			player.getVars().setVar(2044, 0);
		player.getVars().syncVarsToClient();
		Set<Integer> toUpdate = new HashSet<>(markedForUpdate);
		player.getPackets().updateStats(toUpdate.stream().mapToInt(e -> e.intValue()).toArray());
		if (markedForLevelUp != -1)
			sendLevelUp(markedForLevelUp);
		markedForUpdate.clear();
		markedForLevelUp = -1;
		bonusXpDrop = 0;
	}

	public static final int[] SKILL_LEVEL_UP_MUSIC_EFFECTS = { 30, 38, 65, 48,
			58, 56, 52, 34, 70, 44, 42, 39, 36, 64, 54, 46, 28, 68, 62, -1, 60,
			50, 32, 300, 417 };

	private void sendLevelUp(int skill) {
		int level = getLevelForXp(skill);
		player.getInterfaceManager().sendSub(Sub.LEVEL_UP, 1216);
		player.getPackets().sendVarc(1756, Skills.getTargetIdBySkillId(skill));
		switchFlash(player, skill, true);
		int musicEffect = SKILL_LEVEL_UP_MUSIC_EFFECTS[skill];
		if (musicEffect != -1)
			player.jingle(musicEffect);
		if (!player.hasRights(Rights.ADMIN) && (level == 99 || level == 120))
			checkMaxedNotification(player, skill, level);
	}

	public static void checkMaxedNotification(Player player, int skill, int level) {
		boolean reachedAll = true;
		for (int i = 0; i < Constants.SKILL_NAME.length; i++)
			if (player.getSkills().getLevelForXp(i) < 99) {
				reachedAll = false;
				break;
			}
		if (reachedAll)
			World.sendWorldMessage("<img=7><col=ff0000>News: " + player.getDisplayName() + " has just achieved at least level 99 in all skills!", false);
	}

	public static void switchFlash(Player player, int skill, boolean on) {
		int id;
		if (skill == Constants.ATTACK)
			id = 4732;
		else if (skill == Constants.STRENGTH)
			id = 4733;
		else if (skill == Constants.DEFENSE)
			id = 4734;
		else if (skill == Constants.RANGE)
			id = 4735;
		else if (skill == Constants.PRAYER)
			id = 4736;
		else if (skill == Constants.MAGIC)
			id = 4737;
		else if (skill == Constants.HITPOINTS)
			id = 4738;
		else if (skill == Constants.AGILITY)
			id = 4739;
		else if (skill == Constants.HERBLORE)
			id = 4740;
		else if (skill == Constants.THIEVING)
			id = 4741;
		else if (skill == Constants.CRAFTING)
			id = 4742;
		else if (skill == Constants.FLETCHING)
			id = 4743;
		else if (skill == Constants.MINING)
			id = 4744;
		else if (skill == Constants.SMITHING)
			id = 4745;
		else if (skill == Constants.FISHING)
			id = 4746;
		else if (skill == Constants.COOKING)
			id = 4747;
		else if (skill == Constants.FIREMAKING)
			id = 4748;
		else if (skill == Constants.WOODCUTTING)
			id = 4749;
		else if (skill == Constants.RUNECRAFTING)
			id = 4750;
		else if (skill == Constants.SLAYER)
			id = 4751;
		else if (skill == Constants.FARMING)
			id = 4752;
		else if (skill == Constants.CONSTRUCTION)
			id = 4753;
		else if (skill == Constants.HUNTER)
			id = 4754;
		else if (skill == Constants.SUMMONING)
			id = 4755;
		else
			id = 7756;
		player.getVars().setVarBit(id, on ? 1 : 0);
	}

	public int getCounterSkill(int skill) {
		switch (skill) {
		case Constants.ATTACK:
			return 0;
		case Constants.STRENGTH:
			return 1;
		case Constants.DEFENSE:
			return 4;
		case Constants.RANGE:
			return 2;
		case Constants.HITPOINTS:
			return 5;
		case Constants.PRAYER:
			return 6;
		case Constants.AGILITY:
			return 7;
		case Constants.HERBLORE:
			return 8;
		case Constants.THIEVING:
			return 9;
		case Constants.CRAFTING:
			return 10;
		case Constants.MINING:
			return 12;
		case Constants.SMITHING:
			return 13;
		case Constants.FISHING:
			return 14;
		case Constants.COOKING:
			return 15;
		case Constants.FIREMAKING:
			return 16;
		case Constants.WOODCUTTING:
			return 17;
		case Constants.SLAYER:
			return 19;
		case Constants.FARMING:
			return 20;
		case Constants.CONSTRUCTION:
			return 21;
		case Constants.HUNTER:
			return 22;
		case Constants.SUMMONING:
			return 23;
		case Constants.DUNGEONEERING:
			return 24;
		case Constants.MAGIC:
			return 3;
		case Constants.FLETCHING:
			return 18;
		case Constants.RUNECRAFTING:
			return 11;
		default:
			return -1;
		}

	}

	public double addXpLamp(int skill, double exp) {
		player.getControllerManager().trackXP(skill, (int) exp);

		int oldLevel = getLevelForXp(skill);
		double oldXp = xp[skill];
		if (!player.getControllerManager().gainXP(skill, exp))
			return 0.0;

		if (Settings.getConfig().getXpRate() > 1)
			exp *= Settings.getConfig().getXpRate();

		xp[skill] += exp;
		int newLevel = getLevelForXp(skill);
		double newXp = xp[skill];
		for (int i = 0; i < trackSkills.length; i++)
			if (trackSkills[i])
				if (trackSkillsIds[i] == 30 || (trackSkillsIds[i] == 29 && (skill == Constants.ATTACK || skill == Constants.DEFENSE || skill == Constants.STRENGTH || skill == Constants.MAGIC || skill == Constants.RANGE || skill == Constants.HITPOINTS))
				|| trackSkillsIds[i] == getCounterSkill(skill)) {
					xpTracks[i] += exp;
					refreshCounterXp(i);
				}

		if (xp[skill] > MAXIMUM_EXP)
			xp[skill] = MAXIMUM_EXP;

		if (oldLevel < 99 && newLevel >= 99) {
			World.sendWorldMessage("<img=5><col=FF0000>" + player.getDisplayName() + " has achieved 99 " + Constants.SKILL_NAME[skill] + "!", false);
			trimCapes();
		}
		if (oldXp <= 104273166 && newXp > 104273166)
			World.sendWorldMessage("<img=5><col=FF0000>" + player.getDisplayName() + " has achieved the equivalent of 120 " + Constants.SKILL_NAME[skill] + "!", false);

		int levelDiff = newLevel - oldLevel;
		if (newLevel > oldLevel) {
			level[skill] += levelDiff;
			markedForLevelUp = skill;
			//gold fireworks 2589, 2599
			player.setNextSpotAnim(new SpotAnim(2456, 0, 254));
			player.setNextSpotAnim(new SpotAnim(2457, 25, 254));
			player.setNextSpotAnim(new SpotAnim(2456, 50, 220));
			if (newLevel == 99 || newLevel == 120)
				World.sendSpotAnim(Tile.of(player.getTile()), new SpotAnim(1765));
			if (skill == Constants.SUMMONING || (skill >= Constants.ATTACK && skill <= Constants.MAGIC)) {
				player.getAppearance().generateAppearanceData();
				if (skill == Constants.HITPOINTS)
					player.heal(levelDiff * 10);
				else if (skill == Constants.PRAYER)
					player.getPrayer().restorePrayer(levelDiff * 10);
			}
		}
		markForRefresh(skill);
		return exp;
	}

	public void addXpQuest(int skill, double exp) {
		player.getControllerManager().trackXP(skill, (int) exp);
		if (player.isXpLocked())
			return;

		if (Settings.getConfig().getXpRate() > 1)
			exp *= Settings.getConfig().getXpRate();

		int oldLevel = getLevelForXp(skill);
		double oldXp = xp[skill];
		if (!player.getControllerManager().gainXP(skill, exp))
			return;
		xp[skill] += exp;
		int newLevel = getLevelForXp(skill);
		double newXp = xp[skill];
		for (int i = 0; i < trackSkills.length; i++)
			if (trackSkills[i])
				if (trackSkillsIds[i] == 30 || (trackSkillsIds[i] == 29 && (skill == Constants.ATTACK || skill == Constants.DEFENSE || skill == Constants.STRENGTH || skill == Constants.MAGIC || skill == Constants.RANGE || skill == Constants.HITPOINTS))
				|| trackSkillsIds[i] == getCounterSkill(skill)) {
					xpTracks[i] += exp;
					refreshCounterXp(i);
				}

		if (xp[skill] > MAXIMUM_EXP)
			xp[skill] = MAXIMUM_EXP;

		if (oldLevel < 99 && newLevel >= 99) {
			World.sendWorldMessage("<img=5><col=FF0000>" + player.getDisplayName() + " has achieved 99 " + Constants.SKILL_NAME[skill] + "!", false);
			trimCapes();
		}
		if (oldXp <= 104273166 && newXp > 104273166)
			World.sendWorldMessage("<img=5><col=FF0000>" + player.getDisplayName() + " has achieved the equivalent of 120 " + Constants.SKILL_NAME[skill] + "!", false);

		int levelDiff = newLevel - oldLevel;
		if (newLevel > oldLevel) {
			level[skill] += levelDiff;
			markedForLevelUp = skill;
			//gold fireworks 2589, 2599
			player.setNextSpotAnim(new SpotAnim(2456, 0, 254));
			player.setNextSpotAnim(new SpotAnim(2457, 25, 254));
			player.setNextSpotAnim(new SpotAnim(2456, 50, 220));
			if (newLevel == 99 || newLevel == 120)
				player.setNextSpotAnim(new SpotAnim(1765));
			if (skill == Constants.SUMMONING || (skill >= Constants.ATTACK && skill <= Constants.MAGIC)) {
				player.getAppearance().generateAppearanceData();
				if (skill == Constants.HITPOINTS)
					player.heal(levelDiff * 10);
				else if (skill == Constants.PRAYER)
					player.getPrayer().restorePrayer(levelDiff * 10);
			}
		}
		markForRefresh(skill);
	}

	public void addXp(int skill, double exp) {
		player.getControllerManager().trackXP(skill, (int) exp);
		PluginManager.handle(new XPGainEvent(player, skill, exp));
		if (player.isXpLocked())
			return;

		if (Settings.getConfig().getXpRate() > 1)
			exp *= Settings.getConfig().getXpRate();

		double modifier = 1.0;

		if (Utils.random(600) == 0)
			RandomEvents.attemptSpawnRandom(player);

		modifier += getBrawlerModifiers(skill, exp);

		if (player.hasEffect(Effect.DOUBLE_XP))
			modifier += 1.0;

		if (player.getBonusXpRate() > 0.0)
			modifier += player.getBonusXpRate();

		if (player.getAuraManager().isActivated(Aura.WISDOM))
			modifier += 0.025;

		if (modifier > 1.0) {
			double origXp = exp;
			exp *= modifier;
			queueBonusXPDrop(exp - origXp);
		}
		if (!player.getControllerManager().gainXP(skill, exp))
			return;
		int oldLevel = getLevelForXp(skill);
		double oldXp = xp[skill];
		if ((skill == HITPOINTS || skill == MAGIC) && (player.getInventory().containsItem(12850, 1) || player.getInventory().containsItem(12851, 1)))
			; //do not add any XP if they are using minigame runes
		else
			xp[skill] += exp;
		int newLevel = getLevelForXp(skill);
		double newXp = xp[skill];
		for (int i = 0; i < trackSkills.length; i++)
			if (trackSkills[i])
				if (trackSkillsIds[i] == 30 || (trackSkillsIds[i] == 29 && (skill == Constants.ATTACK || skill == Constants.DEFENSE || skill == Constants.STRENGTH || skill == Constants.MAGIC || skill == Constants.RANGE || skill == Constants.HITPOINTS))
				|| trackSkillsIds[i] == getCounterSkill(skill)) {
					xpTracks[i] += exp;
					refreshCounterXp(i);
				}

		if (xp[skill] > MAXIMUM_EXP)
			xp[skill] = MAXIMUM_EXP;

		if (oldLevel < 99 && newLevel >= 99) {
			World.sendWorldMessage("<img=5><col=FF0000>" + player.getDisplayName() + " has achieved 99 " + Constants.SKILL_NAME[skill] + "!", false);
			trimCapes();
		}
		if (oldXp <= 104273166 && newXp > 104273166)
			World.sendWorldMessage("<img=5><col=FF0000>" + player.getDisplayName() + " has achieved the equivalent of 120 " + Constants.SKILL_NAME[skill] + "!", false);
		int levelDiff = newLevel - oldLevel;
		if (newLevel > oldLevel) {
			level[skill] += levelDiff;
			markedForLevelUp = skill;
			//gold fireworks 2589, 2599
			player.setNextSpotAnim(new SpotAnim(2456, 0, 254));
			player.setNextSpotAnim(new SpotAnim(2457, 25, 254));
			player.setNextSpotAnim(new SpotAnim(2456, 50, 220));
			if (newLevel == 99 || newLevel == 120)
				player.setNextSpotAnim(new SpotAnim(1765));
			if (skill == Constants.SUMMONING || (skill >= Constants.ATTACK && skill <= Constants.MAGIC)) {
				player.getAppearance().generateAppearanceData();
				if (skill == Constants.HITPOINTS)
					player.heal(levelDiff * 10);
				else if (skill == Constants.PRAYER)
					player.getPrayer().restorePrayer(levelDiff * 10);
			}
		}
		markForRefresh(skill);
	}

	private double processBrawlers(Item gloves, int charges, int skill, double xp, int... validSkills) {
		boolean validSkill = false;
		for (int valid : validSkills)
			if (valid == skill)
				validSkill = true;
		if (!validSkill)
			return 0.0;
		if (charges <= -1) {
			charges = switch (gloves.getId()) {
				case 13845, 13846, 13847 -> 300000;
				default -> 464;
			};
		} else {
			charges -= switch (gloves.getId()) {
				case 13845, 13846, 13847 -> xp;
				default -> 1;
			};
		}
		gloves.addMetaData("brawlerCharges", charges);
		if (charges <= 0) {
			player.getEquipment().setSlot(Equipment.HANDS, null);
			player.sendMessage("Your brawling gloves have degraded.");
		}
		if (player.getControllerManager().getController() != null && player.getControllerManager().getController() instanceof WildernessController)
			return 2.0;
		return 0.5;
	}

	public double getBrawlerModifiers(int skill, double exp) {
		Item gloves = player.getEquipment().get(Equipment.HANDS);
		if (gloves == null)
			return 0.0;
		int charges = gloves.getMetaDataI("brawlerCharges", -1);
		switch(gloves.getId()) {
		case 13845:
			return processBrawlers(gloves, charges, skill, exp, Constants.ATTACK, Constants.STRENGTH, Constants.DEFENSE);
		case 13846:
			return processBrawlers(gloves, charges, skill, exp, Constants.RANGE);
		case 13847:
			return processBrawlers(gloves, charges, skill, exp, Constants.MAGIC);
		case 13848:
			return processBrawlers(gloves, charges, skill, exp, Constants.PRAYER);
		case 13849:
			return processBrawlers(gloves, charges, skill, exp, Constants.AGILITY);
		case 13850:
			return processBrawlers(gloves, charges, skill, exp, Constants.WOODCUTTING);
		case 13851:
			return processBrawlers(gloves, charges, skill, exp, Constants.FIREMAKING);
		case 13852:
			return processBrawlers(gloves, charges, skill, exp, Constants.MINING);
		case 13853:
			return processBrawlers(gloves, charges, skill, exp, Constants.HUNTER);
		case 13854:
			return processBrawlers(gloves, charges, skill, exp, Constants.THIEVING);
		case 13855:
			return processBrawlers(gloves, charges, skill, exp, Constants.SMITHING);
		case 13856:
			return processBrawlers(gloves, charges, skill, exp, Constants.FISHING);
		case 13857:
			return processBrawlers(gloves, charges, skill, exp, Constants.COOKING);
		}
		return 0.0;
	}

	public void addSkillXpRefresh(int skill, double xp) {
		this.xp[skill] += xp;
		level[skill] = (short) getLevelForXp(skill);
	}

	public void resetSkillNoRefresh(int skill) {
		xp[skill] = 0;
		level[skill] = 1;
	}

	public void resetSkillNoRefresh(int skill, int e) {
		xp[skill] = e;
		level[skill] = 10;
	}

	public void setXp(int skill, double exp) {
		xp[skill] = exp;
		markForRefresh(skill);
	}

	public boolean isMaxed(boolean compCape) {
		boolean maxed = true;
		for (int i = 0; i < level.length; i++)
			if (getLevelForXp(i) < 99) {
				if (player != null)
					player.sendMessage("You need a " + Constants.SKILL_NAME[i] + " level of 99.");
				maxed = false;
			}
		if (compCape && (getLevelForXp(Constants.DUNGEONEERING) < 120)) {
			if (player != null)
				player.sendMessage("You need a Dungeoneering level of 120.");
			maxed = false;
		}
		return maxed;
	}

	public long getTotalXp() {
		long totalXp = 0;
		for (int i = 0; i < level.length; i++)
			totalXp += xp[i];
		return totalXp;
	}

	public static int getSkillIndex(String skillName) {
		for (int i = 0; i < Constants.SKILL_NAME.length; i++)
			if (skillName.equalsIgnoreCase(Constants.SKILL_NAME[i]))
				return i;
		return 0;
	}

	public double getXp(String skillName) {
		for (int i = 0; i < Constants.SKILL_NAME.length; i++)
			if (skillName.equalsIgnoreCase(Constants.SKILL_NAME[i]))
				return xp[i];
		return 0.0;
	}

	public boolean is120(int skillId) {
		if (xp[skillId] >= 104273166)
			return true;
		return false;
	}

	public int[] getXpInt() {
		int[] skills = new int[xp.length];
		for (int i = 0;i < skills.length;i++)
			skills[i] = (int) xp[i];
		return skills;
	}

	public int getHighestSkillLevel() {
		int maxLevel = 1;
		for (int skill = 0; skill < level.length; skill++) {
			int level = getLevelForXp(skill);
			if (level > maxLevel)
				maxLevel = level;
		}
		return maxLevel;
	}

	public void divideXp(double xpRate) {
		if (xpRate > 2)
			for (int i = 0;i < xp.length;i++) {
				xp[i] = xp[i] / xpRate;
				level[i] = (short) getLevelForXp(i);
			}
	}

	public void adjustStat(int baseMod, double mul, int... skills) {
		for (int i : skills)
			adjustStat(baseMod, mul, true, i);
	}

	public void adjustStat(int baseMod, double mul, boolean boost, int... skills) {
		for (int i : skills)
			adjustStat(baseMod, mul, boost, i);
	}

	public void adjustStat(int baseMod, double mul, boolean boost, int skill) {
		int realLevel = getLevelForXp(skill);
		int realBoost = (int) (baseMod + (getLevel(skill) * mul));
		if (realBoost < 0)
			realLevel = getLevel(skill);
		int maxBoost = (int) (realLevel + (baseMod + (realLevel * mul)));
		level[skill] = (short) Utils.clampI(level[skill] + realBoost, 0, boost ? maxBoost : (getLevel(skill) > realLevel ? getLevel(skill) : realLevel));
		markForRefresh(skill);
	}
	
	public void lowerStat(int skill, double mul, double maxDrain) {
		lowerStat(0, mul, maxDrain, skill);
	}
	
	public void lowerStat(int skill, int amt, double maxDrain) {
		lowerStat(amt, 0.0, maxDrain, skill);
	}
	
	public void lowerStat(int skill, int amt) {
		lowerStat(amt, 0.0, 0.0, skill);
	}
	
	public void lowerStat(int baseMod, double mul, double maxDrain, int skill) {
		int realLevel = getLevelForXp(skill);
		int realDrain = (int) (baseMod + (getLevel(skill) * mul));
		level[skill] = (short) Utils.clampI(level[skill] - realDrain, (int) ((double) realLevel * maxDrain), getLevel(skill));
		markForRefresh(skill);
	}

	public static int[] allExcept(int... exclude) {
		int[] skills = new int[1+Constants.SKILL_NAME.length-exclude.length];
		int idx = 0;
		skills: for (int i = 0;i < Constants.SKILL_NAME.length;i++) {
			for (int ex : exclude) {
				if (i == ex)
					continue skills;
			}
			skills[idx++] = i;
		}
		return skills;
	}
	
	public boolean xpCounterOpen() {
		return xpDisplay;
	}
	
	public boolean xpDropsActive() {
		return xpPopup;
	}
}
