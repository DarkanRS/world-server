package com.rs.game.player.dialogues;

import com.rs.cache.loaders.NPCDefinitions;
import com.rs.game.player.Player;
import com.rs.lib.util.Utils;

public class DagonHai extends Dialogue {

	public int npcId;

	@Override
	public void start() {
		npcId = (int) parameters[0];
		Player player = (Player) parameters[1];
		stage = (byte) ((int) parameters[2]);
		if (stage == 1) {
			stage = 2;
			sendEntityDialogue(SEND_1_TEXT_CHAT, new String[] { player.getDisplayName(), "That monk - he called to Zamorak for revenge." }, IS_PLAYER, player.getIndex(), 9827); // TODO
																																													// Need
																																													// correct
																																													// Animation
																																													// ID.
		} else {
			sendEntityDialogue(SEND_3_TEXT_CHAT, new String[] { NPCDefinitions.getDefs(npcId).getName(), "Our Lord Zamorak has power over life and death,",
					Utils.formatPlayerNameForDisplay(player.getDisplayName()) + "! He has seen fit to ressurect Bork to", "continue his great work... and now you will fall before him!" }, IS_NPC, npcId, 9843); // TODO
																																																					// Need
																																																					// correct
																																																					// Animation
																																																					// ID.
		}
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (stage == -1) {
			stage = 1;
			sendPlayerDialogue(9827, "Uh-oh! Here we go again.");
		} else if (stage == 2) {
			stage = 3;
			// player.getPackets().sendCameraShake(3, 30, 5,
			// 30, 5);
			sendPlayerDialogue(9827, "What th-? This power again! It must be Zamorak! I", "can't fight something this strong! I better loot what I ", "can and get out of here!");
		} else {
			end();
		}
	}

	@Override
	public void finish() {

	}

}
