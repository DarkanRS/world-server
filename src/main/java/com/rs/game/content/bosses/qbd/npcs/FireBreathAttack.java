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

public final class FireBreathAttack implements QueenAttack {

	private static final Animation ANIMATION = new Animation(16721);
	private static final SpotAnim GRAPHIC = new SpotAnim(3143);

	@Override
	public int attack(final QueenBlackDragon npc, final Player victim) {
		npc.setNextAnimation(ANIMATION);
		npc.setNextSpotAnim(GRAPHIC);
		npc.getTasks().schedule(new Task() {
			@Override
			public void run() {
				super.stop();
				int hit = 0;
				int protection = PlayerCombat.getAntifireLevel(victim, true);
				if (protection == 1)
					hit = Utils.random(350, 400);
				else if (protection == 2)
					hit = Utils.random(150, 200);
				else
					hit = Utils.random(400, 710);
				victim.setNextAnimation(new Animation(PlayerCombat.getDefenceEmote(victim)));
				victim.applyHit(new Hit(npc, hit, HitLook.TRUE_DAMAGE));
			}
		}, 1);
		return Utils.random(4, 15);
	}

	@Override
	public boolean canAttack(QueenBlackDragon npc, Player victim) {
		return true;
	}
}