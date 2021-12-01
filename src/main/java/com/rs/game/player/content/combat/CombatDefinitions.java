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
//  Copyright © 2021 Trenton Kress
//  This file is part of project: Darkan
//
package com.rs.game.player.content.combat;

import com.rs.cache.loaders.Bonus;
import com.rs.game.player.Player;
import com.rs.game.player.managers.InterfaceManager.Tab;
import com.rs.lib.Constants;
import com.rs.lib.game.Item;
import com.rs.lib.util.Utils;

public final class CombatDefinitions {

	private transient Player player;
	private transient boolean usingSpecialAttack;
	private transient int[] bonuses;

	private byte attackStyle;
	private byte specialAttackPercentage;
	private boolean autoRetaliate;
	private byte sortSpellBook;
	private boolean showCombatSpells;
	private boolean showSkillSpells;
	private boolean showMiscallaneousSpells;
	private boolean showTeleportSpells;
	private boolean defensiveCasting;
	private transient boolean dungSpellBook;
	private byte spellBook;
	private CombatSpell autoCast;

	public CombatSpell getSpell() {
		CombatSpell spell = player.getTempAttribs().getO("manualCastSpell");
		if (spell != null)
			return spell;
		return autoCast;
	}
	
	public boolean hasManualCastQueued() {
		return player.getTempAttribs().getO("manualCastSpell") != null;
	}
	
	public void setManualCastSpell(CombatSpell spell) {
		player.getTempAttribs().setO("manualCastSpell", spell);
	}
	
	public void clearManualCastSpell() {
		player.getTempAttribs().removeO("manualCastSpell");
	}

	public CombatSpell getAutoCast() {
		return autoCast;
	}

	public void resetSpells(boolean removeAutoSpell) {
		clearManualCastSpell();
		if (removeAutoSpell) {
			setAutoCastSpell(null);
			refreshAutoCastSpell();
		}
	}

	public void setAutoCastSpell(CombatSpell spell) {
		autoCast = spell;
		refreshAutoCastSpell();
	}

	public void refreshAutoCastSpell() {
		refreshAttackStyle();
		player.getVars().setVar(108, getSpellAutoCastConfigValue());
	}

	public int getSpellAutoCastConfigValue() {
		if (autoCast == null)
			return 0;
		if (dungSpellBook) {
			switch (autoCast) {
			case WIND_STRIKE:
				return 103;
			case WATER_STRIKE:
				return 105;
			case EARTH_STRIKE:
				return 107;
			case FIRE_STRIKE:
				return 109;
			case WIND_BOLT:
				return 111;
			case WATER_BOLT:
				return 113;
			case EARTH_BOLT:
				return 115;
			case FIRE_BOLT:
				return 117;
			case WIND_BLAST:
				return 119;
			case WATER_BLAST:
				return 121;
			case EARTH_BLAST:
				return 123;
			case FIRE_BLAST:
				return 125;
			case WIND_WAVE:
				return 127;
			case WATER_WAVE:
				return 129;
			case EARTH_WAVE:
				return 131;
			case FIRE_WAVE:
				return 133;
			case WIND_SURGE:
				return 135;
			case WATER_SURGE:
				return 137;
			case EARTH_SURGE:
				return 139;
			case FIRE_SURGE:
				return 141;
			default:
				return 0;
			}
		}
		if (spellBook == 0) {
			switch (autoCast) {
			case WIND_STRIKE:
				return 3;
			case WATER_STRIKE:
				return 5;
			case EARTH_STRIKE:
				return 7;
			case FIRE_STRIKE:
				return 9;
			case WIND_BOLT:
				return 11;
			case WATER_BOLT:
				return 13;
			case EARTH_BOLT:
				return 15;
			case FIRE_BOLT:
				return 17;
			case CRUMBLE_UNDEAD:
				return 35;
			case WIND_BLAST:
				return 19;
			case WATER_BLAST:
				return 21;
			case EARTH_BLAST:
				return 23;
			case FIRE_BLAST:
				return 25;
			case IBAN_BLAST:
				return 45;
			case MAGIC_DART:
				return 37;
			case SARADOMIN_STRIKE:
				return 41;
			case CLAWS_OF_GUTHIX:
				return 39;
			case FLAMES_OF_ZAMORAK:
				return 43;
			case WIND_WAVE:
				return 27;
			case WATER_WAVE:
				return 29;
			case EARTH_WAVE:
				return 31;
			case FIRE_WAVE:
				return 33;
			case WIND_SURGE:
				return 47;
			case WATER_SURGE:
				return 49;
			case EARTH_SURGE:
				return 51;
			case FIRE_SURGE:
				return 53;
			case WIND_RUSH:
				return 143;
			case STORM_OF_ARMADYL:
				return 145;
			default:
				return 0;
			}
		} else if (spellBook == 1) {
			switch (autoCast) {
			case SMOKE_RUSH:
				return 63;
			case SHADOW_RUSH:
				return 65;
			case BLOOD_RUSH:
				return 67;
			case ICE_RUSH:
				return 69;
			case SMOKE_BURST:
				return 71;
			case SHADOW_BURST:
				return 73;
			case BLOOD_BURST:
				return 75;
			case ICE_BURST:
				return 77;
			case SMOKE_BLITZ:
				return 79;
			case SHADOW_BLITZ:
				return 81;
			case BLOOD_BLITZ:
				return 83;
			case ICE_BLITZ:
				return 85;
			case SMOKE_BARRAGE:
				return 87;
			case SHADOW_BARRAGE:
				return 89;
			case BLOOD_BARRAGE:
				return 91;
			case ICE_BARRAGE:
				return 93;
			case MIASMIC_RUSH:
				return 95;
			case MIASMIC_BURST:
				return 97;
			case MIASMIC_BLITZ:
				return 99;
			case MIASMIC_BARRAGE:
				return 101;
			default:
				return 0;
			}
		} else {
			return 0;
		}
	}

	public CombatDefinitions() {
		specialAttackPercentage = 100;
		autoRetaliate = true;
		showCombatSpells = true;
		showSkillSpells = true;
		showMiscallaneousSpells = true;
		showTeleportSpells = true;
	}

	public void setSpellBook(int id) {
		if (id == 3)
			dungSpellBook = true;
		else
			spellBook = (byte) id;
		refreshSpellbook();
		player.getInterfaceManager().sendTab(Tab.MAGIC);
	}
	
	public int getSpellbookId() {
		if (dungSpellBook)
			return 3;
		return spellBook;
	}

	public int getSpellBook() {
		if (dungSpellBook)
			return 950; // dung book
		else {
			if (spellBook == 0)
				return 192; // normal
			else if (spellBook == 1)
				return 193; // ancients
			else
				return 430; // lunar
		}

	}

	public void switchShowCombatSpells() {
		showCombatSpells = !showCombatSpells;
		refreshSpellbookSettings();
	}

	public void switchShowSkillSpells() {
		showSkillSpells = !showSkillSpells;
		refreshSpellbookSettings();
	}

	public void switchShowMiscSpells() {
		showMiscallaneousSpells = !showMiscallaneousSpells;
		refreshSpellbookSettings();
	}

	public void switchShowTeleportSkillSpells() {
		showTeleportSpells = !showTeleportSpells;
		refreshSpellbookSettings();
	}

	public void switchDefensiveCasting() {
		defensiveCasting = !defensiveCasting;
		refreshSpellbookSettings();
	}

	public void setSortSpellBook(int sortId) {
		this.sortSpellBook = (byte) sortId;
		refreshSpellbookSettings();
	}

	public boolean isDefensiveCasting() {
		return defensiveCasting;
	}

	public void refreshSpellbookSettings() {
		player.getVars().setVarBit(357, getSpellbookId());
		player.getVars().setVarBit(5822, sortSpellBook);
		player.getVars().setVarBit(5823, sortSpellBook);
		player.getVars().setVarBit(5824, sortSpellBook);
		player.getVars().setVarBit(7347, sortSpellBook);
		
		player.getVars().setVarBit(6459, showCombatSpells ? 0 : 1);
		player.getVars().setVarBit(6466, showCombatSpells ? 0 : 1);
		player.getVars().setVarBit(6463, showCombatSpells ? 0 : 1);
		player.getVars().setVarBit(7348, showCombatSpells ? 0 : 1);
		
		player.getVars().setVarBit(6460, showSkillSpells ? 0 : 1);
		player.getVars().setVarBit(7349, showSkillSpells ? 0 : 1);
		
		player.getVars().setVarBit(6461, showMiscallaneousSpells ? 0 : 1);
		player.getVars().setVarBit(6464, showMiscallaneousSpells ? 0 : 1);
		player.getVars().setVarBit(7350, showMiscallaneousSpells ? 0 : 1);
		
		player.getVars().setVarBit(6462, showTeleportSpells ? 0 : 1);
		player.getVars().setVarBit(6467, showTeleportSpells ? 0 : 1);
		player.getVars().setVarBit(6465, showTeleportSpells ? 0 : 1);
		player.getVars().setVarBit(7351, showTeleportSpells ? 0 : 1);
		
		player.getVars().setVarBit(2668, defensiveCasting ? 1 : 0);
	}

	public static final Bonus getMeleeDefenceBonus(Bonus attackType) {
		switch(attackType) {
		case STAB_ATT:
			return Bonus.STAB_DEF;
		case SLASH_ATT:
			return Bonus.SLASH_DEF;
		default:
			return Bonus.CRUSH_DEF;
		}
	}

	public void setPlayer(Player player) {
		this.player = player;
		bonuses = new int[18];
	}

	public int getBonus(Bonus bonus) {
		return bonuses[bonus.ordinal()];
	}
	
	public void setBonus(Bonus bonus, int val) {
		bonuses[bonus.ordinal()] = val;
	}

	public void refreshBonuses() {
		bonuses = new int[18];
		for (Item item : player.getEquipment().getItemsCopy()) {
			if (item == null)
				continue;
			int[] bonuses = item.getDefinitions().getBonuses();
			if (bonuses == null)
				continue;
			for (Bonus bonus : Bonus.values()) {
				if (bonus == Bonus.RANGE_STR && getBonus(Bonus.RANGE_STR) != 0)
					continue;
				this.bonuses[bonus.ordinal()] += bonuses[bonus.ordinal()];
			}
			switch(item.getId()) {
			case 11283:
			case 11284:
				this.bonuses[Bonus.STAB_DEF.ordinal()] += item.getMetaDataI("dfsCharges", 0);
				this.bonuses[Bonus.SLASH_DEF.ordinal()] += item.getMetaDataI("dfsCharges", 0);
				this.bonuses[Bonus.CRUSH_DEF.ordinal()] += item.getMetaDataI("dfsCharges", 0);
				this.bonuses[Bonus.RANGE_DEF.ordinal()] += item.getMetaDataI("dfsCharges", 0);
				break;
			case 19152:
			case 19157:
			case 19162:
				this.bonuses[Bonus.RANGE_STR.ordinal()] += Utils.clampI((int) (player.getSkills().getLevelForXp(Constants.RANGE) * 0.7), 0, 49);
				break;
			}
		}
	}

	public void resetSpecialAttack() {
		drainSpec(0);
		specialAttackPercentage = 100;
		refreshSpecialAttackPercentage();
	}

	public void setSpecialAttack(int special) {
		drainSpec(0);
		specialAttackPercentage = (byte) special;
		refreshSpecialAttackPercentage();
	}

	public void restoreSpecialAttack() {
		if (player.getFamiliar() != null)
			player.getFamiliar().restoreSpecialAttack(15);
		if (specialAttackPercentage == 100)
			return;
		restoreSpecialAttack(10);
		if (specialAttackPercentage == 100 || specialAttackPercentage == 50)
			player.sendMessage("<col=00FF00>Your special attack energy is now " + specialAttackPercentage + "%.", true);
	}

	public void restoreSpecialAttack(int percentage) {
		if (specialAttackPercentage >= 100 || player.getInterfaceManager().containsScreenInter())
			return;
		specialAttackPercentage += specialAttackPercentage > (100 - percentage) ? 100 - specialAttackPercentage : percentage;
		refreshSpecialAttackPercentage();
	}

	public void init() {
		refreshUsingSpecialAttack();
		refreshSpecialAttackPercentage();
		refreshAutoRelatie();
		refreshAttackStyle();
		refreshSpellbook();
	}
	
	public void refreshSpellbook() {
		refreshSpellbookSettings();
		refreshAutoCastSpell();
		player.getVars().syncVarsToClient();
		player.getPackets().sendRunScriptBlank(2057);
	}

	public void checkAttackStyle() {
		if (autoCast == null)
			setAttackStyle(attackStyle);
	}

	public void setAttackStyle(int style) {
		AttackStyle[] styles = AttackStyle.getStyles(player.getEquipment().getWeaponId());
		if (style < 0)
			style = 0;
		if (style >= styles.length)
			style = styles.length-1;
		if (style != attackStyle) {
			attackStyle = (byte) style;
			if (autoCast != null)
				resetSpells(true);
			else
				refreshAttackStyle();
		} else if (autoCast != null)
			resetSpells(true);
	}

	public void refreshAttackStyle() {
		player.getVars().setVar(43, autoCast != null ? 4 : attackStyle);
	}

	public void sendUnlockAttackStylesButtons() {
		for (int componentId = 7; componentId <= 10; componentId++)
			player.getPackets().setIFRightClickOps(884, componentId, -1, 0, 0);
	}

	public void switchUsingSpecialAttack() {
		usingSpecialAttack = !usingSpecialAttack;
		refreshUsingSpecialAttack();
	}

	public void drainSpec(int amount) {
		usingSpecialAttack = false;
		refreshUsingSpecialAttack();
		if (player.getTempAttribs().getB("infSpecialAttack"))
			amount = 0;
		if (amount > 0) {
			specialAttackPercentage -= amount;
			refreshSpecialAttackPercentage();
		}
	}

	public boolean hasRingOfVigour() {
		return player.getEquipment().getRingId() == 19669;
	}

	public int getSpecialAttackPercentage() {
		return specialAttackPercentage;
	}

	public void refreshUsingSpecialAttack() {
		player.getVars().setVar(301, usingSpecialAttack ? 1 : 0);
	}

	public void refreshSpecialAttackPercentage() {
		player.getVars().setVar(300, specialAttackPercentage * 10);
	}

	public void switchAutoRetaliate() {
		autoRetaliate = !autoRetaliate;
		refreshAutoRelatie();
	}

	public void refreshAutoRelatie() {
		player.getVars().setVar(172, autoRetaliate ? 0 : 1);
	}

	public boolean isUsingSpecialAttack() {
		return usingSpecialAttack;
	}

	public AttackStyle getAttackStyle() {
		AttackStyle[] styles = AttackStyle.getStyles(player.getEquipment().getWeaponId());
		int style = attackStyle;
		if (style >= styles.length)
			style = styles.length-1;
		return styles[style];
	}
	
	public int getAttackBonusForStyle() {
		return getAttackStyle().getAttackType().getAttackBonus(player);
	}
	
	public int getDefenseBonusForStyle(AttackStyle style) {
		return style.getAttackType().getDefenseBonus(player);
	}

	public boolean isAutoRelatie() {
		return autoRetaliate;
	}

	public void setAutoRelatie(boolean autoRelatie) {
		this.autoRetaliate = autoRelatie;
	}

	public boolean isDungeonneringSpellBook() {
		return dungSpellBook;
	}

	public void removeDungeonneringBook() {
		if (dungSpellBook) {
			dungSpellBook = false;
			player.getInterfaceManager().sendTab(Tab.MAGIC);
		}
	}

	public int getAttackStyleId() {
		return attackStyle;
	}
}
