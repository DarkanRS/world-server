package com.rs.game.content.dialogues_matrix;

import com.rs.game.content.dialogue.Conversation;
import com.rs.game.content.dialogue.Dialogue;
import com.rs.game.content.dialogue.Options;
import com.rs.game.content.skills.dungeoneering.DungeonConstants;
import com.rs.game.content.skills.dungeoneering.DungeonPartyManager;
import com.rs.game.model.entity.player.Player;

public class JesseDungeonSize extends Conversation {
	public JesseDungeonSize(Player player) {
		super(player);
		DungeonPartyManager party = player.getDungManager().getParty();
		addSimple("What size of dungeon would you like?");
		addOptions("Would you like to start a dungeon?", new Options() {
			@Override
			public void create() {
				option("Small.", new Dialogue()
						.addNext(()->{
							if(party != null) {
								player.getDungManager().setSize(DungeonConstants.SMALL_DUNGEON);
								player.getDungManager().enterDungeon(false);
							}
						})
				);
				option("Medium.", new Dialogue()
						.addNext(()->{
							if(party != null) {
								player.getDungManager().setSize(DungeonConstants.MEDIUM_DUNGEON);
								player.getDungManager().enterDungeon(false);
							}
						})
				);
				if(party != null && party.getTeam().size() >= 3)
					option("Large.", new Dialogue()
							.addNext(()->{
								player.getDungManager().setSize(DungeonConstants.LARGE_DUNGEON);
								player.getDungManager().enterDungeon(false);
							})
					);
			}
		});
		create();
	}
}
