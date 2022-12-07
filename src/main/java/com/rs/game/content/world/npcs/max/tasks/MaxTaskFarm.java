package com.rs.game.content.world.npcs.max.tasks;

import com.rs.cache.loaders.ObjectType;
import com.rs.game.World;
import com.rs.game.content.skills.farming.FarmPatch;
import com.rs.game.content.skills.magic.Magic;
import com.rs.game.content.world.npcs.max.Max;
import com.rs.game.model.entity.pathing.RouteEvent;
import com.rs.game.model.object.GameObject;
import com.rs.lib.game.WorldTile;

public class MaxTaskFarm implements Task {
	
	private boolean started = false;
	private GameObject patch;
	private boolean reached;
	
	@Override
	public int tick(Max max) {
		patch = World.getObject(WorldTile.of(3228, 3458, 0), ObjectType.SCENERY_INTERACT);
		if (patch == null)
			return 0;
		if (!started) {
			max.wearItems(5341, 19749);
			started = true;
		}
		if (!max.withinDistance(patch.getTile(), 80)) {
			Magic.npcNormalTeleport(max, WorldTile.of(3213, 3423, 0), true, null);
			return 10;
		}
		if (!reached) {
			if (!max.hasWalkSteps())
				max.setRouteEvent(new RouteEvent(patch, () -> { reached = true; }));
			return 10;
		}
		if (max.getActionManager().hasSkillWorking())
			return 3;
		max.repeatAction(5, rakeCount -> {
			max.faceObject(patch);
			if (rakeCount > 5) {
				max.repeatAction(10, compostCount -> {
					if (compostCount >= 2) {
						max.nextTask();
						return true;
					}
					max.itemAnim(FarmPatch.COMPOST_ANIMATION, 5);
					return true;
				});
				return true;
			}
			max.itemAnim(FarmPatch.RAKING_ANIMATION, 5);
			return true;
		});
		return 2;
	}
}
