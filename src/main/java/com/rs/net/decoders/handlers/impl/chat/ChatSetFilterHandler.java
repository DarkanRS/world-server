package com.rs.net.decoders.handlers.impl.chat;

import com.rs.game.player.Player;
import com.rs.lib.net.packets.PacketHandler;
import com.rs.lib.net.packets.decoders.chat.ChatSetFilter;

public class ChatSetFilterHandler implements PacketHandler<Player, ChatSetFilter> {

	@Override
	public void handle(Player player, ChatSetFilter packet) {
		player.setPublicStatus(packet.getPublicFilter());
		player.getAccount().getSocial().setStatus(packet.getPrivateFilter());
		player.setTradeStatus(packet.getTradeFilter());
	}
}
