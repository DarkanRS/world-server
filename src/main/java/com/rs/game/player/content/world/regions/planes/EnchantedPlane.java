package com.rs.game.player.content.world.regions.planes;

import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class EnchantedPlane {
	
	public static ObjectClickHandler handleEnchantedTree = new ObjectClickHandler(new Object[] { 16265 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			
		}
	};

}
