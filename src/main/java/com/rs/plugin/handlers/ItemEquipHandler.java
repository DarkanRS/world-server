package com.rs.plugin.handlers;

import com.rs.plugin.events.ItemEquipEvent;

public abstract class ItemEquipHandler extends PluginHandler<ItemEquipEvent> {
	public ItemEquipHandler(Object... namesOrIds) {
		super(namesOrIds);
	}
}
