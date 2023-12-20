package com.rs.game.content.minigames.trawler;

import com.rs.game.content.skills.magic.Magic;
import com.rs.game.model.entity.player.Controller;

public class FishingTrawlerCrashedController extends Controller {

	@Override
	public void start() {

	}

	@Override
	public void onTeleported(Magic.TeleType type) {
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
