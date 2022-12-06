package com.rs.game.content.world.npcs.max.tasks;

import com.rs.cache.loaders.ObjectType;
import com.rs.game.content.skills.magic.Magic;
import com.rs.game.content.world.npcs.max.Max;
import com.rs.game.model.entity.pathing.RouteEvent;
import com.rs.game.model.object.GameObject;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;

public class MaxTaskCook implements Task {
	
	private static GameObject RANGE = new GameObject(114, ObjectType.SCENERY_INTERACT, 2, WorldTile.of(3212, 3215, 0));
		
	private boolean started = false;
	private int itemsCooked = Utils.random(85, 150);
	
	@Override
	public int tick(Max max) {
		if (!started) {
			max.wearItems(-1, -1);
			started = true;
		}
		if (!max.withinDistance(RANGE.getTile(), 64)) {
			Magic.npcNormalTeleport(max, WorldTile.of(3235, 3219, 0), true, null);
			return 10;
		}
		if (itemsCooked <= 0) {
			max.nextTask();
			return 10;
		}
		if (!max.getActionManager().hasSkillWorking() && !max.hasWalkSteps()) {
			max.setRouteEvent(new RouteEvent(RANGE, () -> {
				max.repeatAction(3, count -> {
					if (itemsCooked <= 0) {
						max.nextTask();
						return false;
					}
					max.faceObject(RANGE);
					max.anim(897);
					itemsCooked--;
					return true;
				});
			}));
		}
		return 5;
	}
}
