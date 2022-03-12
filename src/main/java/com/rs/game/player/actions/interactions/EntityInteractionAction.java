package com.rs.game.player.actions.interactions;

import com.rs.game.Entity;
import com.rs.game.player.Player;
import com.rs.game.player.actions.Action;

public abstract class EntityInteractionAction<T extends Action> extends EntityInteraction {

	private T action;
	private boolean started = false;

	public EntityInteractionAction(Entity target, T action, int distance) {
		super(target, distance);
		continueAfterReached();
		keepFacing();
		this.action = action;
	}

	@Override
	public void interact(Entity entity) {
		if (!started) {
			if (!action.start(entity)) {
				stop(entity);
				return;
			}
			started = true;
		}
		if (entity.getActionManager().getActionDelay() > 0)
			return;
		int newDelay = action.processWithDelay(entity) + 1;
		if (newDelay == -1) {
			stop(entity);
			return;
		}
		entity.getActionManager().setActionDelay(newDelay);
	}

	public T getAction() {
		return action;
	}

	@Override
	public void onStop(Entity entity) {
		if (!(entity instanceof Player player))
			return;
		action.stop(player);
	}
}
