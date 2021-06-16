package com.rs.game.player.dialogues;

import com.rs.game.player.controllers.FightKilnController;

public class FightKilnDialogue extends Dialogue {

	@Override
	public void start() {
		player.lock();
		sendDialogue("You journey directly to the Kiln.");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		end();
	}

	@Override
	public void finish() {
		player.getControllerManager().startController(new FightKilnController(0));
	}

}
