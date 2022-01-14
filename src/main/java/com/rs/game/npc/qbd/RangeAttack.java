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

import com.rs.game.Hit;
import com.rs.game.Hit.HitLook;
import com.rs.game.player.Player;
import com.rs.game.player.actions.PlayerCombat;
import com.rs.game.player.content.skills.prayer.Prayer;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.util.Utils;

/**
 * Handles the Queen Black Dragon's range attack.
 *
 * @author Emperor
 *
 */
public final class RangeAttack implements QueenAttack {

	/**
	 * The animation.
	 */
	private static final Animation ANIMATION = new Animation(16718);

	@Override
	public int attack(final QueenBlackDragon npc, final Player victim) {
		npc.setNextAnimation(ANIMATION);
		WorldTasks.schedule(new WorldTask() {
			@Override
			public void run() {
				stop();
				int hit = 0;
				if (victim.getPrayer().active(Prayer.DEFLECT_RANGE)) {
					victim.setNextAnimation(new Animation(12573));
					victim.setNextSpotAnim(new SpotAnim(2229));
					victim.sendMessage("You are unable to reflect damage back to this creature.");
				} else if (victim.getPrayer().active(Prayer.PROTECT_RANGE))
					victim.setNextAnimation(new Animation(PlayerCombat.getDefenceEmote(victim)));
				else {
					hit = Utils.random(0 + Utils.random(150), 360);
					victim.setNextAnimation(new Animation(PlayerCombat.getDefenceEmote(victim)));
				}
				victim.applyHit(new Hit(npc, hit, hit == 0 ? HitLook.MISSED : HitLook.RANGE_DAMAGE));
			}
		}, 1);
		return Utils.random(4, 15);
	}

	@Override
	public boolean canAttack(QueenBlackDragon npc, Player victim) {
		return true;
	}

}