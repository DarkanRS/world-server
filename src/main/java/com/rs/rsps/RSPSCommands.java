package com.rs.rsps;

import com.rs.Settings;
import com.rs.game.player.content.commands.Commands;
import com.rs.game.player.content.skills.magic.Magic;
import com.rs.lib.game.Rights;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.annotations.ServerStartupEvent;

@PluginEventHandler
public class RSPSCommands {
	
	@ServerStartupEvent
	public static void init() {
		Commands.add(Rights.PLAYER, "home", "Teleports the player home.", (p, args) -> {
			Magic.sendNormalTeleportSpell(p, Settings.getConfig().getPlayerStartTile());
		});
	}

}
