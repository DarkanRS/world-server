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
package com.rs.plugin.events;

import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Item;
import com.rs.plugin.handlers.ItemClickHandler;
import com.rs.plugin.handlers.PluginHandler;

import java.util.HashMap;
import java.util.Map;

public class ItemClickEvent implements PluginEvent {

	private static Map<Object, ItemClickHandler> HANDLERS = new HashMap<>();

	private Player player;
	private Item item;
	private String option;
	private boolean isEquipped;
	private int slotId;

	public ItemClickEvent(Player player, Item item, int slotId, String option) {
		this(player, item, slotId, option, false);
	}

	public ItemClickEvent(Player player, Item item, int slotId, String option, boolean isEquipped) {
		this.player = player;
		this.item = item.setSlot(slotId);
		this.option = option;
		this.isEquipped = isEquipped;
		this.slotId = slotId;
	}

	public boolean isEquipped() {
		return isEquipped;
	}

	public String getOption() {
		return option;
	}

	public Player getPlayer() {
		return player;
	}

	public Item getItem() {
		return item;
	}

	public int getSlotId() {
		return slotId;
	}

	@Override
	public PluginHandler<? extends PluginEvent> getMethod() {
		ItemClickHandler method = HANDLERS.get(item.getId());
		if (method == null || !method.containsOption(option))
			method = HANDLERS.get(item.getDefinitions().getName());
		if (method == null || !method.containsOption(option))
			method = HANDLERS.get(option);
		if (method == null)
			return null;
		return method;
	}

	public static void registerMethod(Class<?> eventType, PluginHandler<? extends PluginEvent> method) {
		for (Object key : method.keys()) {
			PluginHandler<? extends PluginEvent> old = HANDLERS.put(key, (ItemClickHandler) method);
			if (old != null)
				System.err.println("ERROR: Duplicate ItemClick methods for key: " + key);
		}
	}
}
