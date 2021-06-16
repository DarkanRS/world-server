package com.rs.game.npc.godwars.zaros.attack;

import com.rs.game.Entity;
import com.rs.game.ForceMovement;
import com.rs.game.npc.godwars.zaros.Nex;
import com.rs.game.pathing.Direction;
import com.rs.game.player.Player;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.util.Utils;

public class Drag implements NexAttack {

	@Override
	public int attack(Nex nex, Entity target) {
		int distance = 0;
		Entity settedTarget = null;
		for (Entity t : nex.getPossibleTargets()) {
			if (t instanceof Player) {
				int thisDistance = (int) Utils.getDistance(t.getX(), t.getY(), nex.getX(), nex.getY());
				if (settedTarget == null || thisDistance > distance) {
					distance = thisDistance;
					settedTarget = t;
				}
			}
		}
		if (settedTarget != null) {
			final Player player = (Player) settedTarget;
			player.lock(3);
			player.setNextAnimation(new Animation(14386));
			player.setNextSpotAnim(new SpotAnim(2767));
			player.setNextForceMovement(new ForceMovement(nex, 2, Direction.forDelta(nex.getCoordFaceX(player.getSize()) - player.getX(), nex.getCoordFaceY(player.getSize()) - player.getY())));
			nex.setNextAnimation(new Animation(6986));
			nex.setTarget(player);
			player.setNextAnimation(new Animation(-1));
			WorldTasksManager.schedule(new WorldTask() {
				@Override
				public void run() {
					player.setNextWorldTile(nex);
					player.sendMessage("You've been injured and you can't use protective prayers!");
					player.setProtectionPrayBlock(12);
					player.sendMessage("You're stunned.");
				}
			});
		}
		return nex.getAttackSpeed();
	}

}
