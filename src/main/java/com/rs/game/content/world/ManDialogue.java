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
package com.rs.game.content.world;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.HeadE;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class ManDialogue  {

	static Object[][] POSSIBLE_MESSAGES = { { "I'm fine!", HeadE.HAPPY_TALKING }, { "I think we need a new king. The one we've got isn't good.", HeadE.CALM_TALK },
			{ "Not too bad. But I'm quite worried about the goblin population these days.", HeadE.CALM_TALK }, { "Who are you?..", HeadE.CONFUSED }, { "Hello.", HeadE.HAPPY_TALKING },
			{ "I've heard there are many fearsome creatures that dwell underground...", HeadE.NERVOUS }, { "I'm a little worried. I've heard there are people killing citizens at random.", HeadE.WORRIED } };

	public static Object[] getRandomMessage() {
		return POSSIBLE_MESSAGES[Utils.getRandomInclusive(POSSIBLE_MESSAGES.length - 1)];
	}

	public static NPCClickHandler handleTalkTo = new NPCClickHandler(new Object[] { "Man", "Woman" }, new String[] { "Talk-to" }, e -> {
		if (e.getOpNum() == 1) {
			Object[] message = getRandomMessage();
			e.getPlayer().startConversation(new Conversation(e.getPlayer()).addPlayer(HeadE.HAPPY_TALKING, "Hello, how's it going?")
					.addNPC(e.getNPC().getId(), (HeadE) message[1], (String) message[0]));
		}
	});

}
