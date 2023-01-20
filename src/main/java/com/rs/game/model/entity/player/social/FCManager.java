// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.
//
//  Copyright (C) 2021 Trenton Kress
//  This file is part of project: Darkan
//
package com.rs.game.model.entity.player.social;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.rs.game.model.entity.player.Player;
import com.rs.lib.model.FriendsChat.Rank;
import com.rs.lib.net.ClientPacket;
import com.rs.lib.net.packets.decoders.fc.FCJoin;
import com.rs.lib.web.dto.FCData;
import com.rs.net.LobbyCommunicator;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ButtonClickHandler;

@PluginEventHandler
public class FCManager {

	private static final int FC_SETUP_INTER = 1108;
	private static final int FC_TAB = 1109;

	private static Map<String, FCData> FRIEND_CHATS = new ConcurrentHashMap<>();

	public static ButtonClickHandler handleInterface = new ButtonClickHandler(FC_SETUP_INTER, e -> {
		switch (e.getComponentId()) {
		case 1 -> {
			switch (e.getPacket()) {
			case IF_OP1 -> e.getPlayer().sendInputName("Enter chat prefix:", str -> {
				e.getPlayer().getSocial().getFriendsChat().setName(str);
				LobbyCommunicator.updateFC(e.getPlayer(), res -> {
					if (res == null)
						e.getPlayer().sendMessage("Error communicating with social service.");
					else
						refreshFC(e.getPlayer());
				});
			});
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
		LobbyCommunicator.updateFC(e.getPlayer(), res -> {
			if (res == null)
				e.getPlayer().sendMessage("Error communicating with social service.");
			else
				refreshFC(e.getPlayer());
		});
	});

	public static ButtonClickHandler handleTab = new ButtonClickHandler(FC_TAB, e -> {
		switch (e.getComponentId()) {
		case 26 -> {
			if (e.getPlayer().getSocial().getCurrentFriendsChat() != null) //TODO make sure to force leave the player from existing if they try to join new FC without leaving old
				LobbyCommunicator.forwardPackets(e.getPlayer(), new FCJoin().setOpcode(ClientPacket.FC_JOIN));
		}
		case 31 -> {
			if (e.getPlayer().getInterfaceManager().containsScreenInter()) {
				e.getPlayer().sendMessage("Please close the interface you have opened before using Friends Chat setup.");
				return;
			}
			e.getPlayer().stopAll();
			openFriendChatSetup(e.getPlayer());
		}
		case 19 -> e.getPlayer().toggleLootShare();
		}
	});

	public static void openFriendChatSetup(Player player) {
		player.getInterfaceManager().sendInterface(FC_SETUP_INTER);
		refreshFC(player);
		player.getPackets().setIFHidden(FC_SETUP_INTER, 49, true);
		player.getPackets().setIFHidden(FC_SETUP_INTER, 63, true);
		player.getPackets().setIFHidden(FC_SETUP_INTER, 77, true);
		player.getPackets().setIFHidden(FC_SETUP_INTER, 91, true);
	}

	public static void refreshFC(Player player) {
		player.getPackets().setIFText(FC_SETUP_INTER, 1, player.getSocial().getFriendsChat().getName() == null ? "Chat disabled" : player.getSocial().getFriendsChat().getName());
		sendRankRequirement(player, player.getSocial().getFriendsChat().getRankToEnter(), 2);
		sendRankRequirement(player, player.getSocial().getFriendsChat().getRankToSpeak(), 3);
		sendRankRequirement(player, player.getSocial().getFriendsChat().getRankToKick(), 4);
		sendRankRequirement(player, player.getSocial().getFriendsChat().getRankToLS(), 5);
	}

	public static void sendRankRequirement(Player player, Rank rank, int component) {
		switch (rank) {
		case UNRANKED -> player.getPackets().setIFText(FC_SETUP_INTER, component, "Anyone");
		case FRIEND -> player.getPackets().setIFText(FC_SETUP_INTER, component, "Any friends");
		case RECRUIT -> player.getPackets().setIFText(FC_SETUP_INTER, component, "Recruit+");
		case CORPORAL -> player.getPackets().setIFText(FC_SETUP_INTER, component, "Corporal+");
		case SERGEANT -> player.getPackets().setIFText(FC_SETUP_INTER, component, "Sergeant+");
		case LIEUTENANT -> player.getPackets().setIFText(FC_SETUP_INTER, component, "Lieutenant+");
		case CAPTAIN -> player.getPackets().setIFText(FC_SETUP_INTER, component, "Captain+");
		case GENERAL -> player.getPackets().setIFText(FC_SETUP_INTER, component, "General+");
		case OWNER -> player.getPackets().setIFText(FC_SETUP_INTER, component, component == 5 ? "No-one" : "Only me");
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

	public static FCData getFCData(String username) {
		if (username == null)
			return null;
		return FRIEND_CHATS.get(username);
	}

	public static void updateFCData(FCData fc) {
		FRIEND_CHATS.put(fc.getOwnerDisplayName(), fc);
	}
}
