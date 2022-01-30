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
package com.rs.game.npc.combat.impl;

import com.rs.game.Entity;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.CombatScript;
import com.rs.game.npc.combat.NPCCombatDefinitions;
import com.rs.game.npc.combat.NPCCombatDefinitions.AttackStyle;
import com.rs.game.player.Player;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.util.Utils;
import com.rs.utils.Ticks;
import com.rs.utils.WorldUtil;

public class SkeletalWyvernCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 3068, 3069, 3070, 3071 };
	}

	private static boolean hasShield(Entity target) {
		if (!(target instanceof Player))
			return true;
		int shieldId = ((Player) target).getEquipment().getShieldId();
		return shieldId == 2890 || shieldId == 9731 || shieldId == 20436 || shieldId == 20438 || shieldId == 18691 || (shieldId >= 11283 && shieldId <= 12285);
	}

	@Override
	public int attack(NPC npc, Entity target) {
		NPCCombatDefinitions defs = npc.getCombatDefinitions();
		int attackStyle = Utils.random(WorldUtil.isInRange(npc.getX(), npc.getY(), npc.getSize(), target.getX(), target.getY(), target.getSize(), 0) ? 3 : 2);
		switch (attackStyle) {
		case 0:
			npc.setNextAnimation(new Animation(1592));
			npc.setNextSpotAnim(new SpotAnim(501));
			target.setNextSpotAnim(new SpotAnim(502));
			if (Utils.random(10) == 0)
				target.freeze(Ticks.fromSeconds(5));
			delayHit(npc, 1, target, getRegularHit(npc, Utils.getRandomInclusive(hasShield(target) ? 150 : 600)));
			break;
		case 1:
			npc.setNextAnimation(new Animation(1593));
			npc.setNextSpotAnim(new SpotAnim(499));
			delayHit(npc, 1, target, getRangeHit(npc, getMaxHit(npc, defs.getMaxHit(), AttackStyle.RANGE, target)));
			break;
		case 2:
			npc.setNextAnimation(new Animation(1589 + Utils.random(2)));
			delayHit(npc, 0, target, getMeleeHit(npc, getMaxHit(npc, defs.getMaxHit(), AttackStyle.MELEE, target)));
			break;
		}
		return npc.getAttackSpeed();
	}

}
