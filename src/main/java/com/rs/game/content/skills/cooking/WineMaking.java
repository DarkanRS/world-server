package com.rs.game.content.skills.cooking;

import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.actions.PlayerAction;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.plugin.handlers.LoginHandler;

public class WineMaking extends PlayerAction {

	private boolean rollBadWine(Player player) {
		double level = player.getSkills().getLevel(Constants.COOKING);
		double noFailLevel = 68.0D;
		double chance = Math.max(level / noFailLevel, 0.6D);
		return chance < Math.random();
	}

	private final int JUG_OF_WATER = 1;
	private final int GRAPES = 1;
	private final int UNFERMENTED_WINE = 1;
	private final int JUG_OF_WINE = 1;

	private int amount;

	public static LoginHandler onLogin = new LoginHandler(e -> {

	});

	public WineMaking(int amount) {
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

	private final int WINE_CREATE_TICKS = 2;

	@Override
	public int processWithDelay(Player player) {
		amount--;
		player.setNextAnimation(new Animation(1));
		player.getInventory().deleteItem(GRAPES, 1);
		player.getInventory().deleteItem(JUG_OF_WATER, 1);
		player.getInventory().addItem(UNFERMENTED_WINE);
		player.setWineCreated();
		return amount > 0 ? WINE_CREATE_TICKS : -1;
	}

	@Override
	public void stop(Player player) {
		WorldTasks.schedule(0, 1, () -> {

		});
	}

}
