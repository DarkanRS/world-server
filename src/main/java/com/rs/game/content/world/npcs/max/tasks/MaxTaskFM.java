package com.rs.game.content.world.npcs.max.tasks;

import com.rs.cache.loaders.ObjectType;
import com.rs.game.World;
import com.rs.game.content.skills.firemaking.Firemaking;
import com.rs.game.content.skills.firemaking.Firemaking.Fire;
import com.rs.game.content.skills.magic.Magic;
import com.rs.game.content.skills.woodcutting.Hatchet;
import com.rs.game.content.skills.woodcutting.TreeType;
import com.rs.game.content.skills.woodcutting.Woodcutting;
import com.rs.game.content.world.npcs.max.Max;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.pathing.RouteEvent;
import com.rs.game.model.object.GameObject;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;

public class MaxTaskFM implements Task {
		
	private boolean started = false;
	private GameObject currentBonfire;
	private int firesRelit = Utils.random(2, 4);
	
	@Override
	public int tick(Max max) {
		if (!started) {
			max.wearItems(590, 23027);
			started = true;
		}
		if (!max.withinDistance(new WorldTile(3087, 3494, 0), 64)) {
			Magic.npcItemTeleport(max, new WorldTile(3087, 3494, 0), true, null);
			return 10;
		}
		if (firesRelit <= 0) {
			max.nextTask();
			return 10;
		}
		currentBonfire = World.getClosestObject(ObjectType.SCENERY_INTERACT, Fire.MAGIC.getFireId(), max.getTile());
		if (currentBonfire == null) {
			max.getActionManager().setAction(new Firemaking(Fire.MAGIC) {
				@Override
				public void stop(Entity entity) {
					super.stop(entity);
					firesRelit--;
				}
			});
			return 20;
		}
		if (!max.getActionManager().hasSkillWorking()) {
			max.setBasAnim(2498);
			max.repeatAction(5, count -> {
				if (World.getObjectWithType(currentBonfire, currentBonfire.getType()) != currentBonfire) {
					max.anim(-1);
					max.setBasAnim(-1);
					currentBonfire = null;
					return false;
				}
				max.faceObject(currentBonfire);
				max.anim(16703);
				max.spotAnim(3135);
				return true;
			});
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
