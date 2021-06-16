package com.rs.game.player.content.skills.dungeoneering.dialogues;

import com.rs.game.player.dialogues.Dialogue;

public class DungeonLeaveParty extends Dialogue {

	@Override
	public void start() {
		sendDialogue("Warning: If you leave the dungeon, you will not be able to return to it!");

	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (stage == -1) {
			sendOptionsDialogue("Leave the dungeon for good?", "Yes.", "No.");
			stage = 0;
		} else if (stage == 0) {
			if (componentId == OPTION_1)
				player.getDungManager().leaveParty();
			end();
		}
	}

	@Override
	public void finish() {

	}

}
