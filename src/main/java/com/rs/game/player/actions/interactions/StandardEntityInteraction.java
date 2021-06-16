package com.rs.game.player.actions.interactions;

import com.rs.game.Entity;
import com.rs.game.player.Player;

public class StandardEntityInteraction extends EntityInteraction {
	
	private Runnable onReached;

	public StandardEntityInteraction(Entity target, int distance, Runnable onReached) {
		super(target, distance);
		this.onReached = onReached;
	}

	@Override
	public boolean canStart(Player player) {
		return true;
	}

	@Override
	public boolean checkAll(Player player) {
		return true;
	}
	
	@Override
	public void interact(Player player) {
		onReached.run();
	}

	@Override
	public void onStop(Player player) {
		
	}

}