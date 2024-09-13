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

import com.rs.Settings;
import com.rs.cache.loaders.Bonus;
import com.rs.game.content.combat.AttackType;
import com.rs.game.content.combat.CombatFormulaKt;
import com.rs.game.content.combat.CombatStyle;
import com.rs.game.content.combat.XPType;
import com.rs.game.content.skills.summoning.Pouch;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.Hit;
import com.rs.game.model.entity.Hit.HitLook;
import com.rs.game.model.entity.interactions.PlayerCombatInteraction;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.Constants;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.util.Utils;

import javax.annotation.Nonnull;

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
					if (!player.isLocked() && player.getCombatDefinitions().isAutoRetaliate() && !player.getActionManager().hasSkillWorking() && player.getInteractionManager().getInteraction() == null && !player.hasWalkSteps())
						player.getInteractionManager().setInteraction(new PlayerCombatInteraction(player, npc));
				} else {
					NPC n = (NPC) target;
					if (!n.isUnderCombat() || n.canBeAutoRetaliated())
						n.setCombatTarget(npc);
				}
			});
		}
	}

	public static Hit delayHit(NPC npc, int delay, Entity target, Hit hit) {
		return delayHit(npc, delay, target, hit, null);
	}

	public static Hit delayHit(NPC npc, int delay, Entity target, Hit hit, Runnable afterDelay) {
		npc.getCombat().addAttackedByDelay(target);
		if (npc.isDead() || npc.hasFinished() || target.isDead() || target.hasFinished()) {
			hit.setDamage(0);
			return hit;
		}
		target.applyHit(hit, delay, () -> {
			if (afterDelay != null)
				afterDelay.run();
			npc.getCombat().doDefenceEmote(target);
			if (target instanceof Player player) {
				player.closeInterfaces();
				if (!player.isLocked() && player.getCombatDefinitions().isAutoRetaliate() && !player.getActionManager().hasSkillWorking() && player.getInteractionManager().getInteraction() == null && !player.hasWalkSteps())
					player.getInteractionManager().setInteraction(new PlayerCombatInteraction(player, npc));
			} else {
				NPC n = (NPC) target;
				if (!n.isUnderCombat() || n.canBeAutoRetaliated())
					n.setCombatTarget(npc);
			}
		});
		return hit;
	}

	public static int getMaxHitFromAttackStyleLevel(NPC npc, CombatStyle attackType, Entity target) {
		return getMaxHit(npc, npc.getLevelForStyle(attackType), attackType, target);
	}

	public static int getMaxHit(NPC npc, CombatStyle attackType, Entity target) {
		return getMaxHit(npc, npc.getMaxHit(), attackType, target);
	}

	public static int getMaxHit(NPC npc, int maxHit, CombatStyle attackStyle, Entity target) {
		return getMaxHit(npc, maxHit, attackStyle, target, 1.0D);
	}

	public static int getMaxHit(NPC npc, int maxHit, CombatStyle attackStyle, Entity target, double accuracyModifier) {
		return getMaxHit(npc, maxHit, npc.getCombatDefinitions().getAttackBonus(), attackStyle, target, accuracyModifier);
	}

	public static int getMaxHit(NPC npc, int maxHit, Bonus attackBonus, CombatStyle attackStyle, Entity target) {
		return getMaxHit(npc, maxHit, attackBonus, attackStyle, target, 1.0D);
	}

	public static int getMaxHit(NPC npc, int maxHit, @Nonnull Bonus attackBonus, CombatStyle attackStyle, Entity target, double accuracyModifier) {
		return CombatFormulaKt.calculateHit(npc, target, 1, maxHit, attackBonus, attackStyle, true, accuracyModifier).getDamage();
	}
}
