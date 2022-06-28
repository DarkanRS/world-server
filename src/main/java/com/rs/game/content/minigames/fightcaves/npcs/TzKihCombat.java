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
package com.rs.game.content.minigames.fightcaves.npcs;

import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.npc.combat.CombatScript;
import com.rs.game.model.entity.npc.combat.NPCCombatDefinitions;
import com.rs.game.model.entity.npc.combat.NPCCombatDefinitions.AttackStyle;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Animation;

public class TzKihCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { "Tz-Kih" };
	}

	@Override
	public int attack(NPC npc, Entity target) {// yoa
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		int damage = 0;
		npc.setNextAnimation(new Animation(defs.getAttackEmote()));
		damage = getMaxHit(npc, defs.getMaxHit(), AttackStyle.MELEE, target);
		if (target instanceof Player player)
			player.getPrayer().drainPrayer(damage + 10);
		delayHit(npc, 0, target, getMeleeHit(npc, damage));
		return npc.getAttackSpeed();
	}
}
