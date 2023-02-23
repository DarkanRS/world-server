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
package com.rs.game.content.world.unorganized_dialogue;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.HeadE;
import com.rs.game.model.entity.player.Player;

public class ClanItemClaim extends Conversation {
	public ClanItemClaim(Player player, int itemId) {
		super(player);
		addNPC(itemId == 20708 ? 13633 : 5915, HeadE.HAPPY_TALKING, "Why of course you can have a " + ItemDefinitions.getDefs(itemId).name.toLowerCase() + ".");
		addItem(itemId, "You are handed a " + ItemDefinitions.getDefs(itemId).name.toLowerCase() + ".", () -> player.getInventory().addItem(itemId));
	}
}
