package com.rs.game.player.content.interfacehandlers;

import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ButtonClickEvent;
import com.rs.plugin.handlers.ButtonClickHandler;

@PluginEventHandler
public class Logout {
	
	public static ButtonClickHandler handle = new ButtonClickHandler(182) {
		@Override
		public void handle(ButtonClickEvent e) {
			if (e.getPlayer().getInterfaceManager().containsInventoryInter())
				return;
			if (e.getComponentId() == 6 || e.getComponentId() == 13)
				if (!e.getPlayer().hasFinished())
					e.getPlayer().logout(e.getComponentId() == 6);
		}
	};
	
}
