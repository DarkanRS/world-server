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
package com.rs.game.content.minigames.creations;

import com.rs.game.model.entity.player.Controller;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.object.GameObject;
import com.rs.lib.game.GroundItem;
import com.rs.lib.game.Tile;

public class StealingCreationLobbyController extends Controller {

	@Override
	public void start() {
		sendInterfaces();
	}

	@Override
	public void sendInterfaces() {
		player.getInterfaceManager().sendOverlay(804, false);
		StealingCreationLobby.updateTeamInterface(player, false);
	}

	@Override
	public boolean canTakeItem(GroundItem item) {
		return false;
	}

	@Override
	public boolean processObjectClick1(GameObject object) {
		if (object.getId() == 39508 || object.getId() == 39509) {
			player.getControllerManager().forceStop();
			climbOverStile(player, object, false);
			return false;
		}
		return true;
	}

	public static void climbOverStile(final Player player, final GameObject object, final boolean enterance) {
		player.lock(3);
		if (enterance) {
			if (!StealingCreationLobby.enterTeamLobby(player, object.getId() == 39509))
				return;
		} else
			StealingCreationLobby.removePlayer(player);
		player.forceMove(Tile.of(enterance ? object.getX() : object.getX() + 2, object.getY(), object.getPlane()), 1560, 5, 60);
	}

	@Override
	public boolean processMagicTeleport(Tile toTile) {
		player.simpleDialogue("A magical force prevents you from teleporting from the arena.");
		return false;
	}

	@Override
	public boolean processItemTeleport(Tile toTile) {
		player.simpleDialogue("A magical force prevents you from teleporting from the arena.");
		return false;
	}

	@Override
	public void magicTeleported(int type) {
		StealingCreationLobby.removePlayer(player);
	}

	@Override
	public void forceClose() {
		player.getInterfaceManager().removeOverlay();
		StealingCreationLobby.removePlayer(player);
	}

	@Override
	public boolean logout() {
		StealingCreationLobby.removePlayer(player);
		player.setNextTile(Helper.EXIT);
		return true;
	}

	@Override
	public boolean login() {
		StealingCreationLobby.removePlayer(player);
		player.setNextTile(Helper.EXIT);
		return true;
	}

}
