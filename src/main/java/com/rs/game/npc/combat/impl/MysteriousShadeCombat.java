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
package com.rs.game.npc.combat.impl;

import com.rs.game.Entity;
import com.rs.game.World;
import com.rs.game.WorldProjectile;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.CombatScript;
import com.rs.game.npc.combat.NPCCombatDefinitions.AttackStyle;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.util.Utils;

public class MysteriousShadeCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { "Mysterious shade" };
	}

	@Override
	public int attack(NPC npc, final Entity target) {
		final boolean rangeAttack = Utils.random(1) == 0;

		npc.setNextAnimation(new Animation(rangeAttack ? 13396 : 13398));
		npc.setNextSpotAnim(new SpotAnim(rangeAttack ? 2514 : 2515));
		WorldProjectile projectile = World.sendProjectile(npc, target, rangeAttack ? 2510 : 2511, 18, 18, 25, 3, 0, 0);
		if (rangeAttack)
			delayHit(npc, Utils.projectileTimeToCycles(projectile.getEndTime()) - 1, target, getRangeHit(npc, getMaxHit(npc, AttackStyle.RANGE, target)), () -> {
				target.setNextSpotAnim(new SpotAnim(2512));
			});
		else
			delayHit(npc, Utils.projectileTimeToCycles(projectile.getEndTime()) - 1, target, getMagicHit(npc, getMaxHit(npc, AttackStyle.MAGE, target)), () -> {
				target.setNextSpotAnim(new SpotAnim(2513));
			});
		target.setNextSpotAnim(new SpotAnim(rangeAttack ? 2512 : 2513, projectile.getEndTime(), 0));
		return npc.getAttackSpeed();
	}
}
