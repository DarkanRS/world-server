package com.rs.net.decoders.handlers.impl.record;

import com.rs.game.model.entity.player.Player;
import com.rs.lib.net.packets.PacketHandler;
import com.rs.lib.net.packets.decoders.mouse.MouseClickHW;
import com.rs.utils.record.impl.ClickHW;

public class MouseClickHWHandler implements PacketHandler<Player, MouseClickHW> {

	@Override
	public void handle(Player player, MouseClickHW packet) {
		player.getRecorder().record(new ClickHW(packet.getTimeRecieved(), packet.getTime(), packet.getX(), packet.getY(), packet.getMouseButton(), packet.isHardware()));
	}
}

