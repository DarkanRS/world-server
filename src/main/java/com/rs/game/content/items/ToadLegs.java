package com.rs.game.content.items;

import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ItemClickHandler;

@PluginEventHandler
public class ToadLegs {
	
	public static ItemClickHandler pullLegs = new ItemClickHandler(new Object[] { 2150 }, e -> {
		e.getPlayer().getInventory().deleteItem(2150, 1);
		e.getPlayer().getInventory().addItem(2152, 1);
		e.getPlayer().sendMessage("You pull the legs off the toad. At least they'll grow back...");
	});

}
