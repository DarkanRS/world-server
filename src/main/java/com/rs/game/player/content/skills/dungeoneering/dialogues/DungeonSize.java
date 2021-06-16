package com.rs.game.player.content.skills.dungeoneering.dialogues;

import com.rs.game.player.content.skills.dungeoneering.DungeonConstants;
import com.rs.game.player.content.skills.dungeoneering.DungeonPartyManager;
import com.rs.game.player.dialogues.Dialogue;

public class DungeonSize extends Dialogue {

	@Override
	public void start() {
		sendDialogue("What size of dungeon would you like?");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		DungeonPartyManager party = player.getDungManager().getParty();
		if (stage == -1) {
			if (party == null || party.getTeam().size() < 3)
				sendOptionsDialogue("Would you like to start a dungeon?", "Small.", "Medium.");
			else
				sendOptionsDialogue("Would you like to start a dungeon?", "Small.", "Medium.", "Large.");
			stage = 0;
		} else if (stage == 0) {
			if (party != null) {
				if (componentId == OPTION_1)
					player.getDungManager().setSize(DungeonConstants.SMALL_DUNGEON);
				else if (componentId == OPTION_2)
					player.getDungManager().setSize(DungeonConstants.MEDIUM_DUNGEON);
				else if (componentId == OPTION_3 && party.getTeam().size() >= 3)
					player.getDungManager().setSize(DungeonConstants.LARGE_DUNGEON);
				player.getDungManager().enterDungeon(false);
			}
			end();
		}
	}

	@Override
	public void finish() {

	}

}
