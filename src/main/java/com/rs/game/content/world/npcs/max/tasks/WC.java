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

public class WC implements Task {
		
	private boolean started = false;
	private GameObject currentIvy;
	
	@Override
	public int tick(Max max) {
		if (!started) {
			max.wearItems(6739, -1);
			started = true;
		}
		if (!max.withinDistance(new WorldTile(3213, 3423, 0), 80)) {
			Magic.npcNormalTeleport(max, new WorldTile(3213, 3423, 0), true, null);
			return 10;
		}
		currentIvy = getClosestIvy(max.getTile());
		if (currentIvy == null || !currentIvy.getDefinitions().containsOption("Chop")) {
			System.out.println("Ivy not found... " + currentIvy);
			return 0;
		}
		if (!max.getActionManager().hasSkillWorking() && !max.hasWalkSteps()) {
			max.setRouteEvent(new RouteEvent(currentIvy, () -> max.getActionManager().setAction(new Woodcutting(currentIvy, TreeType.IVY).setHatchet(Hatchet.DRAGON).setLevel(110))));
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
			double dist = Utils.getDistance(obj, tile);
			if (dist < closest) {
				ivy = obj;
				closest = dist;
			}	
		}
		return ivy;
	}
}
