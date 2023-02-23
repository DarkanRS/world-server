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
package com.rs.game.model.entity.player.managers;

import java.util.concurrent.CopyOnWriteArraySet;

import com.rs.cache.loaders.Bonus;
import com.rs.game.World;
import com.rs.game.content.skills.prayer.Leech;
import com.rs.game.content.skills.prayer.Prayer;
import com.rs.game.content.skills.prayer.Sap;
import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.interactions.PlayerCombatInteraction;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.managers.InterfaceManager.Sub;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ButtonClickHandler;

@PluginEventHandler
public class PrayerManager {

	private enum StatMod {
		ATTACK, STRENGTH, DEFENSE, RANGE, MAGE
	}

	private transient Player player;
	private transient CopyOnWriteArraySet<Prayer> active = new CopyOnWriteArraySet<>();
	private transient int[] statMods;
	private transient boolean settingQuickPrayers;
	private transient boolean quickPrayersOn;

	private double points;
	private boolean curses;
	private CopyOnWriteArraySet<Prayer> quickPrays = new CopyOnWriteArraySet<>();
	private CopyOnWriteArraySet<Prayer> quickCurses = new CopyOnWriteArraySet<>();

	public static ButtonClickHandler handlePrayerInterface = new ButtonClickHandler(271, e -> {
		if (e.getComponentId() == 8 || e.getComponentId() == 42)
			e.getPlayer().getPrayer().switchPrayer(e.getSlotId());
		else if (e.getComponentId() == 43 && e.getPlayer().getPrayer().settingQuickPrayers)
			e.getPlayer().getPrayer().switchSettingQuickPrayer();
	});

	public void switchPrayer(int prayerId) {
		Prayer prayer = Prayer.forSlot(prayerId, curses);
		if (prayer == null)
			return;
		if (active.contains(prayer) || (settingQuickPrayers && (quickPrays.contains(prayer) || quickCurses.contains(prayer))))
			closePrayer(prayer);
		else
			activatePrayer(prayer);
	}

	private boolean canUsePrayer(Prayer prayer) {
		if (player.getSkills().getLevelForXp(Constants.PRAYER) < prayer.getReq()) {
			player.sendMessage("You need a prayer level of at least " + prayer.getReq() + " to use this prayer.");
			return false;
		}
		if (prayer.isCurse() && !player.isQuestComplete(Quest.TEMPLE_AT_SENNTISTEN, "to use ancient curses."))
			return false;
		switch(prayer) {
		case RAPID_RENEWAL:
			if (!player.hasRenewalPrayer) {
				player.sendMessage("You must unlock this prayer as a dungeoneering reward.");
				return false;
			}
			break;
		case RIGOUR:
			if (!player.hasRigour) {
				player.sendMessage("You must unlock this prayer as a dungeoneering reward.");
				return false;
			}
			break;
		case AUGURY:
			if (!player.hasAugury) {
				player.sendMessage("You must unlock this prayer as a dungeoneering reward.");
				return false;
			}
			break;
		case CHIVALRY:
		case PIETY:
			if (player.getSkills().getLevelForXp(Constants.DEFENSE) < 70) {
				player.sendMessage("You need a defence level of at least 70 to use this prayer.");
				return false;
			}
			break;
		case PROTECT_MAGIC:
		case PROTECT_RANGE:
		case PROTECT_SUMMONING:
		case PROTECT_MELEE:
		case DEFLECT_MAGIC:
		case DEFLECT_RANGE:
		case DEFLECT_SUMMONING:
		case DEFLECT_MELEE:
			if (player.isProtectionPrayBlocked()) {
				player.sendMessage("You are currently injured and cannot use protection prayers!");
				return false;
			}
			break;
		default:
			break;
		}
		return true;
	}

	private boolean activatePrayer(Prayer prayer) {
		if (!canUsePrayer(prayer))
			return false;
		switch(prayer) {
		case ATK_T1:
		case ATK_T2:
		case ATK_T3:
			closePrayers(Prayer.ATK_T1, Prayer.ATK_T2, Prayer.ATK_T3, Prayer.RNG_T1, Prayer.RNG_T2, Prayer.RNG_T3, Prayer.MAG_T1, Prayer.MAG_T2, Prayer.MAG_T3, Prayer.CHIVALRY, Prayer.PIETY, Prayer.RIGOUR, Prayer.AUGURY);
			break;
		case STR_T1:
		case STR_T2:
		case STR_T3:
			closePrayers(Prayer.STR_T1, Prayer.STR_T2, Prayer.STR_T3, Prayer.RNG_T1, Prayer.RNG_T2, Prayer.RNG_T3, Prayer.MAG_T1, Prayer.MAG_T2, Prayer.MAG_T3, Prayer.CHIVALRY, Prayer.PIETY, Prayer.RIGOUR, Prayer.AUGURY);
			break;
		case DEF_T1:
		case DEF_T2:
		case DEF_T3:
			closePrayers(Prayer.DEF_T1, Prayer.DEF_T2, Prayer.DEF_T3, Prayer.CHIVALRY, Prayer.PIETY, Prayer.RIGOUR, Prayer.AUGURY);
			break;
		case RNG_T1:
		case RNG_T2:
		case RNG_T3:
			closePrayers(Prayer.ATK_T1, Prayer.ATK_T2, Prayer.ATK_T3, Prayer.STR_T1, Prayer.STR_T2, Prayer.STR_T3, Prayer.RNG_T1, Prayer.RNG_T2, Prayer.RNG_T3, Prayer.MAG_T1, Prayer.MAG_T2, Prayer.MAG_T3, Prayer.CHIVALRY, Prayer.PIETY, Prayer.RIGOUR, Prayer.AUGURY);
			break;
		case MAG_T1:
		case MAG_T2:
		case MAG_T3:
			closePrayers(Prayer.ATK_T1, Prayer.ATK_T2, Prayer.ATK_T3, Prayer.STR_T1, Prayer.STR_T2, Prayer.STR_T3, Prayer.RNG_T1, Prayer.RNG_T2, Prayer.RNG_T3, Prayer.MAG_T1, Prayer.MAG_T2, Prayer.MAG_T3, Prayer.CHIVALRY, Prayer.PIETY, Prayer.RIGOUR, Prayer.AUGURY);
			break;
		case CHIVALRY:
		case PIETY:
		case RIGOUR:
		case AUGURY:
			closePrayers(Prayer.ATK_T1, Prayer.ATK_T2, Prayer.ATK_T3, Prayer.STR_T1, Prayer.STR_T2, Prayer.STR_T3);
			closePrayers(Prayer.RNG_T1, Prayer.RNG_T2, Prayer.RNG_T3, Prayer.MAG_T1, Prayer.MAG_T2, Prayer.MAG_T3);
			closePrayers(Prayer.DEF_T1, Prayer.DEF_T2, Prayer.DEF_T3, Prayer.CHIVALRY, Prayer.PIETY, Prayer.RIGOUR, Prayer.AUGURY);
			break;
		case RAPID_RENEWAL:
		case RAPID_HEAL:
			closePrayers(Prayer.RAPID_RENEWAL, Prayer.RAPID_HEAL);
			break;
		case PROTECT_MAGIC:
		case PROTECT_RANGE:
		case PROTECT_MELEE:
			closePrayers(Prayer.PROTECT_MAGIC, Prayer.PROTECT_RANGE, Prayer.PROTECT_MELEE, Prayer.RETRIBUTION, Prayer.REDEMPTION, Prayer.SMITE);
			break;
		case PROTECT_SUMMONING:
			closePrayers(Prayer.PROTECT_SUMMONING, Prayer.RETRIBUTION, Prayer.REDEMPTION, Prayer.SMITE);
			break;
		case SMITE:
		case REDEMPTION:
		case RETRIBUTION:
			closePrayers(Prayer.PROTECT_MAGIC, Prayer.PROTECT_RANGE, Prayer.PROTECT_MELEE, Prayer.PROTECT_SUMMONING, Prayer.RETRIBUTION, Prayer.REDEMPTION, Prayer.SMITE);
			break;
		case PROTECT_ITEM_C:
			if (!settingQuickPrayers) {
				player.setNextAnimation(new Animation(12567));
				player.setNextSpotAnim(new SpotAnim(2213));
			}
			break;
		case BERSERKER:
			if (!settingQuickPrayers) {
				player.setNextAnimation(new Animation(12589));
				player.setNextSpotAnim(new SpotAnim(2266));
			}
			break;
		case SAP_WARRIOR:
			closePrayers(Prayer.TURMOIL, Prayer.LEECH_ATTACK, Prayer.LEECH_STRENGTH, Prayer.LEECH_DEFENSE);
			break;
		case SAP_MAGE:
			closePrayers(Prayer.TURMOIL, Prayer.LEECH_MAGIC);
			break;
		case SAP_RANGE:
			closePrayers(Prayer.TURMOIL, Prayer.LEECH_RANGE);
			break;
		case SAP_SPIRIT:
			closePrayers(Prayer.TURMOIL, Prayer.LEECH_SPECIAL, Prayer.LEECH_ENERGY);
			break;
		case LEECH_ATTACK:
			closePrayers(Prayer.TURMOIL, Prayer.SAP_WARRIOR);
			break;
		case LEECH_STRENGTH:
			closePrayers(Prayer.TURMOIL, Prayer.SAP_WARRIOR);
			break;
		case LEECH_DEFENSE:
			closePrayers(Prayer.TURMOIL, Prayer.SAP_WARRIOR);
			break;
		case LEECH_RANGE:
			closePrayers(Prayer.TURMOIL, Prayer.SAP_RANGE);
			break;
		case LEECH_MAGIC:
			closePrayers(Prayer.TURMOIL, Prayer.SAP_MAGE);
			break;
		case LEECH_SPECIAL:
		case LEECH_ENERGY:
			closePrayers(Prayer.TURMOIL, Prayer.SAP_SPIRIT);
			break;
		case DEFLECT_MAGIC:
		case DEFLECT_RANGE:
		case DEFLECT_MELEE:
			closePrayers(Prayer.DEFLECT_MAGIC, Prayer.DEFLECT_RANGE, Prayer.DEFLECT_MELEE, Prayer.WRATH, Prayer.SOUL_SPLIT);
			break;
		case DEFLECT_SUMMONING:
			closePrayers(Prayer.DEFLECT_SUMMONING, Prayer.WRATH, Prayer.SOUL_SPLIT);
			break;
		case WRATH:
		case SOUL_SPLIT:
			closePrayers(Prayer.DEFLECT_MAGIC, Prayer.DEFLECT_MELEE, Prayer.DEFLECT_RANGE, Prayer.DEFLECT_SUMMONING, Prayer.WRATH, Prayer.SOUL_SPLIT);
			break;
		case TURMOIL:
			closePrayers(Prayer.SAP_WARRIOR, Prayer.SAP_MAGE, Prayer.SAP_RANGE, Prayer.SAP_SPIRIT);
			closePrayers(Prayer.LEECH_ATTACK, Prayer.LEECH_STRENGTH, Prayer.LEECH_DEFENSE, Prayer.LEECH_MAGIC, Prayer.LEECH_RANGE, Prayer.LEECH_SPECIAL, Prayer.LEECH_ENERGY);
			if (!settingQuickPrayers) {
				player.setNextAnimation(new Animation(12565));
				player.setNextSpotAnim(new SpotAnim(2226));
			}
			break;
		default:
			break;
		}
		if (settingQuickPrayers) {
			if (curses)
				quickCurses.add(prayer);
			else
				quickPrays.add(prayer);
		} else {
			active.add(prayer);
			if (isOverhead(prayer))
				player.getAppearance().generateAppearanceData();
			if (prayer.getActivateSound() != -1)
				player.soundEffect(prayer.getActivateSound());
			else
				player.soundEffect(2662);
		}
		refresh();
		return true;
	}

	public void closePrayer(Prayer prayer) {
		if (settingQuickPrayers) {
			if (curses)
				quickCurses.remove(prayer);
			else
				quickPrays.remove(prayer);
			return;
		}
		if (!active.contains(prayer))
			return;
		switch (prayer) {
		case LEECH_ATTACK:
			if (getStatMod(StatMod.ATTACK) > 0)
				player.sendMessage("Your Attack is now unaffected by sap and leech curses.", true);
			setStatMod(StatMod.ATTACK, 0);
			break;
		case LEECH_STRENGTH:
			if (getStatMod(StatMod.STRENGTH) > 0)
				player.sendMessage("Your Strength is now unaffected by sap and leech curses.", true);
			setStatMod(StatMod.STRENGTH, 0);
			break;
		case LEECH_DEFENSE:
			if (getStatMod(StatMod.DEFENSE) > 0)
				player.sendMessage("Your Defense is now unaffected by sap and leech curses.", true);
			setStatMod(StatMod.DEFENSE, 0);
			break;
		case LEECH_RANGE:
			if (getStatMod(StatMod.RANGE) > 0)
				player.sendMessage("Your Range is now unaffected by sap and leech curses.", true);
			setStatMod(StatMod.RANGE, 0);
			break;
		case LEECH_MAGIC:
			if (getStatMod(StatMod.MAGE) > 0)
				player.sendMessage("Your Magic is now unaffected by sap and leech curses.", true);
			setStatMod(StatMod.MAGE, 0);
			break;
		case TURMOIL:
			setStatMod(StatMod.ATTACK, 0);
			setStatMod(StatMod.STRENGTH, 0);
			setStatMod(StatMod.DEFENSE, 0);
			break;
		default:
			break;
		}
		active.remove(prayer);
		if (isOverhead(prayer))
			player.getAppearance().generateAppearanceData();
		player.soundEffect(2663);
		if (active.isEmpty())
			setQuickPrayersOn(false);
		refresh();
	}

	public void closePrayers(Prayer... prayers) {
		for (Prayer p : prayers)
			closePrayer(p);
	}

	public int getPrayerHeadIcon() {
		if (active.isEmpty())
			return -1;
		if (active.contains(Prayer.PROTECT_SUMMONING)) {
			if (active.contains(Prayer.PROTECT_MELEE))
				return 8;
			if (active.contains(Prayer.PROTECT_RANGE))
				return 9;
			if (active.contains(Prayer.PROTECT_MAGIC))
				return 10;
			return 7;
		}
		if (active.contains(Prayer.DEFLECT_SUMMONING)) {
			if (active.contains(Prayer.DEFLECT_MELEE))
				return 16;
			if (active.contains(Prayer.DEFLECT_RANGE))
				return 17;
			if (active.contains(Prayer.DEFLECT_MAGIC))
				return 18;
			return 15;
		}
		if (active.contains(Prayer.PROTECT_MELEE))
			return 0;
		if (active.contains(Prayer.PROTECT_RANGE))
			return 1;
		if (active.contains(Prayer.PROTECT_MAGIC))
			return 2;
		else if (active.contains(Prayer.RETRIBUTION))
			return 3;
		else if (active.contains(Prayer.SMITE))
			return 4;
		else if (active.contains(Prayer.REDEMPTION))
			return 5;
		else if (active.contains(Prayer.DEFLECT_MELEE))
			return 12;
		else if (active.contains(Prayer.DEFLECT_MAGIC))
			return 13;
		else if (active.contains(Prayer.DEFLECT_RANGE))
			return 14;
		else if (active.contains(Prayer.WRATH))
			return 19;
		else if (active.contains(Prayer.SOUL_SPLIT))
			return 20;
		return -1;
	}

	public void processPrayer() {
		if (player.isDead() || !player.isRunning() || active.isEmpty())
			return;
		double drain = 0;
		for (Prayer p : active)
			drain += p.getDrain();
		drain /= 1.0 + ((1.0/30.0) * player.getCombatDefinitions().getBonus(Bonus.PRAYER));
		if (drain > 0) {
			drainPrayer(drain);
			if (!checkPrayer()) {
				closeAllPrayers();
				player.soundEffect(2673);
			}
		}
		if ((player.getTickCounter() % 10) == 0 && active(Prayer.TURMOIL, Prayer.SAP_MAGE, Prayer.SAP_RANGE, Prayer.SAP_SPIRIT, Prayer.SAP_WARRIOR, Prayer.LEECH_ATTACK, Prayer.LEECH_DEFENSE, Prayer.LEECH_STRENGTH, Prayer.LEECH_MAGIC, Prayer.LEECH_RANGE, Prayer.LEECH_SPECIAL, Prayer.LEECH_ENERGY))
			if (player.getInteractionManager().getInteraction() instanceof PlayerCombatInteraction combat)
				if (active(Prayer.TURMOIL))
					processTurmoil(combat.getAction().getTarget());
				else
					processLeeches(combat.getAction().getTarget());
	}

	private void processTurmoil(Entity target) {
		if (target == null)
			return;
		if (player.getTempAttribs().getO("lastTurmTarget") != target) {
			setTurmoilBonus(target);
			player.getTempAttribs().setO("lastTurmTarget", target);
		}
	}

	private void processLeeches(Entity target) {
		if (target == null)
			return;
		for (Sap sap : Sap.values())
			if (active(sap.getPrayer()) && Utils.random(4) == 0) {
				sap.activate(player, target);
				player.setNextAnimation(new Animation(12569));
				player.setNextSpotAnim(new SpotAnim(sap.getSpotAnimStart()));
				World.sendProjectile(player, target, sap.getProjAnim(), 35, 35, 20, 0.6, 0, 0, p -> {
					if (target != null)
						target.setNextSpotAnim(new SpotAnim(sap.getSpotAnimHit()));
				});
			}
		for (Leech leech : Leech.values())
			if (active(leech.getPrayer()) && Utils.random(7) == 0) {
				leech.activate(player, target);
				player.setNextAnimation(new Animation(12575));
				World.sendProjectile(player, target, leech.getProjAnim(), 35, 35, 20, 0.6, 0, 0, p -> {
					if (target != null)
						target.setNextSpotAnim(new SpotAnim(leech.getSpotAnimHit()));
				});
			}
	}

	private void setTurmoilBonus(Entity e) {
		if (e instanceof Player p2) {
			setStatMod(StatMod.ATTACK, (int) Math.floor(((100 * Math.floor(0.15 * p2.getSkills().getLevelForXp(Constants.ATTACK))) / player.getSkills().getLevelForXp(Constants.ATTACK))));
			setStatMod(StatMod.STRENGTH, (int) Math.floor(((100 * Math.floor(0.1 * p2.getSkills().getLevelForXp(Constants.STRENGTH))) / player.getSkills().getLevelForXp(Constants.STRENGTH))));
			setStatMod(StatMod.DEFENSE, (int) Math.floor(((100 * Math.floor(0.15 * p2.getSkills().getLevelForXp(Constants.DEFENSE))) / player.getSkills().getLevelForXp(Constants.DEFENSE))));
		} else if (e instanceof NPC npc) {
			setStatMod(StatMod.ATTACK, (int) Math.floor(((100 * Math.floor(0.15 * Utils.clampI(npc.getAttackLevel(), 1, 99))) / player.getSkills().getLevelForXp(Constants.ATTACK))));
			setStatMod(StatMod.STRENGTH, (int) Math.floor(((100 * Math.floor(0.1 * Utils.clampI(npc.getStrengthLevel(), 1, 99))) / player.getSkills().getLevelForXp(Constants.STRENGTH))));
			setStatMod(StatMod.DEFENSE, (int) Math.floor(((100 * Math.floor(0.15 * Utils.clampI(npc.getDefenseLevel(), 1, 99))) / player.getSkills().getLevelForXp(Constants.DEFENSE))));
		}
		updateStatMods();
	}

	public void closeAllPrayers() {
		active.clear();
		statMods = new int[5];
		setQuickPrayersOn(false);
		player.getVars().setVar(curses ? 1582 : 1395, 0);
		player.getAppearance().generateAppearanceData();
		resetStatMods();
	}

	public void switchSettingQuickPrayer() {
		settingQuickPrayers = !settingQuickPrayers;
		refreshSettingQuickPrayers();
		unlockPrayerBookButtons();
		if (settingQuickPrayers)
			player.getInterfaceManager().openTab(Sub.TAB_PRAYER);
	}

	public void switchQuickPrayers() {
		if (!checkPrayer())
			return;
		boolean wasOn = quickPrayersOn;
		boolean turnedOn = false;
		closeAllPrayers();
		if (!wasOn) {
			if (curses) {
				for (Prayer curse : quickCurses)
					if (activatePrayer(curse))
						turnedOn = true;
			} else
				for (Prayer prayer : quickPrays)
					if (activatePrayer(prayer))
						turnedOn = true;
			setQuickPrayersOn(turnedOn);
		}
	}

	public void setQuickPrayersOn(boolean on) {
		quickPrayersOn = on;
		player.getPackets().sendVarc(182, quickPrayersOn ? 1 : 0);
	}

	public boolean checkPrayer() {
		if (points <= 0) {
			player.soundEffect(2672);
			player.sendMessage("Please recharge your prayer at the Lumbridge Church.");
			return false;
		}
		return true;
	}

	public void refresh() {
		for (Prayer p : Prayer.values()) {
			player.getVars().setVarBit(p.getVarBit(), active.contains(p) ? 1 : 0);
			player.getVars().setVarBit(p.getQPVarBit(), quickPrays.contains(p) || quickCurses.contains(p) ? 1 : 0);
		}
		player.getVars().setVar(1584, curses ? 1 : 0);
	}

	public void refreshSettingQuickPrayers() {
		player.getPackets().sendVarc(181, settingQuickPrayers ? 1 : 0);
	}

	public void init() {
		player.getVars().setVar(curses ? 1582 : 1395, 0);
		resetStatMods();
		refresh();
		refreshSettingQuickPrayers();
		unlockPrayerBookButtons();
	}

	public void unlockPrayerBookButtons() {
		player.getPackets().setIFRightClickOps(271, settingQuickPrayers ? 42 : 8, 0, 29, 0);
	}

	public void setPrayerBook(boolean curses) {
		if (curses && !player.isQuestComplete(Quest.TEMPLE_AT_SENNTISTEN, "to use ancient curses."))
			return;
		closeAllPrayers();
		this.curses = curses;
		player.getInterfaceManager().sendSubDefault(Sub.TAB_PRAYER);
		refresh();
		unlockPrayerBookButtons();
	}

	public PrayerManager() {
		quickPrays = new CopyOnWriteArraySet<>();
		quickCurses = new CopyOnWriteArraySet<>();
		points = 10.0;
	}

	public void setPlayer(Player player) {
		this.player = player;
		active = new CopyOnWriteArraySet<>();
		statMods = new int[5];
		if (quickPrays == null)
			quickPrays = new CopyOnWriteArraySet<>();
		if (quickCurses == null)
			quickCurses = new CopyOnWriteArraySet<>();
	}

	private void resetStatMods() {
		for (StatMod mod : StatMod.values())
			setStatMod(mod, 0);
	}

	private int getStatMod(StatMod mod) {
		return statMods[mod.ordinal()];
	}

	private void setStatMod(StatMod mod, int bonus) {
		statMods[mod.ordinal()] = bonus;
		updateStatMod(mod);
	}

	public boolean decreaseStatModifier(StatMod mod, int bonus, int max) {
		if (statMods[mod.ordinal()] > max) {
			statMods[mod.ordinal()]--;
			updateStatMod(mod);
			return true;
		}
		return false;
	}

	public boolean increaseStatModifier(StatMod mod, int bonus, int max) {
		if (statMods[mod.ordinal()] < max) {
			statMods[mod.ordinal()]++;
			updateStatMod(mod);
			return true;
		}
		return false;
	}

	private void updateStatMod(StatMod mod) {
		player.getVars().setVarBit(6857 + mod.ordinal(), 30 + statMods[mod.ordinal()]);
	}

	private void updateStatMods() {
		for (StatMod m : StatMod.values())
			updateStatMod(m);
	}

	public double getMageMultiplier() {
		if (active.isEmpty())
			return 1.0;
		double value = 1.0;

		if (active(Prayer.MAG_T1))
			value += 0.05;
		else if (active(Prayer.MAG_T2))
			value += 0.10;
		else if (active(Prayer.MAG_T3))
			value += 0.15;
		else if (active(Prayer.AUGURY))
			value += 0.20;
		else if (active(Prayer.LEECH_MAGIC)) {
			double d = (5 + getStatMod(StatMod.MAGE));
			value += d / 100;
		}
		return value;
	}

	public double getRangeMultiplier() {
		if (active.isEmpty())
			return 1.0;
		double value = 1.0;

		if (active(Prayer.RNG_T1))
			value += 0.05;
		else if (active(Prayer.RNG_T2))
			value += 0.10;
		else if (active(Prayer.RNG_T3))
			value += 0.15;
		else if (active(Prayer.RIGOUR))
			value += 0.20;
		else if (active(Prayer.LEECH_RANGE)) {
			double d = (5 + getStatMod(StatMod.RANGE));
			value += d / 100;
		}
		return value;
	}

	public double getAttackMultiplier() {
		if (active.isEmpty())
			return 1.0;
		double value = 1.0;

		if (active(Prayer.ATK_T1))
			value += 0.05;
		else if (active(Prayer.ATK_T2))
			value += 0.10;
		else if (active(Prayer.ATK_T3))
			value += 0.15;
		else if (active(Prayer.CHIVALRY))
			value += 0.15;
		else if (active(Prayer.PIETY))
			value += 0.20;
		else if (active(Prayer.LEECH_ATTACK)) {
			double d = (5 + getStatMod(StatMod.ATTACK));
			value += d / 100;
		} else if (active(Prayer.TURMOIL)) {
			double d = (15 + getStatMod(StatMod.ATTACK));
			value += d / 100;
		}
		return value;
	}

	public double getStrengthMultiplier() {
		if (active.isEmpty())
			return 1.0;
		double value = 1.0;

		if (active(Prayer.STR_T1))
			value += 0.05;
		else if (active(Prayer.STR_T2))
			value += 0.10;
		else if (active(Prayer.STR_T3))
			value += 0.15;
		else if (active(Prayer.CHIVALRY))
			value += 0.18;
		else if (active(Prayer.PIETY))
			value += 0.23;
		else if (active(Prayer.LEECH_STRENGTH)) {
			double d = (5 + getStatMod(StatMod.STRENGTH));
			value += d / 100;
		} else if (active(Prayer.TURMOIL)) {
			double d = (23 + getStatMod(StatMod.STRENGTH));
			value += d / 100;
		}
		return value;
	}

	public double getDefenceMultiplier() {
		if (active.isEmpty())
			return 1.0;
		double value = 1.0;

		if (active(Prayer.DEF_T1))
			value += 0.05;
		else if (active(Prayer.DEF_T2))
			value += 0.10;
		else if (active(Prayer.DEF_T3))
			value += 0.15;
		else if (active(Prayer.CHIVALRY))
			value += 0.20;
		else if (active(Prayer.PIETY) || active(Prayer.RIGOUR) || active(Prayer.AUGURY))
			value += 0.25;
		else if (active(Prayer.LEECH_DEFENSE)) {
			double d = (6 + getStatMod(StatMod.DEFENSE));
			value += d / 100;
		} else if (active(Prayer.TURMOIL)) {
			double d = (15 + getStatMod(StatMod.DEFENSE));
			value += d / 100;
		}
		return value;
	}

	public boolean isCurses() {
		return curses;
	}

	public double getPoints() {
		return points;
	}

	public void setPoints(double points) {
		this.points = points;
		refreshPoints();
	}

	public void refreshPoints() {
		player.getVars().setVar(2382, (int) points);
	}

	public boolean hasFullPoints() {
		return getPoints() >= player.getSkills().getLevelForXp(Constants.PRAYER) * 10;
	}

	public void drainPrayer(double amount) {
		if (player.getNSV().getB("infPrayer"))
			return;
		points -= amount;
		if (points <= 0)
			points = 0;
		refreshPoints();
	}

	public void drainPrayer() {
		if (player.getNSV().getB("infPrayer"))
			return;
		points = 0;
		refreshPoints();
	}

	public void restorePrayer(double amount) {
		int maxPrayer = player.getSkills().getLevelForXp(Constants.PRAYER) * 10;
		amount *= player.getAuraManager().getPrayerResMul();
		if ((points + amount) <= maxPrayer)
			points += amount;
		else
			points = maxPrayer;
		refreshPoints();
	}

	public boolean active(Prayer... prayers) {
		for (Prayer prayer : prayers)
			if (active.contains(prayer))
				return true;
		return false;
	}

	public static boolean isOverhead(Prayer p) {
		switch(p) {
		case PROTECT_MAGIC:
		case PROTECT_SUMMONING:
		case PROTECT_RANGE:
		case PROTECT_MELEE:
		case RETRIBUTION:
		case REDEMPTION:
		case SMITE:
		case DEFLECT_MELEE:
		case DEFLECT_SUMMONING:
		case DEFLECT_MAGIC:
		case DEFLECT_RANGE:
		case WRATH:
		case SOUL_SPLIT:
			return true;
		default:
			return false;
		}
	}

	public boolean isProtectingItem() {
		return active(Prayer.PROTECT_ITEM_C, Prayer.PROTECT_ITEM_N);
	}

	public boolean isProtectingMage() {
		return active(Prayer.PROTECT_MAGIC, Prayer.DEFLECT_MAGIC);
	}

	public boolean isProtectingRange() {
		return active(Prayer.PROTECT_RANGE, Prayer.DEFLECT_RANGE);
	}

	public boolean isProtectingMelee() {
		return active(Prayer.PROTECT_MELEE, Prayer.DEFLECT_MELEE);
	}

	public boolean hasProtectionPrayersOn() {
		return active(Prayer.PROTECT_MAGIC, Prayer.PROTECT_MELEE, Prayer.PROTECT_RANGE, Prayer.PROTECT_SUMMONING, Prayer.DEFLECT_MAGIC, Prayer.DEFLECT_MELEE, Prayer.DEFLECT_RANGE, Prayer.DEFLECT_SUMMONING);
	}

	public void reset() {
		closeAllPrayers();
		points = player.getSkills().getLevelForXp(Constants.PRAYER) * 10;
		refreshPoints();
	}

	public boolean isUsingProtectionPrayer() {
		return isProtectingMage() || isProtectingRange() || isProtectingMelee();
	}

	public boolean hasPrayersOn() {
		return !active.isEmpty();
	}
}
