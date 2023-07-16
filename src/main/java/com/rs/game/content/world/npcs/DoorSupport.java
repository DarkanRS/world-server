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
package com.rs.game.content.world.npcs;

import com.rs.game.World;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.object.GameObject;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Logger;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.annotations.ServerStartupEvent;
import com.rs.plugin.handlers.NPCInstanceHandler;
import com.rs.utils.Ticks;

@PluginEventHandler
public class DoorSupport extends NPC {

	public DoorSupport(int id, Tile tile) {
		super(id, tile, true);
		setCantFollowUnderCombat(true);
	}

	@Override
	public void processNPC() {
		cancelFaceEntityNoCheck();
	}

	public boolean canDestroy(Player player) {
		if (getId() == 2446)
			return player.getY() < getY();
		if (getId() == 2440)
			return player.getY() > getY();
			return player.getX() > getX();
	}

	@Override
	public boolean ignoreWallsWhenMeleeing() {
		return true;
	}

	@Override
	public void sendDeath(Entity killer) {
		setNextNPCTransformation(getId() + 1);
		final GameObject door = World.getObjectWithId(getTile(), 8967);
		if (door != null)
			World.removeObject(door);
		WorldTasks.schedule(Ticks.fromSeconds(60), () -> {
			try {
				setNextNPCTransformation(getId() - 1);
				reset();
				if (door != null)
					World.spawnObject(door);
			} catch (Throwable e) {
				Logger.handle(DoorSupport.class, "DoorSupport.sendDeath", e);
			}
		});
	}
	
	@ServerStartupEvent
	public static void addLoSOverrides() {
		Entity.addLOSOverrides(2440, 2443, 2446);
	}

	public static NPCInstanceHandler toFunc = new NPCInstanceHandler(new Object[] { 2440, 2443, 2446 }, (npcId, tile) -> new DoorSupport(npcId, tile));
}
