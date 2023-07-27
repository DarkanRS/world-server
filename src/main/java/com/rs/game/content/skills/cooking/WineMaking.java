package com.rs.game.content.skills.cooking;

import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.actions.PlayerAction;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ItemOnItemHandler;
import com.rs.plugin.handlers.LoginHandler;

@PluginEventHandler
public class WineMaking extends PlayerAction {

	private boolean rollBadWine(Player player) {
		double level = player.getSkills().getLevel(Constants.COOKING);
		double noFailLevel = 68.0D;
//		if (level >= noFailLevel) {
//			return true;
//		}
		double chance = Math.max(level / noFailLevel, 0.6D);
		return chance < Math.random();
	}

	private static final int JUG_OF_WATER = 1937;
	private static final int GRAPES = 1987;
	private static final int UNFERMENTED_WINE = 1995;
	private static final int JUG_OF_WINE = 1993;
	private static final int JUG_OF_BAD_WINE = 1991;

	private int amount;

	public WineMaking(int amount) {
		this.amount = amount;
	}

	@Override
	public boolean start(Player player) {
		return true;
	}

	@Override
	public boolean process(Player player) {
		return true;
	}

	private final int WINE_CREATE_TICKS = 1;

	@Override
	public int processWithDelay(Player player) {
		amount--;
		if (!player.getInventory().containsItem(GRAPES) || !player.getInventory().containsItem(JUG_OF_WATER)) {
			return -1;
		}
		player.getInventory().deleteItem(GRAPES, 1);
		player.getInventory().deleteItem(JUG_OF_WATER, 1);
		if (rollBadWine(player)) {
			player.getSkills().addXp(Constants.COOKING, 200);
			player.getInventory().addItem(JUG_OF_WINE);
		} else {
			player.getInventory().addItem(JUG_OF_BAD_WINE);
		}
		player.sendMessage("You squeeze the grapes into the jug. The wine begins to ferment.");
		return amount > 0 ? WINE_CREATE_TICKS : -1;
	}

	@Override
	public void stop(Player player) {}

	public static ItemOnItemHandler makeXWine = new ItemOnItemHandler(JUG_OF_WATER, GRAPES, (e) -> {
		e.getPlayer().startConversation(new WineMakingD(e.getPlayer()));
	});

}
