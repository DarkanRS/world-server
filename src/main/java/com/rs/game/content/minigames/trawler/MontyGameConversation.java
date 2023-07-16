package com.rs.game.content.minigames.trawler;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.engine.dialogue.statements.NPCStatement;
import com.rs.engine.dialogue.statements.PlayerStatement;
import com.rs.engine.dialogue.statements.SimpleStatement;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.util.Utils;

public class MontyGameConversation extends Conversation {

	public MontyGameConversation(Player player) {
		super(player);
		buildDialogue();
	}

	public void buildDialogue() {

		FishingTrawler trawler = FishingTrawler.getInstance();

		addNext(new NPCStatement(463, HeadE.CALM, "Woooahh sailor!"));
		addOptions("Select an Option", new Options() {
			@Override
			public void create() {
				option("I've had enough, take me back.", new Dialogue()
						.addNext(new PlayerStatement(HeadE.CALM, "I've had enough, take me back."))
						.addNext(new NPCStatement(463, HeadE.CALM, "Hah! The soft landlubbers have lost their sea legs, eh?"))
						.addNext(new PlayerStatement(HeadE.CALM, "Something like that."))
						.addNext(new NPCStatement(463, HeadE.CALM, "We're too far out now, it'd be dangerous."))
						.addOptions("Select an Option", new Options() {
							@Override
							public void create() {
								option("I insist Murphy, take me back.", new Dialogue()
										.addNext(new PlayerStatement(HeadE.CALM, "I insist Murphy, take me back."))
										.addNext(new NPCStatement(463, HeadE.CALM, "Ok, ok, I'll try, but don't say I didn't warn you.."))
										.addNext(new SimpleStatement("Murphy attempts to sharply turn the large ship."))
										.addNext(new NPCStatement(463, HeadE.CALM, "Save yourself! We're going down!"))
										.addNext(() -> trawler.crash(player))
								);
								option("Okay then Murphy, just keep us afloat.", new Dialogue()
										.addNext(new PlayerStatement(HeadE.CALM, "Okay then Murphy, just keep us afloat."))
										.addNext(new NPCStatement(463, HeadE.CALM, "That's the attitude sailor... Aaaaargh!"))
								);
							}
						})
				);
				String[] messages = Utils.random(2) == 1 ? new String[] { "It's a fierce sea today, traveller!", "You'd best hold on tight." } : new String[] { "Get those fishies!" };
				option("How are you doing, Murphy?", new Dialogue()
						.addNext(new PlayerStatement(HeadE.CALM, "How are you doing, Murphy?"))
						.addNext(new NPCStatement(463, HeadE.CALM, messages))
				);
			}
		});

	}

}
