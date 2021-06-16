package com.rs.game.player.content.skills.dungeoneering.dialogues;

import com.rs.game.player.dialogues.Dialogue;

public class DungeonPartyStart extends Dialogue {

	@Override
	public void start() {
		sendDialogue("You must be in a party to enter a dungeon.");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (stage == -1) {
			sendOptionsDialogue("Would you like to start a dungeon?", "Yes.", "No.");
			stage = 0;
		} else if (stage == 0) {
			if (componentId == OPTION_1)
				player.getDungManager().formParty();
			end();
		}
	}

	@Override
	public void finish() {

	}

}
