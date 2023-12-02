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
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.npc.combat.CombatScript;
import com.rs.game.model.entity.npc.combat.NPCCombatDefinitions.AttackStyle;
import com.rs.game.tasks.Task;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.util.Utils;
import com.rs.utils.WorldUtil;

public class AnimatedBookCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { "Animated book" };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		boolean meleeAttack = Utils.random(2) == 0;
		if (!meleeAttack || !WorldUtil.isInRange(npc.getX(), npc.getY(), npc.getSize(), target.getX(), target.getY(), target.getSize(), 0)) {
			magicAttack(npc, target);
			return npc.getAttackSpeed();
		}
		meleeAttack(npc, target);
		return npc.getAttackSpeed();
	}

	private void meleeAttack(NPC npc, Entity target) {
		npc.setNextAnimation(new Animation(13479));
		delayHit(npc, 0, target, getMeleeHit(npc, getMaxHit(npc, 100, AttackStyle.MELEE, target)));
	}

	private void magicAttack(NPC npc, final Entity target) {
		npc.setNextAnimation(new Animation(13480));
		npc.setNextSpotAnim(new SpotAnim(2728));
		delayHit(npc, 1, target, getMagicHit(npc, getMaxHit(npc, 100, AttackStyle.MAGE, target)));
		World.sendProjectile(npc, target, 2731, 34, 16, 30, 35, 16, 0);
		WorldTasks.schedule(new Task() {

			@Override
			public void run() {
				target.setNextSpotAnim(new SpotAnim(2738, 0, 80));
			}
		}, 2);
	}
}
