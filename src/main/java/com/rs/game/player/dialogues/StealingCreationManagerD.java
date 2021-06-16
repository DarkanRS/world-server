package com.rs.game.player.dialogues;

import com.rs.game.npc.NPC;
import com.rs.game.player.content.minigames.creations.Helper;
import com.rs.game.player.controllers.StealingCreationGameController;
import com.rs.lib.util.Utils;

public class StealingCreationManagerD extends Dialogue {

	StealingCreationGameController game;

	@Override
	public void start() {
		game = (StealingCreationGameController) this.parameters[1];
		player.heal(player.getMaxHitpoints());
		player.getPoison().reset();
		sendNPCDialogue(getNPC().getId(), MAD, Utils.random(3) == 0 ? "It's close. It could be anyone's game." : "Get a move on! Gather some clay before the other team takes it all!");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (stage == -1) {
			sendOptionsDialogue(SEND_DEFAULT_OPTIONS_TITLE, "Show me the remaining clay.", "I want to quit!", "Never mind.");
			stage = 0;
		} else if (stage == 0) {
			if (componentId == OPTION_1) {
				Helper.displayClayStatus(game.getArea(), player);
				end();
			} else if (componentId == OPTION_2) {
				sendNPCDialogue(getNPC().getId(), NORMAL, "Are you sure you want to quit? You will not score any points if you leave.");
				stage = 1;
			} else if (componentId == OPTION_3) {
				end();
			}
		} else if (stage == 1) {
			sendOptionsDialogue(SEND_DEFAULT_OPTIONS_TITLE, "Yes, I want to quit.", "No, I want to stay.");
			stage = 2;
		} else if (stage == 2) {
			if (componentId == OPTION_1) {
				player.getControllerManager().forceStop();
			}
			end();
		}
	}

	@Override
	public void finish() {
		game = null;
		parameters = null;
	}

	public NPC getNPC() {
		return (NPC) parameters[0];
	}
}
