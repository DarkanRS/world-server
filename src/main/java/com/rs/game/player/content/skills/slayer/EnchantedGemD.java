package com.rs.game.player.content.skills.slayer;

import com.rs.game.player.dialogues.Dialogue;
import com.rs.lib.util.Utils;

public class EnchantedGemD extends Dialogue {
	
	private Master npc;

	@Override
	public void start() {
		npc = (Master) parameters[0];
		sendNPCDialogue(npc.npcId, 9827, "Hello there, "+player.getDisplayName()+", what can I help you with?");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			stage = 0;
			sendOptionsDialogue(SEND_DEFAULT_OPTIONS_TITLE, "How am I doing so far?", "Who are you?", "Where are you?");
			break;
		case 0:
			switch (componentId) {
			case OPTION_1:
				stage = 3;
				sendPlayerDialogue(9827, "How am I doing so far?");
				break;
			case OPTION_2:
				stage = 1;
				sendPlayerDialogue(9827, "Who are you?");
				break;
			case OPTION_3:
				stage = 2;
				sendPlayerDialogue(9827, "Where are you?");
				break;
			default:
				end();
				break;
			}
			break;
		case 1:
			stage = -2;
			sendNPCDialogue(npc.npcId, 9827, "My name is "+Utils.formatPlayerNameForDisplay(Master.getMasterForId(npc.npcId).name().toLowerCase())+"; I'm a Slayer Master.");
			break;
		case 2:
			stage = -2;
			sendNPCDialogue(npc.npcId, 9827, "I'm in a meme.");
			break;
		case 3:
			stage = -2;
			sendNPCDialogue(npc.npcId, 9827, player.getSlayer().getTaskString());
			break;
		default:
			end();
			break;
		}
	}

	@Override
	public void finish() {
		
	}

}
