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
package com.rs.game.model.entity.npc.combat;

import com.rs.game.World;
import com.rs.game.model.WorldProjectile;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.npc.combat.NPCCombatDefinitions.AttackStyle;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;

public class Default extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { "Default" };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		NPCCombatDefinitions defs = npc.getCombatDefinitions();
		AttackStyle attackStyle = defs.getAttackStyle();
		if (attackStyle == AttackStyle.MELEE)
			delayHit(npc, 0, target, getMeleeHit(npc, getMaxHit(npc, npc.getMaxHit(), attackStyle, target)));
		else {
			int damage = getMaxHit(npc, npc.getMaxHit(), attackStyle, target);
			WorldProjectile p = World.sendProjectile(npc, target, defs.getAttackProjectile(), 32, 32, 50, 2, 2, 0);
			delayHit(npc, p.getTaskDelay(), target, attackStyle == AttackStyle.RANGE ? getRangeHit(npc, damage) : getMagicHit(npc, damage));
		}
		if (defs.getAttackGfx() != -1)
			npc.setNextSpotAnim(new SpotAnim(defs.getAttackGfx()));
		npc.setNextAnimation(new Animation(defs.getAttackEmote()));
		if (target instanceof Player p)
			npc.soundEffect(npc.getCombatDefinitions().getAttackSound());
		return npc.getAttackSpeed();
	}
}
