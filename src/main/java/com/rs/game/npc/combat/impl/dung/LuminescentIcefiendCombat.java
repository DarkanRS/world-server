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
import com.rs.game.World;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.CombatScript;
import com.rs.game.npc.combat.NPCCombatDefinitions.AttackStyle;
import com.rs.game.npc.dungeoneering.LuminscentIcefiend;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.util.Utils;

public class LuminescentIcefiendCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { "Luminescent icefiend" };
	}

	@Override
	public int attack(final NPC npc, final Entity target) {
		final LuminscentIcefiend icefiend = (LuminscentIcefiend) npc;

		if (icefiend.isSpecialEnabled()) {
			npc.setNextAnimation(new Animation(13338));
			npc.setNextSpotAnim(new SpotAnim(2524));

			icefiend.commenceSpecial();
			return 20;
		}

		boolean magicAttack = Utils.random(2) == 0;

		if (magicAttack) {
			npc.setNextAnimation(new Animation(13352));
			World.sendProjectile(npc, target, 2529, 15, 16, 35, 35, 16, 0);
			delayHit(icefiend, 2, target, getMagicHit(npc, getMaxHit(npc, icefiend.getMaxHit(), AttackStyle.MAGE, target)));
		} else {
			npc.setNextAnimation(new Animation(13337));
			World.sendProjectile(npc, target, 2530, 30, 16, 35, 35, 0, 0);
			delayHit(icefiend, 2, target, getRangeHit(npc, getMaxHit(npc, (int) (icefiend.getMaxHit() * .90), AttackStyle.RANGE, target)));
			WorldTasks.schedule(new WorldTask() {

				@Override
				public void run() {
					target.setNextSpotAnim(new SpotAnim(2531));
				}
			}, 2);
		}
		return 4;
	}
}
