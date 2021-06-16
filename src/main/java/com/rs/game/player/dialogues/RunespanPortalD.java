package com.rs.game.player.dialogues;

import com.rs.game.player.controllers.RunespanController;
import com.rs.lib.game.WorldTile;

public class RunespanPortalD extends Dialogue {

	@Override
	public void start() {
		sendOptionsDialogue("Where would you like to travel to?", "The Runecrafting Guild", "Low level entrance into the Runespan", "High level entrance into the Runespan");
		stage = 1;
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (stage == 1) {
			if (componentId == OPTION_1) {
				player.useStairs(-1, new WorldTile(1696, 5460, 2), 0, 1);
				end();
			} else {
				RunespanController.enterRunespan(player, componentId == OPTION_3);
				end();
			}
		}

	}

	@Override
	public void finish() {

	}

}
