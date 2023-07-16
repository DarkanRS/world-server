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

import com.rs.game.model.entity.Hit;
import com.rs.game.model.entity.Hit.HitLook;
import com.rs.game.model.entity.player.Player;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.util.Utils;

import java.util.Iterator;

/**
 * The Queen Black Dragon's soul siphon attack.
 *
 * @author Emperor
 *
 */
public final class SoulSiphonAttack implements QueenAttack {

	/**
	 * The siphon graphics.
	 */
	private static final SpotAnim SIPHON_GRAPHIC = new SpotAnim(3148);

	@Override
	public int attack(final QueenBlackDragon npc, Player victim) {
		for (Iterator<TorturedSoul> it = npc.getSouls().iterator(); it.hasNext();) {
			TorturedSoul soul = it.next();
			if (soul.isDead())
				it.remove();
		}
		if (npc.getSouls().isEmpty())
			return 1;
		victim.sendMessage("<col=9900CC>The Queen Black Dragon starts to siphon the energy of her mages.</col>");
		WorldTasks.schedule(new WorldTask() {
			@Override
			public void run() {
				for (Iterator<TorturedSoul> it = npc.getSouls().iterator(); it.hasNext();) {
					TorturedSoul soul = it.next();
					if (soul.isDead()) {
						it.remove();
						continue;
					}
					soul.setNextSpotAnim(SIPHON_GRAPHIC);
					soul.applyHit(new Hit(npc, 20, HitLook.TRUE_DAMAGE));
					npc.getNextHits().add(new Hit(npc, 40, HitLook.HEALED_DAMAGE));
					npc.heal(40);
				}
				if (npc.getSouls().isEmpty()) {
					stop();
					npc.getTempAttribs().setI("_last_soul_summon", npc.getTicks() + Utils.random(120) + 125);
				}
			}
		}, 0, 0);
		npc.getTempAttribs().setI("_last_soul_summon", npc.getTicks() + 999);
		npc.getTempAttribs().setI("_soul_siphon_atk", npc.getTicks() + 50 + Utils.random(40));
		return Utils.random(5, 10);
	}

	@Override
	public boolean canAttack(QueenBlackDragon npc, Player victim) {
		int tick = npc.getTempAttribs().getI("_soul_siphon_atk");
		return tick == -1 || tick < npc.getTicks();
	}

}