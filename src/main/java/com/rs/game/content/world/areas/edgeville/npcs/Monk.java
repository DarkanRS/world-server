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
package com.rs.game.content.world.areas.edgeville.npcs;

import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.npc.combat.CombatScript;
import com.rs.game.model.entity.npc.combat.NPCCombatDefinitions;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.util.Utils;

public class Monk extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { "Monk" };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		NPCCombatDefinitions def = npc.getCombatDefinitions();
		if (Utils.random(100) < 70) {
			delayHit(npc, 0, target, getMeleeHit(npc, getMaxHit(npc, npc.getMaxHit(), def.getAttackStyle(), target)));
			npc.setNextAnimation(new Animation(def.getAttackEmote()));
		} else {
			npc.heal(20);
			npc.setNextSpotAnim(new SpotAnim(84));
		}
		return npc.getAttackSpeed();
	}
}
