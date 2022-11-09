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
package com.rs.game.content.bosses.godwars.bandos;

import com.rs.game.World;
import com.rs.game.model.WorldProjectile;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.ForceTalk;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.npc.combat.CombatScript;
import com.rs.game.model.entity.npc.combat.NPCCombatDefinitions;
import com.rs.game.model.entity.npc.combat.NPCCombatDefinitions.AttackStyle;
import com.rs.lib.game.Animation;
import com.rs.lib.util.Utils;

public class GeneralGraardorCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 6260 };
	}

	@Override
	public int attack(final NPC npc, final Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		if (Utils.getRandomInclusive(4) == 0)
			switch (Utils.getRandomInclusive(10)) {
			case 0:
				npc.setNextForceTalk(new ForceTalk("Death to our enemies!"));
				npc.voiceEffect(3219);
				break;
			case 1:
				npc.setNextForceTalk(new ForceTalk("Brargh!"));
				npc.voiceEffect(3209);
				break;
			case 2:
				npc.setNextForceTalk(new ForceTalk("Break their bones!"));
				break;
			case 3:
				npc.setNextForceTalk(new ForceTalk("For the glory of Bandos!"));
				break;
			case 4:
				npc.setNextForceTalk(new ForceTalk("Split their skulls!"));
				npc.voiceEffect(3229);
				break;
			case 5:
				npc.setNextForceTalk(new ForceTalk("We feast on the bones of our enemies tonight!"));
				npc.voiceEffect(3206);
				break;
			case 6:
				npc.setNextForceTalk(new ForceTalk("CHAAARGE!"));
				npc.voiceEffect(3220);
				break;
			case 7:
				npc.setNextForceTalk(new ForceTalk("Crush them underfoot!"));
				npc.voiceEffect(3224);
				break;
			case 8:
				npc.setNextForceTalk(new ForceTalk("All glory to Bandos!"));
				npc.voiceEffect(3205);
				break;
			case 9:
				npc.setNextForceTalk(new ForceTalk("GRAAAAAAAAAR!"));
				npc.voiceEffect(3207);
				break;
			case 10:
				npc.setNextForceTalk(new ForceTalk("FOR THE GLORY OF THE BIG HIGH WAR GOD!"));
				break;
			}
		if (Utils.getRandomInclusive(2) == 0) { // range magical attack
			npc.setNextAnimation(new Animation(7063));
			for (Entity t : npc.getPossibleTargets()) {
				WorldProjectile p = World.sendProjectile(npc, target, 1200, 60, 32, 50, 1, 0, 0);
				delayHit(npc, p.getTaskDelay(), t, getRangeHit(npc, getMaxHit(npc, 355, AttackStyle.RANGE, t)));
			}
		} else { // melee attack
			npc.setNextAnimation(new Animation(defs.getAttackEmote()));
			delayHit(npc, 0, target, getMeleeHit(npc, getMaxHit(npc, defs.getMaxHit(), AttackStyle.MELEE, target)));
		}
		return npc.getAttackSpeed();
	}
}
