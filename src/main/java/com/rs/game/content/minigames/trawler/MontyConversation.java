package com.rs.game.content.minigames.trawler;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.statements.NPCStatement;
import com.rs.engine.dialogue.statements.OptionStatement;
import com.rs.engine.dialogue.statements.PlayerStatement;
import com.rs.game.model.entity.player.Player;

public class MontyConversation extends Conversation {
	public MontyConversation(Player player) {
		super(player);
		buildDialogue();
		//TODO - "Go diving" option or something
	}

	public void buildDialogue() {
		addNext(new PlayerStatement(HeadE.CHEERFUL, "Hello again, Murphy."));
		addNext(new NPCStatement(463, HeadE.CHEERFUL, "Good day to you land lover. Fancy hitting the high seas again?"));
		addNext("montyS", new OptionStatement("Select an Option", "No thanks, I still feel ill from last time.", "We keep sinking! Can you give me some advice?", "Yes, let's do it!"));

		getStage("montyS")
				.addNext(new PlayerStatement(HeadE.CHEERFUL, "No thanks, I still feel ill from last time."))
				.addNext(new NPCStatement(463, HeadE.CALM, "Hah! ...Softy."));

		getStage("montyS")
				.addNext(new PlayerStatement(HeadE.CALM, "We keep sinking! Can you give me some advice?"))
				.addNext(new NPCStatement(463, HeadE.CALM, "Aye aye matey! We'll soon get you ship shape!"))
				.addNext(new NPCStatement(463, HeadE.CALM, "You need a Fishing level of 15 or above to catch any fish on the trawler."))
				.addNext(new NPCStatement(463, HeadE.CALM, "On occasions the net rips, so you'll need some rope to repair it."))
				.addNext(new NPCStatement(463, HeadE.CALM, "Repairing the net is difficult in the harsh conditions so you'll find it easier with a higher Crafting level."))
				.addNext(new PlayerStatement(HeadE.CALM, "Ah yes, got it."))
				.addNext(new NPCStatement(463, HeadE.CALM, "There's also a slight problem with leaks."))
				.addNext(new PlayerStatement(HeadE.AMAZED, "Don't say!"))
				.addNext(new NPCStatement(463, HeadE.CALM, "Nothing some swamp paste won't fix..."))
				.addNext(new PlayerStatement(HeadE.CHEERFUL, "Right so I need lots of swamp paste and plenty of rope. Thanks Murphy!"))
				.addNext(new NPCStatement(463, HeadE.CALM, "If all else fails use a bailing bucket!"));

		getStage("montyS")
				.addNext(new PlayerStatement(HeadE.CHEERFUL, "Yes, let's do it!"))
				.addNext(new NPCStatement(463, HeadE.CALM, "Aye aye! Meet me on board the trawler. I just need to get a few things together."));

		create();

	}
}
