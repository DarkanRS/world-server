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

import com.rs.engine.dialogue.HeadE;
import com.rs.game.model.entity.player.Player;

public class PlayerStatement implements Statement {

	private HeadE emote;
	private String[] texts;

	public PlayerStatement(HeadE emote, String... texts) {
		this.emote = emote;
		this.texts = texts;
	}

	@Override
	public void send(Player player) {
		StringBuilder builder = new StringBuilder();
		for (String text2 : texts)
			builder.append(" " + text2);
		String text = builder.toString();
		player.getInterfaceManager().sendChatBoxInterface(1191);
		player.getPackets().setIFText(1191, 8, player.getDisplayName());
		player.getPackets().setIFText(1191, 17, text);
		player.getPackets().setIFPlayerHead(1191, 15);
		if (emote != null && emote.getEmoteId() != -1)
			player.getPackets().setIFAnimation(emote.getEmoteId(), 1191, 15);
	}

	@Override
	public int getOptionId(int componentId) {
		return 0;
	}

	@Override
	public void close(Player player) {
		
	}
}
