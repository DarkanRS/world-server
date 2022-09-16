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
package com.rs.game.content.dialogue.impl;

import com.rs.game.content.dialogue.Conversation;
import com.rs.game.content.dialogue.statements.DestroyItemStatement;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Item;
import com.rs.plugin.PluginManager;
import com.rs.plugin.events.DestroyItemEvent;

public class DestroyItem extends Conversation {
	
	public DestroyItem(Player player, int slotId, Item item) {
		this(player, slotId, item, null);
	}

	public DestroyItem(Player player, int slotId, Item item, String message) {
		super(player);
		addNext(new DestroyItemStatement(item, message));
		addNext(() -> {
			PluginManager.handle(new DestroyItemEvent(player, item));
			player.getInventory().deleteItem(slotId, item);
			if (item.getDefinitions().isBinded())
				player.getDungManager().unbind(item);
			player.soundEffect(4500);
		});
		create();
	}

}
