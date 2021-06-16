package com.rs.net.decoders.handlers.impl;

import com.rs.game.player.Player;
import com.rs.lib.net.packets.PacketHandler;
import com.rs.lib.net.packets.decoders.MouseClick;
import com.rs.utils.Click;

public class MouseClickHandler implements PacketHandler<Player, MouseClick> {

	@Override
	public void handle(Player player, MouseClick packet) {
		if (packet.getTime() <= 1) {
			return;
		}
		
		player.refreshIdleTime();
		
		if (player.clickQueue != null) {
			player.clickQueue.add(new Click(packet.getX(), packet.getY(), packet.getTime(), System.currentTimeMillis()));
			player.lastClick = new Click(packet.getX(), packet.getY(), packet.getTime(), System.currentTimeMillis());
			if (player.clickQueue.size() > 50) {
				player.clickQueue.poll();
			}
		}
	}
}
