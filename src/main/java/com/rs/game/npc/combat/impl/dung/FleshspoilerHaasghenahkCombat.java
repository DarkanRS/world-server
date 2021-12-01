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
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.CombatScript;
import com.rs.game.npc.combat.NPCCombatDefinitions.AttackStyle;
import com.rs.game.npc.dungeoneering.FleshspoilerHaasghenahk;
import com.rs.game.player.Player;
import com.rs.lib.game.Animation;
import com.rs.lib.util.Utils;
import com.rs.utils.WorldUtil;

public class FleshspoilerHaasghenahkCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { "Flesh-Spoiler Haasghenahk" };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		final FleshspoilerHaasghenahk boss = (FleshspoilerHaasghenahk) npc;

		for (Entity t : npc.getPossibleTargets()) {
			if (WorldUtil.collides(t.getX(), t.getY(), t.getSize(), npc.getX(), npc.getY(), npc.getSize()))
				delayHit(npc, 0, t, getRegularHit(npc, getMaxHit(npc, AttackStyle.MELEE, t)));
		}
		if (boss.isSecondStage())
			return 0;
		boolean magicOnly = boss.canUseMagicOnly();
		if (magicOnly || Utils.random(5) == 0) {
			if (magicOnly) {
				if (target instanceof Player player) {
					if (player.getPrayer().isProtectingMage() && Utils.random(3) == 0)
						boss.setUseMagicOnly(false);
				}
			}
			npc.setNextAnimation(new Animation(14463));
			delayHit(npc, 1, target, getMagicHit(npc, getMaxHit(npc, AttackStyle.MAGE, target)));
		} else {
			npc.setNextAnimation(new Animation(13320));
			delayHit(npc, 0, target, getMeleeHit(npc, getMaxHit(npc, AttackStyle.MELEE, target)));
		}
		return 6;
	}
}
