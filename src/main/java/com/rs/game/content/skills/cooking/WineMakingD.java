package com.rs.game.content.skills.cooking;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.statements.MakeXStatement;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.Constants;

public class WineMakingD extends Conversation {

	private final int JUG_OF_WATER = 1937;
	private final int GRAPES = 1;

	public WineMakingD(Player player) {
		super(player);
		if (player.getSkills().getLevel(Constants.COOKING) < 35) {
			addSimple("You need a cooking level of 35 to make wine.");
		} else {
			addNext(new MakeXStatement(
					MakeXStatement.MakeXType.MAKE,
					Math.min(player.getInventory().getAmountOf(1937), player.getInventory().getAmountOf(1987)),
					"How many would you like to make?",
					new int[] { 1995 },
					null));
			addNext(() -> player.getActionManager().setAction(new WineMaking(MakeXStatement.getQuantity(player))));
		}
		create();
	}

}
