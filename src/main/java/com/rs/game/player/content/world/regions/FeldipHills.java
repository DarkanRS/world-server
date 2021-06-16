package com.rs.game.player.content.world.regions;

import com.rs.lib.game.WorldTile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class FeldipHills {
	
	public static ObjectClickHandler handleRantzCaves = new ObjectClickHandler(new Object[] { 3379, 32068, 32069 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().useStairs(e.getObjectId() == 3379 ? new WorldTile(2646, 9378, 0) : new WorldTile(2631, 2997, 0));
		}
	};
}
