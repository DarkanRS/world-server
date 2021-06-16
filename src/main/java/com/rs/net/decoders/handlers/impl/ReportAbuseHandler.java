package com.rs.net.decoders.handlers.impl;

import com.rs.game.player.Player;
import com.rs.lib.net.packets.PacketHandler;
import com.rs.lib.net.packets.decoders.ReportAbuse;
import com.rs.utils.ReportsManager;

public class ReportAbuseHandler implements PacketHandler<Player, ReportAbuse> {

	@Override
	public void handle(Player player, ReportAbuse packet) {
		if (!player.hasStarted())
			return;
		ReportsManager.report(player, packet.getUsername(), packet.getType(), packet.isMute());
	}

}
