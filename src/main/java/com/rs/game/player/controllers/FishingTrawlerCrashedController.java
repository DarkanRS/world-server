package com.rs.game.player.controllers;

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
