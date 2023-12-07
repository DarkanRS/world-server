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
import com.rs.plugin.handlers.PluginHandler;

import java.util.ArrayList;
import java.util.List;

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

	public Player component1() {
		return player;
	}
}
