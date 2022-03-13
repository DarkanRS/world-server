package com.rs.game.model.entity.player.controllers;

import com.rs.game.content.minigames.trawler.FishingTrawler;
import com.rs.lib.game.WorldTile;

public class FishingTrawlerLobbyController extends Controller {

	@Override
	public void start() {

	}

	public boolean login() {
		leaveLobby();
		player.setNextWorldTile(new WorldTile(2676, 3170, 0));
		return true;
	}

	@Override
	public boolean logout() {
		leaveLobby();
		player.setLocation(new WorldTile(2676, 3170, 0));
		return true;
	}

	@Override
	public void magicTeleported(int type) {
		leaveLobby();
	}

	public void leaveLobby() {
		FishingTrawler.getInstance().removeLobbyPlayer(player);
	}
}
