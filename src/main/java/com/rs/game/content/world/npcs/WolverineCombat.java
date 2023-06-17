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
package com.rs.game.content.world.npcs;

import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.npc.combat.CombatScript;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Animation;

import java.util.Random;

public class WolverineCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 14899 };
	}

	@Override
	public int attack(final NPC npc, final Entity target) {
		final Player player = (Player) target;
		npc.setNextAnimation(new Animation(10961));
		int damage = player.getSkills().getCombatLevel() / 3 + new Random().nextInt(20) + 40;
		int dclaw1 = damage / 2;
		int dclaw2 = damage / 3;
		int dclaw3 = damage / 3;
		delayHit(npc, 2, target, getMeleeHit(npc, damage));
		delayHit(npc, 2, target, getMeleeHit(npc, dclaw1));
		delayHit(npc, 2, target, getMeleeHit(npc, dclaw2 / 10));
		delayHit(npc, 2, target, getMeleeHit(npc, dclaw3 / 10));
		return npc.getAttackSpeed();
	}
}
