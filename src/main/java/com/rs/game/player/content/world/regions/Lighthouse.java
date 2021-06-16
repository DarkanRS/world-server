package com.rs.game.player.content.world.regions;

import com.rs.game.player.content.world.doors.Doors;
import com.rs.lib.game.WorldTile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class Lighthouse {
	
	public static ObjectClickHandler handleEntranceLadders = new ObjectClickHandler(new Object[] { 4383, 4412 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().useLadder(e.getObjectId() == 4412 ? new WorldTile(2510, 3644, 0) : new WorldTile(2519, 9995, 1));
		}
	};
	
	public static ObjectClickHandler handleDoors = new ObjectClickHandler(new Object[] { 4545, 4546 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			Doors.handleDoor(e.getPlayer(), e.getObject());
		}
	};
	
	public static ObjectClickHandler handleLadders = new ObjectClickHandler(new Object[] { 4413, 4485 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().useLadder(e.getPlayer().transform(0, e.getObjectId() == 4485 ? 3 : -3, e.getObjectId() == 4485 ? -1 : 1));
		}
	};
}
