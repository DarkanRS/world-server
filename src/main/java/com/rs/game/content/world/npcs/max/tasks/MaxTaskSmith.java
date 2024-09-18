package com.rs.game.content.world.npcs.max.tasks;

import com.rs.cache.loaders.ObjectType;
import com.rs.engine.pathfinder.RouteEvent;
import com.rs.game.content.skills.magic.Magic;
import com.rs.game.content.world.npcs.max.Max;
import com.rs.game.model.object.GameObject;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Utils;

public class MaxTaskSmith implements Task {
	
	private static final GameObject WEST = new GameObject(2783, ObjectType.SCENERY_INTERACT, 0, Tile.of(3188, 3426, 0));
	private static final GameObject EAST = new GameObject(2783, ObjectType.SCENERY_INTERACT, 0, Tile.of(3228, 3436, 0));
		
	private boolean started = false;
	private int itemsSmithed = Utils.random(85, 150);
	
	@Override
	public int tick(Max max) {
		if (!started) {
			max.wearItems(2347, 22422);
			started = true;
		}
		if (!max.withinDistance(WEST.getTile(), 64)) {
			Magic.npcNormalTeleport(max, Tile.of(3212, 3423, 0), true, null);
			return 10;
		}
		if (itemsSmithed <= 0) {
			max.nextTask();
			return 10;
		}
		if (!max.getActionManager().hasSkillWorking() && !max.hasWalkSteps()) {
			GameObject anvil = Utils.random(2) == 0 ? WEST : EAST;
			max.setRouteEvent(new RouteEvent(anvil, () -> max.repeatAction(3, count -> {
                if (itemsSmithed <= 0) {
                    max.nextTask();
                    return false;
                }
                max.faceObject(anvil);
                max.anim(898);
                itemsSmithed--;
                return true;
            })));
		}
		return 5;
	}
}
