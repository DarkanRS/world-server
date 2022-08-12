package com.rs.net.decoders.handlers.impl.record;

import com.rs.game.model.entity.player.Player;
import com.rs.lib.net.packets.PacketHandler;
import com.rs.lib.net.packets.decoders.mouse.MouseButtonClick;
import com.rs.utils.record.impl.ClickHW;

public class MouseButtonClickHandler implements PacketHandler<Player, MouseButtonClick> {

	@Override
	public void handle(Player player, MouseButtonClick packet) {
		player.getRecorder().record(new ClickHW(packet.getTimeRecieved(), packet.getTime(), packet.getX(), packet.getY(), packet.getMouseButton(), packet.isHardware()));
	}
}

