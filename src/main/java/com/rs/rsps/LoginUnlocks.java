package com.rs.rsps;

import com.rs.game.player.actions.LodestoneAction.Lodestone;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.LoginEvent;

@PluginEventHandler
public class LoginUnlocks {

	public static void login(LoginEvent e) {
		if (e.getPlayer().getStarter() <= 0) {
			for (Lodestone stone : Lodestone.values()) {
				e.getPlayer().unlockLodestone(stone, null);
			}
		}
	}
	
}
