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
package com.rs.game.content.bosses.qbd.npcs;

import com.rs.game.model.entity.player.Player;
import com.rs.lib.util.Utils;

import java.util.Iterator;

/**
 * Handles the summoning of the tortured souls.
 *
 * @author Emperor
 *
 */
public final class SoulSummonAttack implements QueenAttack {

	/**
	 * The spawn offset locations.
	 */
	private static final int[][] SPAWN_LOCATIONS = { { 31, 35 }, { 33, 35 }, { 34, 33 }, { 31, 29 } };

	@Override
	public int attack(QueenBlackDragon npc, Player victim) {
		for (Iterator<TorturedSoul> it = npc.getSouls().iterator(); it.hasNext();)
			if (it.next().isDead())
				it.remove();
		npc.getTempAttribs().setI("_last_soul_summon", npc.getTicks() + Utils.random(41, 100));
		int count = npc.getPhase() - 1;
		if (count == 3)
			count = 4;
		if (npc.getSouls().size() < count) {
			victim.sendMessage(
					(count - npc.getSouls().size()) < 2 ? "<col=9900CC>The Queen Black Dragon summons one of her captive, tortured souls.</col>" : "<col=9900CC>The Queen Black Dragon summons several of her captive, tortured souls.</col>");
			for (int i = npc.getSouls().size(); i < count; i++)
				npc.getSouls().add(new TorturedSoul(npc, victim, npc.getBase().transform(SPAWN_LOCATIONS[i][0], SPAWN_LOCATIONS[i][1], 0)));
		}
		for (int i = 0; i < count; i++) {
			if (i >= npc.getSouls().size())
				break;
			TorturedSoul s = npc.getSouls().get(i);
			if (s == null || s.isDead())
				continue;
			s.specialAttack(npc.getBase().transform(SPAWN_LOCATIONS[i][0], SPAWN_LOCATIONS[i][1], 0));
		}
		return Utils.random(4, 15);
	}

	@Override
	public boolean canAttack(QueenBlackDragon npc, Player victim) {
		int last = npc.getTempAttribs().getI("_last_soul_summon");
		return last == -1 || last < npc.getTicks();
	}

}