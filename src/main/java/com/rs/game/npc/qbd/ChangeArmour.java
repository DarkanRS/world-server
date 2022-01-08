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
package com.rs.game.npc.qbd;

import com.rs.game.player.Player;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.util.Utils;

/**
 * Handles the Queen Black Dragon's change armour "attack".
 *
 * @author Emperor
 *
 */
public final class ChangeArmour implements QueenAttack {

	@Override
	public int attack(final QueenBlackDragon npc, Player victim) {
		npc.switchState(Utils.random(2) < 1 ? QueenState.CRYSTAL_ARMOUR : QueenState.HARDEN);
		WorldTasks.schedule(new WorldTask() {
			@Override
			public void run() {
				npc.switchState(QueenState.DEFAULT);
			}
		}, 40);
		npc.getTempAttribs().setI("_last_armour_change", npc.getTicks() + Utils.random(41, 100));
		return Utils.random(4, 10);
	}

	@Override
	public boolean canAttack(QueenBlackDragon npc, Player victim) {
		return npc.getTempAttribs().getI("_last_armour_change") < npc.getTicks();
	}

}