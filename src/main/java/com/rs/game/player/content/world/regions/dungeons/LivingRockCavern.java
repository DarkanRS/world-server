package com.rs.game.player.content.world.regions.dungeons;

import com.rs.cache.loaders.ObjectType;
import com.rs.cores.CoresManager;
import com.rs.game.World;
import com.rs.game.object.GameObject;
import com.rs.lib.util.Logger;
import com.rs.lib.util.Utils;
import com.rs.utils.Ticks;

public final class LivingRockCavern {

	private static enum Rocks {
		COAL_ROCK_1(new GameObject(5999, ObjectType.SCENERY_INTERACT, 1, 3690, 5146, 0)), 
		COAL_ROCK_2(new GameObject(5999, ObjectType.SCENERY_INTERACT, 2, 3690, 5125, 0)), 
		COAL_ROCK_3(new GameObject(5999, ObjectType.SCENERY_INTERACT, 0, 3687, 5107, 0)), 
		COAL_ROCK_4(new GameObject(5999, ObjectType.SCENERY_INTERACT, 1, 3674, 5098, 0)), 
		COAL_ROCK_5(new GameObject(5999, ObjectType.SCENERY_INTERACT, 2, 3664, 5090, 0)), 
		COAL_ROCK_6(new GameObject(5999, ObjectType.SCENERY_INTERACT, 3, 3615, 5090, 0)), 
		COAL_ROCK_7(new GameObject(5999, ObjectType.SCENERY_INTERACT, 1, 3625, 5107, 0)), 
		COAL_ROCK_8(new GameObject(5999, ObjectType.SCENERY_INTERACT, 3, 3647, 5142, 0)), 
		GOLD_ROCK_1(new GameObject(45076, ObjectType.SCENERY_INTERACT, 1, 3667, 5075, 0)), 
		GOLD_ROCK_2(new GameObject(45076, ObjectType.SCENERY_INTERACT, 0, 3637, 5094, 0)), 
		GOLD_ROCK_3(new GameObject(45076, ObjectType.SCENERY_INTERACT, 0, 3677, 5160, 0)), 
		GOLD_ROCK_4(new GameObject(45076, ObjectType.SCENERY_INTERACT, 1, 3629, 5148, 0));

		private Rocks(GameObject rock) {
			this.rock = rock;
		}

		private GameObject rock;
	}

	private static void respawnRock(final Rocks rock) {
		World.spawnObject(rock.rock);
		CoresManager.schedule(new Runnable() {

			@Override
			public void run() {
				try {
					removeRock(rock);
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}
		}, Ticks.fromMinutes(Utils.random(8) + 3));
	}

	private static void removeRock(final Rocks rock) {
		World.removeObject(rock.rock);
		CoresManager.schedule(new Runnable() {
			@Override
			public void run() {
				try {
					respawnRock(rock);
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}

		}, Ticks.fromMinutes(3));
	}

	public static void init() {
		for (Rocks rock : Rocks.values())
			respawnRock(rock);
	}
}
