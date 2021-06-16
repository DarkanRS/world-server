package com.rs.game;

import com.rs.cache.loaders.ObjectDefinitions;
import com.rs.game.object.GameObject;
import com.rs.game.player.Player;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.lib.game.Projectile;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;

public class WorldProjectile extends Projectile {

	public WorldProjectile(WorldTile from, WorldTile to, int spotAnimId, int startHeight, int endHeight, int startTime, int endTime, int slope, int angle, Runnable task) {
		super(from, to, spotAnimId, startHeight, endHeight, startTime, endTime, slope, angle);
		Entity fromE = from instanceof Entity ? (Entity) from : null;
		this.sourceId = fromE == null ? 0 : (fromE instanceof Player ? -(fromE.getIndex() + 1) : fromE.getIndex() + 1);
		Entity toE = to instanceof Entity ? (Entity) to : null;
		this.lockOnId = toE == null ? 0 : (toE instanceof Player ? -(toE.getIndex() + 1) : toE.getIndex() + 1);
		
		if (task != null) {
			WorldTasksManager.schedule(new WorldTask() {
				@Override
				public void run() {
					task.run();
				}
			}, getTaskDelay());
		}
		
		if (from instanceof Entity)
			fromSizeX = fromSizeY = ((Entity) from).getSize();
		else if (from instanceof GameObject) {
			ObjectDefinitions defs = ((GameObject) from).getDefinitions();
			fromSizeX = defs.getSizeX();
			fromSizeY = defs.getSizeY();
		} else
			fromSizeX = fromSizeY = 1;
		if (to instanceof Entity)
			toSizeX = toSizeY = ((Entity) to).getSize();
		else if (to instanceof GameObject) {
			ObjectDefinitions defs = ((GameObject) to).getDefinitions();
			toSizeX = defs.getSizeX();
			toSizeY = defs.getSizeY();
		} else
			toSizeX = toSizeY = 1;
		fromSizeX -= 1;
		fromSizeY -= 1;
		toSizeX -= 1;
		toSizeY -= 1;
	}

	public int getTaskDelay() {
		return Utils.projectileTimeToCycles(getEndTime()) - 1;
	}
}
