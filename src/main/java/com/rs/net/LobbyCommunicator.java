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
//  Copyright © 2021 Trenton Kress
//  This file is part of project: Darkan
//
package com.rs.net;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

import com.rs.Settings;
import com.rs.game.player.Player;
import com.rs.lib.model.Account;
import com.rs.lib.model.Clan;
import com.rs.lib.model.FriendsChat;
import com.rs.lib.net.packets.Packet;
import com.rs.lib.web.APIUtil;
import com.rs.lib.web.dto.LoginRequest;
import com.rs.lib.web.dto.PacketDto;
import com.rs.lib.web.dto.UpdateFC;
import com.rs.lib.web.dto.WorldPlayerAction;

public class LobbyCommunicator {

	public static void addWorldPlayer(Player player, Consumer<Boolean> cb) {
		post(Boolean.class, new WorldPlayerAction(player.getAccount(), Settings.getConfig().getWorldInfo()), "addworldplayer", cb);
	}

	public static void removeWorldPlayer(Player player) {
		post(new WorldPlayerAction(player.getAccount(), Settings.getConfig().getWorldInfo()), "removeworldplayer");
	}

	public static Account getAccountSync(String username, String password) throws InterruptedException, ExecutionException, IOException {
		return postSync(Account.class, new LoginRequest(username, password), "getaccountauth");
	}

	public static void getAccountByDisplay(String displayName, Consumer<Account> cb) {
		post(Account.class, new LoginRequest(displayName, "cock"), "getaccountbydisplay", cb);
	}

	public static void getAccount(String username, String password, Consumer<Account> cb) {
		post(Account.class, new LoginRequest(username, password), "getaccountauth", cb);
	}

	public static void getAccount(String username, Consumer<Account> cb) {
		post(Account.class, new LoginRequest(username, "cock"), "getaccount", cb);
	}

	public static void updatePunishments(Player player) {
		post(player.getAccount(), "updatepunishments");
	}

	public static void updatePunishments(Player player, Consumer<Boolean> cb) {
		post(Boolean.class, player.getAccount(), "updatepunishments", cb);
	}

	public static void updateRights(Player player) {
		post(player.getAccount(), "updaterights");
	}

	public static void updateRights(Player player, Consumer<Boolean> cb) {
		post(Boolean.class, player.getAccount(), "updaterights", cb);
	}

	public static void updateSocial(Player player) {
		post(player.getAccount(), "updatesocial");
	}

	public static void updateSocial(Player player, Consumer<Boolean> cb) {
		post(Boolean.class, player.getAccount(), "updatesocial", cb);
	}

	public static void updateFC(Player player, Consumer<FriendsChat> cb) {
		post(FriendsChat.class, new UpdateFC(player.getDisplayName(), player.getSocial().getFriendsChat()), "updatefc", cb);
	}

	public static void forwardPackets(Player player, Packet... packets) {
		post(Boolean.class, new PacketDto(player.getUsername(), packets), "forwardpackets", res -> {
			if (res != null && !res)
				player.sendMessage("Error forwarding packet to lobby.");
		});
	}

	public static void forwardPacket(Player player, Packet packet, Consumer<Boolean> cb) {
		post(Boolean.class, new PacketDto(player.getUsername(), new Packet[] { packet }), "forwardpackets", cb);
	}

	public static Clan getClan(String clan) {
		// TODO Auto-generated method stub
		return null;
	}

	public static void updateClan(Clan clan) {
		// TODO Auto-generated method stub

	}

	public static void post(Object body, String endpoint) {
		post(null, body, endpoint, null);
	}

	public static <T> void post(Class<T> type, Object body, String endpoint, Consumer<T> cb) {
		APIUtil.post(type, body, "http://"+Settings.getConfig().getLobbyIp()+":4040/api/"+endpoint, Settings.getConfig().getLobbyApiKey(), cb);
	}

	public static <T> T postSync(Class<T> type, Object body, String endpoint) throws InterruptedException, ExecutionException, IOException {
		return APIUtil.postSync(type, body, "http://"+Settings.getConfig().getLobbyIp()+":4040/api/"+endpoint, Settings.getConfig().getLobbyApiKey());
	}
}
