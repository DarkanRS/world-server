package com.rs.migrator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class FriendsIgnores {
	private String chatName;
	private HashMap<String, Integer> friendsChatRanks;
	private byte whoCanEnterChat;
	private byte whoCanTalkOnChat;
	private byte whoCanKickOnChat;
	private byte whoCanShareloot;
	private boolean coinshare;
	private byte friendsChatStatus;

	private Set<String> friends;
	private Set<String> ignores;

	private byte privateStatus;

	private transient LegacyPlayer player;

	public HashMap<String, Integer> getFriendsChatRanks() {
		if (friendsChatRanks == null) {// temporary
			whoCanKickOnChat = 7;
			whoCanShareloot = -1;
			friendsChatRanks = new HashMap<String, Integer>(200);
			for (String friend : friends)
				friendsChatRanks.put(friend, 0);
		}
		return friendsChatRanks;
	}

	public int getRank(String username) {
		Integer rank = getFriendsChatRanks().get(username);
		if (rank == null)
			return -1;
		return rank;
	}

	public int getWhoCanKickOnChat() {
		return whoCanKickOnChat;
	}

	public boolean hasRankToJoin(String username) {
		return getRank(username) >= whoCanEnterChat;
	}

	public String getChatName() {
		return chatName == null ? "" : chatName;
	}

	public Set<String> getIgnores() {
		return ignores;
	}

	public Set<String> getFriends() {
		return friends;
	}

	public boolean hasFriendChat() {
		return chatName != null;
	}

	public FriendsIgnores() {
		friends = new HashSet<String>(200);
		ignores = new HashSet<String>(100);
		friendsChatRanks = new HashMap<String, Integer>(200);
		whoCanKickOnChat = 7;
		whoCanShareloot = -1;
	}

	public void setPlayer(LegacyPlayer player) {
		this.player = player;
	}

	public byte getPrivateStatus() {
		return privateStatus;
	}

	public void setPrivateStatus(int privateStatus) {
		this.privateStatus = (byte) privateStatus;
	}

	public void init() {
	}

	public int getFriendsChatStatus() {
		return friendsChatStatus;
	}

	public void setFriendsChatStatus(int friendsChatStatus) {
		this.friendsChatStatus = (byte) friendsChatStatus;
	}

	public int getWhoCanShareLoot() {
		return whoCanShareloot;
	}

	public void setChatPrefix(String name) {
		if (name.length() < 1 || name.length() > 20)
			return;
		this.chatName = name;
		refreshChatName();
	}

	public void refreshChatName() {
	}

	public void changeRank(String username, int rank) {

	}

	public void refreshWhoCanShareloot() {
		String text;
		if (whoCanShareloot == 0)
			text = "Any friends";
		else if (whoCanShareloot == 1)
			text = "Recruit+";
		else if (whoCanShareloot == 2)
			text = "Corporal+";
		else if (whoCanShareloot == 3)
			text = "Sergeant+";
		else if (whoCanShareloot == 4)
			text = "Lieutenant+";
		else if (whoCanShareloot == 5)
			text = "Captain+";
		else if (whoCanShareloot == 6)
			text = "General+";
		else
			text = "No-one";
	}

	public void refreshWhoCanKickOnChat() {
		String text;
		if (whoCanKickOnChat == 0)
			text = "Any friends";
		else if (whoCanKickOnChat == 1)
			text = "Recruit+";
		else if (whoCanKickOnChat == 2)
			text = "Corporal+";
		else if (whoCanKickOnChat == 3)
			text = "Sergeant+";
		else if (whoCanKickOnChat == 4)
			text = "Lieutenant+";
		else if (whoCanKickOnChat == 5)
			text = "Captain+";
		else if (whoCanKickOnChat == 6)
			text = "General+";
		else if (whoCanKickOnChat == 7)
			text = "Only Me";
		else
			text = "Anyone";
	}

	public void refreshWhoCanTalkOnChat() {
		String text;
		if (whoCanTalkOnChat == 0)
			text = "Any friends";
		else if (whoCanTalkOnChat == 1)
			text = "Recruit+";
		else if (whoCanTalkOnChat == 2)
			text = "Corporal+";
		else if (whoCanTalkOnChat == 3)
			text = "Sergeant+";
		else if (whoCanTalkOnChat == 4)
			text = "Lieutenant+";
		else if (whoCanTalkOnChat == 5)
			text = "Captain+";
		else if (whoCanTalkOnChat == 6)
			text = "General+";
		else if (whoCanTalkOnChat == 7)
			text = "Only Me";
		else
			text = "Anyone";
	}

	public void refreshWhoCanEnterChat() {
		String text;
		if (whoCanEnterChat == 0)
			text = "Any friends";
		else if (whoCanEnterChat == 1)
			text = "Recruit+";
		else if (whoCanEnterChat == 2)
			text = "Corporal+";
		else if (whoCanEnterChat == 3)
			text = "Sergeant+";
		else if (whoCanEnterChat == 4)
			text = "Lieutenant+";
		else if (whoCanEnterChat == 5)
			text = "Captain+";
		else if (whoCanEnterChat == 6)
			text = "General+";
		else if (whoCanEnterChat == 7)
			text = "Only Me";
		else
			text = "Anyone";
	}

	public void openFriendChatSetup() {
		refreshChatName();
		refreshWhoCanEnterChat();
		refreshWhoCanTalkOnChat();
		refreshWhoCanKickOnChat();
		refreshWhoCanShareloot();
	}

}