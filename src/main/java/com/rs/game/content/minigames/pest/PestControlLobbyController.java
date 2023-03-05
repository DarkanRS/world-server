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
package com.rs.game.content.minigames.pest;

import com.rs.game.model.entity.player.Controller;
import com.rs.game.model.object.GameObject;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Utils;

public final class PestControlLobbyController extends Controller {

	private int landerId;

	public PestControlLobbyController(int landerId) {
		this.landerId = landerId;
	}

	@Override
	public void start() {

	}

	@Override
	public void sendInterfaces() {
		player.getPackets().setIFText(407, 3, Utils.fixChatMessage(Lander.getLanders()[landerId].toString()));
		int minutesLeft = (Lander.getLanders()[landerId].getTimer().getMinutes());
		player.getPackets().setIFText(407, 13, "Next Departure: " + minutesLeft + " minutes " + (!(minutesLeft % 2 == 0) ? " 30 seconds" : ""));
		player.getPackets().setIFText(407, 14, "Player's Ready: " + Lander.getLanders()[landerId].getPlayers().size());
		player.getPackets().setIFText(407, 16, "Commendations: " + player.getPestPoints());
		player.getInterfaceManager().sendOverlay(407);
	}

	@Override
	public void magicTeleported(int teleType) {
		player.getControllerManager().forceStop();
	}

	@Override
	public boolean processMagicTeleport(Tile toTile) {
		player.getControllerManager().forceStop();
		return true;
	}

	@Override
	public boolean processItemTeleport(Tile toTile) {
		player.getControllerManager().forceStop();
		return true;
	}

	@Override
	public void forceClose() {
		player.getInterfaceManager().removeOverlay();
		Lander.getLanders()[landerId].exitLander(player);
	}

	@Override
	public boolean logout() {
		Lander.getLanders()[landerId].remove(player);// to stop the timer in the
		// lander and prevent
		// future errors
		return false;
	}

	@Override
	public boolean canSummonFamiliar() {
		player.sendMessage("You feel it's best to keep your Familiar away during this game.");
		return false;
	}

	@Override
	public boolean processObjectClick1(GameObject object) {
		switch (object.getId()) {
		case 14314:
		case 25629:
		case 25630:
			player.startConversation(new LanderD(player));
			return true;
		}
		return true;
	}
}
