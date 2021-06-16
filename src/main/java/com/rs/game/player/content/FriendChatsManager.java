package com.rs.game.player.content;

public class FriendChatsManager {

//	private String owner;
//	private String ownerDisplayName;
//	private FriendsIgnores settings;
//	private CopyOnWriteArrayList<Player> players;
//	private ConcurrentHashMap<String, Long> bannedPlayers;
//	private byte[] dataBlock;
//
//
//	private ClanWars clanWars;
//
//	private static HashMap<String, FriendChatsManager> cachedFriendChats = new HashMap<>();
//
//	public int getRank(Rights staffRights, String username) {
//		if (staffRights.ordinal() >= Rights.ADMIN.ordinal())
//			return 127;
//		if (username.equals(owner))
//			return 7;
//		return settings.getRank(username);
//	}
//
//	public CopyOnWriteArrayList<Player> getPlayers() {
//		return players;
//	}
//
//	public int getWhoCanKickOnChat() {
//		return settings.getWhoCanKickOnChat();
//	}
//
//	public String getOwnerDisplayName() {
//		return ownerDisplayName;
//	}
//
//	public String getOwnerName() {
//		return owner;
//	}
//
//	public String getChannelName() {
//		return settings.getChatName().replaceAll("<img=", "");
//	}
//
//	private void joinChat(Player player) {
//		synchronized (this) {
//			if (!player.getUsername().equals(owner) && !settings.hasRankToJoin(player.getUsername()) && !player.hasRights(Rights.ADMIN)) {
//				player.sendMessage("You do not have a enough rank to join this friends chat channel.");
//				return;
//			}
//			if (players.size() >= 100) {
//				player.sendMessage("This chat is full.");
//				return;
//			}
//			Long bannedSince = bannedPlayers.get(player.getUsername());
//			if (bannedSince != null) {
//				if (bannedSince + 3600000 > System.currentTimeMillis()) {
//					player.sendMessage("You have been banned from this channel.");
//					return;
//				}
//				bannedPlayers.remove(player.getUsername());
//			}
//			joinChatNoCheck(player);
//		}
//	}
//
//	public void leaveChat(Player player, boolean logout) {
//		synchronized (this) {
//			player.setCurrentFriendChat(null);
//			players.remove(player);
//			if (players.size() == 0) { // no1 at chat so uncache it
//				synchronized (cachedFriendChats) {
//					cachedFriendChats.remove(owner);
//				}
//			} else
//				refreshChannel();
//			if (!logout) {
//				player.setCurrentFriendChatOwner(null);
//				player.sendMessage("You have left the channel.");
//				player.getPackets().sendFriendsChatChannel();
//			}
//			if (clanWars != null) {
//				clanWars.leave(player, false);
//			}
//		}
//	}
//
//	public Player getPlayer(String username) {
//		String formatedUsername = Utils.formatPlayerNameForProtocol(username);
//		for (Player player : players) {
//			if (player.getUsername().equals(formatedUsername) || player.getDisplayName().equals(username))
//				return player;
//		}
//		return null;
//	}
//
//	public void kickPlayerFromChat(Player player, String username) {
//		String name = "";
//		for (char character : username.toCharArray()) {
//			name += Utils.containsInvalidCharacter(character) ? " " : character;
//		}
//		synchronized (this) {
//			int rank = getRank(player.getRights(), player.getUsername());
//			if (rank < getWhoCanKickOnChat())
//				return;
//			Player kicked = getPlayer(name);
//			if (kicked == null) {
//				player.sendMessage("This player is not this channel.");
//				return;
//			}
//			if (rank <= getRank(kicked.getRights(), kicked.getUsername()))
//				return;
//			kicked.setCurrentFriendChat(null);
//			kicked.setCurrentFriendChatOwner(null);
//			players.remove(kicked);
//			bannedPlayers.put(kicked.getUsername(), System.currentTimeMillis());
//			kicked.getPackets().sendFriendsChatChannel();
//			kicked.sendMessage("You have been kicked from the friends chat channel.");
//			player.sendMessage("You have kicked " + kicked.getUsername() + " from friends chat channel.");
//			refreshChannel();
//
//		}
//	}
//
//	private void joinChatNoCheck(Player player) {
//		synchronized (this) {
//			if (players.contains(player))
//				players.remove(player);
//			players.add(player);
//			player.setCurrentFriendChat(this);
//			player.setCurrentFriendChatOwner(owner);
//			player.sendMessage("You are now talking in the friends chat channel " + settings.getChatName());
//			refreshChannel();
//		}
//	}
//
//	public void destroyChat() {
//		synchronized (this) {
//			for (Player player : players) {
//				player.setCurrentFriendChat(null);
//				player.setCurrentFriendChatOwner(null);
//				player.getPackets().sendFriendsChatChannel();
//				player.sendMessage("You have been removed from this channel!");
//			}
//		}
//		synchronized (cachedFriendChats) {
//			cachedFriendChats.remove(owner);
//		}
//
//	}
//	
//	public int getWhoCanShareLoot() {
//		return settings.getWhoCanShareLoot();
//	}
//
//	public void sendQuickMessage(Player player, QuickChatMessage message) {
//		synchronized (this) {
//			if (!player.getUsername().equals(owner) && !settings.canTalk(player) && !player.hasRights(Rights.ADMIN)) {
//				player.sendMessage("You do not have a enough rank to talk on this friends chat channel.");
//				return;
//			}
//			int rights = player.getMessageIcon();
//			for (Player p2 : players)
//				p2.getPackets().receiveFriendChatQuickMessage(player.getDisplayName(), rights, settings.getChatName(), message);
//		}
//	}
//
//	public void sendMessage(Player player, String message) {
//		synchronized (this) {
//			if (!player.getUsername().equals(owner) && !settings.canTalk(player) && !player.hasRights(Rights.ADMIN)) {
//				player.sendMessage("You do not have a enough rank to talk on this friends chat channel.");
//				return;
//			}
//			int rights = player.getMessageIcon();
//			for (Player p2 : players)
//				p2.getPackets().sendFriendsChatMessage(player.getDisplayName(), rights, settings.getChatName(), message);
//		}
//	}
//
//	public void sendDiceMessage(Player player, String message) {
//		synchronized (this) {
//			if (!player.getUsername().equals(owner) && !settings.canTalk(player) && !player.hasRights(Rights.ADMIN)) {
//				player.sendMessage("You do not have a enough rank to talk on this friends chat channel.");
//				return;
//			}
//			for (Player p2 : players) {
//				p2.sendMessage(message);
//			}
//		}
//	}
//
//	private void refreshChannel() {
//		synchronized (this) {
//			OutputStream stream = new OutputStream();
//			stream.writeString(ownerDisplayName);
//			String ownerName = Utils.formatPlayerNameForDisplay(owner);
//			stream.writeByte(getOwnerDisplayName().equals(ownerName) ? 0 : 1);
//			if (!getOwnerDisplayName().equals(ownerName))
//				stream.writeString(ownerName);
//			stream.writeLong(Utils.stringToLong(getChannelName()));
//			int kickOffset = stream.getOffset();
//			stream.writeByte(0);
//			stream.writeByte(getPlayers().size());
//			for (Player player : getPlayers()) {
//				String displayName = player.getDisplayName();
//				String name = Utils.formatPlayerNameForDisplay(player.getUsername());
//				stream.writeString(displayName);
//				stream.writeByte(displayName.equals(name) ? 0 : 1);
//				if (!displayName.equals(name))
//					stream.writeString(name);
//				stream.writeShort(player.getWorldId());
//				int rank = getRank(player.getRights(), player.getUsername());
//				stream.writeByte(rank);
//				stream.writeString(player.getWorldStatus());
//			}
//			dataBlock = new byte[stream.getOffset()];
//			stream.setOffset(0);
//			stream.getBytes(dataBlock, 0, dataBlock.length);
//			for (Player player : players) {
//				dataBlock[kickOffset] = (byte) (player.getUsername().equals(owner) ? 0 : getWhoCanKickOnChat());
//				player.getPackets().sendFriendsChatChannel();
//			}
//		}
//	}
//
//	public byte[] getDataBlock() {
//		return dataBlock;
//	}
//
//	private FriendChatsManager(Player player) {
//		owner = player.getUsername();
//		ownerDisplayName = player.getDisplayName();
//		settings = player.getFriendsIgnores();
//		players = new CopyOnWriteArrayList<Player>();
//		bannedPlayers = new ConcurrentHashMap<String, Long>();
//	}
//
//	public static void destroyChat(Player player) {
//		synchronized (cachedFriendChats) {
//			FriendChatsManager chat = cachedFriendChats.get(player.getUsername());
//			if (chat == null)
//				return;
//			chat.destroyChat();
//			player.sendMessage("Your friends chat channel has now been disabled!");
//		}
//	}
//
//	public static void linkSettings(Player player) {
//		synchronized (cachedFriendChats) {
//			FriendChatsManager chat = cachedFriendChats.get(player.getUsername());
//			if (chat == null)
//				return;
//			chat.settings = player.getFriendsIgnores();
//		}
//	}
//
//	public static void refreshChat(Player player) {
//		synchronized (cachedFriendChats) {
//			FriendChatsManager chat = cachedFriendChats.get(player.getUsername());
//			if (chat == null)
//				return;
//			chat.refreshChannel();
//		}
//	}
//
//	public static void joinChat(String ownerName, Player player) {
//		synchronized (cachedFriendChats) {
//			if (player.getCurrentFriendChat() != null)
//				return;
//			player.sendMessage("Attempting to join channel...");
//			String formatedName = Utils.formatPlayerNameForProtocol(ownerName);
//			FriendChatsManager chat = cachedFriendChats.get(formatedName);
//			if (chat == null) {
//				Player owner = World.getPlayer(ownerName);
//				if (owner == null) {
//					if (!JsonFileManager.containsPlayer(formatedName)) {
//						player.sendMessage("The channel you tried to join does not exist.");
//						return;
//					}
//					owner = JsonFileManager.loadPlayer(formatedName);
//					if (owner == null) {
//						player.sendMessage("The channel you tried to join does not exist.");
//						return;
//					}
//					owner.setUsername(formatedName);
//				}
//				FriendsIgnores settings = owner.getFriendsIgnores();
//				if (!settings.hasFriendChat()) {
//					player.sendMessage("The channel you tried to join does not exist.");
//					return;
//				}
//				if (!player.getUsername().equals(ownerName) && !settings.hasRankToJoin(player.getUsername()) && !player.hasRights(Rights.ADMIN)) {
//					player.sendMessage("You do not have a enough rank to join this friends chat channel.");
//					return;
//				}
//				if (cachedFriendChats.get(ownerName) != null) {
//					cachedFriendChats.remove(ownerName);
//				}
//				chat = new FriendChatsManager(owner);
//				cachedFriendChats.put(ownerName, chat);
//				chat.joinChatNoCheck(player);
//			} else
//				chat.joinChat(player);
//		}
//
//	}
//
//	public ClanWars getClanWars() {
//		return clanWars;
//	}
//
//	public void setClanWars(ClanWars clanWars) {
//		this.clanWars = clanWars;
//	}
//
//	public int averageCombatLevel() {
//		int total = 0;
//		int totalP = 0;
//		for (Player p : getPlayers()) {
//			if (p == null)
//				continue;
//			total += p.getSkills().getCombatLevelWithSummoning();
//			totalP++;
//		}
//		if (totalP == 0)
//			return 0;
//		return total / totalP;
//	}
}
