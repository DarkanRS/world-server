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
package com.rs.game.npc.combat.impl;

import com.rs.game.Entity;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.CombatScript;
import com.rs.game.npc.combat.NPCCombatDefinitions.AttackStyle;
import com.rs.game.npc.familiar.Familiar;
import com.rs.game.player.Player;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.util.Utils;

public class LavaTitanCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 7342, 7341 };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		Familiar familiar = (Familiar) npc;
		boolean usingSpecial = familiar.hasSpecialOn();
		int damage = 0;
		if (usingSpecial) {// priority over regular attack
			npc.setNextAnimation(new Animation(7883));
			npc.setNextSpotAnim(new SpotAnim(1491));
			delayHit(npc, 1, target, getMeleeHit(npc, getMaxHit(npc, 140, AttackStyle.MELEE, target)));
			if (damage <= 4 && target instanceof Player player)
				player.getCombatDefinitions().drainSpec((player.getCombatDefinitions().getSpecialAttackPercentage() / 10));
		} else {
			damage = getMaxHit(npc, 140, AttackStyle.MELEE, target);
			npc.setNextAnimation(new Animation(7980));
			npc.setNextSpotAnim(new SpotAnim(1490));
			delayHit(npc, 1, target, getMeleeHit(npc, damage));
		}
		if (Utils.getRandomInclusive(10) == 0)// 1/10 chance of happening
			delayHit(npc, 1, target, getMeleeHit(npc, Utils.getRandomInclusive(50)));
		return npc.getAttackSpeed();
	}
}
