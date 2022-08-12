package com.rs.net.decoders.handlers.impl.record;

import com.rs.game.model.entity.player.Player;
import com.rs.lib.net.packets.PacketHandler;
import com.rs.lib.net.packets.decoders.ClientFocus;
import com.rs.utils.record.impl.Focus;

public class ClientFocusHandler implements PacketHandler<Player, ClientFocus> {

	@Override
	public void handle(Player player, ClientFocus packet) {
		player.getRecorder().record(new Focus(packet.getTimeRecieved(), packet.isInFocus()));
	}

}
