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
package com.rs.game.player.content.interfacehandlers;

import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ButtonClickEvent;
import com.rs.plugin.handlers.ButtonClickHandler;

@PluginEventHandler
public class Logout {

	public static ButtonClickHandler handle = new ButtonClickHandler(182) {
		@Override
		public void handle(ButtonClickEvent e) {
			if (e.getPlayer().getInterfaceManager().containsInventoryInter())
				return;
			if (e.getComponentId() == 6 || e.getComponentId() == 13)
				if (!e.getPlayer().hasFinished())
					e.getPlayer().logout(e.getComponentId() == 6);
		}
	};

}
