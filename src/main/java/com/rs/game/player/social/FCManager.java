package com.rs.game.player.social;

import com.rs.game.player.Player;
import com.rs.lib.model.FriendsChat.Rank;
import com.rs.lib.net.ClientPacket;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ButtonClickEvent;
import com.rs.plugin.handlers.ButtonClickHandler;

@PluginEventHandler
public class FCManager {

	public static ButtonClickHandler handleInterface = new ButtonClickHandler(1108) {
		@Override
		public void handle(ButtonClickEvent e) {
			switch (e.getComponentId()) {
				case 1 -> {
					switch (e.getPacket()) {
						case IF_OP1 -> e.getPlayer().getPackets().sendRunScriptReverse(109,
								new Object[] { "Enter chat prefix:" });
						case IF_OP2 -> e.getPlayer().getSocial().getFriendsChat().setName(null); // TODO when fc block update is
																									// sent, check if name was
																									// set to null and destroy
																									// chat
						default -> e.getPlayer().sendMessage("Unexpected FC interface packet...");
					}
				}
				case 2 -> e.getPlayer().getSocial().getFriendsChat().setRankToEnter(getRankFromPacket(e.getPacket()));
				case 3 -> e.getPlayer().getSocial().getFriendsChat().setRankToSpeak(getRankFromPacket(e.getPacket()));
				case 4 -> e.getPlayer().getSocial().getFriendsChat().setRankToKick(getRankFromPacket(e.getPacket()));
				case 5 -> e.getPlayer().getSocial().getFriendsChat().setRankToLS(getRankFromPacket(e.getPacket()));
			}
		}
	};

	public static ButtonClickHandler handleTab = new ButtonClickHandler(1109) {
		@Override
		public void handle(ButtonClickEvent e) {
			switch (e.getComponentId()) {
				case 26 -> {
					if (e.getPlayer().getCurrentFriendChat() != null)
						e.getPlayer().getCurrentFriendChat().leaveChat(e.getPlayer(), false);
				}
				case 31 -> {
					if (e.getPlayer().getInterfaceManager().containsScreenInter()) {
						e.getPlayer()
								.sendMessage("Please close the interface you have opened before using Friends Chat setup.");
						return;
					}
					e.getPlayer().stopAll();
					openFriendChatSetup(e.getPlayer());
				}
				case 19 -> e.getPlayer().toggleLootShare();
			}
		}
	};

	public static void openFriendChatSetup(Player player) {
		player.getInterfaceManager().sendInterface(1108);
		player.getPackets().setIFText(1108, 1, player.getSocial().getFriendsChat().getName() == null ? "Chat disabled" : player.getSocial().getFriendsChat().getName());
		sendRankRequirement(player, player.getSocial().getFriendsChat().getRankToEnter(), 2);
		sendRankRequirement(player, player.getSocial().getFriendsChat().getRankToSpeak(), 3);
		sendRankRequirement(player, player.getSocial().getFriendsChat().getRankToKick(), 4);
		sendRankRequirement(player, player.getSocial().getFriendsChat().getRankToLS(), 5);
		player.getPackets().setIFHidden(1108, 49, true);
		player.getPackets().setIFHidden(1108, 63, true);
		player.getPackets().setIFHidden(1108, 77, true);
		player.getPackets().setIFHidden(1108, 91, true);
	}

	public static void sendRankRequirement(Player player, Rank rank, int component) {
		switch (rank) {
			case UNRANKED -> player.getPackets().setIFText(1108, component, "No-one");
			case FRIEND -> player.getPackets().setIFText(1108, component, "Any friends");
			case RECRUIT -> player.getPackets().setIFText(1108, component, "Recruit+");
			case CORPORAL -> player.getPackets().setIFText(1108, component, "Corporal+");
			case SERGEANT -> player.getPackets().setIFText(1108, component, "Sergeant+");
			case LIEUTENANT -> player.getPackets().setIFText(1108, component, "Lieutenant+");
			case CAPTAIN -> player.getPackets().setIFText(1108, component, "Captain+");
			case GENERAL -> player.getPackets().setIFText(1108, component, "General+");
			case OWNER -> player.getPackets().setIFText(1108, component, "Only me");
			default -> throw new IllegalArgumentException("Unexpected value: " + rank);
		}
	}

	public static Rank getRankFromPacket(ClientPacket packet) {
		return switch (packet) {
			case IF_OP1 -> Rank.UNRANKED;
			case IF_OP2 -> Rank.FRIEND;
			case IF_OP3 -> Rank.RECRUIT;
			case IF_OP4 -> Rank.CORPORAL;
			case IF_OP5 -> Rank.SERGEANT;
			case IF_OP6 -> Rank.LIEUTENANT;
			case IF_OP7 -> Rank.CAPTAIN;
			case IF_OP8 -> Rank.GENERAL;
			case IF_OP9 -> Rank.OWNER;
			default -> Rank.UNRANKED;
		};
	}
}
