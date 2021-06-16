package com.rs.plugin.events;

import java.util.List;

import com.rs.plugin.handlers.PluginHandler;

public interface PluginEvent {
	
	public default PluginHandler<? extends PluginEvent> getMethod() {
		return null;
	}
	
	public default List<PluginHandler<? extends PluginEvent>> getMethods() {
		return null;
	}
}
