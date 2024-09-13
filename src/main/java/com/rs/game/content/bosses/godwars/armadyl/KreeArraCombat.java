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
package com.rs.game.content.bosses.godwars.armadyl;

import com.rs.game.World;
import com.rs.game.content.combat.CombatStyle;
import com.rs.game.model.WorldProjectile;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.Hit;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.npc.combat.CombatScript;
import com.rs.engine.pathfinder.Direction;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.util.Utils;
import com.rs.utils.WorldUtil;
import kotlin.Pair;

public class KreeArraCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 6222 };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		if (!npc.isUnderCombat()) {
			npc.setNextAnimation(new Animation(6997));
			delayHit(npc, 1, target, Hit.melee(npc, getMaxHit(npc, 260, CombatStyle.MELEE, target)));
			return npc.getAttackSpeed();
		}
		npc.setNextAnimation(new Animation(6976));
		for (Entity t : npc.getPossibleTargets())
			if (Utils.getRandomInclusive(2) == 0) {
				WorldProjectile p = World.sendProjectile(npc, t, 1198, new Pair<>(60, 32), 50, 5, 0);
				npc.setNextAnimation(new Animation(6976));
				delayHit(npc, p.getTaskDelay(), t, Hit.magic(npc, getMaxHit(npc, 210, CombatStyle.MAGE, t)));
				t.setNextSpotAnim(new SpotAnim(1196, p.getTaskDelay()));
			} else {
				WorldProjectile p = World.sendProjectile(npc, t, 1197, new Pair<>(60, 32), 50, 5, 0);
				delayHit(npc, p.getTaskDelay(), t, Hit.range(npc, getMaxHit(npc, 720, CombatStyle.RANGE, t)));
				WorldTasks.schedule(p.getTaskDelay(), () -> {
					Direction dir = WorldUtil.getDirectionTo(npc, target);
					if (dir != null)
						if (World.checkWalkStep(target.getTile(), dir, target.getSize())) {
							target.resetWalkSteps();
							target.tele(target.transform(dir.dx, dir.dy));
						}
				});
			}
		return npc.getAttackSpeed();
	}
}
