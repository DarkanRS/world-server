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

import com.rs.game.content.skills.magic.Alchemy;
import com.rs.game.content.skills.magic.Enchanting;
import com.rs.game.content.skills.magic.Lunars;
import com.rs.game.model.entity.player.Equipment;
import com.rs.game.model.entity.player.Inventory;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Item;
import com.rs.lib.game.Rights;
import com.rs.lib.net.packets.PacketHandler;
import com.rs.lib.net.packets.decoders.interfaces.IFOnIF;
import com.rs.lib.util.Logger;
import com.rs.lib.util.Utils;
import com.rs.net.decoders.handlers.InventoryOptionsHandler;
import com.rs.plugin.PluginManager;
import com.rs.plugin.events.ButtonClickEvent;
import com.rs.plugin.events.IFOnIFEvent;

public class IFOnIFHandler implements PacketHandler<Player, IFOnIF> {

	@Override
	public void handle(Player player, IFOnIF packet) {
		if ((packet.getFromInter() >= Utils.getInterfaceDefinitionsSize()) || (player.isLocked() || player.isDead() || !player.getInterfaceManager().topOpen(packet.getFromInter())))
			return;
		if ((packet.getFromComp() != 65535 && Utils.getInterfaceDefinitionsComponentsSize(packet.getFromInter()) <= packet.getFromComp()))
			return;
		if ((packet.getToInter() >= Utils.getInterfaceDefinitionsSize()) || !player.getInterfaceManager().topOpen(packet.getToInter()))
			return;
		if ((packet.getToComp() != 65535 && Utils.getInterfaceDefinitionsComponentsSize(packet.getToInter()) <= packet.getToComp()))
			return;
		PluginManager.handle(new IFOnIFEvent(player, packet.getFromInter(), packet.getFromComp(), packet.getFromSlot(), packet.getFromItemId(), packet.getToInter(), packet.getToComp(), packet.getToSlot(), packet.getToItemId()));
		Logger.debug(IFOnIFHandler.class, "handle", "IF on IF: (" + packet.getFromInter() + ", " + packet.getFromComp() + ", " + packet.getFromSlot() + ") -> (" + packet.getToInter() + ", " + packet.getToComp() + ", " + packet.getToSlot() + ")");
	}

}
