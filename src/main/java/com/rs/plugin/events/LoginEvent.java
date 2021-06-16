package com.rs.plugin.events;

import java.util.ArrayList;
import java.util.List;

import com.rs.game.player.Player;
import com.rs.plugin.handlers.PluginHandler;

public class LoginEvent implements PluginEvent {
	
	private static List<PluginHandler<? extends PluginEvent>> HANDLERS = new ArrayList<>();

	private Player player;

	public LoginEvent(Player player) {
		this.player = player;
	}

	public Player getPlayer() {
		return player;
	}

	@Override
	public List<PluginHandler<? extends PluginEvent>> getMethods() {
		return HANDLERS;
	}

	public static void registerMethod(Class<?> eventType, PluginHandler<? extends PluginEvent> method) {
		HANDLERS.add(method);
	}
}
