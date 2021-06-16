package com.rs.game.player.content.skills.construction;

import com.rs.game.player.content.skills.construction.House.RoomReference;
import com.rs.game.player.dialogues.Dialogue;

public class RemoveRoomD extends Dialogue {
	
	private RoomReference room;

	@Override
	public void start() {
		this.room = (RoomReference) parameters[0];
		sendOptionsDialogue("Do you really want to remove the room?", "Yes", "No");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (componentId == OPTION_1 && stage != 1) {
			stage = 1;
			sendOptionsDialogue("You can't get anything back? Remove room?", "Yes! Get rid of my money already!", "No");
		} else if (componentId == OPTION_1 && stage == 1) {
			player.getHouse().removeRoom(room);
			end();
		} else {
			end();
		}
	}

	@Override
	public void finish() {
		
	}
}
