package com.rs.game.player;

import com.rs.lib.net.ClientPacket;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ButtonClickEvent;
import com.rs.plugin.handlers.ButtonClickHandler;

@PluginEventHandler
public class FriendsIgnores {
	
	public static ButtonClickHandler handle = new ButtonClickHandler(1108, 1109) {
		@Override
		public void handle(ButtonClickEvent e) {
			handleFriendChatButtons(e.getPlayer(), e.getInterfaceId(), e.getComponentId(), e.getPacket());
		}
	};

	public static void handleFriendChatButtons(Player player, int interfaceId, int componentId, ClientPacket packet) {
//		if (interfaceId == 1109) {
//			if (componentId == 26) {
//				if (player.getCurrentFriendChat() != null)
//					player.getCurrentFriendChat().leaveChat(player, false);
//			} else if (componentId == 31) {
//				if (player.getInterfaceManager().containsScreenInter()) {
//					player.sendMessage("Please close the interface you have opened before using Friends Chat setup.");
//					return;
//				}
//				player.stopAll();
//				openFriendChatSetup();
//			} else if (componentId == 19) {
//				player.toggleLootShare();
//			}
//		} else if (interfaceId == 1108) {
//			if (componentId == 1) {
//				if (packet == ClientPacket.IF_OP1) {
//					player.getPackets().sendRunScriptReverse(109, new Object[] { "Enter chat prefix:" });
//				} else if (packet == ClientPacket.IF_OP2) {
//					if (chatName != null) {
//						chatName = null;
//						refreshChatName();
//						FriendChatsManager.destroyChat(player);
//					}
//				}
//			} else if (componentId == 2) {
//				if (packet == ClientPacket.IF_OP1)
//					whoCanEnterChat = -1;
//				else if (packet == ClientPacket.IF_OP2)
//					whoCanEnterChat = 0;
//				else if (packet == ClientPacket.IF_OP3)
//					whoCanEnterChat = 1;
//				else if (packet == ClientPacket.IF_OP4)
//					whoCanEnterChat = 2;
//				else if (packet == ClientPacket.IF_OP5)
//					whoCanEnterChat = 3;
//				else if (packet == ClientPacket.IF_OP6)
//					whoCanEnterChat = 4;
//				else if (packet == ClientPacket.IF_OP7)
//					whoCanEnterChat = 5;
//				else if (packet == ClientPacket.IF_OP8)
//					whoCanEnterChat = 6;
//				else if (packet == ClientPacket.IF_OP9)
//					whoCanEnterChat = 7;
//				refreshWhoCanEnterChat();
//			} else if (componentId == 3) {
//				if (packet == ClientPacket.IF_OP1)
//					whoCanTalkOnChat = -1;
//				else if (packet == ClientPacket.IF_OP2)
//					whoCanTalkOnChat = 0;
//				else if (packet == ClientPacket.IF_OP3)
//					whoCanTalkOnChat = 1;
//				else if (packet == ClientPacket.IF_OP4)
//					whoCanTalkOnChat = 2;
//				else if (packet == ClientPacket.IF_OP5)
//					whoCanTalkOnChat = 3;
//				else if (packet == ClientPacket.IF_OP6)
//					whoCanTalkOnChat = 4;
//				else if (packet == ClientPacket.IF_OP7)
//					whoCanTalkOnChat = 5;
//				else if (packet == ClientPacket.IF_OP8)
//					whoCanTalkOnChat = 6;
//				else if (packet == ClientPacket.IF_OP9)
//					whoCanTalkOnChat = 7;
//				refreshWhoCanTalkOnChat();
//			} else if (componentId == 4) {
//				if (packet == ClientPacket.IF_OP1)
//					whoCanKickOnChat = -1;
//				else if (packet == ClientPacket.IF_OP2)
//					whoCanKickOnChat = 0;
//				else if (packet == ClientPacket.IF_OP3)
//					whoCanKickOnChat = 1;
//				else if (packet == ClientPacket.IF_OP4)
//					whoCanKickOnChat = 2;
//				else if (packet == ClientPacket.IF_OP5)
//					whoCanKickOnChat = 3;
//				else if (packet == ClientPacket.IF_OP6)
//					whoCanKickOnChat = 4;
//				else if (packet == ClientPacket.IF_OP7)
//					whoCanKickOnChat = 5;
//				else if (packet == ClientPacket.IF_OP8)
//					whoCanKickOnChat = 6;
//				else if (packet == ClientPacket.IF_OP9)
//					whoCanKickOnChat = 7;
//				refreshWhoCanKickOnChat();
//				FriendChatsManager.refreshChat(player);
//			} else if (componentId == 5) {
//				if (packet == ClientPacket.IF_OP1)
//					whoCanShareloot = -1;
//				else if (packet == ClientPacket.IF_OP2)
//					whoCanShareloot = 0;
//				else if (packet == ClientPacket.IF_OP3)
//					whoCanShareloot = 1;
//				else if (packet == ClientPacket.IF_OP4)
//					whoCanShareloot = 2;
//				else if (packet == ClientPacket.IF_OP5)
//					whoCanShareloot = 3;
//				else if (packet == ClientPacket.IF_OP6)
//					whoCanShareloot = 4;
//				else if (packet == ClientPacket.IF_OP7)
//					whoCanShareloot = 5;
//				else if (packet == ClientPacket.IF_OP8)
//					whoCanShareloot = 6;
//				refreshWhoCanShareloot();
//			}
//		}
	}

//	// friends chat
//	private String chatName;
//	private HashMap<String, Integer> friendsChatRanks;
//	private byte whoCanEnterChat;
//	private byte whoCanTalkOnChat;
//	private byte whoCanKickOnChat;
//	private byte whoCanShareloot;
//	@SuppressWarnings("unused")
//	private boolean coinshare;
//	private byte friendsChatStatus;
//
//	// friends list
//	private Set<String> friends;
//	private Set<String> ignores;
//	private Set<String> tillLogoutIgnores;
//
//	private byte privateStatus;
//
//	private transient Player player;
//
//	public HashMap<String, Integer> getFriendsChatRanks() {
//		if (friendsChatRanks == null) {// temporary
//			whoCanKickOnChat = 7;
//			whoCanShareloot = -1;
//			friendsChatRanks = new HashMap<String, Integer>(200);
//			for (String friend : friends)
//				friendsChatRanks.put(friend, 0);
//		}
//		return friendsChatRanks;
//	}
//
//	public boolean canTalk(Player player) {
//		return getRank(player.getUsername()) >= whoCanTalkOnChat;
//	}
//
//	public int getRank(String username) {
//		Integer rank = getFriendsChatRanks().get(username);
//		if (rank == null)
//			return -1;
//		return rank;
//	}
//
//	public int getWhoCanKickOnChat() {
//		return whoCanKickOnChat;
//	}
//
//	public boolean hasRankToJoin(String username) {
//		return getRank(username) >= whoCanEnterChat;
//	}
//
//	public String getChatName() {
//		return chatName == null ? "" : chatName;
//	}
//
//	public Set<String> getIgnores() {
//		return ignores;
//	}
//
//	public Set<String> getFriends() {
//		return friends;
//	}
//
//	public boolean hasFriendChat() {
//		return chatName != null;
//	}
//
//	public FriendsIgnores() {
//		friends = new HashSet<String>(200);
//		ignores = new HashSet<String>(100);
//		friendsChatRanks = new HashMap<String, Integer>(200);
//		whoCanKickOnChat = 7;
//		whoCanShareloot = -1;
//	}
//
//	public void setPlayer(Player player) {
//		this.player = player;
//	}
//
//	public byte getPrivateStatus() {
//		return privateStatus;
//	}
//
//	public void setPrivateStatus(int privateStatus) {
//		this.privateStatus = (byte) privateStatus;
//		sendFriendsMyStatus();
//	}
//
//	public void updateFriendStatus(Player p2) {
//		if (!friends.contains(p2.getUsername()))
//			return;
//		player.getPackets().sendFriend(p2.getDisplayName());
//	}
//
//	public void sendFriendsMyStatus() {
//		for (Player p2 : World.getPlayers()) {
//			if (p2 == null || !p2.hasStarted() || p2.hasFinished())
//				continue;
//			p2.getFriendsIgnores().updateFriendStatus(player);
//		}
//		for (Player p2 : Lobby.getPlayers()) {
//			if (p2 == null)
//				continue;
//			p2.getFriendsIgnores().updateFriendStatus(player);
//		}
//	}
//
//	public void sendMessage(Player p2, String message) {
//		if (privateStatus == 2) {// off
//			privateStatus = 0;
//			sendFriendsMyStatus();
//			player.getPackets().sendChatFilterSettingsPrivateChat();
//		}
//		if (!player.getFriendsIgnores().onlineTo(p2) || !p2.getFriendsIgnores().onlineTo(player)) {
//			player.sendMessage("Player is not online.");
//			return;
//		}
//		player.getPackets().sendPrivateMessage(p2.getDisplayName(), message);
//		p2.getPackets().receivePrivateMessage(player.getDisplayName(), player.getMessageIcon(), message);
//	}
//
//	public void sendQuickChatMessage(Player p2, QuickChatMessage quickChatMessage) {
//		if (!player.getFriendsIgnores().onlineTo(p2) || !p2.getFriendsIgnores().onlineTo(player)) {
//			player.sendMessage("Player is not online.");
//			return;
//		}
//		player.getPackets().sendPrivateQuickMessage(p2.getDisplayName(), quickChatMessage);
//		p2.getPackets().receivePrivateChatQuickMessage(player.getDisplayName(), player.getMessageIcon(), quickChatMessage);
//
//	}
//
//	public void addIgnore(String username, boolean tillLogout) {
//		if (ignores.size() + tillLogoutIgnores.size() >= 100) {
//			player.sendMessage("Your ignores list is full.");
//			return;
//		}
//		if (username.equals(player.getUsername())) {
//			player.sendMessage("You can't add yourself.");
//			return;
//		}
//		if (friends.contains(username)) {
//			player.sendMessage("Please remove " + username + " from your friends list first.");
//			return;
//		}
//		Player p2 = World.getPlayer(username);
//		if (p2 == null)
//			p2 = Lobby.getAccount(username);
//		String formatedUsername = p2 != null ? p2.getUsername() : Utils.formatPlayerNameForProtocol(username);
//		if (Utils.containsInvalidCharacter(formatedUsername))
//			return;
//		if (ignores.contains(formatedUsername) || tillLogoutIgnores.contains(formatedUsername)) {
//			player.sendMessage(formatedUsername + " is already on your ignores list.");
//			return;
//		}
//		if (tillLogout)
//			tillLogoutIgnores.add(formatedUsername);
//		else
//			ignores.add(formatedUsername);
//		player.getPackets().sendIgnore(Utils.formatPlayerNameForDisplay(username));
//	}
//
//	public void removeIgnore(String username) {
//		String formatedUsername = Utils.formatPlayerNameForProtocol(username);
//		Player p2 = World.getPlayer(username);
//		if (p2 == null)
//			p2 = Lobby.getAccount(username);
//		if (!ignores.remove(formatedUsername) && !tillLogoutIgnores.remove(formatedUsername)) {
//			if (p2 == null)
//				return;
//			if (!ignores.remove(p2.getUsername()))
//				tillLogoutIgnores.remove(p2.getUsername());
//		}
//	}
//
//	public void addFriend(String username) {
//		if (friends.size() >= 200) {
//			player.sendMessage("Your friends list is full.");
//			return;
//		}
//		if (username.equals(player.getUsername())) {
//			player.sendMessage("You can't add yourself.");
//			return;
//		}
//		if (ignores.contains(player.getUsername()) || tillLogoutIgnores.contains(player.getUsername())) {
//			player.sendMessage("Please remove " + username + " from your ignore list first.");
//			return;
//		}
//		Player p2 = World.getPlayer(username);
//		if (p2 == null)
//			p2 = Lobby.getAccount(username);
//		String formatedUsername = Utils.formatPlayerNameForProtocol(username);
//		if (friends.contains(formatedUsername)) {
//			player.sendMessage((username) + " is already on your friends list.");
//			return;
//		}
//		friends.add(formatedUsername);
//		getFriendsChatRanks().put(formatedUsername, 0);
//		FriendChatsManager.refreshChat(player);
//		player.getPackets().sendFriend(Utils.formatPlayerNameForDisplay(username));
//		if (privateStatus == 1 && p2 != null)
//			p2.getFriendsIgnores().updateFriendStatus(player);
//	}
//
//	public void removeFriend(String username) {
//		String formatedUsername = Utils.formatPlayerNameForProtocol(username);
//		Player p2 = World.getPlayer(username);
//		if (p2 == null)
//			p2 = Lobby.getAccount(username);
//		if (!friends.remove(formatedUsername)) {
//			if (p2 == null)
//				return;
//			friends.remove(p2.getUsername());
//			getFriendsChatRanks().remove(p2.getUsername());
//			FriendChatsManager.refreshChat(player);
//		} else {
//			getFriendsChatRanks().remove(formatedUsername);
//			FriendChatsManager.refreshChat(player);
//		}
//		if (privateStatus == 1 && p2 != null)
//			p2.getFriendsIgnores().updateFriendStatus(player);
//	}
//
//	public boolean onlineTo(Player p2) {
//		if (p2.getTempB("realFinished") || player.getTempB("realFinished"))
//			return false;
//		if (p2.getFriendsIgnores().privateStatus == 2)
//			return false;
//		if (p2.getFriendsIgnores().privateStatus == 1 && !p2.getFriendsIgnores().friends.contains(player.getUsername()))
//			return false;
//		return true;
//	}
//
//	public void init() {
//		tillLogoutIgnores = new HashSet<String>(100);
//		player.getPackets().sendAllFriends();
//		player.getPackets().sendIgnores();
//		if (privateStatus != 2)
//			sendFriendsMyStatus();
//		if (hasFriendChat())
//			FriendChatsManager.linkSettings(player);
//	}
//
//	public int getFriendsChatStatus() {
//		return friendsChatStatus;
//	}
//
//	public void setFriendsChatStatus(int friendsChatStatus) {
//		this.friendsChatStatus = (byte) friendsChatStatus;
//	}
	
//	public int getWhoCanShareLoot() {
//		return whoCanShareloot;
//	}
//
//	public void setChatPrefix(String name) {
//		if (name.length() < 1 || name.length() > 20)
//			return;
//		this.chatName = name;
//		refreshChatName();
//		FriendChatsManager.refreshChat(player);
//	}
//
//	public void refreshChatName() {
//		player.getPackets().setIFText(1108, 1, chatName == null ? "Chat disabled" : chatName);
//	}
//	
//	public void changeRank(String username, int rank) {
//		if (rank < 0 || rank > 6)
//			return;
//		String formatedUsername = Utils.formatPlayerNameForProtocol(username);
//		if (!friends.contains(formatedUsername))
//			return;
//		getFriendsChatRanks().put(formatedUsername, rank);
//		player.getPackets().sendFriend(Utils.formatPlayerNameForDisplay(username));
//		FriendChatsManager.refreshChat(player);
//	}
//
//	public void refreshWhoCanShareloot() {
//		String text;
//		if (whoCanShareloot == 0)
//			text = "Any friends";
//		else if (whoCanShareloot == 1)
//			text = "Recruit+";
//		else if (whoCanShareloot == 2)
//			text = "Corporal+";
//		else if (whoCanShareloot == 3)
//			text = "Sergeant+";
//		else if (whoCanShareloot == 4)
//			text = "Lieutenant+";
//		else if (whoCanShareloot == 5)
//			text = "Captain+";
//		else if (whoCanShareloot == 6)
//			text = "General+";
//		else
//			text = "No-one";
//		player.getPackets().setIFText(1108, 5, text);
//	}
//
//	public void refreshWhoCanKickOnChat() {
//		String text;
//		if (whoCanKickOnChat == 0)
//			text = "Any friends";
//		else if (whoCanKickOnChat == 1)
//			text = "Recruit+";
//		else if (whoCanKickOnChat == 2)
//			text = "Corporal+";
//		else if (whoCanKickOnChat == 3)
//			text = "Sergeant+";
//		else if (whoCanKickOnChat == 4)
//			text = "Lieutenant+";
//		else if (whoCanKickOnChat == 5)
//			text = "Captain+";
//		else if (whoCanKickOnChat == 6)
//			text = "General+";
//		else if (whoCanKickOnChat == 7)
//			text = "Only Me";
//		else
//			text = "Anyone";
//		player.getPackets().setIFText(1108, 4, text);
//	}
//
//	public void refreshWhoCanTalkOnChat() {
//		String text;
//		if (whoCanTalkOnChat == 0)
//			text = "Any friends";
//		else if (whoCanTalkOnChat == 1)
//			text = "Recruit+";
//		else if (whoCanTalkOnChat == 2)
//			text = "Corporal+";
//		else if (whoCanTalkOnChat == 3)
//			text = "Sergeant+";
//		else if (whoCanTalkOnChat == 4)
//			text = "Lieutenant+";
//		else if (whoCanTalkOnChat == 5)
//			text = "Captain+";
//		else if (whoCanTalkOnChat == 6)
//			text = "General+";
//		else if (whoCanTalkOnChat == 7)
//			text = "Only Me";
//		else
//			text = "Anyone";
//		player.getPackets().setIFText(1108, 3, text);
//	}
//
//	public void refreshWhoCanEnterChat() {
//		String text;
//		if (whoCanEnterChat == 0)
//			text = "Any friends";
//		else if (whoCanEnterChat == 1)
//			text = "Recruit+";
//		else if (whoCanEnterChat == 2)
//			text = "Corporal+";
//		else if (whoCanEnterChat == 3)
//			text = "Sergeant+";
//		else if (whoCanEnterChat == 4)
//			text = "Lieutenant+";
//		else if (whoCanEnterChat == 5)
//			text = "Captain+";
//		else if (whoCanEnterChat == 6)
//			text = "General+";
//		else if (whoCanEnterChat == 7)
//			text = "Only Me";
//		else
//			text = "Anyone";
//		player.getPackets().setIFText(1108, 2, text);
//	}
//
//	public void openFriendChatSetup() {
//		player.getInterfaceManager().sendInterface(1108);
//		refreshChatName();
//		refreshWhoCanEnterChat();
//		refreshWhoCanTalkOnChat();
//		refreshWhoCanKickOnChat();
//		refreshWhoCanShareloot();
//		player.getPackets().setIFHidden(1108, 49, true);
//		player.getPackets().setIFHidden(1108, 63, true);
//		player.getPackets().setIFHidden(1108, 77, true);
//		player.getPackets().setIFHidden(1108, 91, true);
//	}

}
