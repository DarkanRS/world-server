package com.rs.game.player.actions.interactions;

import com.rs.game.player.Player;

public abstract class Interaction {
	private boolean stopped = false;
	public abstract boolean start(Player player);
	public abstract boolean process(Player player);
	public void stop(Player player) {
		stopped = true;
	}
	
	public boolean isStopped() {
		return stopped;
	}
}
