package com.rs.rsps;

import com.rs.game.player.content.skills.farming.FarmPatch;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.annotations.ServerStartupEvent;

@PluginEventHandler
public class SettingsInitialization {
	
	@ServerStartupEvent
	public static void init() {
		FarmPatch.FARMING_TICK = 250;
	}

}
