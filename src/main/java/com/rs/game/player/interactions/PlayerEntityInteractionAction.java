package com.rs.game.player.interactions;

import com.rs.game.Entity;
import com.rs.game.player.Player;
import com.rs.game.player.actions.PlayerAction;
import com.rs.game.player.actions.interactions.EntityInteractionAction;

public abstract class PlayerEntityInteractionAction<T extends PlayerAction> extends EntityInteractionAction<T> {
	
	public abstract boolean canStart(Player player);
	public abstract boolean checkAll(Player player);

	public PlayerEntityInteractionAction(Entity target, T action, int distance) {
		super(target, action, distance);
	}

	@Override
	public boolean canStart(Entity entity) {
		if (!(entity instanceof Player player))
			return false;
		return canStart(player);
	}

	@Override
	public boolean checkAll(Entity entity) {
		if (!(entity instanceof Player player))
			return false;
		return checkAll(player);
	}

}
