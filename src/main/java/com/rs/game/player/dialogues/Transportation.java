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
//  Copyright © 2021 Trenton Kress
//  This file is part of project: Darkan
//
package com.rs.game.player.dialogues;

import com.rs.game.player.content.transportation.ItemTeleports;
import com.rs.lib.game.Item;

public class Transportation extends Dialogue {

	Item item;

	@Override
	public void start() {
		String[] locations = (String[]) parameters[0];
		if (parameters.length > 2 && parameters[2] != null)
			item = (Item) parameters[2];
		sendOptionsDialogue("Where would you like to teleport to", locations);
	}

	@Override
	public void run(int interfaceId, int componentId) {
		String[] locations = (String[]) parameters[0];
		if (item != null)
			ItemTeleports.sendTeleport(player, item, componentId == OPTION_1 ? 0 : componentId - 12);
		else
			ItemTeleports.sendTeleport(player, player.getInventory().getItems().lookup((Integer) parameters[1]), componentId == OPTION_1 ? 0 : componentId - 12, false, locations.length);
		end();
	}

	@Override
	public void finish() {
	}

}
