package com.rs.game.content.world.npcs.max.tasks;

import com.rs.game.World;
import com.rs.game.content.skills.magic.Magic;
import com.rs.game.content.skills.woodcutting.Hatchet;
import com.rs.game.content.skills.woodcutting.TreeType;
import com.rs.game.content.skills.woodcutting.Woodcutting;
import com.rs.game.content.world.npcs.max.Max;
import com.rs.game.model.entity.pathing.RouteEvent;
import com.rs.game.model.object.GameObject;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;

public class MaxTaskWC implements Task {
		
	private boolean started = false;
	private GameObject currentIvy;
	private int ivysCut = Utils.random(3, 5);
	
	@Override
	public int tick(Max max) {
		if (!started) {
			max.wearItems(6739, -1);
			started = true;
		}
		if (max.getTile().getRegionId() != 12854) {
			if (max.getTile().getRegionId() != 12853)
				Magic.npcNormalTeleport(max, WorldTile.of(3213, 3423, 0), true, null);
			else {
				if (!max.hasWalkSteps())
					max.setRouteEvent(new RouteEvent(WorldTile.of(3232, 3459, 0), () -> {  }));
			}
			return 10;
		}
		if (ivysCut <= 0) {
			max.nextTask();
			return 10;
		}
		currentIvy = getClosestIvy(max.getTile());
		if (currentIvy == null || !currentIvy.getDefinitions().containsOption("Chop"))
			return 0;
		if (!max.getActionManager().hasSkillWorking() && !max.hasWalkSteps()) {
			max.setRouteEvent(new RouteEvent(currentIvy, () -> max.getActionManager().setAction(new Woodcutting(currentIvy, TreeType.IVY) {
				@Override
				public void fellTree() {
					super.fellTree();
					ivysCut--;
				}
			}.setHatchet(Hatchet.DRAGON).setLevel(110))));
			return 10;
		}
		return 0;
	}
	
	public GameObject getClosestIvy(WorldTile tile) {
		GameObject ivy = null;
		double closest = Double.MAX_VALUE;
		for (GameObject obj : World.getRegion(tile.getRegionId()).getObjects()) {
			if (obj == null || obj.getDefinitions() == null || !obj.getDefinitions().getName().equals("Ivy") || !obj.getDefinitions().containsOption("Chop"))
				continue;
			double dist = Utils.getDistance(obj.getTile(), tile);
			if (dist < closest) {
				ivy = obj;
				closest = dist;
			}	
		}
		return ivy;
	}
}
