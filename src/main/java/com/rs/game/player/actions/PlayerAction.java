package com.rs.game.player.actions;

import com.rs.game.Entity;
import com.rs.game.player.Player;

public abstract class PlayerAction extends Action {
	public abstract boolean start(Player entity);
	public abstract boolean process(Player entity);
	public abstract int processWithDelay(Player entity);
	public abstract void stop(Player entity);

	@Override
	public boolean start(Entity entity) {
		if (!(entity instanceof Player player))
			return false;
		return start(player);
	}

	@Override
	public boolean process(Entity entity) {
		if (!(entity instanceof Player player))
			return false;
		return process(player);
	}

	@Override
	public int processWithDelay(Entity entity) {
		if (!(entity instanceof Player player))
			return -1;
		return processWithDelay(player);
	}

	@Override
	public void stop(Entity entity) {
		if (entity instanceof Player player)
			stop(player);
	}

}
