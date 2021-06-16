package com.rs.net.decoders.handlers.impl;

import com.rs.game.player.Player;
import com.rs.lib.net.packets.PacketHandler;
import com.rs.lib.net.packets.decoders.CutsceneFinished;

public class CutsceneFinishedHandler implements PacketHandler<Player, CutsceneFinished> {

	@Override
	public void handle(Player player, CutsceneFinished packet) {
		player.loadMapRegions();
	}
}
