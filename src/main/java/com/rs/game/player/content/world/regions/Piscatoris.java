package com.rs.game.player.content.world.regions;

import com.rs.game.ForceMovement;
import com.rs.game.pathing.Direction;
import com.rs.game.player.content.world.doors.Doors;
import com.rs.game.player.quests.Quest;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.lib.game.Animation;
import com.rs.lib.game.WorldTile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class Piscatoris {
	
	public static ObjectClickHandler handleColonyDoors = new ObjectClickHandler(new Object[] { 14929, 14931 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			Doors.handleDoubleDoors.handle(e);
		}
	};
	
	public static ObjectClickHandler handleColonyTunnels = new ObjectClickHandler(new Object[] { 14922 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (!Quest.SWAN_SONG.meetsRequirements(e.getPlayer(), "to enter the Piscatoris Fishing Colony."))
				return;
			final boolean isNorth = e.getPlayer().getY() > 3653;
			final WorldTile tile = isNorth ? new WorldTile(2344, 3650, 0) : new WorldTile(2344, 3655, 0);
			WorldTasksManager.schedule(new WorldTask() {
				int ticks = 0;

				@Override
				public void run() {
					e.getPlayer().lock();
					ticks++;
					if (ticks == 1) {
						e.getPlayer().setNextAnimation(new Animation(2589));
						e.getPlayer().setNextForceMovement(new ForceMovement(e.getObject(), 1, isNorth ? Direction.SOUTH : Direction.NORTH));
					} else if (ticks == 3) {
						e.getPlayer().setNextWorldTile(new WorldTile(2344, 3652, 0));
						e.getPlayer().setNextAnimation(new Animation(2590));
					} else if (ticks == 5) {
						e.getPlayer().setNextAnimation(new Animation(2591));
					} else if (ticks == 6) {
						e.getPlayer().setNextWorldTile(new WorldTile(tile.getX(), tile.getY(), tile.getPlane()));
						e.getPlayer().unlock();
						stop();
					}
				}
			}, 0, 0);
		}
	};

}
