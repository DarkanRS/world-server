package com.rs.game.player.content.minigames.pyramidplunder;

import com.rs.lib.game.WorldTile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class PyramidPlunderHandler {//All objects within the minigame

	public static ObjectClickHandler handlePyramidExits = new ObjectClickHandler(new Object[] { 16458 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().setNextWorldTile(new WorldTile(3288, 2801, 0));
			e.getPlayer().getControllerManager().forceStop();
		}
	};


}
