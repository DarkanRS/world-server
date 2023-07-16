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
package com.rs.game.content.holidayevents.halloween.hw09;

import com.rs.cache.loaders.NPCDefinitions;
import com.rs.engine.dialogue.statements.Statement;
import com.rs.game.model.entity.player.Player;

public class SpiderStatement implements Statement {

	private String[] texts;

	public SpiderStatement(String... texts) {
		this.texts = texts;
	}

	@Override
	public void send(Player player) {
		StringBuilder builder = new StringBuilder();
		for (String text2 : texts)
			builder.append(" " + text2);
		String text = builder.toString();
		player.getInterfaceManager().sendChatBoxInterface(1184);
		player.getPackets().setIFText(1184, 17, NPCDefinitions.getDefs(8985, player.getVars()).getName());
		player.getPackets().setIFText(1184, 13, text);
		player.getPackets().setIFModel(1184, 11, 24613);
		player.getPackets().setIFAngle(1184, 11, 100, 1900, 500);
		player.getPackets().setIFAnimation(6247, 1184, 11);
	}

	@Override
	public int getOptionId(int componentId) {
		return 0;
	}

	@Override
	public void close(Player player) {
		
	}

}
