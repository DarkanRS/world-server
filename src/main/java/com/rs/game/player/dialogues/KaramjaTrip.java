package com.rs.game.player.dialogues;

import com.rs.cache.loaders.NPCDefinitions;

public class KaramjaTrip extends Dialogue {

	private int npcId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendEntityDialogue(SEND_1_TEXT_CHAT, new String[] { NPCDefinitions.getDefs(npcId).getName(), "Do you want to go on a trip to Karamja?" }, IS_NPC, npcId, 9827);
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (stage == -1) {
			sendEntityDialogue(SEND_1_TEXT_CHAT, new String[] { NPCDefinitions.getDefs(npcId).getName(), "The trip will cost you 30 coins." }, IS_NPC, npcId, 9827);
			stage = 0;
		} else if (stage == 0) {
			sendOptionsDialogue("Would you like to go?", "Yes please.", "No thank you.");
			stage = 1;
		} else if (stage == 1) {
			if (componentId == OPTION_1) {
				doTrip();
			} else if (componentId == OPTION_2) {
				sendPlayerDialogue(9827, "No thank you.");
			}
			stage = 3;
		} else if (stage == 2) {
			sendDialogue("The ship arrived at Karamja.");
			stage = 3;
		} else if (stage == 3) {
			end();
		}
	}

	@Override
	public void finish() {

	}

	public void doTrip() {
		// Inter 299
		sendDialogue("We are having problems... Please wait");

	}

}
