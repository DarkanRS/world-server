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

import com.rs.game.content.combat.PlayerCombat;
import com.rs.game.model.entity.Hit;
import com.rs.game.model.entity.Hit.HitLook;
import com.rs.game.model.entity.player.Player;
import com.rs.game.tasks.Task;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.util.Utils;

/**
 * Handles the super dragonfire attack.
 *
 * @author Emperor
 *
 */
public final class SuperFireAttack implements QueenAttack {

	/**
	 * The animation.
	 */
	private static final Animation ANIMATION = new Animation(16745);

	/**
	 * The graphics.
	 */
	private static final SpotAnim GRAPHIC = new SpotAnim(3152);

	@Override
	public int attack(final QueenBlackDragon npc, final Player victim) {
		npc.setNextAnimation(ANIMATION);
		npc.setNextSpotAnim(GRAPHIC);
		victim.sendMessage("<col=FFCC00>The Queen Black Dragon gathers her strength to breath extremely hot flames.</col>");
		if (Utils.getDistance(npc.getBase().transform(33, 31, 0), victim.getTile()) <= 4)
			victim.getTempAttribs().setB("canBrandish", true);
		npc.getTasks().schedule(new Task() {
			int count = 0;

			@Override
			public void run() {
				int hit;

				int protection = PlayerCombat.getAntifireLevel(victim, true);
				if (protection == 1)
					hit = Utils.random(380, 450);
				else if (protection == 2)
					hit = Utils.random(300, 310);
				else
					hit = Utils.random(500, 800);
				int distance = (int) Utils.getDistance(npc.getBase().transform(33, 31, 0), victim.getTile());
				if (distance <= 4)
					victim.getTempAttribs().setB("canBrandish", true);
				hit /= (distance / 3) + 1;
				victim.setNextAnimation(new Animation(PlayerCombat.getDefenceEmote(victim)));
				victim.applyHit(new Hit(npc, hit, HitLook.TRUE_DAMAGE));
				if (++count == 3) {
					victim.getTempAttribs().setB("canBrandish", false);
					stop();
				}
			}
		}, 4, 1);
		return Utils.random(8, 15);
	}

	@Override
	public boolean canAttack(QueenBlackDragon npc, Player victim) {
		return true;
	}

}