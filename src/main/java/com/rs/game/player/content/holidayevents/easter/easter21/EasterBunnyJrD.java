package com.rs.game.player.content.holidayevents.easter.easter21;

import com.rs.game.player.Player;
import com.rs.game.player.content.dialogue.Conversation;
import com.rs.game.player.content.dialogue.HeadE;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class EasterBunnyJrD extends Conversation {
	
	private static final int EASTER_BUNNY_JR = 7411;
	
	public static NPCClickHandler handleEasterBunnyJrTalk = new NPCClickHandler(EASTER_BUNNY_JR) {
		@Override
		public void handle(NPCClickEvent e) {
			e.getPlayer().startConversation(new EasterBunnyJrD(e.getPlayer()));
		}
	};

	public EasterBunnyJrD(Player player) {
		super(player);

		switch(player.getI(Easter2021.STAGE_KEY)) {
		case 4:
			addPlayer(HeadE.CHEERFUL, "Hello!");
			addNPC(EASTER_BUNNY_JR, HeadE.CAT_PURRING, "What do you want?..");
			addPlayer(HeadE.CHEERFUL, "I need help fixing the incubator and your father told me you might know what happened to it.");
			addNPC(EASTER_BUNNY_JR, HeadE.CAT_SAD, "Sure whatever.. The incubator exploded and they're somewhere around the factory.");
			addPlayer(HeadE.CONFUSED, "Can you tell me where they are exactly?..");
			addNPC(EASTER_BUNNY_JR, HeadE.CAT_SAD, "I don't really care and I don't remember.. All I know is that 3 parts went flying off when it exploded. Leave me alone now, I'm trying to sleep.");
			addPlayer(HeadE.ANGRY, "That doesn't help much at all!");
			addNPC(EASTER_BUNNY_JR, HeadE.CAT_DISAPPOINTED2, "Ok boomer.");
			addPlayer(HeadE.ROLL_EYES, "*What a lazy sack of garbage.*", () -> {
				player.save(Easter2021.STAGE_KEY, 5);
			});
			break;
		case 5:
			addPlayer(HeadE.ANGRY, "Tell me where the incubator parts are.");
			addNPC(EASTER_BUNNY_JR, HeadE.CAT_SAD, "As I said before, I don't really care and I don't remember.. All I know is that 3 parts went flying off around the factory when it exploded.");
			break;
		default:
			addPlayer(HeadE.CHEERFUL, "Hello!");
			addNPC(EASTER_BUNNY_JR, HeadE.CAT_PURRING, "Zzzz...");
			break;
		}
		
		create();
	}

}
