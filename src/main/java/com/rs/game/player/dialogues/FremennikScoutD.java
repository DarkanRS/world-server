package com.rs.game.player.dialogues;

import com.rs.game.player.content.skills.Fletching;
import com.rs.game.player.content.skills.dungeoneering.rooms.PuzzleRoom;
import com.rs.game.player.content.skills.dungeoneering.rooms.puzzles.FremennikCampRoom;

public class FremennikScoutD extends Dialogue {

	@Override
	public void start() {
		PuzzleRoom room = (PuzzleRoom) parameters[0];
		if (room.isComplete()) {
			sendNPCDialogue(FremennikCampRoom.FREMENNIK_SCOUT, NORMAL, "Wonderful! That was the last of them. As promised, I'll unlock the door for you.");
			stage = 100;
		} else {
			sendNPCDialogue(FremennikCampRoom.FREMENNIK_SCOUT, NORMAL, "Need some tools?");
			stage = 1;
		}
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (stage == 1) {
			if (!player.getInventory().containsItem(Fletching.DUNGEONEERING_KNIFE)) {
				player.getInventory().addItem(Fletching.DUNGEONEERING_KNIFE, 1);
			}
			stage = 100;
		}
		if (stage == 100) {
			end();
		}
	}

	@Override
	public void finish() {

	}

}
