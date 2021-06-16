package com.rs.game.player.content.world;

import com.rs.game.player.content.dialogue.Conversation;
import com.rs.game.player.content.dialogue.HeadE;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class ManDialogue  {

	static Object[][] POSSIBLE_MESSAGES = { { "I'm fine!", HeadE.HAPPY_TALKING }, { "I think we need a new king. The one we've got isn't good.", HeadE.CALM_TALK },
			{ "Not too bad. But I'm quite worried about the goblin population these days.", HeadE.CALM_TALK }, { "Who are you?..", HeadE.CONFUSED }, { "Hello.", HeadE.HAPPY_TALKING },
			{ "I've heard there are many fearsome creatures that dwell underground...", HeadE.NERVOUS }, { "I'm a little worried. I've heard there are people killing citizens at random.", HeadE.WORRIED } };

	public static Object[] getRandomMessage() {
		return POSSIBLE_MESSAGES[Utils.getRandomInclusive(POSSIBLE_MESSAGES.length - 1)];
	}

	public static NPCClickHandler handleTalkTo = new NPCClickHandler("Man") {
		@Override
		public void handle(NPCClickEvent e) {
			if (e.getOpNum() == 1) {
				Object[] message = getRandomMessage();
				e.getPlayer().startConversation(new Conversation(e.getPlayer()).addPlayer(HeadE.HAPPY_TALKING, "Hello, how's it going?")
						.addNPC(e.getNPC().getId(), (HeadE) message[1], (String) message[0]));
			}
		}
	};

}
