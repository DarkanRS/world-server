package com.rs.net.decoders.handlers.impl.chat;

import com.rs.game.player.Player;
import com.rs.lib.net.packets.PacketHandler;
import com.rs.lib.net.packets.decoders.chat.ChatType;

public class ChatTypeHandler implements PacketHandler<Player, ChatType> {
	@Override
	public void handle(Player player, ChatType packet) {
		player.chatType = packet.getType();
	}
}
