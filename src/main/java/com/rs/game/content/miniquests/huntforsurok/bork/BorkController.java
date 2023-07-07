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
package com.rs.game.content.miniquests.huntforsurok.bork;

import com.rs.game.World;
import com.rs.game.content.miniquests.huntforsurok.ChaosTunnels;
import com.rs.game.map.instance.Instance;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.ForceTalk;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.pathing.Direction;
import com.rs.game.model.entity.player.Controller;
import com.rs.game.model.entity.player.InstancedController;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.object.GameObject;
import com.rs.lib.game.Tile;

public class BorkController extends InstancedController {
	public BorkController() {
		super(Instance.of(ChaosTunnels.PortalPair.BORK.tile1, 8, 8).setEntranceOffset(new int[] { 43, 24, 0 }));
	}

	@Override
	public void onBuildInstance() {
		player.lock();
		getInstance().copyMapAllPlanes(384, 688).thenAccept(b -> player.playCutscene(cs -> {
			cs.fadeIn(5);
			cs.hideMinimap(true);
			cs.action(() -> {
				getInstance().teleportLocal(player, 43, 24, 0);
				player.setForceMultiArea(true);
				cs.setEndTile(Tile.of(cs.getX(43), cs.getY(24), 0));
			});
			cs.action(() -> player.getInterfaceManager().sendForegroundInterfaceOverGameWindow(692));
			cs.delay(15);
			cs.action(() -> player.getInterfaceManager().closeInterfacesOverGameWindow());
			cs.fadeOut(5);
			cs.action(() -> {
				World.spawnNPC(7134, Tile.of(cs.getX(27), cs.getY(33), 0), -1, true, true, true).setForceMultiArea(true);
				player.resetReceivedHits();
				player.unlock();
				player.getPackets().setBlockMinimapState(0);
			});
		}));
	}

	@Override
	public void onDestroyInstance() {
		player.setForceMultiArea(false);
		player.unlock();
	}

	@Override
	public boolean processMagicTeleport(Tile toTile) {
		return true;
	}

	@Override
	public boolean processObjectClick1(GameObject object) {
		if (object.getId() == 29537) {
			player.setNextTile(getInstance().getReturnTo());
			player.getControllerManager().forceStop();
		}
		return true;
	}
}
