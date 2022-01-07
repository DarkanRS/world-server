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
package com.rs.game.npc.combat.impl.dung;

import com.rs.game.Entity;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.CombatScript;
import com.rs.game.npc.combat.NPCCombatDefinitions.AttackStyle;
import com.rs.game.npc.familiar.Familiar;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;

public class BloodragerCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 11106, 11108, 11110, 11112, 11114, 11116, 11118, 11120, 11122, 11124, 11126 };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		Familiar familiar = (Familiar) npc;
		boolean usingSpecial = familiar.hasSpecialOn();
		int tier = (npc.getId() - 11106) / 2;

		int damage = 0;
		if (usingSpecial) {
			npc.setNextSpotAnim(new SpotAnim(2444));
			damage = getMaxHit(npc, (int) (npc.getMaxHit(AttackStyle.MELEE) * (1.05 * tier)), AttackStyle.MELEE, target);
		} else
			damage = getMaxHit(npc, AttackStyle.MELEE, target);
		delayHit(npc, usingSpecial ? 1 : 0, target, getMeleeHit(npc, damage));
		npc.setNextAnimation(new Animation(13617));
		return npc.getAttackSpeed();
	}
}
