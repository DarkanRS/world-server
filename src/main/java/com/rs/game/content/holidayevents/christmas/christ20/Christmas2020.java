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
package com.rs.game.content.holidayevents.christmas.christ20;

import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.WorldTile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.annotations.ServerStartupEvent;
import com.rs.plugin.annotations.ServerStartupEvent.Priority;
import com.rs.plugin.events.ItemClickEvent;
import com.rs.plugin.events.LoginEvent;
import com.rs.plugin.handlers.ItemClickHandler;
import com.rs.plugin.handlers.LoginHandler;
import com.rs.utils.spawns.NPCSpawn;
import com.rs.utils.spawns.NPCSpawns;

@PluginEventHandler
public class Christmas2020 {

	public static final String STAGE_KEY = "christ2022";

	private static boolean ACTIVE = true;

	@ServerStartupEvent(Priority.FILE_IO)
	public static void load() {
		if (!ACTIVE)
			return;
		NPCSpawns.add(new NPCSpawn(9398, WorldTile.of(2655, 5678, 0), "Queen of Snow"));
		NPCSpawns.add(new NPCSpawn(9400, WorldTile.of(2654, 5679, 0), "Santa"));
	}

	public static LoginHandler login = new LoginHandler() {
		@Override
		public void handle(LoginEvent e) {
			if (!ACTIVE)
				return;
			e.getPlayer().getVars().setVarBit(6934, 1);
		}
	};

	public static ItemClickHandler handleYoyo = new ItemClickHandler(new Object[] { 4079 }, new String[] { "Play", "Loop", "Walk", "Crazy" }) {
		@Override
		public void handle(ItemClickEvent e) {
			switch(e.getOption()) {
			case "Play":
				e.getPlayer().setNextAnimation(new Animation(1457));
				break;
			case "Loop":
				e.getPlayer().setNextAnimation(new Animation(1458));
				break;
			case "Walk":
				e.getPlayer().setNextAnimation(new Animation(1459));
				break;
			case "Crazy":
				e.getPlayer().setNextAnimation(new Animation(1460));
				break;
			}
		}
	};

	public static ItemClickHandler handleReinhat = new ItemClickHandler(new Object[] { 10507 }, new String[] { "Emote" }) {
		@Override
		public void handle(ItemClickEvent e) {
			e.getPlayer().setNextAnimation(new Animation(5059));
			e.getPlayer().setNextSpotAnim(new SpotAnim(859));
		}
	};
}
