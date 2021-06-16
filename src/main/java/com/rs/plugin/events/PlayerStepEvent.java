package com.rs.plugin.events;

import java.util.HashMap;
import java.util.Map;

import com.rs.game.pathing.WalkStep;
import com.rs.game.player.Player;
import com.rs.lib.game.WorldTile;
import com.rs.plugin.handlers.PlayerStepHandler;
import com.rs.plugin.handlers.PluginHandler;

public class PlayerStepEvent implements PluginEvent {
	
	private static Map<Object, PlayerStepHandler> METHODS = new HashMap<>();

	private Player player;
	private WorldTile tile;
	private WalkStep step;

	public PlayerStepEvent(Player player, WalkStep step, WorldTile tile) {
		this.player = player;
		this.tile = tile;
		this.step = step;
	}

	public Player getPlayer() {
		return player;
	}
	
	public WorldTile getTile() {
		return tile;
	}
	
	public WalkStep getStep() {
		return step;
	}
	
	@Override
	public PluginHandler<? extends PluginEvent> getMethod() {
		return METHODS.get(tile.getTileHash());
	}

	public static void registerMethod(Class<?> eventType, PluginHandler<? extends PluginEvent> method) {
		PlayerStepHandler handler = (PlayerStepHandler) method;
		for (Object key : handler.keys()) {
			if (METHODS.put(key, handler) != null)
				System.err.println("Duplicate player step events for key " + key);
		}
	}

}
