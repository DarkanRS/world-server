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

import com.rs.game.World;
import com.rs.game.model.WorldProjectile;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.npc.combat.CombatScript;
import com.rs.game.model.entity.npc.combat.NPCCombatDefinitions;
import com.rs.game.model.entity.npc.combat.NPCCombatDefinitions.AttackStyle;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.util.Utils;

public class JadCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 2745, 15208 };
	}

	@Override
	public int attack(final NPC npc, final Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		int attackStyle = Utils.random(3);
		if (attackStyle == 2) {
			if (npc.inMeleeRange(target)) {
				npc.setNextAnimation(new Animation(defs.getAttackEmote()));
				delayHit(npc, 1, target, getMeleeHit(npc, getMaxHit(npc, defs.getMaxHit(), AttackStyle.MELEE, target)));
				return npc.getAttackSpeed();
			}
			attackStyle = Utils.random(2);
		}
		if (attackStyle == 1) { // range
			npc.setNextAnimation(new Animation(16202));
			npc.setNextSpotAnim(new SpotAnim(2994));
			WorldTasks.schedule(new WorldTask() {
				@Override
				public void run() {
					WorldTasks.schedule(new WorldTask() {
						@Override
						public void run() {
							target.setNextSpotAnim(new SpotAnim(3000));
						}
					}, 0);
					delayHit(npc, 2, target, getRangeHit(npc, getMaxHit(npc, defs.getMaxHit() - 2, AttackStyle.RANGE, target)));
				}
			}, 2);
		} else {
			npc.setNextAnimation(new Animation(16195));
			npc.setNextSpotAnim(new SpotAnim(2995));
			WorldTasks.schedule(new WorldTask() {
				@Override
				public void run() {
					WorldProjectile p = World.sendProjectile(npc, target, 2996, 80, 30, 40, 5, 5, 0);
					target.setNextSpotAnim(new SpotAnim(2741, 0, 100));
					delayHit(npc, p.getTaskDelay(), target, getMagicHit(npc, getMaxHit(npc, defs.getMaxHit() - 2, AttackStyle.MAGE, target)));
				}
			}, 2);
		}

		return 8;
	}

}
