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
package com.rs.net.decoders.handlers.impl;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.ge.GE;
import com.rs.game.player.Player;
import com.rs.lib.net.packets.PacketHandler;
import com.rs.lib.net.packets.decoders.GEItemSelect;

public class GEItemSelectHandler implements PacketHandler<Player, GEItemSelect> {

	@Override
	public void handle(Player player, GEItemSelect packet) {
		ItemDefinitions def = ItemDefinitions.getDefs(packet.getItemId());
		if (def != null)
			GE.selectItem(player, packet.getItemId(), 1);
	}

}
