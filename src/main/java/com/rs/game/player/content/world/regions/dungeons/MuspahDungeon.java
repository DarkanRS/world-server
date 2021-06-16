package com.rs.game.player.content.world.regions.dungeons;

import com.rs.lib.game.WorldTile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class MuspahDungeon {
	
	public static ObjectClickHandler handleExit = new ObjectClickHandler(new Object[] { 42891 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().setNextWorldTile(new WorldTile(2736, 3729, 0));
		}
	};
	
	public static ObjectClickHandler handleEntrance = new ObjectClickHandler(new Object[] { 42793 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			//subtract 64 from x if muspah has not escaped
			e.getPlayer().setNextWorldTile(new WorldTile(3485, 5511, 0));
		}
	};
	
	public static ObjectClickHandler handleOpenings = new ObjectClickHandler(new Object[] { 42794, 42795 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().setNextWorldTile(e.getPlayer().transform(0, e.getObjectId() == 42794 ? 8 : -8, 0));
		}
	};
	
	public static ObjectClickHandler handleEnterIceStryke = new ObjectClickHandler(new Object[] { 48188 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().setNextWorldTile(new WorldTile(3435, 5646, 0));
		}
	};
	
	public static ObjectClickHandler handleExitIceStryke = new ObjectClickHandler(new Object[] { 48189 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().setNextWorldTile(new WorldTile(3509, 5515, 0));
		}
	};
}
