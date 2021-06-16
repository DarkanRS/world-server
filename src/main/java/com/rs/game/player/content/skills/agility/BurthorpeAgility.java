package com.rs.game.player.content.skills.agility;

import com.rs.lib.game.WorldTile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class BurthorpeAgility  {
	
	public static ObjectClickHandler handleLogWalk = new ObjectClickHandler(new Object[] { 66894 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			Agility.walkToAgility(e.getPlayer(), 155, new WorldTile(2919, 3558, 0), 5.5);
		}
	};
	
	public static ObjectClickHandler handleClimb1 = new ObjectClickHandler(new Object[] { 66912 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			Agility.handleObstacle(e.getPlayer(), 15765, 7, new WorldTile(2919, 3562, 1), 5.5);
		}
	};
	
	public static ObjectClickHandler handleRopeSwing = new ObjectClickHandler(false, new Object[] { 66904 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			Agility.swingOnRopeSwing(e.getPlayer(), new WorldTile(2912, 3562, 1), new WorldTile(2916, 3562, 1), e.getObject(), 5.5);
		}
	};
	
	public static ObjectClickHandler handleMonkeyBars = new ObjectClickHandler(false, new Object[] { 66897 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			Agility.crossMonkeybars(e.getPlayer(), new WorldTile(2917, 3561, 1), new WorldTile(2917, 3554, 1), 5.5);
		}
	};
	
	public static ObjectClickHandler handleShimmy = new ObjectClickHandler(new Object[] { 66909 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			Agility.walkToAgility(e.getPlayer(), 2349, new WorldTile(2912, 3564, 1), 5.5);
		}
	};
	
	public static ObjectClickHandler handleClimb2 = new ObjectClickHandler(new Object[] { 66902 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			Agility.handleObstacle(e.getPlayer(), 15782, 2, new WorldTile(2912, 3562, 1), 5.5);
		}
	};
	
	public static ObjectClickHandler handleJumpDown = new ObjectClickHandler(new Object[] { 66910 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			Agility.handleObstacle(e.getPlayer(), 2588, 1, new WorldTile(2916, 3552, 0), 46);
			e.getPlayer().incrementCount("Burthorpe laps");
		}
	};
}
