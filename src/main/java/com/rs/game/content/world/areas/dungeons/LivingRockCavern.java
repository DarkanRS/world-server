// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.
//
//  Copyright (C) 2021 Trenton Kress
//  This file is part of project: Darkan
//
package com.rs.game.content.world.areas.dungeons;

import com.rs.cache.loaders.ObjectType;
import com.rs.game.World;
import com.rs.game.model.object.GameObject;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.util.Logger;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.annotations.ServerStartupEvent;
import com.rs.utils.Ticks;

@PluginEventHandler
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
		WorldTasks.schedule(Ticks.fromMinutes(Utils.random(8) + 3), () -> {
			try {
				removeRock(rock);
			} catch (Throwable e) {
				Logger.handle(LivingRockCavern.class, "respawnRock", e);
			}
		});
	}

	private static void removeRock(final Rocks rock) {
		World.removeObject(rock.rock);
		WorldTasks.schedule(Ticks.fromMinutes(3), () -> {
			try {
				respawnRock(rock);
			} catch (Throwable e) {
				Logger.handle(LivingRockCavern.class, "removeRock", e);
			}
		});
	}

	@ServerStartupEvent
	public static void init() {
		for (Rocks rock : Rocks.values())
			respawnRock(rock);
	}
}
