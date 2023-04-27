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

import com.rs.game.model.entity.player.Player;
import com.rs.plugin.handlers.PlayerClickHandler;
import com.rs.plugin.handlers.PluginHandler;

import java.util.HashMap;
import java.util.Map;

public class PlayerClickEvent implements PluginEvent {

	private static Map<Object, PlayerClickHandler> HANDLERS = new HashMap<>();

	private Player player;
	private Player otherPlayer;
	private String option;
	private boolean atPlayer;

	public PlayerClickEvent(Player player, Player otherPlayer, String option, boolean atPlayer) {
		this.player = player;
		this.otherPlayer = otherPlayer;
		this.option = option;
		this.atPlayer = atPlayer;
	}

	public Player getPlayer() {
		return player;
	}

	public Player getTarget() {
		return otherPlayer;
	}

	public String getOption() {
		return option;
	}

	public boolean isAtPlayer() {
		return atPlayer;
	}

	@Override
	public PluginHandler<? extends PluginEvent> getMethod() {
		PlayerClickHandler method = HANDLERS.get(option);
		if ((method == null) || (!isAtPlayer() && method.isCheckDistance()))
			return null;
		return method;
	}

	public static void registerMethod(Class<?> eventType, PluginHandler<? extends PluginEvent> method) {
		for (Object key : method.keys()) {
			PluginHandler<? extends PluginEvent> old = HANDLERS.put(key, (PlayerClickHandler) method);
			if (old != null)
				System.err.println("ERROR: Duplicate NPCClick methods for key: " + key);
		}
	}

}
