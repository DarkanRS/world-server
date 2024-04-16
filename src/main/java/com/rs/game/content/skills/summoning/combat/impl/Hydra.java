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

import com.rs.game.World;
import com.rs.game.content.skills.summoning.Pouch;
import com.rs.game.content.skills.summoning.combat.FamiliarCombatScript;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.npc.combat.NPCCombatDefinitions.AttackStyle;
import com.rs.lib.util.Utils;
import kotlin.Pair;

public class Hydra extends FamiliarCombatScript {

	@Override
	public Object[] getKeys() {
		return Pouch.HYDRA.getIdKeys();
	}
	
	@Override
	public int alternateAttack(final NPC npc, final Entity target) {
		if (Utils.random(2) != 0)
			return CANCEL;
		npc.anim(npc.getCombatDefinitions().getAttackEmote());
		delayHit(npc, World.sendProjectile(npc, target, 1489, new Pair<>(34, 16), 30, 5, 16).getTaskDelay(), target, getMagicHit(npc, getMaxHit(npc, npc.getCombatDefinitions().getMaxHit(), AttackStyle.MAGE, target)));
		return npc.getAttackSpeed();
	}
}
