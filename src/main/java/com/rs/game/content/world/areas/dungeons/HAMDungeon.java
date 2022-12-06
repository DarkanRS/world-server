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
package com.rs.game.content.world.areas.dungeons;

import com.rs.lib.game.WorldTile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.LoginEvent;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.handlers.LoginHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class HAMDungeon {

	public static LoginHandler unlockTrapdoor = new LoginHandler() {
		@Override
		public void handle(LoginEvent e) {
			e.getPlayer().getVars().setVarBit(2270, 2);
		}
	};

	public static ObjectClickHandler handleKeyTrapdoor = new ObjectClickHandler(new Object[] { 15766, 15747 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (e.getObjectId() == 15766) {
				if (e.getOption().equals("Climb-down"))
					e.getPlayer().useLadder(WorldTile.of(2568, 5185, 0));
			} else
				e.getPlayer().useLadder(WorldTile.of(3166, 9623, 0));
		}
	};
}
