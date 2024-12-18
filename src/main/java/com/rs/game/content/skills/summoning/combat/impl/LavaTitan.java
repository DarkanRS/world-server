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
package com.rs.game.content.skills.summoning.combat.impl;

import com.rs.game.content.combat.CombatStyle;
import com.rs.game.content.skills.summoning.Pouch;
import com.rs.game.content.skills.summoning.combat.FamiliarCombatScript;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.Hit;
import com.rs.game.model.entity.npc.NPC;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.util.Utils;

public class LavaTitan extends FamiliarCombatScript {

	@Override
	public Object[] getKeys() {
		return Pouch.LAVA_TITAN.getIdKeys();
	}
	
	@Override
	public int alternateAttack(final NPC npc, final Entity target) {
		npc.setNextAnimation(new Animation(7980));
		npc.setNextSpotAnim(new SpotAnim(1490));
		delayHit(npc, 1, target, Hit.melee(npc, getMaxHit(npc, 140, CombatStyle.MELEE, target)));
		if (Utils.getRandomInclusive(10) == 0) // 1/10 chance of happening
			delayHit(npc, 1, target, Hit.melee(npc, Utils.getRandomInclusive(50)));
		return npc.getAttackSpeed();
	}
}
