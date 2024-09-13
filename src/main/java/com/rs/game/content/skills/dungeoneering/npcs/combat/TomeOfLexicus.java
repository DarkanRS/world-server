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
package com.rs.game.content.skills.dungeoneering.npcs.combat;

import com.rs.game.World;
import com.rs.game.content.combat.CombatStyle;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.Hit;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.npc.combat.CombatScript;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import kotlin.Pair;

public class TomeOfLexicus extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { "Tome of Strength", "Tome of Ranging", "Tome of Magic" };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		int type = npc.getId() - 9856;
		switch (type) {
			case 0 -> {
				npc.setNextAnimation(new Animation(13479));
				delayHit(npc, 0, target, Hit.magic(npc, getMaxHitFromAttackStyleLevel(npc, CombatStyle.MAGE, target)));
			}
			case 1, 2 -> {
				boolean range_style = type == 1;
				npc.setNextAnimation(new Animation(13480));
				npc.setNextSpotAnim(new SpotAnim(range_style ? 2408 : 2424));
				World.sendProjectile(npc, target, range_style ? 2409 : 2425, new Pair<>(40, 40), 54, 5, 5);
				if (range_style)
					delayHit(npc, 1, target, Hit.range(npc, getMaxHitFromAttackStyleLevel(npc, CombatStyle.RANGE, target)));
				else
					delayHit(npc, 1, target, Hit.magic(npc, getMaxHitFromAttackStyleLevel(npc, CombatStyle.MAGE, target)));
				target.setNextSpotAnim(new SpotAnim(range_style ? 2410 : 2426, 75, 0));
			}
		}
		return 4;
	}
}
