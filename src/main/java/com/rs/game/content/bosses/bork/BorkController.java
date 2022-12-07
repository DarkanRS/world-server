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
package com.rs.game.content.bosses.bork;

import com.rs.game.World;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.ForceTalk;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.pathing.Direction;
import com.rs.game.model.entity.player.Controller;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.object.GameObject;
import com.rs.lib.game.WorldTile;

public class BorkController extends Controller {
	int stage = 0;
	public transient int borkStage;
	public transient NPC bork;

	public BorkController(int borkStage, NPC bork) {
		this.borkStage = borkStage;
		this.bork = bork;
	}

	@Override
	public void start() {
		process();
	}

	@Override
	public void process() {
		if (borkStage == 0) {
			if(player.getTempAttribs().getB("justSawBorkCutscene")) {
				player.setNextWorldTile(WorldTile.of(3115, 5528, 0));
				removeController();
				return;
			}
			if (stage == 0) {
				player.getPackets().setBlockMinimapState(2);
				player.lock();
				player.getTempAttribs().setB("CUTSCENE_INTERFACE_CLOSE_DISABLED", true);
				player.getInterfaceManager().sendBackgroundInterfaceOverGameWindow(115);
			}
			if (stage == 5) {
				player.setNextWorldTile(WorldTile.of(3115, 5528, 0));
				sendInterfaces();
			}
			if (stage == 18) {
				player.getInterfaceManager().closeInterfacesOverGameWindow();
				//player.startConversation(new DagonHai(), 7137, player, -1);//Rewrite this from a YouTube video.
				player.getTempAttribs().setB("CUTSCENE_INTERFACE_CLOSE_DISABLED", false);
				player.resetReceivedHits();
				player.unlock();
				player.getPackets().setBlockMinimapState(0);
				player.getTempAttribs().setB("justSawBorkCutscene", true);
				removeController();
			}
		} else if (borkStage == 1)
			if (stage == 4) {
				sendInterfaces();
				bork.setCantInteract(true);
			} else if (stage == 14) {
				World.spawnNPC(7135, WorldTile.of(bork.getTile(), 1), -1, true, true).setForceAgressive(true);
				World.spawnNPC(7135, WorldTile.of(bork.getTile(), 1), -1, true, true).setForceAgressive(true);
				World.spawnNPC(7135, WorldTile.of(bork.getTile(), 1), -1, true, true).setForceAgressive(true);
				player.getInterfaceManager().closeInterfacesOverGameWindow();
				bork.setCantInteract(false);
				bork.setNextForceTalk(new ForceTalk("Destroy the intruder, my Legions!"));
				removeController();
			}
		stage++;
	}

	@Override
	public void sendInterfaces() {
		if (borkStage == 0)
			player.getInterfaceManager().sendBackgroundInterfaceOverGameWindow(692);
		else if (borkStage == 1)
			for (Entity t : bork.getPossibleTargets()) {
				Player pl = (Player) t;
				pl.getInterfaceManager().sendBackgroundInterfaceOverGameWindow(691);
			}
	}

	@Override
	public boolean processMagicTeleport(WorldTile toTile) {
		return true;
	}

	@Override
	public boolean processObjectClick1(GameObject object) {
		if (object.getId() == 29537)
			removeController();
		return true;
	}

	@Override
	public boolean keepCombating(Entity target) {
		if (borkStage == 1 && stage == 4)
			return false;
		return true;
	}

	@Override
	public boolean canEquip(int slotId, int itemId) {
		if (borkStage == 1 && stage == 4)
			return false;
		return true;
	}

	@Override
	public boolean canAttack(Entity target) {
		if (borkStage == 1 && stage == 4)
			return false;
		return true;
	}

	@Override
	public boolean canMove(Direction dir) {
		if (borkStage == 1 && stage == 4)
			return false;
		return true;
	}

}
