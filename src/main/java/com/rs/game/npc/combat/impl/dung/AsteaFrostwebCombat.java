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
package com.rs.game.npc.combat.impl.dung;

import java.util.List;

import com.rs.game.Entity;
import com.rs.game.Hit;
import com.rs.game.Hit.HitLook;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.CombatScript;
import com.rs.game.npc.combat.NPCCombatDefinitions;
import com.rs.game.npc.combat.NPCCombatDefinitions.AttackStyle;
import com.rs.game.npc.dungeoneering.AsteaFrostweb;
import com.rs.game.player.content.Effect;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.util.Utils;

public class AsteaFrostwebCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[]
				{ "Astea Frostweb" };
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
				delayHit(npc, 1, t, new Hit(npc, Utils.random((int) (npc.getMaxHit(AttackStyle.MAGE) * 0.5) + 1), HitLook.TRUE_DAMAGE));
			return npc.getAttackSpeed();
		}
		int attackStyle = Utils.random(2);
		if (attackStyle == 1)
			if (Utils.getDistance(npc.getX(), npc.getY(), target.getX(), target.getY()) > 1)
				attackStyle = 0; // set mage
			else { // melee
				npc.setNextAnimation(new Animation(defs.getAttackEmote()));
				delayHit(npc, 0, target, getMeleeHit(npc, getMaxHit(npc, AttackStyle.MELEE, target)));
				return npc.getAttackSpeed();
			}
		if (attackStyle == 0) { // mage
			npc.setNextAnimation(new Animation(defs.getAttackEmote()));
			List<Entity> possibleTargets = npc.getPossibleTargets();

			int d = getMaxHit(npc, AttackStyle.MAGE, target);
			delayHit(npc, 1, target, getMagicHit(npc, d));
			if (d != 0) {
				WorldTasks.schedule(new WorldTask() {
					@Override
					public void run() {
						if (target.hasEffect(Effect.FREEZE))
							target.setNextSpotAnim(new SpotAnim(1677, 0, 100));
						else {
							target.setNextSpotAnim(new SpotAnim(369));
							target.freeze(8);
						}
					}
				}, 1);
				for (final Entity t : possibleTargets)
					if (t != target && t.withinDistance(target, 2)) {
						int damage = getMaxHit(npc, AttackStyle.MAGE, t);
						delayHit(npc, 1, t, getMagicHit(npc, damage));
						if (damage != 0)
							WorldTasks.schedule(new WorldTask() {
								@Override
								public void run() {
									if (t.hasEffect(Effect.FREEZE))
										t.setNextSpotAnim(new SpotAnim(1677, 0, 100));
									else {
										t.setNextSpotAnim(new SpotAnim(369));
										t.freeze(8);
									}
								}
							}, 1);

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
