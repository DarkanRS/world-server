package com.rs.plugin.handlers;

import com.rs.plugin.events.ButtonClickEvent;

public abstract class ButtonClickHandler extends PluginHandler<ButtonClickEvent> {
	public ButtonClickHandler(Object... interfaceIds) {
		super(interfaceIds);
	}
}
