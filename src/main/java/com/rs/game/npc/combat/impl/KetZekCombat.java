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
import com.rs.game.World;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.CombatScript;
import com.rs.game.npc.combat.NPCCombatDefinitions;
import com.rs.game.npc.combat.NPCCombatDefinitions.AttackStyle;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.util.Utils;

public class KetZekCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { "Ket-Zek", 15207 };
	}// anims: DeathEmote: 9257 DefEmote: 9253 AttackAnim: 9252 gfxs: healing:
	// 444 - healer

	@Override
	public int attack(final NPC npc, final Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		int distanceX = target.getX() - npc.getX();
		int distanceY = target.getY() - npc.getY();
		int size = npc.getSize();
		int hit = 0;
		if (distanceX > size || distanceX < -1 || distanceY > size || distanceY < -1) {
			commenceMagicAttack(npc, target, hit);
			return npc.getAttackSpeed();
		}
		int attackStyle = Utils.getRandomInclusive(1);
		switch (attackStyle) {
		case 0:
			hit = getMaxHit(npc, defs.getMaxHit(), AttackStyle.MELEE, target);
			npc.setNextAnimation(new Animation(defs.getAttackEmote()));
			delayHit(npc, 0, target, getMeleeHit(npc, hit));
			break;
		case 1:
			commenceMagicAttack(npc, target, hit);
			break;
		}
		return npc.getAttackSpeed();
	}

	private void commenceMagicAttack(final NPC npc, final Entity target, int hit) {
		hit = getMaxHit(npc, npc.getCombatDefinitions().getMaxHit() - 50, AttackStyle.MAGE, target);
		npc.setNextAnimation(new Animation(16136));
		// npc.setNextGraphics(new Graphics(1622, 0, 96 << 16));
		World.sendProjectile(npc, target, 2984, 34, 16, 30, 2, 16, 0);
		delayHit(npc, 2, target, getMagicHit(npc, hit));
		WorldTasks.schedule(new WorldTask() {

			@Override
			public void run() {
				target.setNextSpotAnim(new SpotAnim(2983, 0, 96 << 16));
			}
		}, 2);
	}
}
