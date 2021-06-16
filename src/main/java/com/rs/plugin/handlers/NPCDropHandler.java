package com.rs.plugin.handlers;

import com.rs.plugin.events.NPCDropEvent;

public abstract class NPCDropHandler extends PluginHandler<NPCDropEvent> {
	public NPCDropHandler(Object... namesOrIds) {
		super(namesOrIds);
	}
}
