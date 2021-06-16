package com.rs.game.player.content.world.regions;

import com.rs.lib.game.WorldTile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.LoginEvent;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.handlers.LoginHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class Meiyerditch {
	
	public static LoginHandler unlockVars = new LoginHandler() {
		@Override
		public void handle(LoginEvent e) {
			e.getPlayer().getVars().setVarBit(2587, 1); //boat
			e.getPlayer().getVars().setVarBit(2589, 1); //kick down floor
		}
	};
	
	public static ObjectClickHandler handleBoat = new ObjectClickHandler(false, new Object[] { 12945, 17955 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (e.getObjectId() == 12945) {
				e.getPlayer().walkToAndExecute(new WorldTile(3525, 3170, 0), () -> {
					e.getPlayer().faceObject(e.getObject());
					e.getPlayer().fadeScreen(() -> e.getPlayer().setNextWorldTile(new WorldTile(3605, 3163, 0)));	
				});
			} else {
				e.getPlayer().walkToAndExecute(new WorldTile(3605, 3163, 0), () -> { 
					e.getPlayer().faceObject(e.getObject());
					e.getPlayer().fadeScreen(() -> e.getPlayer().setNextWorldTile(new WorldTile(3525, 3170, 0)));
				});
			}
		}
	};
	
	public static ObjectClickHandler handleRocks1 = new ObjectClickHandler(new Object[] { 17960, 17679 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().useLadder(e.getPlayer().transform(e.getObjectId() == 17960 ? 4 : -4, 0, e.getObjectId() == 17960 ? -1 : 1));
		}
	};
	
	public static ObjectClickHandler handleFloorClimb = new ObjectClickHandler(new Object[] { 18122, 18124 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().useLadder(e.getObject().transform(e.getObjectId() == 18122 ? -1 : 1, 0, e.getObjectId() == 18122 ? -1 : 1));
		}
	};
	
	public static ObjectClickHandler handleRubble = new ObjectClickHandler(new Object[] { 18037, 18038 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().useLadder(e.getObject().transform(e.getObjectId() == 18037 ? 3 : -3, 0, 0));
		}
	};

}
