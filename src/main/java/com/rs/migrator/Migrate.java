package com.rs.migrator;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.rs.Settings;
import com.rs.cache.Cache;
import com.rs.db.WorldDB;
import com.rs.game.npc.familiar.Familiar;
import com.rs.game.player.Player;
import com.rs.game.player.controllers.Controller;
import com.rs.lib.file.JsonFileManager;
import com.rs.lib.game.Item;
import com.rs.lib.game.WorldTile;
import com.rs.lib.json.DateAdapter;
import com.rs.lib.model.Account;
import com.rs.lib.model.FriendsChat;
import com.rs.lib.net.packets.Packet;
import com.rs.lib.net.packets.PacketEncoder;
import com.rs.lib.util.PacketAdapter;
import com.rs.lib.util.PacketEncoderAdapter;
import com.rs.lib.util.RecordTypeAdapterFactory;
import com.rs.lib.util.Utils;
import com.rs.migrator.legacyge.Offer;
import com.rs.utils.json.ControllerAdapter;
import com.rs.utils.json.FamiliarAdapter;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class Migrate {

	private final static String PATH = "./dumps/migration";

	private static final Set<String> PARSED_EMAILS = new HashSet<>();
	private static WorldDB DB;

	public static void main(String[] args) throws IOException {
		JsonFileManager.setGSON(new GsonBuilder()
				.registerTypeAdapter(Familiar.class, new FamiliarAdapter())
				.registerTypeAdapter(Controller.class, new ControllerAdapter())
				.registerTypeAdapter(Date.class, new DateAdapter())
				.registerTypeAdapter(PacketEncoder.class, new PacketEncoderAdapter())
				.registerTypeAdapter(Packet.class, new PacketAdapter())
				.registerTypeAdapterFactory(new RecordTypeAdapterFactory())
				.disableHtmlEscaping()
				.setPrettyPrinting()
				.create());
		Settings.loadConfig();
		Cache.init(Settings.getConfig().getCachePath());
		DB = new WorldDB();
		DB.init();
		File[] playerFiles = new File(PATH + "/legacy").listFiles();
		if (playerFiles == null) {
			System.err.println("No player files found!");
			return;
		}
		for (File f : playerFiles) {
			try {
				processPlayer(f);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static void processPlayer(File f) throws JsonIOException, IOException {
		LegacyPlayer legacy = JsonFileManager.loadJsonFile(f, LegacyPlayer.class);
		String username = Utils.formatPlayerNameForProtocol(f.getName().replace(".json", ""));
		try {
			Account account = migrateAccount(legacy, username);
			JsonFileManager.saveJsonFile(account, new File(PATH + "/accounts/" + username + ".json"));
		} catch (Exception e) {
			System.err.println("Failed to migrate account: " + username);
		}
		try {
			Player player = migratePlayer(legacy, username);
			JsonFileManager.saveJsonFile(player, new File(PATH + "/players/" + username + ".json"));
			WorldDB.getPlayers().saveSync(player);
		} catch (Exception e) {
			System.err.println("Failed to migrate player: " + username);
		}
	}

	private static Player migratePlayer(LegacyPlayer legacy, String username) throws IOException {
		Player player = JsonFileManager.loadJsonFile(new File(PATH + "/legacy/" + username + ".json"), Player.class);
		player.setUsername(username);
		player.setTile(new WorldTile(legacy.x, legacy.y, legacy.plane));
		if (legacy.getOfferSet() != null) {
			for (Offer offer : legacy.getOfferSet().offers) {
				if (offer == null)
					continue;
				if (offer.getOfferType() == Offer.OfferType.BUY) {
					if (offer.getCashToClaim() > 0)
						player.getBank().addItem(new Item(995, offer.getCashToClaim()), false);
					if (offer.getAmountProcessed() > 0)
						player.getBank().addItem(new Item(offer.getItemId(), offer.getAmountProcessed()), false);
					if (offer.getAmountLeft() > 0)
						player.getBank().addItem(new Item(995, offer.getAmountLeft() * offer.getPricePerItem()), false);
				} else if (offer.getOfferType() == Offer.OfferType.SELL) {
					if (offer.getCashToClaim() > 0)
						player.getBank().addItem(new Item(995, offer.getCashToClaim()), false);
					if (offer.getAmountLeft() > 0)
						player.getBank().addItem(new Item(offer.getItemId(), offer.getAmountLeft()), false);
				}
			}
		}
		JsonFileManager.saveJsonFile(player, new File(PATH + "/players/" + username + ".json"));
		player = JsonFileManager.loadJsonFile(new File(PATH + "/players/" + username + ".json"), Player.class);
		return player;
	}

	private static Account migrateAccount(LegacyPlayer player, String username) {
		Account account = new Account(username);
		if (PARSED_EMAILS.contains(player.getEmail())) {
			account.setRecoveryEmail(player.getEmail());
		} else {
			account.setEmail(player.getEmail());
			PARSED_EMAILS.add(player.getEmail());
		}
		account.setLegacyPass(player.getPassword());
		account.banSpecific(player.banned - System.currentTimeMillis());
		account.muteSpecific(player.muted - System.currentTimeMillis());
		account.setLastIP(player.getLastIP());
		account.getSocial().setStatus(player.getFriendsIgnores().getPrivateStatus());
		account.getSocial().setFcStatus(player.getFriendsIgnores().getFriendsChatStatus());
		account.getSocial().setCurrentFriendsChat(player.getCurrentFriendChatOwner());
		account.getSocial().getFriendsChat().setName(player.getFriendsIgnores().getChatName());
		account.getSocial().getFriendsChat().setRankToEnter(FriendsChat.Rank.forId(player.getFriendsIgnores().whoCanEnterChat));
		account.getSocial().getFriendsChat().setRankToSpeak(FriendsChat.Rank.forId(player.getFriendsIgnores().whoCanTalkOnChat));
		account.getSocial().getFriendsChat().setRankToKick(FriendsChat.Rank.forId(player.getFriendsIgnores().whoCanKickOnChat));
		account.getSocial().getFriendsChat().setRankToLS(FriendsChat.Rank.forId(player.getFriendsIgnores().whoCanShareloot));
		for (String name : player.getFriendsIgnores().getFriendsChatRanks().keySet())
			account.getSocial().getFriendsChat().setRank(name, FriendsChat.Rank.forId(player.getFriendsIgnores().getFriendsChatRanks().get(name)));
		for (String name : player.getFriendsIgnores().getFriends()) {
			name = Utils.formatPlayerNameForProtocol(name);
			if (!new File(PATH + "/legacy/" + name + ".json").exists())
				continue;
			account.getSocial().getFriends().add(name);
		}
		for (String name : player.getFriendsIgnores().getIgnores()) {
			name = Utils.formatPlayerNameForProtocol(name);
			if (!new File(PATH + "/legacy/" + name + ".json").exists())
				continue;
			account.getSocial().getIgnores().add(name);
		}
		return account;
	}
}
