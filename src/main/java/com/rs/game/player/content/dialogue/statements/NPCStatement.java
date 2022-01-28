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

import com.rs.cache.loaders.NPCDefinitions;
import com.rs.game.player.Player;
import com.rs.game.player.content.dialogue.HeadE;

public class NPCStatement implements Statement {

	private int npcId;
	private HeadE emote;
	private String[] texts;

	public NPCStatement(int npcId, HeadE emote, String... texts) {
		this.npcId = npcId;
		this.emote = emote;
		this.texts = texts;
	}

	@Override
	public void send(Player player) {
		StringBuilder builder = new StringBuilder();
		for (String text2 : texts)
			builder.append(" " + text2);
		String text = builder.toString();
		player.getInterfaceManager().sendChatBoxInterface(1184);
		player.getPackets().setIFText(1184, 17, NPCDefinitions.getDefs(npcId, player.getVars()).getName());
		player.getPackets().setIFText(1184, 13, text);
		player.getPackets().setIFNPCHead(1184, 11, npcId);
		if (emote != null && emote.getEmoteId() != -1)
			player.getPackets().setIFAnimation(emote.getEmoteId(), 1184, 11);
	}

	@Override
	public int getOptionId(int componentId) {
		return 0;
	}
}
