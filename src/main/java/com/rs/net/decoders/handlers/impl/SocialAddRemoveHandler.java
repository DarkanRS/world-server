package com.rs.net.decoders.handlers.impl;

import com.rs.game.player.Player;
import com.rs.lib.net.packets.PacketHandler;
import com.rs.lib.net.packets.decoders.SocialAddRemove;
import com.rs.net.LobbyCommunicator;

public class SocialAddRemoveHandler implements PacketHandler<Player, SocialAddRemove> {

	@Override
	public void handle(Player player, SocialAddRemove packet) {
		switch(packet.getOpcode()) {
		case ADD_FRIEND:
			player.getAccount().getSocial().addFriend(packet.getName());
			LobbyCommunicator.updateAccount(player);
			break;
		case ADD_IGNORE:
			player.getAccount().getSocial().addIgnore(packet.getName());
			LobbyCommunicator.updateAccount(player);
			break;
		case REMOVE_FRIEND:
			player.getAccount().getSocial().removeFriend(packet.getName());
			LobbyCommunicator.updateAccount(player);
			break;
		case REMOVE_IGNORE:
			player.getAccount().getSocial().removeIgnore(packet.getName());
			LobbyCommunicator.updateAccount(player);
			break;
		default:
			break;
		}
	}

}
