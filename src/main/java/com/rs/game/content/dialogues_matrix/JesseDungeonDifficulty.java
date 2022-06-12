package com.rs.game.content.dialogues_matrix;

import com.rs.game.content.dialogue.Conversation;
import com.rs.game.content.dialogue.Dialogue;
import com.rs.game.content.dialogue.Options;
import com.rs.game.model.entity.player.Player;

public class JesseDungeonDifficulty extends Conversation {
	public JesseDungeonDifficulty(Player player, int partySize) {
		super(player);
		addOptions("What difficulty of dungeon would you like?", new Options() {
			@Override
			public void create() {
				for(int i = 1; i <= partySize; i++) {
					final int size = i;
					option(i + (i == partySize ? " (recommended)" : ""), new Dialogue().addNext(() -> {
						player.getDungManager().setDificulty(size);
						player.getDungManager().enterDungeon(true);
					}));
				}
			}
		});
		create();
	}
}
