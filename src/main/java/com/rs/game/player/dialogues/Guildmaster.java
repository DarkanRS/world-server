package com.rs.game.player.dialogues;

import com.rs.game.npc.NPC;

public class Guildmaster extends Dialogue {

	NPC npc;

	@Override
	public void start() {
		npc = (NPC) parameters[0];
		sendNPCDialogue(npc.getId(), 9827, "Greetings!");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			stage = 0;
			sendPlayerDialogue(9827, "What is this place?");
			break;
		case 0:
			stage = -2;
			sendNPCDialogue(npc.getId(), 9827, "This is the Champions' Guild. Only adventurers who have", "proved themselves worthy by gaining influence from", "quests are allowed in here.");
			break;
		default:
			end();
			break;
		}

	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub

	}

}
