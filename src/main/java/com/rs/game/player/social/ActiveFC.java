package com.rs.game.player.social;

import java.util.Set;
import com.rs.game.player.Player;
import com.rs.lib.game.Rights;
import com.rs.lib.model.Account;
import com.rs.lib.model.FriendsChat;

public class ActiveFC {

	private Account owner;
	private FriendsChat settings;
	private Set<String> usernames;
	
	public int getRank(Player player) {
		return getRank(player.getRights(), player.getUsername());
	}

	private int getRank(Rights rights, String username) {
		if (rights.ordinal() >= Rights.ADMIN.ordinal())
			return 127;
		if (username.equals(owner.getUsername()))
			return 7;
		return settings.getRank(username);
	}
	
	public Set<String> getUsers() {
		return usernames;
	}

}
