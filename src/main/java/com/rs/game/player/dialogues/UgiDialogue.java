package com.rs.game.player.dialogues;

import com.rs.game.npc.others.Ugi;
import com.rs.game.player.managers.TreasureTrailsManager;
import com.rs.lib.util.Utils;

public class UgiDialogue extends Dialogue {

	@Override
	public void start() {
		Ugi npc = (Ugi) parameters[0];
		stage = npc.getOwner() == player && player.getTreasureTrailsManager().getPhase() == 4 ? (byte) 0 : (byte) -1;
		run(-1, -1);
	}

	@Override
	public void run(int interfaceId, int componentId) {
		Ugi npc = (Ugi) parameters[0];
		if (stage == 0) {
			sendNPCDialogue(npc.getId(), NORMAL, TreasureTrailsManager.UGIS_QUOTES[Utils.random(TreasureTrailsManager.UGIS_QUOTES.length)]);
			stage = 1;
		} else if (stage == 1) {
			sendPlayerDialogue(NORMAL, "What?");
			stage = 2;
		} else if (stage == 2) {
			end();
			npc.finish();

			player.getTreasureTrailsManager().setPhase(5);
			player.getTreasureTrailsManager().setNextClue(TreasureTrailsManager.SOURCE_EMOTE);
		} else if (stage == -1) {
			sendNPCDialogue(npc.getId(), NORMAL, TreasureTrailsManager.UGI_BADREQS);
			stage = -2;
		} else {
			end();
		}
	}

	@Override
	public void finish() {

	}
}
