package com.rs.game.player.dialogues;

import com.rs.game.player.content.transportation.SpiritTree;

public class SpiritTreeD extends Dialogue {

	private int npcId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendNPCDialogue(npcId, 9827, "If you are a friend of the gnome people, you are a friend of mine.", " Do you wish to travel?");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (stage == -1) {
			stage = 0;
			sendOptionsDialogue(SEND_DEFAULT_OPTIONS_TITLE, "Yes please.", "No thanks.");
		} else if (stage == 0) {
			if (componentId == OPTION_1) {
				stage = 1;
				sendPlayerDialogue(9827, "Yes please.");
			} else {
				stage = 2;
				sendPlayerDialogue(9827, "No thanks.");
			}
		} else if (stage == 1) {
			SpiritTree.openInterface(player, npcId == 3636);
			end();
		} else if (stage == 2) {
			end();
		}
	}

	@Override
	public void finish() {

	}
}
