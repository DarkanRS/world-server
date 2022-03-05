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
package com.rs.game.player.content.dialogue.statements;

import com.rs.game.player.Player;
import com.rs.lib.util.Utils;

public class ItemStatement implements Statement {

	private int[] itemIds;
	private String[] text;

	public ItemStatement(int itemId, String... text) {
		this.itemIds =  new int[] { itemId };
		this.text = text;
	}

	public ItemStatement(int itemId1, int itemId2, String... text) {
		this.itemIds = new int[] { itemId1, itemId2 };
		this.text = text;
	}

	@Override
	public void send(Player player) {
		String text = "";
		for (String s : this.text)
			text += s + "<br>";
		Utils.interfaceIdFromHash(77922305);
		player.getInterfaceManager().sendChatBoxInterface(1189);
		player.getPackets().sendRunScript(3449, itemIds[0], 500);
		player.getPackets().setIFText(1189, 4, text);
		if (itemIds.length > 1)
			player.getPackets().setIFItem(1189, 3, itemIds[1], 1);
	}

	@Override
	public int getOptionId(int componentId) {
		return 0;
	}
}
