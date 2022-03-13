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
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.npc.combat.CombatScript;
import com.rs.game.model.entity.npc.combat.NPCCombatDefinitions;
import com.rs.lib.game.Animation;
import com.rs.utils.WorldUtil;

public class DarkBeastCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 2783 };
	}

	@Override
	public int attack(NPC npc, final Entity target) {
		NPCCombatDefinitions def = npc.getCombatDefinitions();
		npc.setNextAnimation(new Animation(2731));
		if (WorldUtil.isInRange(target, npc, 3))
			delayHit(npc, 0, target, getMeleeHit(npc, getMaxHit(npc, 170, def.getAttackStyle(), target)));
		else {
			final int damage = getMaxHit(npc, 90, def.getAttackStyle(), target);
			World.sendProjectile(npc, target, 2181, 41, 16, 41, 35, 16, 0);
			delayHit(npc, 2, target, getMagicHit(npc, damage));
		}
		return npc.getAttackSpeed();
	}
}
