package com.rs.game.player.content.world.regions.dungeons;

import com.rs.lib.game.WorldTile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class UndergroundPass {
	
	public static ObjectClickHandler handleSkullDoorEnter = new ObjectClickHandler(new Object[] { 3220, 3221 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().useStairs(new WorldTile(2173, 4725, 1));
		}
	};
	
	public static ObjectClickHandler handleSkullDoorExit = new ObjectClickHandler(new Object[] { 34288, 34289 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().useStairs(new WorldTile(2369, 9718, 0));
		}
	};
	
	public static ObjectClickHandler handleWellDoorEnter = new ObjectClickHandler(new Object[] { 3333, 3334 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().useStairs(e.getPlayer().getX() < e.getObject().getX() ? new WorldTile(2145, 4648, 1) : new WorldTile(2014, 4712, 1));
		}
	};
	
}
