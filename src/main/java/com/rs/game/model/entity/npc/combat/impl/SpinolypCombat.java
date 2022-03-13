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
package com.rs.game.model.entity.npc.combat.impl;

import com.rs.game.World;
import com.rs.game.model.WorldProjectile;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.npc.combat.CombatScript;
import com.rs.game.model.entity.npc.combat.NPCCombatDefinitions;
import com.rs.game.model.entity.npc.combat.NPCCombatDefinitions.AttackStyle;
import com.rs.lib.game.Animation;
import com.rs.lib.util.Utils;

public class SpinolypCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { "Spinolyp" };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		switch (Utils.random(2)) {
		case 0:
			npc.setNextAnimation(new Animation(defs.getAttackEmote()));
			WorldProjectile projectile = World.sendProjectile(npc, target, 2705, 34, 16, 35, 2, 10, 0);
			delayHit(npc, projectile.getTaskDelay(), target, getMagicHit(npc, getMaxHit(npc, AttackStyle.RANGE, target)));
			break;
		case 1:
			npc.setNextAnimation(new Animation(defs.getAttackEmote()));
			projectile = World.sendProjectile(npc, target, 473, 34, 16, 35, 2, 10, 0);
			delayHit(npc, projectile.getTaskDelay(), target, getRangeHit(npc, getMaxHit(npc, AttackStyle.RANGE, target)));
			break;
		}
		if (Utils.random(10) == 0)
			target.getPoison().makePoisoned(68);
		return npc.getAttackSpeed();
	}
}
