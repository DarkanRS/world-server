package com.rs.game.player.dialogues;

import com.rs.game.npc.NPC;
import com.rs.game.player.controllers.SorceressGardenController;

public class SorceressGardenNPCs extends Dialogue {

	public NPC npc;

	@Override
	public void start() {
		npc = (NPC) parameters[0];
		sendPlayerDialogue(9827, ((npc.getId() != 5532 && npc.getId() == 5563) ? "Hey kitty!" : "Hey apprentice, do you want to try out your teleport skills again?"));
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (stage == -1) {
			stage = 0;
			if (npc.getId() == 5563) {
				sendNPCDialogue(npc.getId(), 9827, "Hiss!");
				finish();
				return;
			} else if (npc.getId() == 5532) {
				sendNPCDialogue(npc.getId(), 9827, "Okay, here goes - and remember, to return just drink from the fountain.");
			}
		} else if (stage == 0) {
			stage = 1;
			if (npc.getId() == 5532) {
				SorceressGardenController.teleportToSorceressGardenNPC(npc, player);
			}
			end();
		}
	}

	@Override
	public void finish() {

	}

}
