package com.rs.game.model.entity.interactions;

import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.actions.Action;
import com.rs.game.model.entity.player.Player;

public abstract class EntityInteractionAction<T extends Action> extends EntityInteraction {

	private final T action;
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
