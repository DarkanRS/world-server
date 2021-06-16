package com.rs.game.player.content.world.regions;

import com.rs.lib.game.WorldTile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class Burthorpe {
	
	public static ObjectClickHandler handleCaveEntrance = new ObjectClickHandler(new Object[] { 66876 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().setNextWorldTile(new WorldTile(2292, 4516, 0));
		}
	};
	
	public static ObjectClickHandler handleCaveExit = new ObjectClickHandler(new Object[] { 67002 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().setNextWorldTile(new WorldTile(2876, 3502, 0));
		}
	};

}
