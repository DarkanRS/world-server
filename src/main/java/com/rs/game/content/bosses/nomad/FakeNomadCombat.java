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
package com.rs.game.content.bosses.nomad;

import com.rs.game.World;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.npc.combat.CombatScript;
import com.rs.game.model.entity.npc.combat.NPCCombatDefinitions.AttackStyle;
import com.rs.game.tasks.Task;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;

public class FakeNomadCombat extends CombatScript {

	@Override
	public Object[] getKeys() {

		return new Object[] { 8529 };
	}

	@Override
	public int attack(final NPC npc, final Entity target) {
		npc.setNextAnimation(new Animation(12697));
		boolean hit = getMaxHit(npc, 50, AttackStyle.MAGE, target) != 0;
		delayHit(npc, 2, target, getRegularHit(npc, hit ? 50 : 0));
		World.sendProjectile(npc, target, 1657, 30, 30, 75, 25, 0, 0);
		if (hit)
			WorldTasks.schedule(new Task() {
				@Override
				public void run() {
					target.setNextSpotAnim(new SpotAnim(2278, 0, 100));
				}
			}, 1);
		return npc.getAttackSpeed();
	}

}
