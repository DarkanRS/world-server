package com.rs.game.content.world.npcs.max.tasks;

import com.rs.cache.loaders.ObjectType;
import com.rs.game.World;
import com.rs.game.content.skills.firemaking.Firemaking;
import com.rs.game.content.skills.firemaking.Firemaking.Fire;
import com.rs.game.content.skills.magic.Magic;
import com.rs.game.content.world.npcs.max.Max;
import com.rs.game.model.entity.pathing.RouteEvent;
import com.rs.game.model.object.GameObject;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Utils;

public class MaxTaskFM implements Task {
		
	private boolean started = false;
	private GameObject currentBonfire;
	private int logsBurned = Utils.random(85, 150);
	
	@Override
	public int tick(Max max) {
		if (!started) {
			max.wearItems(590, 23027);
			started = true;
		}
		if (!max.withinDistance(Tile.of(3087, 3494, 0), 64)) {
			Magic.npcItemTeleport(max, Tile.of(3087, 3494, 0), true, null);
			return 10;
		}
		if (logsBurned <= 0) {
			max.setBas(-1);
			max.nextTask();
			return 10;
		}
		currentBonfire = World.getClosestObject(ObjectType.SCENERY_INTERACT, Fire.MAGIC.getFireId(), max.getTile());
		if (currentBonfire == null) {
			max.getActionManager().setAction(new Firemaking(Fire.MAGIC));
			return 20;
		}
		if (!max.getActionManager().hasSkillWorking() && !max.hasWalkSteps()) {
			max.setRouteEvent(new RouteEvent(currentBonfire, () -> {
				max.setBas(2498);
				max.repeatAction(5, count -> {
					if (World.getObjectWithType(currentBonfire.getTile(), currentBonfire.getType()) != currentBonfire) {
						max.anim(-1);
						max.setBas(-1);
						currentBonfire = null;
						return false;
					}
					max.faceObject(currentBonfire);
					max.anim(16703);
					max.spotAnim(3135);
					logsBurned--;
					return true;
				});
			}));
		}
		return 5;
	}
}
