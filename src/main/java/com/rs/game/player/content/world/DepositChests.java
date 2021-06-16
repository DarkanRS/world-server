package com.rs.game.player.content.world;

import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class DepositChests {
	
	public static ObjectClickHandler handleDepositChests = new ObjectClickHandler(new Object[] { "Deposit chest" }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (e.getOption().equals("Deposit"))
				e.getPlayer().getBank().openDepositBox();
		}
	};
	
}
