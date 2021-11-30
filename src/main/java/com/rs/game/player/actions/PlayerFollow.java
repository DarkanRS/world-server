package com.rs.game.player.actions;

import com.rs.game.pathing.EntityStrategy;
import com.rs.game.pathing.RouteFinder;
import com.rs.game.player.Player;
import com.rs.game.player.content.Effect;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;
import com.rs.utils.WorldUtil;

public class PlayerFollow extends Action {

	private Player target;

	public PlayerFollow(Player target) {
		this.target = target;
		this.setNoRandoms(true);
	}

	@Override
	public boolean start(Player player) {
		player.setNextFaceEntity(target);
		if (checkAll(player))
			return true;
		player.setNextFaceEntity(null);
		return false;
	}

	private boolean checkAll(Player player) {
		if (player.isDead() || player.hasFinished() || target.isDead() || target.hasFinished())
			return false;
		if (player.getPlane() != target.getPlane())
			return false;
		if (player.hasEffect(Effect.FREEZE))
			return true;
		int distanceX = player.getX() - target.getX();
		int distanceY = player.getY() - target.getY();
		int size = player.getSize();
		int maxDistance = 16;
		if (player.getPlane() != target.getPlane() || distanceX > size + maxDistance || distanceX < -1 - maxDistance || distanceY > size + maxDistance || distanceY < -1 - maxDistance)
			return false;
		int lastFaceEntity = target.getLastFaceEntity();
		WorldTile toTile = target.getTileBehind() != null && Utils.getDistance(target, target.getTileBehind()) <= 3 ? target.getTileBehind() : target.getBackfacingTile();
		if (lastFaceEntity == player.getClientIndex() && target.getActionManager().getAction() instanceof PlayerFollow) {
			player.addWalkSteps(toTile.getX(), toTile.getY());
		} else if (!player.lineOfSightTo(target, true) || !WorldUtil.isInRange(player.getX(), player.getY(), size, target.getX(), target.getY(), target.getSize(), 0)) {
			int steps = RouteFinder.findRoute(RouteFinder.WALK_ROUTEFINDER, player.getX(), player.getY(), player.getPlane(), player.getSize(), new EntityStrategy(target), true);
			if (steps == -1)
				return false;

			if (steps > 0) {
				player.resetWalkSteps();

				int[] bufferX = RouteFinder.getLastPathBufferX();
				int[] bufferY = RouteFinder.getLastPathBufferY();
				for (int step = steps - 1; step >= 0; step--) {
					if (!player.addWalkSteps(bufferX[step], bufferY[step], 25, true, true))
						break;
				}
			}
			return true;
		}
		return true;
	}

	@Override
	public boolean process(Player player) {
		return checkAll(player);
	}

	@Override
	public int processWithDelay(Player player) {
		return 0;
	}

	@Override
	public void stop(final Player player) {
		player.setNextFaceEntity(null);
	}
}
