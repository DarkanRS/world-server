package com.rs.game.player.content.world.regions;

import com.rs.game.pathing.Direction;
import com.rs.game.player.content.world.AgilityShortcuts;
import com.rs.game.player.content.world.doors.Doors;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.lib.game.WorldTile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.events.PlayerStepEvent;
import com.rs.plugin.handlers.ObjectClickHandler;
import com.rs.plugin.handlers.PlayerStepHandler;

@PluginEventHandler
public class AlKharid {
	
	public static PlayerStepHandler shantayPass = new PlayerStepHandler(new WorldTile(3303, 3116, 0), new WorldTile(3303, 3117, 0), new WorldTile(3305, 3116, 0), new WorldTile(3305, 3117, 0)) {
		@Override
		public void handle(PlayerStepEvent e) {
			if (e.getStep().getY() == 3116 && e.getStep().getDir() == Direction.SOUTH) {
				if (!e.getPlayer().getInventory().containsItem(1854, 1)) {
					e.getPlayer().sendMessage("You should check with Shantay for a pass.");
					return;
				}
				e.getPlayer().getInventory().deleteItem(1854, 1);
			}
			e.getStep().setCheckClip(false);
			e.getPlayer().setRunHidden(false);
			WorldTasksManager.delay(3, () -> {
				e.getPlayer().setRunHidden(true);
			});
		}
	};
	
	public static ObjectClickHandler clickShantayPass = new ObjectClickHandler(new Object[] { 12774 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().sendMessage("Walk on through with a pass!");
		}
	};

	public static ObjectClickHandler handleGates = new ObjectClickHandler(new Object[] { 35549, 35551 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (e.getPlayer().getInventory().containsItem(995, 10)) {
				e.getPlayer().getInventory().deleteItem(995, 10);
				Doors.handleDoubleDoor(e.getPlayer(), e.getObject());
			} else
				e.getPlayer().sendMessage("You need 10 gold to pass through this gate.");
		}
	};
	
	public static ObjectClickHandler handleStrykewyrmStile = new ObjectClickHandler(new Object[] { 48208 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			AgilityShortcuts.climbOver(e.getPlayer(), e.getPlayer().transform(e.getPlayer().getX() < e.getObject().getX() ? 3 : -3, 0, 0));
		}
	};

}
