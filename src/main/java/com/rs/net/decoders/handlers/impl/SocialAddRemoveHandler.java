package com.rs.net.decoders.handlers.impl;

import com.rs.game.player.Player;
import com.rs.lib.net.packets.PacketHandler;
import com.rs.lib.net.packets.decoders.SocialAddRemove;
import com.rs.lib.net.packets.encoders.social.MessageGame.MessageType;
import com.rs.net.LobbyCommunicator;

public class SocialAddRemoveHandler implements PacketHandler<Player, SocialAddRemove> {

	@Override
	public void handle(Player player, SocialAddRemove packet) {
		switch(packet.getOpcode()) {
		case ADD_FRIEND:
			if (player.getTempB("addFriendLock")) {
				player.sendMessage(MessageType.FRIEND_NOTIFICATION, "Please wait...");
				return;
			}
			player.setTempB("addFriendLock", true);
			LobbyCommunicator.getAccountByDisplay(packet.getName(), acc -> {
				player.setTempB("addFriendLock", false);
				if (acc == null || acc.getUsername() == null) {
					player.sendMessage(MessageType.FRIEND_NOTIFICATION, "Player not found.");
					return;
				}
				player.getAccount().getSocial().addFriend(acc);
				LobbyCommunicator.updateAccount(player);
			});
			break;
		case ADD_IGNORE:
			if (player.getTempB("addIgnoreLock")) {
				player.sendMessage(MessageType.IGNORE_NOTIFICATION, "Please wait...");
				return;
			}
			player.setTempB("addIgnoreLock", true);
			LobbyCommunicator.getAccountByDisplay(packet.getName(), acc -> {
				player.setTempB("addIgnoreLock", false);
				if (acc == null || acc.getUsername() == null) {
					player.sendMessage(MessageType.IGNORE_NOTIFICATION, "Player not found.");
					return;
				}
				player.getAccount().getSocial().addIgnore(acc);
				LobbyCommunicator.updateAccount(player);
			});
			break;
		case REMOVE_FRIEND:
			if (player.getTempB("removeFriendLock")) {
				player.sendMessage(MessageType.FRIEND_NOTIFICATION, "Please wait...");
				return;
			}
			player.setTempB("removeFriendLock", true);
			LobbyCommunicator.getAccountByDisplay(packet.getName(), acc -> {
				player.setTempB("removeFriendLock", false);
				if (acc == null || acc.getUsername() == null) {
					player.sendMessage(MessageType.FRIEND_NOTIFICATION, "Error removing friend.");
					return;
				}
				player.getAccount().getSocial().removeFriend(acc);
				LobbyCommunicator.updateAccount(player);
			});
			break;
		case REMOVE_IGNORE:
			if (player.getTempB("removeIgnoreLock")) {
				player.sendMessage(MessageType.IGNORE_NOTIFICATION, "Please wait...");
				return;
			}
			player.setTempB("removeIgnoreLock", true);
			LobbyCommunicator.getAccountByDisplay(packet.getName(), acc -> {
				player.setTempB("removeIgnoreLock", false);
				if (acc == null || acc.getUsername() == null) {
					player.sendMessage(MessageType.IGNORE_NOTIFICATION, "Error removing ignore.");
					return;
				}
				player.getAccount().getSocial().removeIgnore(acc);
				LobbyCommunicator.updateAccount(player);
			});
			break;
		default:
			break;
		}
	}

}
