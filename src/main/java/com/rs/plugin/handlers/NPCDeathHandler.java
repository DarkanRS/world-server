package com.rs.plugin.handlers;

import com.rs.plugin.events.NPCDeathEvent;

public abstract class NPCDeathHandler extends PluginHandler<NPCDeathEvent> {
	public NPCDeathHandler(Object... namesOrIds) {
		super(namesOrIds);
	}
}
