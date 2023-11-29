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

import com.rs.game.content.skills.dungeoneering.npcs.Rammernaut;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.npc.combat.CombatScript;
import com.rs.game.model.entity.npc.combat.NPCCombatDefinitions;
import com.rs.game.model.entity.npc.combat.NPCCombatDefinitions.AttackStyle;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.util.Utils;
import com.rs.utils.WorldUtil;

public class RammernautCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { "Rammernaut" };
	}

	public static int getChargeCount(NPC npc) {
		return npc.getTempAttribs().getI("RAMMERNAUT_CHARGE", 0);
	}

	public static void setChargeCount(NPC npc, int count) {
		npc.getTempAttribs().setI("RAMMERNAUT_CHARGE", count);

	}

	@Override
	public int attack(final NPC npc, final Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		int chargeCount = getChargeCount(npc);

		if (!(npc instanceof Rammernaut rammernaut))
			return 0;

		if (chargeCount > 1 && target instanceof Player player) {
			((Rammernaut) npc).setChargeTarget(player);
			setChargeCount(npc, 0);
			return 0;
		}

		if (!WorldUtil.isInRange(npc.getX(), npc.getY(), npc.getSize(), target.getX(), target.getY(), target.getSize(), 0)) {
			setChargeCount(npc, chargeCount + 1);
			return 3;
		}
		setChargeCount(npc, Utils.random(10) == 0 ? 2 : 0); // 1 in 10 change charging next att

		if (Utils.random(5) == 0) {
			npc.setNextAnimation(new Animation(13705));
			for (Entity entity : npc.getPossibleTargets()) {
				if (!WorldUtil.isInRange(npc.getX(), npc.getY(), npc.getSize(), entity.getX(), entity.getY(), entity.getSize(), 0))
					continue;
				((Rammernaut) npc).applyStunHit(entity, npc.getLevelForStyle(AttackStyle.MELEE));
			}
			return npc.getAttackSpeed();
		}

		if (rammernaut.isRequestSpecNormalAttack() && target instanceof Player player) {
			rammernaut.setRequestSpecNormalAttack(false);
			player.sendMessage("Your prayers have been disabled.");
			player.setProtectionPrayBlock(12);
			player.sendMessage("Your defence been reduced.");
			player.getSkills().drainLevel(Constants.DEFENSE, Utils.random(3) + 1);
		}

		// default melee attack can be protected with prayer
		npc.setNextAnimation(new Animation(defs.getAttackEmote()));
		delayHit(npc, 0, target, getMeleeHit(npc, getMaxHitFromAttackStyleLevel(npc, AttackStyle.MELEE, target)));
		return npc.getAttackSpeed();
	}
}
