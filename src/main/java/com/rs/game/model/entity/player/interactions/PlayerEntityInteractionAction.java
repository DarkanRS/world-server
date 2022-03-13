package com.rs.game.model.entity.player.interactions;

import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.interactions.EntityInteractionAction;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.actions.PlayerAction;

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
