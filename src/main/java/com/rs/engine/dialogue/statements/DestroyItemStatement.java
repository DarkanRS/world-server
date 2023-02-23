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
package com.rs.engine.dialogue.statements;

import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Item;

public class DestroyItemStatement implements Statement {
	
	private Item item;
	private String message;

	public DestroyItemStatement(Item item, String message) {
		this.item = item;
		this.message = message;
	}
	
	@Override
	public void send(Player player) {
		player.getInterfaceManager().sendChatBoxInterface(1183);
		player.getPackets().setIFText(1183, 7, item.getName());
		if (message != null)
			player.getPackets().setIFText(1183, 12, message);
		player.getPackets().setIFItem(1183, 13, item.getId(), 1);
	}

	@Override
	public int getOptionId(int componentId) {
		return componentId == 9 ? 0 : 1;
	}

	@Override
	public void close(Player player) {
		
	}
}
