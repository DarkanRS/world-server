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
package com.rs.game.npc.others;

import java.util.List;

import com.rs.game.World;
import com.rs.game.npc.NPC;
import com.rs.game.object.GameObject;
import com.rs.game.pathing.RouteEvent;
import com.rs.game.player.Player;
import com.rs.game.player.content.skills.hunter.BoxHunterType;
import com.rs.game.player.content.skills.hunter.BoxTrapType;
import com.rs.game.player.content.skills.hunter.traps.BoxStyleTrap;
import com.rs.game.player.content.skills.hunter.traps.BoxStyleTrap.Status;
import com.rs.lib.Constants;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCInstanceHandler;

@PluginEventHandler
public class BoxHunterNPC extends NPC {

	private BoxTrapType trap;
	private BoxHunterType type;

	private int captureTicks;

	public BoxHunterNPC(BoxHunterType type, int id, WorldTile tile, boolean spawned) {
		super(id, tile, spawned);
		this.type = type;
		trap = type.getTrap();
		setIgnoreNPCClipping(true);
	}

	@Override
	public void processNPC() {
		super.processNPC();
		if (timeSinceSpawned() < 3000)
			return;
		if (captureTicks > 0 || hasFinished()) {
			if (captureTicks > 20) {
				setRouteEvent(null);
				captureTicks = 0;
				return;
			}
			captureTicks++;
			return;
		}
		List<GameObject> objects = World.getRegion(getRegionId()).getSpawnedObjects();
		if (objects == null)
			return;
		for (final GameObject o : objects) {
			if (!(o instanceof BoxStyleTrap trapO))
				continue;
			Player owner = trapO.getOwner();
			if (owner == null || owner.getSkills().getLevel(Constants.HUNTER) < type.getLevel() || trapO.getStatus() != Status.IDLE || trapO.getLife() < 10 || trapO.getLife() > 75 || trapO.getTrapType() != trap || trapO.getBait() != type.getBaitId() || !withinDistance(o, 2))
				continue;
			captureTicks = 1;
			trapO.setStatus(Status.CATCHING);
			resetWalkSteps();
			setRouteEvent(new RouteEvent(trapO, () -> {
				if (trapO.getOwner() == null || trapO.isDestroyed() || trapO.getTrapType() != trap)
					return;
				boolean success = Utils.skillSuccess(owner.getSkills().getLevel(Constants.HUNTER), type.getRate1(), type.getRate99());
				faceObject(trapO);
				setNextAnimation(success ? type.getAnimSuccess() : type.getAnimFail());
				trapO.handleCatch(this, success);
			}));
			break;
		}
	}

	@Override
	public void setRespawnTask() {
		super.setRespawnTask(4);
	}

	public BoxHunterType getType() {
		return type;
	}

	public static NPCInstanceHandler toFunc = new NPCInstanceHandler(BoxHunterType.ID_MAP.keySet().toArray()) {
		@Override
		public NPC getNPC(int npcId, WorldTile tile) {
			return new BoxHunterNPC(BoxHunterType.forId(npcId), npcId, tile, false);
		}
	};
}