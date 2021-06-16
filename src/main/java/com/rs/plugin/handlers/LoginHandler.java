package com.rs.plugin.handlers;

import com.rs.plugin.events.LoginEvent;

public abstract class LoginHandler extends PluginHandler<LoginEvent> {
	public LoginHandler() {
		super(new Object[] { "meme" });
	}
}
