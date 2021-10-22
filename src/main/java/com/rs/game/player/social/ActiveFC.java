package com.rs.game.player.social;

import java.util.Set;

import com.rs.game.player.Player;
import com.rs.lib.game.Rights;
import com.rs.lib.model.Account;
import com.rs.lib.model.FriendsChat;
import com.rs.lib.model.FriendsChat.Rank;

public class ActiveFC {

	private Account owner;
	private FriendsChat settings;
	private Set<String> usernames;
	
	public Rank getRank(Player player) {
		return getRank(player.getRights(), player.getUsername());
	}

	private Rank getRank(Rights rights, String username) {
		if (rights.ordinal() >= Rights.ADMIN.ordinal())
			return Rank.JMOD;
		if (username.equals(owner.getUsername()))
			return Rank.OWNER;
		return settings.getRank(username);
	}
	
	public Set<String> getUsers() {
		return usernames;
	}

	public FriendsChat getSettings() {
		return settings;
	}
	
	public Account getOwner() {
		return owner;
	}
}
