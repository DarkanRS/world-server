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
import com.rs.game.npc.godwars.zaros.Nex;
import com.rs.game.npc.godwars.zaros.Nex.Phase;
import com.rs.game.player.Player;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.util.Utils;

public class EmbraceDarkness implements NexAttack {

	@Override
	public int attack(Nex nex, Entity target) {
		nex.setNextForceTalk(new ForceTalk("Embrace darkness!"));
		nex.playSound(3322, 2);
		nex.setNextAnimation(new Animation(6355));
		nex.setNextSpotAnim(new SpotAnim(1217));
		WorldTasks.schedule(new WorldTask() {
			@Override
			public void run() {
				if (nex.getPhase() != Phase.SHADOW || nex.hasFinished()) {
					for (Entity entity : nex.getPossibleTargets())
						if (entity instanceof Player player) {
							player.getPackets().sendVarc(1435, 255);
						}
					stop();
					return;
				}
				if (Utils.getRandomInclusive(2) == 0)
					for (Entity entity : nex.getPossibleTargets())
						if (entity instanceof Player player) {
							int distance = (int) Utils.getDistance(player.getX(), player.getY(), nex.getX(), nex.getY());
							if (distance > 30)
								distance = 30;
							player.getPackets().sendVarc(1435, (distance * 255 / 30));
						}
			}
		}, 0, 0);
		return nex.getAttackSpeed();
	}

}
