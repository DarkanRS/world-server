package com.rs.game.player.content.world.regions;

import com.rs.game.player.content.skills.agility.Agility;
import com.rs.game.player.content.transportation.FairyRings;
import com.rs.game.player.content.world.AgilityShortcuts;
import com.rs.lib.game.WorldTile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ItemOnObjectEvent;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.handlers.ItemOnObjectHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class Zanaris {
	
	public static ItemOnObjectHandler handleEnterBlackDragonPlane = new ItemOnObjectHandler(new Object[] { 12093 }) {
		@Override
		public void handle(ItemOnObjectEvent e) {
			if (e.getItem().getId() == 2138) {
				e.getPlayer().getInventory().deleteItem(2138, 1);
				FairyRings.sendTeleport(e.getPlayer(), new WorldTile(1565, 4356, 0));
			}
		}
	};
	
	public static ItemOnObjectHandler handleDownBabyBlackDragons = new ItemOnObjectHandler(new Object[] { 12253 }) {
		@Override
		public void handle(ItemOnObjectEvent e) {
			if (e.getItem().getId() == 954)
				e.getPlayer().useLadder(new WorldTile(1544, 4381, 0));
		}
	};
	
	public static ObjectClickHandler handleUpBabyBlackDragons = new ObjectClickHandler(new Object[] { 12255 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().useLadder(new WorldTile(1561, 4380, 0));
		}
	};
	
	public static ObjectClickHandler handleExitBlackDragonPlane = new ObjectClickHandler(new Object[] { 12260 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().setNextWorldTile(new WorldTile(2453, 4476, 0));
		}
	};
	
	public static ObjectClickHandler handleCosmicAltarShortcuts = new ObjectClickHandler(new Object[] { 12127 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (!Agility.hasLevel(e.getPlayer(), e.getObject().isAt(2400, 4403) ? 46 : 66))
				return;
			AgilityShortcuts.sidestep(e.getPlayer(), e.getPlayer().transform(0, e.getPlayer().getY() > e.getObject().getY() ? -2 : 2, 0));
		}
	};

}
