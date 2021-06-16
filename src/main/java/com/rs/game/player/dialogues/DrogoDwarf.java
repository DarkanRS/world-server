package com.rs.game.player.dialogues;

import com.rs.cache.loaders.NPCDefinitions;
import com.rs.game.npc.NPC;
import com.rs.utils.shop.ShopsHandler;

public class DrogoDwarf extends Dialogue {

	NPC npc;

	@Override
	public void start() {
		npc = (NPC) parameters[0];
		sendEntityDialogue(SEND_1_TEXT_CHAT, new String[] { NPCDefinitions.getDefs(npc.getId()).getName(), "'Ello. Welcome to my Mining shop, friend." }, IS_NPC, npc.getId(), 9827);

	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (stage == -1) {
			stage = 0;
			sendOptionsDialogue(SEND_DEFAULT_OPTIONS_TITLE, "Do you want to trade?", "Hello, shorty.", "Why don't you ever restock ores and bars?");
		} else if (stage == 0) {
			if (componentId == OPTION_1) {
				stage = 2;
				sendPlayerDialogue(9827, "Do you want to trade?");
			} else if (componentId == OPTION_2) {
				stage = 3;
				sendPlayerDialogue(9827, "Hello, shorty.");
			} else {
				stage = 4;
				sendPlayerDialogue(9827, "Why don't you ever restock ores and bars?");
			}
		} else if (stage == 2) {
			ShopsHandler.openShop(player, "drogos_mining_emporium");
			end();
		} else if (stage == 3) {
			stage = -2;
			sendEntityDialogue(SEND_1_TEXT_CHAT, new String[] { NPCDefinitions.getDefs(npc.getId()).getName(), "I may be short but at least I've got manners." }, IS_NPC, npc.getId(), 9827);
		} else if (stage == 4) {
			stage = -2;
			sendEntityDialogue(SEND_1_TEXT_CHAT, new String[] { NPCDefinitions.getDefs(npc.getId()).getName(), "The only ores and bars I sell are those sold to me." }, IS_NPC, npc.getId(), 9827);

		} else
			end();

	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub

	}

}
