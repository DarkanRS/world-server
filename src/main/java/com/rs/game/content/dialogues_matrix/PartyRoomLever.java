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
package com.rs.game.content.dialogues_matrix;

import com.rs.game.content.minigames.partyroom.PartyRoom;
import com.rs.game.model.entity.pathing.RouteEvent;
import com.rs.lib.game.WorldTile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class PartyRoomLever extends MatrixDialogue {

	public static ObjectClickHandler handle = new ObjectClickHandler(false, new Object[] { 26194 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().setRouteEvent(new RouteEvent(new WorldTile(e.getObject()), () -> {
				e.getPlayer().getDialogueManager().execute(new PartyRoomLever());
			}));
		}
	};

	@Override
	public void start() {
		sendOptionsDialogue(SEND_DEFAULT_OPTIONS_TITLE, "Balloon Bonanza (1000 coins).", "Nightly Dance (500 coins).", "No action.");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (componentId == OPTION_1)
			PartyRoom.purchase(player, true);
		else if (componentId == OPTION_2)
			PartyRoom.purchase(player, false);
		end();
	}

	@Override
	public void finish() {

	}
}
