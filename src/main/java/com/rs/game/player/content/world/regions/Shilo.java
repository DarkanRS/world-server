package com.rs.game.player.content.world.regions;

import com.rs.game.pathing.Direction;
import com.rs.game.pathing.RouteEvent;
import com.rs.game.player.Player;
import com.rs.game.player.content.skills.agility.Agility;
import com.rs.game.player.content.world.AgilityShortcuts;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.lib.game.WorldObject;
import com.rs.lib.game.WorldTile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class Shilo {
	public static ObjectClickHandler handleSteppingStone = new ObjectClickHandler(false, new Object[] { 10536 }) {
		@Override
		public void handle(ObjectClickEvent e) {
            if (!Agility.hasLevel(e.getPlayer(), 77)) {
                e.getPlayer().sendMessage("You need 77 agility");
                return;
            }
			Player p = e.getPlayer();
            WorldObject obj = e.getObject();
            Direction dir = Direction.NORTH;
            if(!obj.matches(new WorldTile(2860, 2974, 0)))
                return;
            if(p.getY() > obj.getY())
                dir = Direction.SOUTH;

            final Direction direction = dir;
            p.setRouteEvent(new RouteEvent(direction == Direction.NORTH ? new WorldTile(2860, 2971, 0) : new WorldTile(2860, 2977, 0), () -> {
                AgilityShortcuts.forceMovementInstant(p, new WorldTile(2860, 2974, 0), 741, 1, 0, direction);
                WorldTasksManager.schedule(new WorldTask() {
                    @Override
                    public void run() {
                        AgilityShortcuts.forceMovementInstant(p, new WorldTile(2860, 2977, 0), 741, 1, 0, direction);
                        p.unlock();
                    }
                }, 2);
            }));
		}
	};
}
