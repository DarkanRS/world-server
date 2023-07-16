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
package com.rs.game.content.skills.hunter.puropuro;

import com.rs.game.content.skills.magic.Magic;
import com.rs.game.model.entity.player.Controller;
import com.rs.game.model.object.GameObject;
import com.rs.lib.game.Tile;
import com.rs.plugin.annotations.PluginEventHandler;

@PluginEventHandler
public class PuroPuroController extends Controller {

	private Tile entranceTile;

	public PuroPuroController(Tile tile) {
		this.entranceTile = tile;
	}

	@Override
	public void start() {
		player.getPackets().setBlockMinimapState(2);
		player.getInterfaceManager().sendOverlay(169);
	}

	@Override
	public void forceClose() {
		player.getPackets().setBlockMinimapState(0);
		player.getInterfaceManager().removeOverlay();
	}

	@Override
	public void magicTeleported(int type) {
		player.getControllerManager().forceStop();
	}

	@Override
	public boolean logout() {
		return false;
	}

	@Override
	public boolean login() {
		start();
		return false;
	}

	@Override
	public boolean processObjectClick1(GameObject object) {
		switch (object.getId()) {
			case 25014:
				player.getControllerManager().forceStop();
				Magic.sendTeleportSpell(player, 6601, -1, 1118, -1, 0, 0, entranceTile, 9, false, Magic.OBJECT_TELEPORT, null);
				return true;
		}
		return true;
	}

}