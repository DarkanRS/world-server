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
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.CombatScript;
import com.rs.game.npc.combat.NPCCombatDefinitions;
import com.rs.game.npc.combat.NPCCombatDefinitions.AttackStyle;
import com.rs.game.player.actions.PlayerCombat;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.util.Utils;

public class LeatherDragonCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { "Green dragon", "Blue dragon", "Red dragon", "Black dragon", 742, 14548 };
	}

	@Override
	public int attack(final NPC npc, final Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		int distanceX = target.getX() - npc.getX();
		int distanceY = target.getY() - npc.getY();
		int size = npc.getSize();
		if (distanceX > size || distanceX < -1 || distanceY > size || distanceY < -1)
			return 0;
		if (Utils.getRandomInclusive(3) != 0) {
			npc.setNextAnimation(new Animation(defs.getAttackEmote()));
			delayHit(npc, 0, target, getMeleeHit(npc, getMaxHit(npc, defs.getMaxHit(), AttackStyle.MELEE, target)));
		} else {
			int damage = Utils.getRandomInclusive(500);
			npc.setNextAnimation(new Animation(12259));
			npc.setNextSpotAnim(new SpotAnim(1, 0, 100));
			int protection = PlayerCombat.getAntifireLevel(target, true);
			if (protection == 1)
				damage = Utils.getRandomInclusive(50);
			else if (protection == 2)
				damage = 0;
			delayHit(npc, 1, target, getRegularHit(npc, damage));
		}
		return npc.getAttackSpeed();
	}
}
