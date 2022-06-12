package com.rs.game.content.dialogues_matrix;

import com.rs.game.content.dialogue.Conversation;
import com.rs.game.content.dialogue.Dialogue;
import com.rs.game.content.dialogue.HeadE;
import com.rs.game.content.dialogue.Options;
import com.rs.game.model.entity.player.Player;

public class JesseDungeonPartyStart extends Conversation {
	public JesseDungeonPartyStart(Player player) {
		super(player);
		addSimple("You must be in a party to enter a dungeon.");
		addOptions("Would you like to start a dungeon?", new Options() {
			@Override
			public void create() {
				option("Yes.", new Dialogue()
						.addNext(()->{player.getDungManager().formParty();})
				);
				option("No.", new Dialogue());
			}
		});
		create();
	}
}
