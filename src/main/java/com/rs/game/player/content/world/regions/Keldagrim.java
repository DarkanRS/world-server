package com.rs.game.player.content.world.regions;

import com.rs.lib.game.WorldTile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class Keldagrim {
	
	public static ObjectClickHandler handleRellekkaEntrance = new ObjectClickHandler(new Object[] { 5973 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().setNextWorldTile(new WorldTile(2838, 10124, 0));
		}
	};
	
	public static ObjectClickHandler handleRellekkaExit = new ObjectClickHandler(new Object[] { 5998 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().setNextWorldTile(new WorldTile(2780, 10161, 0));
		}
	};
	
	public static ObjectClickHandler handleChaosDwarfBattlefieldEnter = new ObjectClickHandler(new Object[] { 45060 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().setNextWorldTile(new WorldTile(1520, 4704, 0));
		}
	};
	
	public static ObjectClickHandler handleChaosDwarfBattlefieldExit = new ObjectClickHandler(new Object[] { 45008 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().setNextWorldTile(new WorldTile(2817, 10155, 0));
		}
	};
	
	public static ObjectClickHandler handleBlastFurnaceEntrances = new ObjectClickHandler(new Object[] { 9084, 9138 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().useStairs(e.getObjectId() == 9084 ? new WorldTile(1939, 4958, 0) : new WorldTile(2931, 10196, 0));
		}
	};
	
	public static ObjectClickHandler handleBreweryStairCase = new ObjectClickHandler(new Object[] { 6085, 6086 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (e.getPlayer().getPlane() == 0)
				e.getPlayer().setNextWorldTile(new WorldTile(2914, 10196, 1));
			else if (e.getPlayer().getPlane() == 1) {
				e.getPlayer().setNextWorldTile(new WorldTile(2917, 10196, 0));
			}
		}
	};
}
