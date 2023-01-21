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
package com.rs.game.content.minigames.fightpits;

import com.rs.game.model.entity.player.Controller;
import com.rs.game.model.object.GameObject;

public class FightPitsLobbyController extends Controller {

	@Override
	public void start() {

	}

	@Override
	public boolean login() {
		FightPits.enterLobby(player, true);
		return false;
	}

	@Override
	public void magicTeleported(int type) {
		FightPits.leaveLobby(player, 2);
	}

	@Override
	public boolean processObjectClick1(GameObject object) {
		if (object.getId() == 68223) {
			FightPits.leaveLobby(player, 1);
			return false;
		}
		if (object.getId() == 68222) {
			player.sendMessage("The heat prevents you passing through.");
			return false;
		}
		if (object.getId() == 68220) {
			player.getActionManager().setAction(new FightPitsViewingOrb());
			return false;
		}
		return true;
	}

	@Override
	public boolean sendDeath() {
		FightPits.leaveLobby(player, 2);
		return true;
	}

	@Override
	public boolean logout() {
		FightPits.leaveLobby(player, 0);
		return false;
	}

	@Override
	public void forceClose() {
		FightPits.leaveLobby(player, 2);
	}

}
