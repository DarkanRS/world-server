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
package com.rs.game.npc.combat;

import com.rs.Settings;
import com.rs.cache.loaders.Bonus;
import com.rs.game.Entity;
import com.rs.game.Hit;
import com.rs.game.Hit.HitLook;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.NPCCombatDefinitions.AttackStyle;
import com.rs.game.npc.familiar.Steeltitan;
import com.rs.game.player.Player;
import com.rs.game.player.actions.PlayerCombat;
import com.rs.game.player.content.combat.AttackType;
import com.rs.game.player.content.combat.XPType;
import com.rs.lib.Constants;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.util.Utils;

public abstract class CombatScript {

	public abstract Object[] getKeys();

	public abstract int attack(NPC npc, Entity target);

	public static void delayHit(NPC npc, int delay, final Entity target, final int gfx, final Hit... hits) {
		npc.getCombat().addAttackedByDelay(target);
		for (Hit hit : hits) {
			if (npc.isDead() || npc.hasFinished() || target.isDead() || target.hasFinished())
				return;
			target.applyHit(hit, delay, () -> {
				npc.getCombat().doDefenceEmote(target);
				target.setNextSpotAnim(new SpotAnim(gfx));
				if (target instanceof Player player) {
					player.closeInterfaces();
					if (player.getCombatDefinitions().isAutoRetaliate() && !player.getActionManager().hasSkillWorking() && player.getInteractionManager().getInteraction() == null && !player.hasWalkSteps())
						player.getActionManager().setAction(new PlayerCombat(npc));
				} else {
					NPC n = (NPC) target;
					if (!n.isUnderCombat() || n.canBeAutoRetaliated())
						n.setTarget(npc);
				}
			});
		}
	}

	public static void delayHit(NPC npc, int delay, Entity target, Hit hit) {
		delayHit(npc, delay, target, hit, null);
	}

	public static void delayHit(NPC npc, int delay, Entity target, Hit hit, Runnable afterDelay) {
		npc.getCombat().addAttackedByDelay(target);
		if (npc.isDead() || npc.hasFinished() || target.isDead() || target.hasFinished())
			return;
		target.applyHit(hit, delay, () -> {
			if (afterDelay != null)
				afterDelay.run();
			npc.getCombat().doDefenceEmote(target);
			if (target instanceof Player player) {
				player.closeInterfaces();
				if (player.getCombatDefinitions().isAutoRetaliate() && !player.getActionManager().hasSkillWorking() && player.getInteractionManager().getInteraction() == null && !player.hasWalkSteps())
					player.getActionManager().setAction(new PlayerCombat(npc));
			} else {
				NPC n = (NPC) target;
				if (!n.isUnderCombat() || n.canBeAutoRetaliated())
					n.setTarget(npc);
			}
		});
	}

	public static Hit getRangeHit(NPC npc, int damage) {
		return new Hit(npc, damage, HitLook.RANGE_DAMAGE);
	}

	public static Hit getMagicHit(NPC npc, int damage) {
		return new Hit(npc, damage, HitLook.MAGIC_DAMAGE);
	}

	public static Hit getRegularHit(NPC npc, int damage) {
		return new Hit(npc, damage, HitLook.TRUE_DAMAGE);
	}

	public static Hit getMeleeHit(NPC npc, int damage) {
		return new Hit(npc, damage, HitLook.MELEE_DAMAGE);
	}

	public static int getMaxHit(NPC npc, AttackStyle attackType, Entity target) {
		return getMaxHit(npc, npc.getMaxHit(attackType), attackType, target);
	}

	public static int getMaxHit(NPC npc, int maxHit, AttackStyle attackStyle, Entity target) {
		return getMaxHit(npc, maxHit, attackStyle, target, 1.0D);
	}

	public static int getMaxHit(NPC npc, int maxHit, AttackStyle attackStyle, Entity target, double accuracyModifier) {
		return getMaxHit(npc, maxHit, null, attackStyle, target, accuracyModifier);
	}

	public static int getMaxHit(NPC npc, int maxHit, Bonus attackBonus, AttackStyle attackStyle, Entity target) {
		return getMaxHit(npc, maxHit, attackBonus, attackStyle, target, 1.0D);
	}

	public static int getMaxHit(NPC npc, int maxHit, Bonus attackBonus, AttackStyle attackStyle, Entity target, double accuracyModifier) {
		double atkLvl;
		double atkBonus;
		Bonus attType;
		if (attackStyle == AttackStyle.RANGE)
			atkLvl = npc.getRangeLevel() + 8;
		else if (attackStyle == AttackStyle.MAGE)
			atkLvl = npc.getMagicLevel() + 8;
		else
			atkLvl = npc.getAttackLevel() + 8;
		if (attackStyle == AttackStyle.RANGE) {
			atkBonus = npc.getBonus(Bonus.RANGE_ATT);
			attType = Bonus.RANGE_ATT;
		} else if (attackStyle == AttackStyle.MAGE) {
			atkBonus = npc.getBonus(Bonus.MAGIC_ATT);
			attType = Bonus.MAGIC_ATT;
		} else if (attackBonus == null) {
			if (npc.getCombatDefinitions().getAttackBonus() == null) {
				int highest = npc.getBonus(Bonus.STAB_ATT);
				attType = Bonus.STAB_ATT;
				if (npc.getBonus(Bonus.SLASH_ATT) > highest) {
					highest = npc.getBonus(Bonus.SLASH_ATT);
					attType = Bonus.SLASH_ATT;
				}
				if (npc.getBonus(Bonus.CRUSH_ATT) > highest) {
					highest = npc.getBonus(Bonus.CRUSH_ATT);
					attType = Bonus.CRUSH_ATT;
				}
				atkBonus = highest;
			} else {
				attType = npc.getCombatDefinitions().getAttackBonus();
				atkBonus = npc.getBonus(npc.getCombatDefinitions().getAttackBonus());
			}
		} else {
			attType = attackBonus;
			atkBonus = npc.getBonus(attackBonus);
		}

		double atk = Math.floor(atkLvl * (atkBonus + 64));
		atk *= accuracyModifier;

		double def;
		if (target instanceof Player player) {
			def = switch (attType) {
			case RANGE_ATT -> {
				double defLvl = Math.floor(player.getSkills().getLevel(Constants.DEFENSE) * player.getPrayer().getDefenceMultiplier());
				double defBonus = player.getCombatDefinitions().getBonus(Bonus.RANGE_DEF);
				defLvl += 8;
				yield Math.floor(defLvl * (defBonus + 64));
			}
			case MAGIC_ATT -> {
				double defLvl = Math.floor(player.getSkills().getLevel(Constants.DEFENSE) * player.getPrayer().getDefenceMultiplier());
				defLvl += player.getCombatDefinitions().getAttackStyle().getAttackType() == AttackType.LONG_RANGE || player.getCombatDefinitions().getAttackStyle().getXpType() == XPType.DEFENSIVE ? 3 : player.getCombatDefinitions().getAttackStyle().getXpType() == XPType.CONTROLLED ? 1 : 0;
				defLvl += 8;
				defLvl *= 0.3;
				double magLvl = Math.floor(player.getSkills().getLevel(Constants.MAGIC) * player.getPrayer().getMageMultiplier());
				magLvl *= 0.7;
				double totalDefLvl = defLvl+magLvl;
				double defBonus = player.getCombatDefinitions().getBonus(Bonus.MAGIC_DEF);
				yield Math.floor(totalDefLvl * (defBonus + 64));
			}
			case STAB_ATT, SLASH_ATT, CRUSH_ATT -> {
				double defLvl = Math.floor(player.getSkills().getLevel(Constants.DEFENSE) * player.getPrayer().getDefenceMultiplier());
				double defBonus = player.getCombatDefinitions().getBonus(attType == Bonus.CRUSH_ATT ? Bonus.CRUSH_DEF : attType == Bonus.STAB_ATT ? Bonus.STAB_DEF : Bonus.SLASH_DEF);
				defLvl += 8;
				yield Math.floor(defLvl * (defBonus + 64));
			}
			default -> player.getCombatDefinitions().getBonus(Bonus.STAB_ATT);
			};
			if (attackStyle == AttackStyle.MELEE)
				if (player.getFamiliar() instanceof Steeltitan)
					def *= 1.15;
		} else {
			NPC n = (NPC) target;
			def = switch (attType) {
			case RANGE_ATT -> {
				int defLvl = n.getDefenseLevel();
				int defBonus = n.getDefinitions().getRangeDef();
				defLvl += 8;
				yield Math.floor(defLvl * (defBonus + 64));
			}
			case MAGIC_ATT -> {
				double defLvl = n.getMagicLevel();
				double defBonus = n.getDefinitions().getMagicDef();
				defLvl += 8;
				yield Math.floor(defLvl * (defBonus + 64));
			}
			case STAB_ATT -> {
				double defLvl = n.getDefenseLevel();
				double defBonus = n.getDefinitions().getStabDef();
				defLvl += 8;
				yield Math.floor(defLvl * (defBonus + 64));
			}
			case SLASH_ATT -> {
				double defLvl = n.getDefenseLevel();
				double defBonus = n.getDefinitions().getSlashDef();
				defLvl += 8;
				yield Math.floor(defLvl * (defBonus + 64));
			}
			case CRUSH_ATT -> {
				double defLvl = n.getDefenseLevel();
				double defBonus = n.getDefinitions().getCrushDef();
				defLvl += 8;
				yield Math.floor(defLvl * (defBonus + 64));
			}
			default -> {
				double defLvl = n.getDefenseLevel();
				double defBonus = n.getDefinitions().getStabDef();
				defLvl += 8;
				yield Math.floor(defLvl * (defBonus + 64));
			}
			};
		}
		double prob = atk > def ? (1 - (def+2) / (2*(atk+1))) : (atk / (2*(def+1)));
		if (Settings.getConfig().isDebug() && target instanceof Player player)
			if (player.getNSV().getB("hitChance"))
				player.sendMessage("Your chance of being hit: " + Utils.formatDouble(prob*100.0) + "%");
		if (prob <= Math.random())
			return 0;
		return Utils.getRandomInclusive(maxHit);
	}
}
