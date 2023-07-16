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

import com.rs.engine.dialogue.Conversation;
import com.rs.game.model.entity.player.Equipment;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Item;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ItemClickHandler;

@PluginEventHandler
public class FlamingSkull extends Conversation {

	private static final String COLORS[] = { "Green", "Purple", "Blue", "Red" };
	
	public static ItemClickHandler handleSwitchColor = new ItemClickHandler(new Object[] { 24437, 24439, 24440, 24441 }, new String[] { "Change-colour", "Change colour" }, e -> {
		e.getPlayer().startConversation(new FlamingSkull(e.getPlayer(), e.getItem(), e.isEquipped()));
	});
	
	public FlamingSkull(Player player, Item item, boolean worn) {
		super(player);
		int index = (item.getId() == 24437 ? 24442 : item.getId()) - 24439;
		addOptions("What color do you want your skull to be?", ops -> {
			ops.add(COLORS[(index + 1) % 4], () -> {
				int itemId = 24439 + ((index + 1) % 4);
				item.setId(itemId == 24442 ? 24437 : itemId);
				if (worn) {
					player.getEquipment().refresh(Equipment.HEAD);
					player.getAppearance().generateAppearanceData();
				} else
					player.getInventory().refresh(item.getSlot());
			});
			ops.add(COLORS[(index + 2) % 4], () -> {
				int itemId = 24439 + ((index + 2) % 4);
				item.setId(itemId == 24442 ? 24437 : itemId);
				if (worn) {
					player.getEquipment().refresh(Equipment.HEAD);
					player.getAppearance().generateAppearanceData();
				} else
					player.getInventory().refresh(item.getSlot());
			});
			ops.add(COLORS[(index + 3) % 4], () -> {
				int itemId = 24439 + ((index + 3) % 4);
				item.setId(itemId == 24442 ? 24437 : itemId);
				if (worn) {
					player.getEquipment().refresh(Equipment.HEAD);
					player.getAppearance().generateAppearanceData();
				} else
					player.getInventory().refresh(item.getSlot());
			});
		});
	}
}
