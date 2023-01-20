package com.rs.game.content.world;

import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class Ladders {

	public static ObjectClickHandler world6400Up = new ObjectClickHandler(new Object[] { 29355 }, e -> {
		e.getPlayer().useStairs(828, e.getPlayer().transform(0, -6400), 1, 2);
	});
	
}
