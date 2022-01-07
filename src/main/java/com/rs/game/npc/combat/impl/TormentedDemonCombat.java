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
import com.rs.game.npc.combat.NPCCombatDefinitions.AttackStyle;
import com.rs.game.npc.others.TormentedDemon;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;

public class TormentedDemonCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { "Tormented demon" };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		TormentedDemon torm = null;
		if (npc instanceof TormentedDemon td)
			torm = td;
		if (torm == null)
			return 0;
		int hit = 0;
		int attackStyle = torm.getCombatStyle();
		if (torm.getCombatStyleTimer() >= 23)
			return 0;
		switch (attackStyle) {
		case 0:
			if (!npc.inMeleeRange(target) || !npc.lineOfSightTo(target, true)) {
				npc.calcFollow(target, false);
				return 0;
			}
			hit = getMaxHit(npc, 189, AttackStyle.MELEE, target);
			npc.setNextAnimation(new Animation(10922));
			npc.setNextSpotAnim(new SpotAnim(1886));
			delayHit(npc, 1, target, getMeleeHit(npc, hit));
			return 7;
		case 1:
			hit = getMaxHit(npc, 270, AttackStyle.MAGE, target);
			npc.setNextAnimation(new Animation(10918));
			npc.setNextSpotAnim(new SpotAnim(1883, 0, 96 << 16));
			delayHit(npc, World.sendProjectile(npc, target, 1884, 34, 16, 30, 35, 16, 0).getTaskDelay(), target, getMagicHit(npc, hit));
			break;
		case 2:
			hit = getMaxHit(npc, 270, AttackStyle.RANGE, target);
			npc.setNextAnimation(new Animation(10919));
			npc.setNextSpotAnim(new SpotAnim(1888));
			delayHit(npc, World.sendProjectile(npc, target, 1887, 34, 16, 30, 35, 16, 0).getTaskDelay(), target, getRangeHit(npc, hit));
			break;
		}
		return 7;
	}
}
