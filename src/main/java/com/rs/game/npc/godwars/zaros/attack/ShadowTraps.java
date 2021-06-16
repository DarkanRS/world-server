package com.rs.game.npc.godwars.zaros.attack;

import java.util.HashMap;
import java.util.List;

import com.rs.cache.loaders.ObjectType;
import com.rs.game.Entity;
import com.rs.game.ForceTalk;
import com.rs.game.Hit;
import com.rs.game.Hit.HitLook;
import com.rs.game.World;
import com.rs.game.npc.godwars.zaros.Nex;
import com.rs.game.object.GameObject;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;

public class ShadowTraps implements NexAttack {

	@Override
	public int attack(Nex nex, Entity target) {
		nex.setNextForceTalk(new ForceTalk("Fear the Shadow!"));
		nex.playSound(3314, 2);
		nex.setNextAnimation(new Animation(6984));
		nex.setNextSpotAnim(new SpotAnim(1215));
		List<Entity> possibleTargets = nex.getPossibleTargets();
		final HashMap<String, int[]> tiles = new HashMap<String, int[]>();
		for (Entity t : possibleTargets) {
			String key = t.getX() + "_" + t.getY();
			if (!tiles.containsKey(t.getX() + "_" + t.getY())) {
				tiles.put(key, new int[] { t.getX(), t.getY() });
				World.spawnObjectTemporary(new GameObject(57261, ObjectType.SCENERY_INTERACT, 0, t.getX(), t.getY(), 0), 4);
			}
		}
		WorldTasksManager.schedule(new WorldTask() {
			private boolean firstCall;

			@Override
			public void run() {
				if (!firstCall) {
					List<Entity> possibleTargets = nex.getPossibleTargets();
					for (int[] tile : tiles.values()) {
						World.sendSpotAnim(null, new SpotAnim(383), new WorldTile(tile[0], tile[1], 0));
						for (Entity t : possibleTargets)
							if (t.getX() == tile[0] && t.getY() == tile[1])
								t.applyHit(new Hit(nex, Utils.getRandomInclusive(400) + 400, HitLook.TRUE_DAMAGE));
					}
					firstCall = true;
				} else
					stop();
			}

		}, 3, 3);
		return nex.getAttackSpeed();
	}

}
