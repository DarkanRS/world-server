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
package com.rs.game.content.skills.dungeoneering.npcs.bosses.asteafrostweb;

import com.rs.game.content.Effect;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.Hit;
import com.rs.game.model.entity.Hit.HitLook;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.npc.combat.CombatScript;
import com.rs.game.model.entity.npc.combat.NPCCombatDefinitions;
import com.rs.game.model.entity.npc.combat.NPCCombatDefinitions.AttackStyle;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.util.Utils;

import java.util.List;

public class AsteaFrostwebCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { "Astea Frostweb" };
	}

	@Override
	public int attack(final NPC npc, final Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		if (Utils.getRandomInclusive(10) == 0) {
			AsteaFrostweb boss = (AsteaFrostweb) npc;
			boss.spawnSpider();
		}
		if (Utils.getRandomInclusive(10) == 0) { // spikes
			List<Entity> possibleTargets = npc.getPossibleTargets();
			npc.setNextAnimation(new Animation(defs.getAttackEmote()));
			for (Entity t : possibleTargets)
				delayHit(npc, 1, t, new Hit(npc, Utils.random((int) (npc.getLevelForStyle(AttackStyle.MAGE) * 0.5) + 1), HitLook.TRUE_DAMAGE));
			return npc.getAttackSpeed();
		}
		int attackStyle = Utils.random(2);
		if (attackStyle == 1) {
			if (Utils.getDistance(npc.getX(), npc.getY(), target.getX(), target.getY()) <= 1) { // melee
				npc.setNextAnimation(new Animation(defs.getAttackEmote()));
				delayHit(npc, 0, target, getMeleeHit(npc, getMaxHitFromAttackStyleLevel(npc, AttackStyle.MELEE, target)));
				return npc.getAttackSpeed();
			}
			attackStyle = 0;
		}
		if (attackStyle == 0) { // mage
			npc.setNextAnimation(new Animation(defs.getAttackEmote()));
			List<Entity> possibleTargets = npc.getPossibleTargets();

			int d = getMaxHitFromAttackStyleLevel(npc, AttackStyle.MAGE, target);
			delayHit(npc, 1, target, getMagicHit(npc, d));
			if (d != 0) {
				WorldTasks.delay(1, () -> {
					if (target.hasEffect(Effect.FREEZE))
						target.setNextSpotAnim(new SpotAnim(1677, 0, 100));
					else {
						target.setNextSpotAnim(new SpotAnim(369));
						target.freeze(8);
					}
				});
				for (final Entity t : possibleTargets)
					if (t != target && t.withinDistance(target.getTile(), 2)) {
						int damage = getMaxHitFromAttackStyleLevel(npc, AttackStyle.MAGE, t);
						delayHit(npc, 1, t, getMagicHit(npc, damage));
						if (damage != 0)
							WorldTasks.delay(1, () -> {
								if (t.hasEffect(Effect.FREEZE))
									t.setNextSpotAnim(new SpotAnim(1677, 0, 100));
								else {
									t.setNextSpotAnim(new SpotAnim(369));
									t.freeze(8);
								}
							});

					}
			}
			if (Utils.getDistance(npc.getX(), npc.getY(), target.getX(), target.getY()) <= 1) { // lure
				// after
				// freeze
				npc.resetWalkSteps();
				npc.addWalkSteps(target.getX() + Utils.random(3), target.getY() + Utils.random(3));
			}
		}
		return npc.getAttackSpeed();
	}
}
