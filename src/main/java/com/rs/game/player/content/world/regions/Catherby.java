package com.rs.game.player.content.world.regions;

import com.rs.lib.game.WorldTile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class Catherby {
	
	public static ObjectClickHandler taverlyDungeonClimbToWaterObelisk = new ObjectClickHandler(new Object[] { 32015 }, new WorldTile(2842, 9824, 0)) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().ladder(new WorldTile(2842, 3423, 0));
		}
	};

}
