package com.rs.game.player.dialogues;

import com.rs.game.player.controllers.QueenBlackDragonController;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.WorldTile;

public class GrotwormLairD extends Dialogue {

	@Override
	public void start() {
		if ((boolean) this.parameters[0]) {
			stage = -1;
			sendDialogue("The shortcut leads to the deepest level of the dungeon. The worms in that area are significantly more dangerous.");
		} else {
			stage = 2;
			sendDialogue("You will be sent to the heart of this cave complex - alone. There is no way out other than victory, teleportation, or death. Only those who can endure dangerous counters (level 110 or more) should proceed.");
		}
	}

	@Override
	public void run(int interfaceId, int componentId) {
		stage++;
		if (stage == 0) {
			sendOptionsDialogue("Slide down the worm burrow?", "Yes.", "No.");
		} else if (stage == 1) {
			if (componentId == OPTION_1)
				player.useStairs(-1, new WorldTile(1206, 6506, 0), 1, 2);
			end();
		} else if (stage == 3) {
			sendOptionsDialogue(SEND_DEFAULT_OPTIONS_TITLE, "Proceed.", "Step away from the portal.");
			stage = 3;
		} else if (stage == 4) {
			if (componentId == OPTION_1) {
				if (player.getSkills().getLevelForXp(Constants.SUMMONING) < 60) {
					player.sendMessage("You need a summoning level of 60 to go through this portal.");
					return;
				}
				player.lock();
				player.getControllerManager().startController(new QueenBlackDragonController());
				player.setNextAnimation(new Animation(16752));
			}
			end();
		}
	}

	@Override
	public void finish() {

	}
}
