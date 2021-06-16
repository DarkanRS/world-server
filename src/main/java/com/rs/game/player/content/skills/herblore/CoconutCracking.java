package com.rs.game.player.content.skills.herblore;

import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ItemOnItemEvent;
import com.rs.plugin.handlers.ItemOnItemHandler;

@PluginEventHandler
public class CoconutCracking  {
	
	public static final int HAMMER = 2347;
	public static final int COCONUT = 5974;
	public static final int OPEN_COCONUT = 5976;
	
	public static ItemOnItemHandler handle = new ItemOnItemHandler(HAMMER, COCONUT) {
		@Override
		public void handle(ItemOnItemEvent e) {
			e.getPlayer().getInventory().deleteItem(COCONUT, 1);
			e.getPlayer().getInventory().addItem(OPEN_COCONUT, 1);
			e.getPlayer().sendMessage("You break the coconut open with the hammer.");
		}
	};
}
