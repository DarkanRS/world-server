package com.rs.game.player.content.world.regions.dungeons;

import com.rs.lib.game.WorldTile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.LoginEvent;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.handlers.LoginHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class HAMDungeon {
	
	public static LoginHandler unlockTrapdoor = new LoginHandler() {
		@Override
		public void handle(LoginEvent e) {
			e.getPlayer().getVars().setVarBit(2270, 2);
		}
	};
	
	public static ObjectClickHandler handleKeyTrapdoor = new ObjectClickHandler(new Object[] { 15766, 15747 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (e.getObjectId() == 15766) {
				if (e.getOption().equals("Climb-down"))
					e.getPlayer().useLadder(new WorldTile(2568, 5185, 0));
			} else {
				e.getPlayer().useLadder(new WorldTile(3166, 9623, 0));
			}
		}
	};
}
