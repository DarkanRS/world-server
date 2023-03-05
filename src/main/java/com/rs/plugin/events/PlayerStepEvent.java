// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.
//
//  Copyright (C) 2021 Trenton Kress
//  This file is part of project: Darkan
//
package com.rs.plugin.events;

import java.util.HashMap;
import java.util.Map;

import com.rs.game.model.entity.pathing.WalkStep;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Tile;
import com.rs.plugin.handlers.PlayerStepHandler;
import com.rs.plugin.handlers.PluginHandler;

public class PlayerStepEvent implements PluginEvent {

	private static Map<Object, PlayerStepHandler> METHODS = new HashMap<>();

	private Player player;
	private Tile tile;
	private WalkStep step;

	public PlayerStepEvent(Player player, WalkStep step, Tile tile) {
		this.player = player;
		this.tile = tile;
		this.step = step;
	}

	public Player getPlayer() {
		return player;
	}

	public Tile getTile() {
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
		for (Object key : handler.keys())
			if (METHODS.put(key, handler) != null)
				System.err.println("Duplicate player step events for key " + key);
	}

}
