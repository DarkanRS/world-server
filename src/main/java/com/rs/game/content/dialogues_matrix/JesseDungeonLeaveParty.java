package com.rs.game.content.dialogues_matrix;

import com.rs.game.content.dialogue.Conversation;
import com.rs.game.content.dialogue.Dialogue;
import com.rs.game.content.dialogue.Options;
import com.rs.game.model.entity.player.Player;

public class JesseDungeonLeaveParty extends Conversation {
	public JesseDungeonLeaveParty(Player player) {
		super(player);
		addSimple("Warning: If you leave the dungeon, you will not be able to return to it!");
		addOptions("Leave the dungeon for good?", new Options() {
			@Override
			public void create() {
				option("Yes.", new Dialogue()
						.addNext(()->{player.getDungManager().leaveParty();})
				);
				option("No.", new Dialogue());
			}
		});
		create();
	}
}
