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
import com.rs.game.content.combat.CombatStyle;
import com.rs.game.content.skills.summoning.Pouch;
import com.rs.game.content.skills.summoning.combat.FamiliarCombatScript;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.Hit;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import kotlin.Pair;

public class TzKih extends FamiliarCombatScript {

	@Override
	public Object[] getKeys() {
		return Pouch.SPIRIT_TZ_KIH.getIdKeys();
	}
	
	@Override
	public int alternateAttack(final NPC npc, final Entity target) {
		npc.sync(npc.getCombatDefinitions().getAttackEmote(), 1422);
		Hit hit = Hit.magic(npc, getMaxHit(npc, npc.getCombatDefinitions().getMaxHit(), CombatStyle.MAGIC, target));
		delayHit(npc, World.sendProjectile(npc, target, 1423, new Pair<>(34, 16), 30, 5, 16).getTaskDelay(), target, hit);
		if (hit.getDamage() > 0 && target instanceof Player player)
			player.getPrayer().drainPrayer(player.getPrayer().getPoints() * 0.02);
		return npc.getAttackSpeed();
	}
}
