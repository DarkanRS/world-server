package com.rs.game.content.world;

import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class Ladders {

	public static ObjectClickHandler world6400Up = new ObjectClickHandler(new Object[] { 29355 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().useStairs(828, e.getPlayer().transform(0, -6400), 1, 2);
		}
	};
	
}
