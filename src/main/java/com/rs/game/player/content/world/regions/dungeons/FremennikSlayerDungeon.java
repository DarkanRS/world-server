package com.rs.game.player.content.world.regions.dungeons;

import com.rs.game.player.content.skills.agility.Agility;
import com.rs.game.player.content.world.AgilityShortcuts;
import com.rs.lib.game.WorldTile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class FremennikSlayerDungeon {
	
	public static ObjectClickHandler handleChasm = new ObjectClickHandler(false, new Object[] { 44339 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (!Agility.hasLevel(e.getPlayer(), 81))
				return;
			final WorldTile toTile = e.getPlayer().getX() < 2772 ? new WorldTile(2775, 10002, 0) : new WorldTile(2768, 10002, 0);
			e.getPlayer().walkToAndExecute(e.getPlayer().getX() > 2772 ? new WorldTile(2775, 10002, 0) : new WorldTile(2768, 10002, 0), () -> {
				AgilityShortcuts.forceMovement(e.getPlayer(), toTile, 4721, 1, 1);
			});
		}
	};
	
	public static ObjectClickHandler handleShortcut2 = new ObjectClickHandler(new Object[] { 9321 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (!Agility.hasLevel(e.getPlayer(), 62))
				return;
			AgilityShortcuts.forceMovement(e.getPlayer(), e.getPlayer().transform(e.getObject().getRotation() == 0 ? 5 : -5, 0), 3844, 1, 1);
		}
	};
}
