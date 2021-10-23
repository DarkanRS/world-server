package com.rs.game.npc.godwars.zaros.attack;

import com.rs.cache.loaders.ObjectType;
import com.rs.game.Entity;
import com.rs.game.ForceTalk;
import com.rs.game.Hit;
import com.rs.game.Hit.HitLook;
import com.rs.game.World;
import com.rs.game.npc.godwars.zaros.Nex;
import com.rs.game.object.GameObject;
import com.rs.game.player.Player;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.lib.game.Animation;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;

public class IcePrison implements NexAttack {

	@Override
	public int attack(Nex nex, Entity target) {
		nex.setNextForceTalk(new ForceTalk("Die now, in a prison of ice!"));
		nex.playSound(3308, 2);
		nex.setNextAnimation(new Animation(6987));
		World.sendProjectile(nex, target, 362, 20, 20, 20, 0.45, 10, 0);
		final WorldTile base = new WorldTile(target.getX(), target.getY(), target.getPlane());
		target.getTempAttribs().setB("inIcePrison", true);
		for (int x = -1; x <= 1; x++) {
			for (int y = -1; y <= 1; y++) {
				final WorldTile tile = base.transform(x, y, target.getPlane());
				final GameObject object = new GameObject(57263, ObjectType.SCENERY_INTERACT, 0, tile);
				if (!tile.matches(base) && World.floorAndWallsFree(tile, (object.getDefinitions().getSizeX() + object.getDefinitions().getSizeY()) / 2))
					World.spawnObject(object);
				WorldTasksManager.schedule(new WorldTask() {

					boolean remove = false;

					@Override
					public void run() {
						if (remove) {
							World.removeObject(object);
							stop();
							return;
						}
						remove = true;
						target.getTempAttribs().setB("inIcePrison", false);
						if (target.getX() == tile.getX() && target.getY() == tile.getY()) {
							if (target instanceof Player)
								((Player)target).sendMessage("The centre of the ice prison freezes you to the bone!");
							target.resetWalkSteps();
							target.applyHit(new Hit(nex, Utils.random(600, 800), HitLook.TRUE_DAMAGE));
						}
					}
				}, 8, 0);
			}
		}
		return nex.getAttackSpeed() * 2;
	}

}
