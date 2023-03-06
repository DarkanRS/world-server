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
package com.rs.game.content.transportation;

import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Tile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ButtonClickHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class GnomeGlider {

	private static final int GLIDER_INTEFACE = 138;
	private static final Tile[] GLIDERS = {
			Tile.of(3,38,54,32,47),
			Tile.of(0,44,54,29,42),
			Tile.of(0,51,53,57,38),
			Tile.of(0,51,50,20,11),
			Tile.of(0,46,46,29,25),
			Tile.of(0,39,46,53,27),
			Tile.of(0,39,49,0,55)
	};

	private static final int[][] CONFIGS = {
			{ -1, 1, 3, 4, 7, 10, 12 },
			{ -1, 2, -1, 5, 6, 11, 13 }
	};

	public static ButtonClickHandler handleButtons = new ButtonClickHandler(138, e -> {
		if (e.getPlayer().getTempAttribs().getB("using_carrier"))
			return;

		switch (e.getComponentId()) {
		case 19:
			sendGlider(e.getPlayer(), 6, false);
			break;
		case 22:
			sendGlider(e.getPlayer(), 4, false);
		case 23:
			//grand tree...
			break;
		case 24:
			sendGlider(e.getPlayer(), 1, false);
			break;
		case 25:
			sendGlider(e.getPlayer(), 2, false);
			break;
		case 26:
			sendGlider(e.getPlayer(), 3, false);
			break;
		case 27:
			sendGlider(e.getPlayer(), 5, false);
			break;
		default:
			break;
		}
	});

	public static NPCClickHandler openInterface = new NPCClickHandler(new Object[] { 1800, 3809, 3810, 3811, 3812, 6563 }, e -> {
		switch (e.getNPCId()) {
		case 3809:
			sendGlider(e.getPlayer(), 3, true);
			break;
		case 3810:
			sendGlider(e.getPlayer(), 1, true);
			break;
		case 3812:
			sendGlider(e.getPlayer(), 4, true);
			break;
		case 1800:
			sendGlider(e.getPlayer(), 5, true);
			break;
		case 6563:
			sendGlider(e.getPlayer(), 6, true);
			break;
		default:
			break;
		}
		e.getPlayer().getInterfaceManager().sendInterface(GLIDER_INTEFACE);
	});

	public static void sendGlider(final Player player, final int index, final boolean isReturning) {
		player.getVars().setVar(153, CONFIGS[isReturning ? 1 : 0][index]);
		player.getTempAttribs().setB("using_carrier", true);
		FadingScreen.fade(player, 3, () -> {
			player.useStairs(-1, GLIDERS[isReturning ? 0 : index], 0, 2);
			player.closeInterfaces();
			player.getTempAttribs().removeB("using_carrier");
			player.getVars().setVar(153, 0);
		});
	}
}
