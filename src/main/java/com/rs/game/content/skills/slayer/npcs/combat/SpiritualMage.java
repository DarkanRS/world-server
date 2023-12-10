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
package com.rs.game.content.skills.slayer.npcs.combat;

import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.npc.combat.CombatScript;
import com.rs.game.model.entity.npc.combat.NPCCombatDefinitions;
import com.rs.game.model.entity.npc.combat.NPCCombatDefinitions.AttackStyle;
import com.rs.game.tasks.Task;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;

public class SpiritualMage extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 6257, 6221, 6278 };
	}

	@Override
	public int attack(NPC npc, final Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		npc.setNextAnimation(new Animation(defs.getAttackEmote()));
		int damage = getMaxHit(npc, defs.getMaxHit(), AttackStyle.MAGE, target);
		npc.setNextSpotAnim(new SpotAnim(defs.getAttackGfx()));
		delayHit(npc, 2, target, getMagicHit(npc, damage));
		if (damage > 0)
			WorldTasks.schedule(new Task() {

				@Override
				public void run() {
					target.setNextSpotAnim(new SpotAnim(defs.getAttackProjectile()));
				}
			}, 2);
		return npc.getAttackSpeed() + 2;
	}

}
