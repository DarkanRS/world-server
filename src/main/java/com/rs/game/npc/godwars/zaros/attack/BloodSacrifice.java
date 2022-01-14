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
package com.rs.game.npc.godwars.zaros.attack;

import com.rs.game.Entity;
import com.rs.game.ForceTalk;
import com.rs.game.Hit;
import com.rs.game.Hit.HitLook;
import com.rs.game.World;
import com.rs.game.npc.godwars.zaros.Nex;
import com.rs.game.player.Player;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.util.Utils;

public class BloodSacrifice implements NexAttack {

	@Override
	public int attack(Nex nex, Entity target) {
		if (!(target instanceof Player))
			return 0;
		nex.setNextForceTalk(new ForceTalk("I demand a blood sacrifice!"));
		nex.playSound(3293, 2);
		final Player player = (Player) target;
		player.getAppearance().setGlowRed(true);
		player.sendMessage("Nex has marked you as a sacrifice, RUN!");
		WorldTasks.schedule(new WorldTask() {
			@Override
			public void run() {
				player.getAppearance().setGlowRed(false);
				if (Utils.getDistance(nex, player) < 7) {
					player.sendMessage("You didn't make it far enough in time - Nex fires a punishing attack!");
					nex.setNextAnimation(new Animation(6987));
					for (final Entity t : nex.getPossibleTargets())
						World.sendProjectile(nex, t, 374, 41, 16, 41, 35, 16, 0, () -> {
							nex.heal(t.getHitpoints());
							t.applyHit(new Hit(nex, (int) (t.getHitpoints() * 0.1), HitLook.TRUE_DAMAGE));
						});
				}
			}
		}, nex.getAttackSpeed());
		return nex.getAttackSpeed() * 2;
	}

}
