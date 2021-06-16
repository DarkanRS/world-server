package com.rs.game.player.cutscenes.actions;

import com.rs.game.player.Player;

public class CutsceneCodeAction extends CutsceneAction {

	private Runnable runnable;

	public CutsceneCodeAction(Runnable runnable, int actionDelay) {
		super(-1, actionDelay);
		this.runnable = runnable;
	}

	@Override
	public void process(Player player, Object[] cache) {
		runnable.run();
	}

}
