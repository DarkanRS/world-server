package com.rs.game.content.dialogues_matrix;

import com.rs.game.content.dialogue.Conversation;
import com.rs.game.content.dialogue.Dialogue;
import com.rs.game.content.dialogue.Options;
import com.rs.game.model.entity.player.Player;

public class JessePrestigeReset extends Conversation {
	public JessePrestigeReset(Player player) {
		super(player);
		addSimple("Are you sure you want to reset your dungeon progress? Your previous progress will be set to the number of floors you have completed " +
				"and all floors will be marked as incomplete. This cannot be undone.");
		addOptions("Are you sure?", new Options() {
			@Override
			public void create() {
				option("Yes, reset my progress.", new Dialogue()
						.addNext(()->{player.getDungManager().resetProgress();})
				);
				option("No, don't reset my progress.", new Dialogue());
			}
		});
		create();
	}
}
