package com.rs.game.npc.godwars.zaros.attack;

import com.rs.game.Entity;
import com.rs.game.ForceTalk;
import com.rs.game.npc.godwars.zaros.Nex;
import com.rs.game.npc.godwars.zaros.Nex.Phase;
import com.rs.game.player.Player;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
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
		WorldTasksManager.schedule(new WorldTask() {
			@Override
			public void run() {
				if (nex.getPhase() != Phase.SHADOW || nex.hasFinished()) {
					for (Entity entity : nex.getPossibleTargets()) {
						if (entity instanceof Player) {
							Player player = (Player) entity;
							player.getPackets().sendVarc(1435, 255);
						}
					}
					stop();
					return;
				}
				if (Utils.getRandomInclusive(2) == 0) {
					for (Entity entity : nex.getPossibleTargets()) {
						if (entity instanceof Player) {
							Player player = (Player) entity;
							int distance = (int) Utils.getDistance(player.getX(), player.getY(), nex.getX(), nex.getY());
							if (distance > 30)
								distance = 30;
							player.getPackets().sendVarc(1435, (distance * 255 / 30));
						}
					}
				}
			}
		}, 0, 0);
		return nex.getAttackSpeed();
	}

}
