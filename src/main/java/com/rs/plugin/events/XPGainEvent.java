package com.rs.plugin.events;

import java.util.ArrayList;
import java.util.List;

import com.rs.game.player.Player;
import com.rs.plugin.handlers.PluginHandler;

public class XPGainEvent implements PluginEvent {
	
	private static List<PluginHandler<? extends PluginEvent>> HANDLERS = new ArrayList<>();

	private Player player;
	private int skillId;
	private double xp;

	public XPGainEvent(Player player, int skillId, double xp) {
		this.player = player;
		this.skillId = skillId;
		this.xp = xp;
	}
	
	public Player getPlayer() {
		return player;
	}

	public int getSkillId() {
		return skillId;
	}

	public double getXp() {
		return xp;
	}

	@Override
	public List<PluginHandler<? extends PluginEvent>> getMethods() {
		return HANDLERS;
	}

	public static void registerMethod(Class<?> eventType, PluginHandler<? extends PluginEvent> method) {
		HANDLERS.add(method);
	}
}
