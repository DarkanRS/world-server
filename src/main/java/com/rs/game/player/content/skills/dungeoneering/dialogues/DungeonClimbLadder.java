package com.rs.game.player.content.skills.dungeoneering.dialogues;

import com.rs.game.player.controllers.DungeonController;
import com.rs.game.player.dialogues.Dialogue;

public class DungeonClimbLadder extends Dialogue {

	private DungeonController dungeon;

	@Override
	public void start() {
		dungeon = (DungeonController) parameters[0];
		sendOptionsDialogue("Are you sure you wish to proceed and take your party with you?", "Yes.", "No.");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (componentId == OPTION_1)
			dungeon.voteToMoveOn();
		end();
	}

	@Override
	public void finish() {

	}

}
