package com.rs.game.player.actions.interactions;

import com.rs.game.Entity;
import com.rs.game.player.Player;
import com.rs.game.player.actions.Action;

public abstract class EntityInteractionAction extends EntityInteraction {

	private Action action;
	private boolean started = false;

	public EntityInteractionAction(Entity target, Action action, int distance) {
		super(target, distance);
		continueAfterReached();
		keepFacing();
		this.action = action;
	}

	@Override
	public void interact(Player player) {
		if (!started) {
			if (!action.start(player)) {
				stop(player);
				return;
			}
			started = true;
		}
		if (player.getActionManager().getActionDelay() > 0)
			return;
		int newDelay = action.processWithDelay(player) + 1;
		if (newDelay == -1) {
			stop(player);
			return;
		}
		player.getActionManager().setActionDelay(newDelay);
	}

	@Override
	public void onStop(Player player) {
		action.stop(player);
	}
}
