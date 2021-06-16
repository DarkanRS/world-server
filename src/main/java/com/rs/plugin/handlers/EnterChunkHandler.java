package com.rs.plugin.handlers;

import com.rs.plugin.events.EnterChunkEvent;

public abstract class EnterChunkHandler extends PluginHandler<EnterChunkEvent> {
	public EnterChunkHandler() {
		super(new Object[] { "meme" });
	}
}
