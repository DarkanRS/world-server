package com.rs.net.decoders.handlers.impl.record;

import com.rs.game.model.entity.player.Player;
import com.rs.lib.net.packets.PacketHandler;
import com.rs.lib.net.packets.decoders.mouse.MouseMoveJav;
import com.rs.utils.record.impl.MouseMove;

public class MouseMoveJavHandler implements PacketHandler<Player, MouseMoveJav> {

	@Override
	public void handle(Player player, MouseMoveJav packet) {
		player.getRecorder().record(new MouseMove(packet.getTimeRecieved(), 0, packet.getSteps(), false));
	}
}

