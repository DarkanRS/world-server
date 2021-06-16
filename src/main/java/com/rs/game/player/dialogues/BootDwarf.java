package com.rs.game.player.dialogues;

import com.rs.cache.loaders.NPCDefinitions;

public class BootDwarf extends Dialogue {

	private int npcId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendEntityDialogue(SEND_1_TEXT_CHAT, new String[] { NPCDefinitions.getDefs(npcId).getName(), "'Hello tall person." }, IS_NPC, npcId, 9827);

	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (stage == -1) {
			stage = 0;
			sendOptionsDialogue(SEND_DEFAULT_OPTIONS_TITLE, "Hello short person.", "Why are you called boot?");
		} else if (stage == 0) {
			if (componentId == OPTION_1) {
				stage = -2;
				sendPlayerDialogue(9827, "Hello short person.");
			} else {
				stage = 1;
				sendPlayerDialogue(9827, "Why are you called boot?");
			}
		} else if (stage == 1) {
			stage = 2;
			sendEntityDialogue(SEND_2_TEXT_CHAT, new String[] { NPCDefinitions.getDefs(npcId).getName(), "I'm called Boot, becasue when I was very young, I used to", "sleep, in a largue boot." }, IS_NPC, npcId, 9827);
		} else if (stage == 2) {
			stage = -2;
			sendPlayerDialogue(9827, "Yeah, great, I didn't want your life story.");
		} else
			end();

	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub

	}

}
