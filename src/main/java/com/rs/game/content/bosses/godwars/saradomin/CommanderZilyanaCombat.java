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
package com.rs.game.content.bosses.godwars.saradomin;

import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.ForceTalk;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.npc.combat.CombatScript;
import com.rs.game.model.entity.npc.combat.NPCCombatDefinitions;
import com.rs.game.model.entity.npc.combat.NPCCombatDefinitions.AttackStyle;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.util.Utils;

public class CommanderZilyanaCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 6247 };
	}

	@Override
	public int attack(final NPC npc, final Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		if (Utils.getRandomInclusive(4) == 0)
			switch (Utils.getRandomInclusive(9)) {
			case 0:
				npc.setNextForceTalk(new ForceTalk("Death to the enemies of the light!"));
				npc.voiceEffect(3247);
				break;
			case 1:
				npc.setNextForceTalk(new ForceTalk("Slay the evil ones!"));
				npc.voiceEffect(3242);
				break;
			case 2:
				npc.setNextForceTalk(new ForceTalk("Saradomin lend me strength!"));
				npc.voiceEffect(3263);
				break;
			case 3:
				npc.setNextForceTalk(new ForceTalk("By the power of Saradomin!"));
				npc.voiceEffect(3262);
				break;
			case 4:
				npc.setNextForceTalk(new ForceTalk("May Saradomin be my sword."));
				npc.voiceEffect(3251);
				break;
			case 5:
				npc.setNextForceTalk(new ForceTalk("Good will always triumph!"));
				npc.voiceEffect(3260);
				break;
			case 6:
				npc.setNextForceTalk(new ForceTalk("Forward! Our allies are with us!"));
				npc.voiceEffect(3245);
				break;
			case 7:
				npc.setNextForceTalk(new ForceTalk("Saradomin is with us!"));
				npc.voiceEffect(3266);
				break;
			case 8:
				npc.setNextForceTalk(new ForceTalk("In the name of Saradomin!"));
				npc.voiceEffect(3250);
				break;
			case 9:
				npc.setNextForceTalk(new ForceTalk("Attack! Find the Godsword!"));
				npc.voiceEffect(3258);
				break;
			}
		if (Utils.getRandomInclusive(1) == 0) { // mage magical attack
			npc.setNextAnimation(new Animation(6967));
			for (Entity t : npc.getPossibleTargets()) {
				if (!t.withinDistance(npc.getTile(), 3))
					continue;
				int damage = getMaxHit(npc, defs.getMaxHit(), AttackStyle.MAGE, t);
				if (damage > 0) {
					delayHit(npc, 1, t, getMagicHit(npc, damage));
					t.setNextSpotAnim(new SpotAnim(1194));
				}
			}

		} else { // melee attack
			npc.setNextAnimation(new Animation(defs.getAttackEmote()));
			delayHit(npc, 0, target, getMeleeHit(npc, getMaxHit(npc, defs.getMaxHit(), AttackStyle.MELEE, target)));
		}
		return npc.getAttackSpeed();
	}
}
