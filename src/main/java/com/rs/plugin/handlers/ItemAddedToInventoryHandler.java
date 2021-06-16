package com.rs.plugin.handlers;

import com.rs.plugin.events.ItemAddedToInventoryEvent;

public abstract class ItemAddedToInventoryHandler extends PluginHandler<ItemAddedToInventoryEvent> {
	public ItemAddedToInventoryHandler(Object... namesOrIds) {
		super(namesOrIds);
	}
}
