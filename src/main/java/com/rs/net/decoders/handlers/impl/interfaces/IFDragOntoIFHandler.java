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
package com.rs.net.decoders.handlers.impl.interfaces;

import com.rs.engine.Shop;
import com.rs.game.model.entity.player.Inventory;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.net.packets.PacketHandler;
import com.rs.lib.net.packets.decoders.interfaces.IFDragOntoIF;
import com.rs.lib.util.Utils;

public class IFDragOntoIFHandler implements PacketHandler<Player, IFDragOntoIF> {

	@Override
	public void handle(Player player, IFDragOntoIF packet) {
		if (Utils.getInterfaceDefinitionsSize() <= packet.getFromInter() || Utils.getInterfaceDefinitionsSize() <= packet.getToInter() || !player.getInterfaceManager().topOpen(packet.getFromInter()) || !player.getInterfaceManager().topOpen(packet.getToInter()))
			return;
		if ((packet.getFromComp() != -1 && Utils.getInterfaceDefinitionsComponentsSize(packet.getFromInter()) <= packet.getFromComp()) || (packet.getToComp() != -1 && Utils.getInterfaceDefinitionsComponentsSize(packet.getToInter()) <= packet.getToComp()))
			return;

		if (packet.getFromInter() == Inventory.INVENTORY_INTERFACE && packet.getFromComp() == 0 && packet.getToInter() == Inventory.INVENTORY_INTERFACE && packet.getToComp() == 0) {
			if (packet.getToSlot()-28 < 0 || packet.getToSlot()-28 >= player.getInventory().getItemsContainerSize() || packet.getFromSlot() >= player.getInventory().getItemsContainerSize())
				return;
			player.getInventory().switchItem(packet.getFromSlot(), packet.getToSlot()-28);
		} else if (packet.getFromInter() == 763 && packet.getFromComp() == 0 && packet.getToInter() == 763 && packet.getToComp() == 0) {
			if (packet.getToSlot() >= player.getInventory().getItemsContainerSize() || packet.getFromSlot() >= player.getInventory().getItemsContainerSize())
				return;
			player.getInventory().switchItem(packet.getFromSlot(), packet.getToSlot());
		} else if (packet.getFromInter() == 762 && packet.getToInter() == 762)
			player.getBank().switchItem(packet.getFromSlot(), packet.getToSlot(), packet.getFromComp(), packet.getToComp());
		else if (packet.getFromInter() == 1265 && packet.getToInter() == 1266 && player.getTempAttribs().getB("shop_buying")) {
			if (player.getTempAttribs().getB("shop_buying")) {
				Shop shop = player.getTempAttribs().getO("Shop");
				if (shop == null)
					return;
				shop.buy(player, packet.getFromSlot(), 1);
			}
		} else if (packet.getFromInter() == 34 && packet.getToInter() == 34)
			player.getNotes().switchNotes(packet.getFromSlot(), packet.getToSlot());
	}

}
