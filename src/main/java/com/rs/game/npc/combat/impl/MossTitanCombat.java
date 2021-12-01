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
import com.rs.game.npc.familiar.Familiar;
import com.rs.game.player.controllers.WildernessController;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.util.Utils;

public class MossTitanCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 7330, 7329 };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		Familiar familiar = (Familiar) npc;
		boolean usingSpecial = familiar.hasSpecialOn();
		int damage = 0;
		if (usingSpecial) {// priority over regular attack
			npc.setNextAnimation(new Animation(8223));
			npc.setNextSpotAnim(new SpotAnim(1460));
			for (Entity targets : npc.getPossibleTargets()) {
				if (targets.equals(target) && !targets.isAtMultiArea())
					continue;
				sendSpecialAttack(targets, npc);
			}
			sendSpecialAttack(target, npc);
		} else {
			damage = getMaxHit(npc, 160, AttackStyle.MELEE, target);
			npc.setNextAnimation(new Animation(8222));
			delayHit(npc, 1, target, getMeleeHit(npc, damage));
		}
		return npc.getAttackSpeed();
	}

	public void sendSpecialAttack(Entity target, NPC npc) {
		if (target.isAtMultiArea() && WildernessController.isAtWild(target)) {
			delayHit(npc, 1, target, getMagicHit(npc, getMaxHit(npc, 160, AttackStyle.MAGE, target)));
			World.sendProjectile(npc, target, 1462, 34, 16, 30, 35, 16, 0);
			if (Utils.getRandomInclusive(3) == 0)// 1/3 chance of being poisioned
				target.getPoison().makePoisoned(58);
		}
	}
}
