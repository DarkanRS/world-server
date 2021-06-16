package com.rs.game.player.controllers;

import com.rs.game.object.GameObject;
import com.rs.game.player.actions.FightPitsViewingOrb;
import com.rs.game.player.content.minigames.FightPits;

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
		} else if (object.getId() == 68222) {
			player.sendMessage("The heat prevents you passing through.");
			return false;
		} else if (object.getId() == 68220) {
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
