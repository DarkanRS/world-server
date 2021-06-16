package com.rs.plugin.handlers;

import com.rs.plugin.events.ItemOnNPCEvent;

public abstract class ItemOnNPCHandler extends PluginHandler<ItemOnNPCEvent> {
	private boolean checkDistance = true;
	
	public ItemOnNPCHandler(boolean checkDistance, Object[] namesOrIds) {
		super(namesOrIds);
		this.checkDistance = checkDistance;
	}
	
	public ItemOnNPCHandler(Object... namesOrIds) {
		super(namesOrIds);
	}

	public boolean isCheckDistance() {
		return checkDistance;
	}
}
