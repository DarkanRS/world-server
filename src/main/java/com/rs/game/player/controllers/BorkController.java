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
package com.rs.game.player.controllers;

import com.rs.game.Entity;
import com.rs.game.ForceTalk;
import com.rs.game.World;
import com.rs.game.npc.NPC;
import com.rs.game.pathing.Direction;
import com.rs.game.player.Player;
import com.rs.game.player.content.skills.magic.Magic;
import com.rs.game.player.dialogues.DagonHai;
import com.rs.lib.game.WorldTile;

public class BorkController extends Controller {

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

	int stage = 0;

	@Override
	public void process() {
		if (borkStage == 0) {
			if (stage == 0)
				Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(3114, 5528, 0));
			if (stage == 5)
				sendInterfaces();
			if (stage == 18) {
				player.getInterfaceManager().removeOverlay();
				player.getDialogueManager().execute(new DagonHai(), 7137, player, -1);
				player.sendMessage("The choas teleporter transports you to an unknown portal.");
				removeController();
			}
		} else if (borkStage == 1)
			if (stage == 4) {
				sendInterfaces();
				bork.setCantInteract(true);
			} else if (stage == 14) {
				World.spawnNPC(7135, new WorldTile(bork.getTile(), 1), -1, true, true);
				World.spawnNPC(7135, new WorldTile(bork.getTile(), 1), -1, true, true);
				World.spawnNPC(7135, new WorldTile(bork.getTile(), 1), -1, true, true);
				player.getInterfaceManager().removeOverlay();
				bork.setCantInteract(false);
				bork.setNextForceTalk(new ForceTalk("Destroy the intruder, my Legions!"));
				removeController();
			}
		stage++;
	}

	@Override
	public void sendInterfaces() {
		if (borkStage == 0)
			player.getInterfaceManager().setOverlay(692);
		else if (borkStage == 1)
			for (Entity t : bork.getPossibleTargets()) {
				Player pl = (Player) t;
				pl.getInterfaceManager().setOverlay(691);
			}
	}

	@Override
	public boolean processMagicTeleport(WorldTile toTile) {
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
