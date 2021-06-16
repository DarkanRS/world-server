package com.rs.plugin.handlers;

import com.rs.plugin.events.XPGainEvent;

public abstract class XPGainHandler extends PluginHandler<XPGainEvent> {
	public XPGainHandler() {
		super(new Object[] { "meme" });
	}
}
