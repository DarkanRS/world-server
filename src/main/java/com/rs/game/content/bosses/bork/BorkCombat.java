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
package com.rs.game.content.bosses.bork;

import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.ForceTalk;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.npc.combat.CombatScript;
import com.rs.game.model.entity.npc.combat.NPCCombatDefinitions;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Animation;
import com.rs.lib.util.Utils;

public class BorkCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { "Bork" };
	}

	public boolean spawnOrk = false;

	@Override
	public int attack(NPC npc, Entity target) {
		final NPCCombatDefinitions cdef = npc.getCombatDefinitions();
		if (npc.getHitpoints() <= (cdef.getHitpoints() * 0.4) && !spawnOrk) {
			Player player = (Player) target;
			npc.setNextForceTalk(new ForceTalk("Come to my aid, brothers!"));
			player.getControllerManager().startController(new BorkController(1, npc));
			spawnOrk = true;
		}
		npc.setNextAnimation(new Animation(Utils.getRandomInclusive(1) == 0 ? cdef.getAttackEmote() : 8757));
		delayHit(npc, 0, target, getMeleeHit(npc, getMaxHit(npc, cdef.getMaxHit(), null, target)));
		return npc.getAttackSpeed();
	}

}
