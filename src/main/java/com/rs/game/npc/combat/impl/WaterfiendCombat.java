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
import com.rs.game.Hit;
import com.rs.game.World;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.CombatScript;
import com.rs.game.npc.combat.NPCCombatDefinitions;
import com.rs.game.npc.combat.NPCCombatDefinitions.AttackStyle;
import com.rs.lib.game.Animation;
import com.rs.lib.util.Utils;

public class WaterfiendCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 5361 };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		int spellType = Utils.getRandomInclusive(2);
		Hit hit;
		if (spellType > 1)
			hit = getMagicHit(npc, getMaxHit(npc, defs.getMaxHit(), AttackStyle.MAGE, target));
		else
			hit = getRangeHit(npc, getMaxHit(npc, defs.getMaxHit(), AttackStyle.RANGE, target));

		npc.setNextAnimation(new Animation(defs.getAttackEmote()));
		if (spellType == 0) {
			World.sendProjectile(npc, target, 11, 20, 20, 25, 30, 25, 0);
			delayHit(npc, 2, target, hit);
		} else if (spellType == 1) {
			World.sendProjectile(npc, target, 11, 20, 20, 25, 30, 25, 0);
			delayHit(npc, 2, target, hit);
		} else if (spellType == 2) {
			World.sendProjectile(npc, target, 2704, 20, 20, 25, 30, 25, 0);
			delayHit(npc, 2, target, hit);
		} else {
			World.sendProjectile(npc, target, 2704, 20, 20, 25, 30, 25, 0);
			delayHit(npc, 2, target, hit);
		}

		return npc.getAttackSpeed();
	}
}
