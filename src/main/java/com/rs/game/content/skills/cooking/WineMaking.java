package com.rs.game.content.skills.cooking;

import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.actions.PlayerAction;
import com.rs.lib.Constants;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ItemOnItemHandler;

@PluginEventHandler
public class Wine extends PlayerAction {

	private boolean rollBadWine(Player player)
	{
		double level = player.getSkills().getLevel(Constants.COOKING);
		double noFailLevel = 68.0D;
		double chance = Math.max(level / noFailLevel, 0.6D);
		return chance < Math.random();
	}

	private int amount;

	public Wine(int amount)
	{
		this.amount = amount;
	}

	@Override
	public boolean start(Player player) {
		if (player.getSkills().getLevel(Constants.COOKING) < 35) {
			player.simpleDialogue("You need a cooking level of 35 to make wine.");
			return false;
		}
		return true;
	}

	@Override
	public boolean process(Player player) {
		if (player.getSkills().getLevel(Constants.COOKING) < 35) {
			player.simpleDialogue("You need a cooking level of 35 to make wine.");
			return false;
		}

		return true;
	}

	@Override
	public int processWithDelay(Player player) {
		amount--;
		
		return 0;
	}

	@Override
	public void stop(Player entity) {

	}
}
