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
//  Copyright © 2021 Trenton Kress
//  This file is part of project: Darkan
//
package com.rs.game.content.world;

import com.rs.cache.loaders.ObjectType;
import com.rs.game.World;
import com.rs.game.content.skills.dungeoneering.DamonheimController;
import com.rs.game.content.world.areas.wilderness.WildernessController;
import com.rs.game.content.world.doors.DoorPair;
import com.rs.game.model.entity.pathing.Direction;
import com.rs.game.model.object.GameObject;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Tile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.PlayerStepEvent;
import com.rs.plugin.handlers.ObjectClickHandler;
import com.rs.plugin.handlers.PlayerStepHandler;

@PluginEventHandler
public class GuardBypasses {

	static {
		PlayerStepEvent.registerMethod(PlayerStepEvent.class, createDoubleGuardBypass(Tile.of(3146, 3336, 0),
		Tile.of(3145, 3336, 0), Tile.of(3147, 3336, 0), 85, 24)); //Draynor Manor East

		PlayerStepEvent.registerMethod(PlayerStepEvent.class, createDoubleGuardBypass(Tile.of(3069, 3276, 0),
		Tile.of(3070, 3275, 0), Tile.of(3070, 3277, 0), 85, 24)); //Draynor West

		PlayerStepEvent.registerMethod(PlayerStepEvent.class, createDoubleGuardBypass(Tile.of(3077, 3333, 0),
		Tile.of(3076, 3333, 0), Tile.of(3078, 3333, 0), 85, 24)); //Draynor Manor West

		PlayerStepEvent.registerMethod(PlayerStepEvent.class, createDoubleGuardBypass(Tile.of(3108, 3420, 0),
		Tile.of(3109, 3419, 0), Tile.of(3109, 3421, 0), 88, 87)); //Varrock to Barb Village

		PlayerStepEvent.registerMethod(PlayerStepEvent.class, createDoubleGuardBypass(Tile.of(3138, 3467, 0),
		Tile.of(3137, 3467, 0), Tile.of(3139, 3467, 0), 85, 24)); //GE to Edgeville

		PlayerStepEvent.registerMethod(PlayerStepEvent.class, createDoubleGuardBypass(Tile.of(3260, 3173, 0),
		Tile.of(3261, 3172, 0), Tile.of(3261, 3174, 0), 88, 87)); //Lumbridge swamp to Al Kharid

		PlayerStepEvent.registerMethod(PlayerStepEvent.class, createDoubleGuardBypass(Tile.of(3283, 3329, 0),
		Tile.of(3282, 3330, 0), Tile.of(3284, 3330, 0), 88, 87)); //Al Kharid to Varrock

		PlayerStepEvent.registerMethod(PlayerStepEvent.class, createDoubleGuardBypass(Tile.of(3292, 3385, 0),
		Tile.of(3291, 3385, 0), Tile.of(3293, 3385, 0), 4640, 4636)); //Varrock East to Wall
	}

	public static PlayerStepHandler createDoubleGuardBypass(Tile bottomLeftTile, Tile guard1, Tile guard2, int openAnim, int closeAnim) {
		return new PlayerStepHandler(new Tile[] { bottomLeftTile, bottomLeftTile.transform(0, 1), bottomLeftTile.transform(1, 0), bottomLeftTile.transform(1, 1) }, e -> {
			e.getStep().setCheckClip(false);
			e.getPlayer().setRunHidden(false);

			World.sendObjectAnimation(World.getObject(guard1, ObjectType.SCENERY_INTERACT), new Animation(openAnim));
			World.sendObjectAnimation(World.getObject(guard2, ObjectType.SCENERY_INTERACT), new Animation(openAnim));
			WorldTasks.delay(3, () -> {
				e.getPlayer().setRunHidden(true);
				World.sendObjectAnimation(World.getObject(guard1, ObjectType.SCENERY_INTERACT), new Animation(closeAnim));
				World.sendObjectAnimation(World.getObject(guard2, ObjectType.SCENERY_INTERACT), new Animation(closeAnim));
			});
		});
	}

	public static PlayerStepHandler gatesToExamCentreStep = new PlayerStepHandler(new Tile[] { Tile.of(3311, 3331, 0), Tile.of(3311, 3332, 0), Tile.of(3312, 3331, 0), Tile.of(3312, 3332, 0) }, e -> {
		GameObject gate1 = World.getObjectWithId(Tile.of(3312, 3331, 0), 45856);
		GameObject gate2 = World.getObjectWithId(Tile.of(3312, 3332, 0), 45857);
		if (gate1 != null && gate2 != null) {
			World.spawnObjectTemporary(new GameObject(gate1).setIdNoRefresh(83), 3, true);
			World.spawnObjectTemporary(new GameObject(gate2).setIdNoRefresh(83), 3, true);
			World.spawnObjectTemporary(new GameObject(DoorPair.getOpposingDoor(e.getPlayer(), gate1), gate1.getType(), gate1.getRotation(-1), gate1.getTile().transform(-1, 0, 0)), 3, true);
			World.spawnObjectTemporary(new GameObject(DoorPair.getOpposingDoor(e.getPlayer(), gate2), gate2.getType(), gate2.getRotation(1), gate2.getTile().transform(-1, 0, 0)), 3, true);
		}
		e.getStep().setCheckClip(false);
		e.getPlayer().setRunHidden(false);
		World.sendObjectAnimation(World.getObject(Tile.of(3311, 3332, 0), ObjectType.SCENERY_INTERACT), new Animation(4640));
		World.sendObjectAnimation(World.getObject(Tile.of(3311, 3330, 0), ObjectType.SCENERY_INTERACT), new Animation(4640));
		WorldTasks.delay(3, () -> {
			e.getPlayer().setRunHidden(true);
			World.sendObjectAnimation(World.getObject(Tile.of(3311, 3332, 0), ObjectType.SCENERY_INTERACT), new Animation(4636));
			World.sendObjectAnimation(World.getObject(Tile.of(3311, 3330, 0), ObjectType.SCENERY_INTERACT), new Animation(4636));
		});
	});

	public static PlayerStepHandler varrockEastGates = new PlayerStepHandler(new Tile[] { Tile.of(3273, 3428, 0), Tile.of(3273, 3429, 0), Tile.of(3274, 3428, 0), Tile.of(3274, 3429, 0) }, e -> {
		GameObject gate1 = World.getObjectWithId(Tile.of(3273, 3429, 0), 45853);
		GameObject gate2 = World.getObjectWithId(Tile.of(3273, 3428, 0), 45855);
		if (gate1 != null && gate2 != null) {
			World.spawnObjectTemporary(new GameObject(gate1).setIdNoRefresh(83), 3, true);
			World.spawnObjectTemporary(new GameObject(gate2).setIdNoRefresh(83), 3, true);
			World.spawnObjectTemporary(new GameObject(gate1.getId(), gate1.getType(), gate1.getRotation(-1), gate1.getTile().transform(1, 0, 0)), 3, true);
			World.spawnObjectTemporary(new GameObject(gate2.getId(), gate2.getType(), gate2.getRotation(1), gate2.getTile().transform(1, 0, 0)), 3, true);
		}
		e.getStep().setCheckClip(false);
		e.getPlayer().setRunHidden(false);
		WorldTasks.delay(3, () -> {
			e.getPlayer().setRunHidden(true);
		});
	});

	public static PlayerStepHandler varrockNorthDoor = new PlayerStepHandler(new Tile[] { Tile.of(3245, 3501, 0), Tile.of(3245, 3502, 0) }, e -> {
		GameObject door = World.getObjectWithId(Tile.of(3245, 3501, 0), 45853);
		if (door != null) {
			World.spawnObjectTemporary(new GameObject(door).setIdNoRefresh(83), 3, true);
			World.spawnObjectTemporary(new GameObject(door.getId(), door.getType(), door.getRotation(-1), door.getTile().transform(0, 1, 0)), 3, true);
		}
		e.getStep().setCheckClip(false);
		e.getPlayer().setRunHidden(false);
		WorldTasks.delay(3, () -> e.getPlayer().setRunHidden(true));
	});

	public static PlayerStepHandler daemonheimWildyEntrance = new PlayerStepHandler(new Tile[] { Tile.of(3384, 3615, 0), Tile.of(3385, 3615, 0) }, e -> {
		e.getStep().setCheckClip(false);
		e.getPlayer().setRunHidden(false);
		if (e.getStep().getX() <= 3384 && e.getStep().getDir() == Direction.WEST) {
			e.getPlayer().getControllerManager().forceStop();
			e.getPlayer().getControllerManager().startController(new WildernessController());
		} else if (e.getStep().getX() > 3384 && e.getStep().getDir() == Direction.EAST) {
			e.getPlayer().getControllerManager().forceStop();
			e.getPlayer().getControllerManager().startController(new DamonheimController());
		}
		World.sendObjectAnimation(World.getObject(Tile.of(3385, 3614, 0), ObjectType.SCENERY_INTERACT), new Animation(1366));
		WorldTasks.delay(3, () -> {
			e.getPlayer().setRunHidden(true);
			World.sendObjectAnimation(World.getObject(Tile.of(3385, 3614, 0), ObjectType.SCENERY_INTERACT), new Animation(1365));
		});
	});

	public static ObjectClickHandler uselessDoors = new ObjectClickHandler(new Object[] {  45854, 45856, 45857, 45858, 45859 }, e -> { });

	public static ObjectClickHandler handleNorthAndEastVarrockGates = new ObjectClickHandler(new Object[] { 45853, 45855 }, e -> {
		GameObject gate1 = World.getObjectWithId(Tile.of(3273, 3429, 0), 45853);
		GameObject gate2 = World.getObjectWithId(Tile.of(3273, 3428, 0), 45855);
		if (gate1 != null && gate2 != null) {
			World.spawnObjectTemporary(new GameObject(gate1).setIdNoRefresh(83), 3, true);
			World.spawnObjectTemporary(new GameObject(gate2).setIdNoRefresh(83), 3, true);
			World.spawnObjectTemporary(new GameObject(gate1.getId(), gate1.getType(), gate1.getRotation(-1), gate1.getTile().transform(1, 0, 0)), 3, true);
			World.spawnObjectTemporary(new GameObject(gate2.getId(), gate2.getType(), gate2.getRotation(1), gate2.getTile().transform(1, 0, 0)), 3, true);
		}
		e.getPlayer().lock(2);
		if(e.getObject().getRotation() == 2) //East
			e.getPlayer().addWalkSteps(e.getPlayer().getX() > e.getObject().getX() ? e.getObject().getTile().transform(-1, 0) : e.getObject().getTile().transform(1, 0), 3, false);
		if(e.getObject().getRotation() == 1) //North
			e.getPlayer().addWalkSteps(e.getPlayer().getY() > e.getObject().getY() ? e.getObject().getTile().transform(0, -1) : e.getObject().getTile().transform(0, 1), 3, false);
	});

}