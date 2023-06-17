package com.rs.game.content.items.liquid_containers;

import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.statements.MakeXStatement;
import com.rs.game.content.items.liquid_containers.FillAction.Filler;
import com.rs.lib.game.Item;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ItemOnItemHandler;

@PluginEventHandler
public class ClayWetting {
	private static int CLAY = 434;
	private static int SOFT_CLAY = 1761;
	private static int JUG = 1937;
	private static int BOWL = 1921;
	private static int BUCKET = 1929;
	
	public static ItemOnItemHandler clayWet = new ItemOnItemHandler(CLAY, new int[] { JUG, BOWL, BUCKET }, e -> {
		e.getPlayer().startConversation(new Dialogue()
				.addMakeX(SOFT_CLAY, e.getPlayer().getInventory().getAmountOf(CLAY))
				.addNext(() -> e.getPlayer().repeatAction(0, tick -> {
					if (tick >= MakeXStatement.getQuantity(e.getPlayer()))
						return false;
					Item water = e.getPlayer().getInventory().findItemByIds(JUG, BOWL, BUCKET);
					Item clay = e.getPlayer().getInventory().findItemByIds(CLAY);
					if (water == null || clay == null)
						return false;
					Filler filler = Filler.forFull(water.getId());
					if (filler == null)
						return false;
					clay.setId(SOFT_CLAY);
					water.setId(filler.getEmptyItem().getId());
					e.getPlayer().getInventory().refresh();
					return true;
				})));
	});
}
