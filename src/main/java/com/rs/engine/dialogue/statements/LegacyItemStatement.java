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

public class LegacyItemStatement implements Statement {

	private int[] itemIds;
	private String title;
	private String[] text;

	public LegacyItemStatement(int item, String title, String... text) {
		itemIds = new int[] { item };
		this.title = title;
		this.text = text;
	}

	public LegacyItemStatement(int item1, int item2, String title, String... text) {
		itemIds = new int[] { item1, item2 };
		this.title = title;
		this.text = text;
	}

	@Override
	public void send(Player player) {
		int interfaceId = itemIds.length > 1 ? 131 : 519;
		String text = "";
		text += title+"<br>";
		for (String s : this.text)
			text += s + "<br>";
		player.getInterfaceManager().sendChatBoxInterface(interfaceId);
		player.getPackets().setIFText(interfaceId, 1, text);
		player.getPackets().setIFItem(interfaceId, 0, itemIds[0], 1);
		if (itemIds.length > 1)
			player.getPackets().setIFItem(interfaceId, 2, itemIds[1], 1);
	}

	@Override
	public int getOptionId(int componentId) {
		return 0;
	}

	@Override
	public void close(Player player) {
		
	}
}
