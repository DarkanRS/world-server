package com.rs.game.player.controllers;

import com.rs.game.player.content.minigames.trawler.FishingTrawler;

public class FishingTrawlerCrashedController extends Controller {

	@Override
	public void start() {

	}

	@Override
	public void magicTeleported(int type) {
		player.getAppearance().setBAS(-1);
		player.getControllerManager().forceStop();
	}

	@Override
	public boolean login() {
		player.getAppearance().setBAS(152);
		return false;
	}

	public boolean logout() {
		return false;
	}
}
