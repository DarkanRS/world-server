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
