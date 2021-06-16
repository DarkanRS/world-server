package com.rs.game.player.managers;

import com.rs.game.player.Player;
import com.rs.game.player.cutscenes.Cutscene;

public final class CutscenesManager {

	private Player player;
	private Cutscene cutscene;

	public CutscenesManager(Player player) {
		this.player = player;
	}

	public void process() {
		if (cutscene == null)
			return;
		if (cutscene.process(player))
			return;
		cutscene = null;
	}

	public void logout() {
		if (hasCutscene())
			cutscene.logout(player);
	}

	public boolean hasCutscene() {
		return cutscene != null;
	}

	public boolean play(Cutscene cutscene) {
		if (hasCutscene()) {
			return false;
		}
		if (cutscene == null) {
			return false;
		}
		cutscene.createCache(player);
		this.cutscene = cutscene;
		return true;
	}

}
