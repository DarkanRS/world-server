package com.rs.net;

import java.util.function.Consumer;

import com.rs.Settings;
import com.rs.game.player.Player;
import com.rs.lib.game.QuickChatMessage;
import com.rs.lib.model.Account;
import com.rs.lib.model.Clan;
import com.rs.lib.net.packets.Packet;
import com.rs.lib.web.APIUtil;
import com.rs.lib.web.dto.LoginRequest;
import com.rs.lib.web.dto.PacketDto;
import com.rs.lib.web.dto.WorldPlayerAction;

public class LobbyCommunicator {
	
	public static void addWorldPlayer(Player player, Consumer<Boolean> cb) {
		post(Boolean.class, new WorldPlayerAction(player.getAccount(), Settings.getConfig().getWorldInfo()), "addworldplayer", cb);
	}
	
	public static void removeWorldPlayer(Player player) {
		post(new WorldPlayerAction(player.getAccount(), Settings.getConfig().getWorldInfo()), "removeworldplayer");
	}
	
	public static Account getAccountSync(String username, String password) {
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
	
	public static void updateAccount(Player player) {
		post(player.getAccount(), "updatewholeaccount");
	}
	
	public static void updateAccount(Player player, Consumer<Account> cb) {
		post(Account.class, player.getAccount(), "updatewholeaccount", cb);
	}
	
	public static void forwardPackets(Player player, Packet... packets) {
		post(Boolean.class, new PacketDto(player.getUsername(), packets), "forwardpackets", res -> {
			if (res != null && !res)
				player.sendMessage("Error forwarding packet to lobby.");
		});
	}
	
	public static void clanChatKick(Player player, boolean guest, String name) {
		// TODO Auto-generated method stub
		
	}

	public static void sendFCMessage(Player player, String message) {
		// TODO Auto-generated method stub
		
	}

	public static void sendCCMessage(Player player, String message) {
		// TODO Auto-generated method stub
		
	}

	public static void sendGCCMessage(Player player, String message) {
		// TODO Auto-generated method stub
		
	}

	public static void sendFCQuickChat(Player player, QuickChatMessage quickChatMessage) {
		// TODO Auto-generated method stub
		
	}

	public static void sendCCQuickChat(Player player, QuickChatMessage quickChatMessage) {
		// TODO Auto-generated method stub
		
	}

	public static void sendGCCQuickChat(Player player, QuickChatMessage quickChatMessage) {
		// TODO Auto-generated method stub
		
	}

	public static void sendPMQuickChat(Player player, String toUsername, QuickChatMessage quickChatMessage) {
		// TODO Auto-generated method stub
		
	}

	public static void leaveFC(Player player) {
		// TODO Auto-generated method stub
		
	}

	public static void joinFC(Player player, String name) {
		// TODO Auto-generated method stub
		
	}

	public static void kickFCPlayer(Player player, String name) {
		// TODO Auto-generated method stub
		
	}

	public static Clan getClan(String clan) {
		// TODO Auto-generated method stub
		return null;
	}

	public static void createClan(Player player, String text) {
		// TODO Auto-generated method stub
		
	}
	
	public static void updateClan(Clan clan) {
		// TODO Auto-generated method stub
		
	}	

	public static void connectToClan(Player player, String text, boolean b) {
		// TODO Auto-generated method stub
		
	}

	public static void banClanPlayer(Player player, String text) {
		// TODO Auto-generated method stub
		
	}

	public static void unbanClanPlayer(Player player, String text) {
		// TODO Auto-generated method stub
		
	}

	public static void setClanMotto(Player player, String text) {
		// TODO Auto-generated method stub
		
	}

	public static void addClanMember(Clan clan, Player player) {
		// TODO Auto-generated method stub
		
	}

	public static void leaveClanCompletely(Player player) {
		// TODO Auto-generated method stub
		
	}
	
	public static void post(Object body, String endpoint) {
		post(null, body, endpoint, null);
	}
	
	public static <T> void post(Class<T> type, Object body, String endpoint, Consumer<T> cb) {
		APIUtil.post(type, body, "http://"+Settings.getConfig().getLobbyIp()+":4040/api/"+endpoint, Settings.getConfig().getLobbyApiKey(), cb);
	}
	
	public static <T> T postSync(Class<T> type, Object body, String endpoint) {
		return APIUtil.postSync(type, body, "http://"+Settings.getConfig().getLobbyIp()+":4040/api/"+endpoint, Settings.getConfig().getLobbyApiKey());
	}
}
