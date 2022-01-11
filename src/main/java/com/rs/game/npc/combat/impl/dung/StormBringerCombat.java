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

import com.rs.game.Entity;
import com.rs.game.World;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.CombatScript;
import com.rs.game.npc.combat.NPCCombatDefinitions.AttackStyle;
import com.rs.game.npc.familiar.Familiar;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.util.Utils;

public class StormBringerCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 11126, 11128, 11130, 11132, 11134, 11136, 11138, 11140, 11142, 11144, 11146 };
	}

	@Override
	public int attack(NPC npc, final Entity target) {
		Familiar familiar = (Familiar) npc;
		boolean usingSpecial = familiar.hasSpecialOn();
		int tier = (npc.getId() - 11126) / 2;

		int damage = 0;
		if (usingSpecial) {
			damage = getMaxHit(npc, (int) (npc.getMaxHit(AttackStyle.MAGE) * (1.05 * tier)), AttackStyle.MAGE, target);
			if (Utils.random(11 - tier) == 0)
				target.freeze(8); // Five seconds cannot move.
		} else
			damage = getMaxHit(npc, AttackStyle.MAGE, target);
		npc.setNextSpotAnim(new SpotAnim(2591));
		npc.setNextAnimation(new Animation(13620));
		World.sendProjectile(npc, target, 2592, 41, 16, 41, 35, 16, 0);// 2593
		delayHit(npc, 2, target, getRangeHit(npc, damage));
		if (damage > 0)
			WorldTasks.schedule(new WorldTask() {

				@Override
				public void run() {
					target.setNextSpotAnim(new SpotAnim(2593));
				}
			}, 2);
		return npc.getAttackSpeed();
	}
}
