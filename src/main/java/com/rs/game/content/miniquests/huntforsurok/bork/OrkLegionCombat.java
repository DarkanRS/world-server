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
package com.rs.game.content.miniquests.huntforsurok.bork;

import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.ForceTalk;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.npc.combat.CombatScript;
import com.rs.game.model.entity.npc.combat.NPCCombatDefinitions;
import com.rs.lib.game.Animation;
import com.rs.lib.util.Utils;

public class OrkLegionCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { "Ork legion" };
	}

	public String[] messages = { "For Bork!", "Die Human!", "To the attack!", "All together now!" };

	@Override
	public int attack(NPC npc, Entity target) {
		final NPCCombatDefinitions cdef = npc.getCombatDefinitions();
		npc.setNextAnimation(new Animation(cdef.getAttackEmote()));
		if (Utils.getRandomInclusive(3) == 0)
			npc.setNextForceTalk(new ForceTalk(messages[Utils.getRandomInclusive(messages.length > 3 ? 3 : 0)]));
		delayHit(npc, 0, target, getMeleeHit(npc, cdef.getMaxHit()));
		return npc.getAttackSpeed();
	}

}
